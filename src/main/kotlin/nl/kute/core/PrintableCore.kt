@file:JvmName("PrintableCore")
@file:Suppress("SameParameterValue", "SameParameterValue")

// TODO: caching of properties & annotations

package nl.kute.core

import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.printable.annotation.modifiy.hashString
import nl.kute.printable.annotation.modifiy.mask
import nl.kute.printable.annotation.modifiy.replacePattern
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.printable.annotation.option.PrintOption.DefaultOption.defaultPrintOption
import nl.kute.reflection.annotationfinder.annotationOfPropertySubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfPropertySuperSubHierarchy
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfToStringSubSuperHierarchy
import nl.kute.reflection.getPropValue
import nl.kute.reflection.propertiesFromSubSuperHierarchy
import nl.kute.util.asString
import nl.kute.util.lineEnd
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

// Static stuff (package level) only

const val maxValueLength: Int = 500

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asStringExcluding]
 * @see [asStringExcludingNames]
 */
fun Any.asString(): String {
    return asStringExcluding()
}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 * @param propsToExclude accessible properties that you don't want to be included in the result.
 * E.g. `override fun toString() = `[asStringExcluding]`(::myExcludedProp1, ::myExcludedProp2)`
 * **NB:** Excluding properties will not work from Java classes; use [asStringExcludingNames] instead
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asStringExcludingNames]
 * @see [asString]
 */
fun Any.asStringExcluding(vararg propsToExclude: KProperty<*>): String {
    return asStringExcludingNames(*(propsToExclude.map { it.name }.toTypedArray()))
}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 *
 * This method allows you to exclude any properties by name, including inaccessible private ones.
 * @param propNamesToExclude accessible properties that you don't want to be included in the result.
 * E.g. use it when not calling from inside the class:
 * `someObjectWithPrivateProps.`[asStringExcludingNames]`("myExcludedPrivateProp1", "myExcludedProp2")
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asString]
 * @see [asStringExcluding]
 */
fun <T: Any> T.asStringExcludingNames(vararg propNamesToExclude: String): String {
    try {
        val annotationsByProperty = this::class.propertiesWithPrintModifyingAnnotations()

        return annotationsByProperty
            .filterNot { propNamesToExclude.contains(it.key.name) }
            .filterNot { entry -> entry.value.any { annotation -> annotation is PrintOmit } }
            .entries.joinToString(", ", prefix = "${this::class.simpleName ?: this::class.toString()}(", ")") { entry ->
                @Suppress("UNCHECKED_CAST")
                val prop = entry.key as KProperty1<T, *>
                val annotationSet = entry.value
                val printOption: PrintOption = (annotationSet.firstOrNull { it is PrintOption } ?: defaultPrintOption) as PrintOption
                val maxValLengthProp = min(max(printOption.propMaxStringValueLength, 0), maxValueLength)
                "${prop.name}=${getPropValueString(prop, annotationSet)?.take(maxValLengthProp) ?: printOption.showNullAs}"
            }
    } catch (e: Exception) {
        // We have no logger in the Kute project; we don't want to interfere with the caller project's logger
        // So we can only return a value that describes what happened.
        return "ERROR: Exception ${e.javaClass.simpleName} occurred when building string value for object of class ${this.javaClass};$lineEnd${e.asString()}"
    } catch (t: Throwable) {
        return "FATAL ERROR: Throwable ${t.javaClass.simpleName} occurred when building string value for object of class ${this.javaClass};$lineEnd${t.asString()}"
    }
}

/** @return
 *  * for [Array]s: [Array.contentDeepToString]
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[PrintOmit],
 *  @[PrintPatternReplace], @[PrintMask], @[PrintHash]
 */
private fun <T: Any> T.getPropValueString(prop: KProperty1<T, *>, annotations:  Set<Annotation>): String? {
    val value: Any? = this.getPropValue(prop)
    var strValue = if (value is Array<*>)
        value.contentDeepToString()
    else {
        value?.toString()
    }
    if (annotations.isEmpty()) {
        return strValue
    }
    if (annotations.any { it is PrintOmit }) {
        return ""
    }
    val printPatternReplace = annotations.firstOrNull { it is PrintPatternReplace } as PrintPatternReplace?
    val printMask = annotations.firstOrNull { it is PrintMask } as PrintMask?
    val printHash = annotations.firstOrNull { it is PrintHash } as PrintHash?
    strValue = printPatternReplace.replacePattern(strValue)
    strValue = printMask.mask(strValue)
    strValue = printHash.hashString(strValue)
    return strValue
}

internal fun <T: Any> KClass<T>.propertiesWithPrintModifyingAnnotations(): Map<KProperty1<T, *>, Set<Annotation>> {
    // map each property to an (empty yet) mutable set of annotations
    val resultMap: Map<KProperty1<T, *>, MutableSet<Annotation>> =
        propertiesFromSubSuperHierarchy().associateWith { mutableSetOf() }

    resultMap
        .forEach { (prop, annotations) ->
            (prop.annotationOfPropertySuperSubHierarchy<PrintOmit>())?.let { annotation ->
                annotations.add(annotation)
                // any further annotations are meaningless because output will be omitted when PrintOmit is present
                return@forEach
            }
            (prop.annotationOfPropertySuperSubHierarchy<PrintHash>())?.let { annotation ->
                annotations.add(annotation)
            }
            (prop.annotationOfPropertySuperSubHierarchy<PrintMask>())?.let { annotation ->
                annotations.add(annotation)
            }
            (prop.annotationOfPropertySuperSubHierarchy<PrintPatternReplace>())?.let { annotation ->
                annotations.add(annotation)
            }
            // PrintOption from lowest subclass in hierarchy with this annotation
            val printOptionClassAnnotation =
                this.annotationOfToStringSubSuperHierarchy() ?: annotationOfSubSuperHierarchy() ?: defaultPrintOption
            (prop.annotationOfPropertySubSuperHierarchy() ?: printOptionClassAnnotation).let { annotation ->
                annotations.add(annotation)
            }
        }

    return resultMap
}
