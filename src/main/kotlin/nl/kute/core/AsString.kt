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
import kotlin.math.max
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
@Synchronized
internal fun <T : Any?> T?.asString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>): String {
    try {
        val obj = this ?: return defaultNullString
        if (!(obj is Array<*> || obj is Collection<*>) && (
                    this is Number
                            || obj is CharSequence
                            || obj is Char
                            || obj is Date
                            || obj is Temporal
                            || obj::class.java.packageName.startsWith("kotlin")
                            || obj::class.java.packageName.startsWith("java.")
                            || obj::class.java.packageName.startsWith("sun.")
                            || obj::class.java.packageName.startsWith("com.sun.")
                    )
        ) {
            // For built-in stuff except Arrays/Collections, we just stick to the default toString()
            return obj.toString()
        } else {
            objectsStack.get().alreadyPresent(obj).also { alreadyPresent ->
                if (alreadyPresent) {
                    return "recursive: ${obj::class.simplifyClassName()}(...)"
                }
            }
            // Array and Collection toString methods are *not* resilient to mutual reference,
            // where list1 is element of list2 and vice versa.
            // So we mimic the default toString behaviour, but recursion safe
            if (obj is Array<*>) {
                return obj.joinToString(prefix = "[", separator = ", ", postfix = "]") { it.asString() }
            }
            if (obj is Collection<*>) {
                return obj.joinToString(prefix = "[", separator = ", ", postfix = "]") { it.asString() }
            }
            val objClass = obj::class
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
                // Kotlin's reflection can't handle synthetic classes, like for lambda, callable reference etc.
                // (more details, see KDoc of SyntheticClassException)
                // It's not the intended usage for AsString() anyway, so we just don't care. No log message.
                return obj.asStringFallBack()
            } catch (e: Exception) {
                log(
                    "ERROR: Exception ${e.javaClass.simpleName} occurred when retrieving string value" +
                            " for object of class ${this.javaClass};$lineEnd${e.asString(50)}")
                return obj.asStringFallBack()
            } catch (t: Throwable) {
                log(
                    "FATAL ERROR: Throwable ${t.javaClass.simpleName} occurred when retrieving string value" +
                            " for object of class ${this.javaClass};$lineEnd${t.asString(50)}")
                return obj.asStringFallBack()
            } finally {
                objectsStack.get().remove(obj)
            }
        }
    } catch (e: Exception) {
        // It's probably a secondary exception somewhere. Not much more we can do here
        e.printStackTrace()
        return ""
    } finally {
        this?.let {
            objectsStack.get().remove(this)
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

/**
 * Helper class to keep track of objects being processed in [asString].
 *
 * > @Developer: it is **not advisable** to set any _breakpoints_ in this class!
 *
 * > Even simply showing an object (with recursive references) in the debugger will implicitly call `toString()`,
 * > **this may already influence the content of the [objectsMap]** (so may influence result of [asString]);
 * > it may cause stack overflow too.
 */
private class ObjectsProcessed {
    // Only used within ThreadLocal, so non-thread safe map is OK
    private val objectsMap = mutableMapOf<Int, Pair<Any, Int>>()

    @Suppress("unused")
    val size: Int
        get() = max(objectsMap.size, objectsMap.map { it.value.second }.sum())

    fun clear() = objectsMap.clear()

    private fun <T : Any> get(obj: T): Pair<T, Int>? =
        objectsMap[System.identityHashCode(obj)].let {
            @Suppress("UNCHECKED_CAST")
            if (it?.first === obj) it as Pair<T, Int> else null
        }
    // Uncomment statement below to print debug messages. Debugging is troublesome with recursive stuff
    // Even simply showing an object in the debugger may already influence content of the `objectsMap` and / or cause stack overflow
    // Do not remove!
    // .also { println("Got obj: ${it?.let { obj::class.simplifyClassName() + " (" + it.second + ")"} } - size = $size") }

    /** @return `true` if [obj] is newly added; `false` if it was present already (like [Set]`.add` behaviour */
    fun <T : Any> alreadyPresent(obj: T): Boolean =
        get(obj).let { if (it == null) obj to 1 else obj to it.second + 1 }
            .also { objectsMap[System.identityHashCode(obj)] = it }
            // Uncomment statement below to print debug messages. Debugging is troublesome with recursive stuff
            // Even simply showing an object in the debugger may already influence content of the `objectsMap` and / or cause stack overflow
            // Do not remove!
            // .also { println("Added obj: ${obj::class.simplifyClassName() + " (" + it.second + ")"} - size = $size") }
            .second > 1

    /** @return `true` if [obj] was present and is removed; `false` if it was not present (like [Set]`.remove` behaviour) */
    fun <T : Any> remove(obj: T): Boolean =
        get(obj)?.let {
            if (it.second == 1) {
                // Uncomment statement below to print debug messages. Debugging is troublesome with recursive stuff
                // Even simply showing an object in the debugger may already influence content of the `objectsMap` and / or cause stack overflow
                // Do not remove!
                // println("Removing: obj: ${obj::class.simplifyClassName() + " (last)"} - size = $size")
                objectsMap.remove(System.identityHashCode(it.first))
            } else {
                // Uncomment statement below to print debug messages. Debugging is troublesome with recursive stuff
                // Even simply showing an object in the debugger may already influence content of the `objectsMap` and / or cause stack overflow
                // Do not remove!
                // println("Removing: obj: ${obj::class.simplifyClassName() + " (" + it.second +")"} - size = $size")
                objectsMap[System.identityHashCode(it.first)] = obj to it.second - 1
            }
        } != null
}

/** Technically not a stack implementation, but the term `stack` gives a good indication of the purpose */
private val objectsStack: ThreadLocal<ObjectsProcessed> =
    ThreadLocal.withInitial(::ObjectsProcessed)

internal fun getObjectsStackSize() = max(objectsStack.get().size, 0)

internal fun clearObjectsStack() = objectsStack.get().clear()