@file:JvmName("PrintableCore")
@file:Suppress("SameParameterValue", "SameParameterValue")

// TODO: caching of properties & annotations

package nl.kute.core

import nl.kute.core.property.getPropValueString
import nl.kute.core.property.propertiesWithPrintModifyingAnnotations
import nl.kute.log.log
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.printable.annotation.option.defaultNullString
import nl.kute.printable.namedvalues.NameValue
import nl.kute.printable.namedvalues.namedVal
import nl.kute.printable.namedvalues.resolver.getNamedValue
import nl.kute.util.asString
import nl.kute.util.lineEnd
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation

private val regexPackage = Regex(""".+\.(.*)$""")
private fun String.simplifyClassName() = this.replace(regexPackage, "$1")

private val emptyStringList: List<String> = listOf()
private val emptyPropertyList: List<KProperty<*>> = listOf()

private val valueSeparator: String = ", "

internal fun KClass<*>.nameToPrint() = simpleName ?: toString().simplifyClassName()

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

fun Any.asString(vararg props: KProperty<*>): String =
    asString(*props.map { namedVal(it) }.toTypedArray())

fun Any.asString(vararg nameValues: NameValue<*>): String =
    asStringExcludingNames(emptyStringList, *nameValues)

fun Any.asStringWithOnly(vararg props: KProperty<*>): String =
    asString(*props.map { namedVal(it) }.toTypedArray())

fun Any.asStringWithOnly(vararg nameValues: NameValue<*>): String {
    return nameValues
        .filterNot { prop -> prop is KProperty<*> && prop.hasAnnotation<PrintOmit>() }
        .joinToString(separator = ", ", prefix = "${this::class.nameToPrint()}(", postfix = ")") {
            getNamedValue(it)
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
fun Any.asStringExcluding(propsToExclude: Collection<KProperty<*>> = emptyPropertyList, vararg nameValues: NameValue<*>): String {
    return asStringExcludingNames(propsToExclude.map { it.name }, *nameValues)
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
 * adhering to related annotations;
 * for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asString]
 * @see [asStringExcluding]
 */
fun <T : Any> T.asStringExcludingNames(propNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>
): String {
    val nameValueSeparator = if (nameValues.isEmpty()) "" else valueSeparator
    try {
        try {
            val annotationsByProperty: Map<KProperty<*>, Set<Annotation>> =
                this::class.propertiesWithPrintModifyingAnnotations()
            return annotationsByProperty
                .filterNot { propNamesToExclude.contains(it.key.name) }
                .filterNot { entry -> entry.value.any { annotation -> annotation is PrintOmit } }
                .entries.joinToString(separator = valueSeparator, prefix = "${this::class.nameToPrint()}(") { entry ->
                    val prop = entry.key
                    val annotationSet = entry.value
                    "${prop.name}=${getPropValueString(prop, annotationSet)}"
                } + nameValues.joinToString(prefix = nameValueSeparator, separator = ", ", postfix = ")") {
                    it.valueString ?: defaultNullString
            }
        } catch (e: Exception) {
            log("ERROR: Exception ${e.javaClass.simpleName} occurred when retrieving string value for object of class ${this.javaClass};$lineEnd${e.asString()}")
            return ""
        } catch (t: Throwable) {
            log("FATAL ERROR: Throwable ${t.javaClass.simpleName} occurred when retrieving string value for object of class ${this.javaClass};$lineEnd${t.asString()}")
            return ""
        }
    } catch (t: Throwable) {
        @Suppress("UNNECESSARY_SAFE_CALL")
        return "FATAL ERROR: Unhandled Throwable ${t?.javaClass} (cause: ${t.cause?.javaClass}) occurred when retrieving string value"
    }
}

