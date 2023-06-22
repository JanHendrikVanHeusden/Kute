@file:JvmName("AsString")
@file:Suppress("SameParameterValue", "SameParameterValue")

// TODO: caching of properties & annotations

package nl.kute.core

import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.defaultNullString
import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.PropertyValue
import nl.kute.core.property.getPropValueString
import nl.kute.core.property.propertiesWithPrintModifyingAnnotations
import nl.kute.log.log
import nl.kute.reflection.error.SyntheticClassException
import nl.kute.reflection.simplifyClassName
import nl.kute.util.asString
import nl.kute.util.lineEnd
import nl.kute.util.toByteArray
import nl.kute.util.toHex
import java.time.temporal.Temporal
import java.util.Date
import kotlin.reflect.KProperty

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[AsStringOption] to override the default
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[AsStringOption] and others; see package `nl.kute.core.annotation.modify`
 */
fun Any?.asString(): String {
    return asString(emptyStringList)
}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[AsStringOption] to override the default
 *
 * This method allows you to exclude any properties by name, including inaccessible private ones.
 * @param propertyNamesToExclude accessible properties that you don't want to be included in the result.
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations;
 * for these annotations, e.g. @[AsStringOption] and others; see package `nl.kute.core.annotation.modify`
 * @see [asString]
 * @see [AsStringBuilder]
 */
// Compiler warnings that we don't need the `obj!!` and this? - but compilation fails if we remove `!!` or `?.`
@Suppress("UNNECESSARY_NOT_NULL_ASSERTION", "UNNECESSARY_SAFE_CALL")
internal fun <T : Any?> T?.asString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>): String {
    try {
        if (this == null) {
            return defaultNullString
        } else if (this is Array<*>) {
             return this.contentDeepToString()
        } else if (
        // For built-in stuff, we just stick to the default toString()
            this is Number
            || this is CharSequence
            || this is Char
            || this is Date
            || this is Temporal
            || this?.let { it::class.java.packageName.startsWith("kotlin") } == true
            || this?.let { it::class.java.packageName.startsWith("java.") } == true
            || this?.let { it::class.java.packageName.startsWith("sun.") } == true
            || this?.let { it::class.java.packageName.startsWith("com.sun.") } == true
        ) {
            return this.toString()
        } else {
            this?.let {
                objectsProcessed.add(it).also { isAdded ->
                    if (!isAdded) {
                        return "recursive: ${this?.let { it::class.simplifyClassName() }}(...)"
                    }
                }
            }
            val objClass = this!!::class
            try {
                val annotationsByProperty: Map<KProperty<*>, Set<Annotation>> =
                    objClass.propertiesWithPrintModifyingAnnotations()
                        .filterNot { propertyNamesToExclude.contains(it.key.name) }
                        .filterNot { entry -> entry.value.any { annotation -> annotation is AsStringOmit } }
                val named = nameValues
                    .filterNot { it is PropertyValue<*, *> && it.printModifyingAnnotations.any { it is AsStringOmit } }
                val nameValueSeparator =
                    if (annotationsByProperty.isEmpty() || named.isEmpty()) "" else valueSeparator
                return annotationsByProperty
                    .entries.joinToString(
                        separator = valueSeparator,
                        prefix = "${objClass.simplifyClassName()}("
                    ) { entry ->
                        val prop = entry.key
                        val annotationSet = entry.value
                        "${prop.name}=${getPropValueString(prop, annotationSet)}"
                    } + named.joinToString(prefix = nameValueSeparator, separator = ", ", postfix = ")") {
                    "${it.name}=${it.valueString ?: defaultNullString}"
                }
            } catch (e: SyntheticClassException) {
                // Kotlin's reflection can't handle synthetic classes, like for a lambda, callable reference etc.
                // (more details, see KDoc of SyntheticClassException)
                // It's not the intended usage for AsString() anyway, so we just don't care. No log message.
                return this.asStringFallBack()
            } catch (e: Exception) {
                log(
                    "ERROR: Exception ${e.javaClass.simpleName} occurred when retrieving string value for object of class ${this.javaClass};$lineEnd${
                        e.asString(
                            50
                        )
                    }"
                )
                return this.asStringFallBack()
            } catch (t: Throwable) {
                log(
                    "FATAL ERROR: Throwable ${t.javaClass.simpleName} occurred when retrieving string value for object of class ${this.javaClass};$lineEnd${
                        t.asString(
                            50
                        )
                    }"
                )
                return this.asStringFallBack()
            }
        }
    } catch (e: Exception) {
        // It's probably a secondary exception somewhere. Not much more we can do here
        e.printStackTrace()
        return ""
    } finally {
        this?.let {
            objectsProcessed.remove(this)
        }
    }
}

private val emptyStringList: List<String> = listOf()

private const val valueSeparator: String = ", "

private val classPrefix = Regex("^class ")

@Suppress("UNNECESSARY_SAFE_CALL")
private fun Any?.asStringFallBack(): String =
    // mimics the Java toString() output when toString() would not be overridden
    if (this == null) defaultNullString else "${this?.let { it::class }}@${this?.hashCode()?.toByteArray()?.toHex()}"
        .replace(classPrefix, "")

private class ObjectsProcessed {
    private val objectsProcessed: MutableMap<Int, Any> = mutableMapOf()
    @Suppress("unused")
    val size: Int
        get() = objectsProcessed.size
    fun <T: Any>get(obj: T): T? = obj.let { if (it === objectsProcessed[System.identityHashCode(it)]) it else null }

    /** @return `true` if [obj] is newly added; `false` if it was present already (like [Set]`.add` behaviour */
    fun <T: Any> add(obj: T): Boolean =
        objectsProcessed.put(System.identityHashCode(obj), obj) == null

    /** @return `true` if [obj] was present and is removed; `false` if it was not present (like [Set]`.remove` behaviour) */
    fun remove(obj: Any): Boolean = (objectsProcessed.remove(System.identityHashCode(obj)) != null)
}

private val objectsProcessed = ObjectsProcessed()
