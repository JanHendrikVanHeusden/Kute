@file:JvmName("AsString")
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
import nl.kute.printable.namedvalues.PropertyValue
import nl.kute.printable.namedvalues.namedVal
import nl.kute.util.asString
import nl.kute.util.lineEnd
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

private val regexPackage = Regex(""".+\.(.*)$""")
private fun String.simplifyClassName() = this.replace(regexPackage, "$1")

private val emptyStringList: List<String> = listOf()
private val emptyPropertyList: List<KProperty<*>> = listOf()

private const val valueSeparator: String = ", "

internal fun KClass<*>.nameToPrint() = simpleName ?: toString().simplifyClassName()

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asStringExcluding]
 * @see [objectAsString]
 */
fun Any?.asString(): String {
    return asStringExcluding()
}

fun Any?.asString(vararg props: KProperty<*>): String =
    asString(*props.map { namedVal(it) }.toTypedArray())

fun Any?.asString(vararg nameValues: NameValue<*>): String =
    objectAsString(emptyStringList, *nameValues)

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 * @param propsToExclude accessible properties that you don't want to be included in the result.
 * E.g. `override fun toString() = `[asStringExcluding]`(::myExcludedProp1, ::myExcludedProp2)`
 * **NB:** Excluding properties will not work from Java classes; use [objectAsString] instead
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [objectAsString]
 * @see [asString]
 */
internal fun Any?.asStringExcluding(propsToExclude: Collection<KProperty<*>> = emptyPropertyList, vararg nameValues: NameValue<*>): String {
    return objectAsString(propsToExclude.map { it.name }, *nameValues)
}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 *
 * This method allows you to exclude any properties by name, including inaccessible private ones.
 * @param properytyNamesToExclude accessible properties that you don't want to be included in the result.
 * E.g. use it when not calling from inside the class:
 * `someObjectWithPrivateProps.`[objectAsString]`("myExcludedPrivateProp1", "myExcludedProp2")
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations;
 * for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asString]
 * @see [asStringExcluding]
 */
@Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // Compiler warning that we don't need the `obj!!` - but compilation fails if we remove `!!`
internal fun <T : Any?> T.objectAsString(properytyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>
): String {
    if (this == null) {
        return defaultNullString
    } else
    try {
        try {
            val annotationsByProperty: Map<KProperty<*>, Set<Annotation>> =
                this!!::class.propertiesWithPrintModifyingAnnotations()
                    .filterNot { properytyNamesToExclude.contains(it.key.name) }
                    .filterNot { entry -> entry.value.any { annotation -> annotation is PrintOmit } }
            val named = nameValues
                .filterNot { it is PropertyValue<*, *> && it.printModifyingAnnotations.any { it is PrintOmit } }
            val nameValueSeparator = if (annotationsByProperty.isEmpty() || named.isEmpty()) "" else valueSeparator
            return annotationsByProperty
                .entries.joinToString(separator = valueSeparator, prefix = "${this!!::class.nameToPrint()}(") { entry ->
                    val prop = entry.key
                    val annotationSet = entry.value
                    "${prop.name}=${getPropValueString(prop, annotationSet)}"
                } + named.joinToString(prefix = nameValueSeparator, separator = ", ", postfix = ")") {
                    "${it.name}=${it.valueString ?: defaultNullString}"
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

