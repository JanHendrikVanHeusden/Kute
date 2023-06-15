@file:JvmName("PrintableCore")
@file:Suppress("SameParameterValue", "SameParameterValue")

// TODO: caching of properties & annotations

package nl.kute.core

import nl.kute.log.log
import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.printable.annotation.modifiy.hashString
import nl.kute.printable.annotation.modifiy.mask
import nl.kute.printable.annotation.modifiy.replacePattern
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.printable.annotation.option.PrintOption.DefaultOption.defaultPrintOption
import nl.kute.printable.annotation.option.applyOption
import nl.kute.reflection.annotationfinder.annotationOfPropertySubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfPropertySuperSubHierarchy
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfToStringSubSuperHierarchy
import nl.kute.reflection.getPropValue
import nl.kute.reflection.propertiesFromSubSuperHierarchy
import nl.kute.util.asString
import nl.kute.util.lineEnd
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.full.hasAnnotation

// Static stuff (package level) only

private val regexPackage = Regex(""".+\.(.*)$""")
private fun String.simplifyClassName() = this.replace(regexPackage, "$1")

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

fun Any.asStringWithOnly(vararg props: Any): String {
    // FIXME: honour annotations
    return props
        .filterNot { prop -> prop is KProperty<*> && prop.hasAnnotation<PrintOmit>() }
        .joinToString(
            separator = ", ",
            prefix = "${this::class.simpleName ?: this::class.toString().simplifyClassName()}(",
            postfix = ")"
        ) {
            // fixme: take annotations into account!
            when (it) {
                is KProperty0<*> -> {
                    "${it.name}=${this.getPropValue(it)}"
                }

                is KProperty1<*, *> -> {
                    "${it.name}=${this.getPropValue(it)}"
                }

                is Pair<*, *> -> {
                    "${it.first}=${it.second}"
                }

                else -> {
                    // if not a property or a pair, we just don't know the variable name, so the only thing we have is the class name
                    "${it::class.simpleName ?: it::class.toString().simplifyClassName()}=${it}"
                }
            }
        }
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
fun <T : Any> T.asStringExcludingNames(vararg propNamesToExclude: String): String {
    try {
        try {
            val annotationsByProperty = this::class.propertiesWithPrintModifyingAnnotations()
            return annotationsByProperty
                .filterNot { propNamesToExclude.contains(it.key.name) }
                .filterNot { entry -> entry.value.any { annotation -> annotation is PrintOmit } }
                .entries.joinToString(
                    separator = ", ",
                    prefix = "${this::class.simpleName ?: this::class.toString().simplifyClassName()}(",
                    postfix = ")"
                ) { entry ->
                    val prop = entry.key
                    val annotationSet = entry.value
                    "${prop.name}=${getPropValueString(prop, annotationSet)
                    }"
                }
        } catch (e: Exception) {
            // We have no logger in the Kute project; we don't want to interfere with the caller project's logger
            // So we can only return a value that describes what happened.
            return "ERROR: Exception ${e.javaClass.simpleName} occurred when retrieving string value for object of class ${this.javaClass};$lineEnd${e.asString()}"
                .also { log(it) }
        } catch (t: Throwable) {
            return "FATAL ERROR: Throwable ${t.javaClass.simpleName} occurred when retrieving string value for object of class ${this.javaClass};$lineEnd${t.asString()}"
                .also { log(it) }
        }
    } catch (t: Throwable) {
        @Suppress("UNNECESSARY_SAFE_CALL")
        return "FATAL ERROR: Unhandled Throwable ${t?.javaClass} occurred when retrieving string value"
    }
}

/** @return
 *  * for [Array]s: [Array.contentDeepToString]
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[PrintOmit],
 *  @[PrintPatternReplace], @[PrintMask], @[PrintHash]
 */
internal fun <T : Any> T.getPropValueString(prop: KProperty<*>, annotations: Set<Annotation>): String? {
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
    val printPatternReplace: PrintPatternReplace? = annotations.findAnnotation()
    val printMask: PrintMask? = annotations.findAnnotation()
    val printHash: PrintHash? = annotations.findAnnotation()
    val printOption: PrintOption = annotations.findAnnotation()!! // always present
    strValue = printPatternReplace.replacePattern(strValue)
    strValue = printMask.mask(strValue)
    strValue = printHash.hashString(strValue)
    strValue = printOption.applyOption(strValue)
    return strValue
}

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

internal fun <T : Any> KClass<T>.propertiesWithPrintModifyingAnnotations(): Map<KProperty<*>, Set<Annotation>> {
    // map each property to an (empty yet) mutable set of annotations
    val resultMap: Map<KProperty<*>, MutableSet<Annotation>> =
        propertiesFromSubSuperHierarchy().associateWith { mutableSetOf() }

    resultMap.forEach { (prop, annotations) -> collectPropertyAnnotations(prop, annotations) }
    return resultMap
}

private fun <T : Any> KClass<T>.collectPropertyAnnotations(prop: KProperty<*>, annotations: MutableSet<Annotation>) {
    (prop.annotationOfPropertySuperSubHierarchy<PrintOmit>())?.let { annotation ->
        annotations.add(annotation)
        // any further annotations are meaningless because output will be omitted when PrintOmit is present
        return
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
