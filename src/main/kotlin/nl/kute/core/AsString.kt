@file:JvmName("AsString")
@file:Suppress("SameParameterValue", "SameParameterValue")

package nl.kute.core

import nl.kute.config.defaultNullString
import nl.kute.config.stringJoinMaxCount
import nl.kute.config.subscribeConfigChange
import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.PropertyValue
import nl.kute.core.property.getPropValueString
import nl.kute.core.property.propertiesWithAsStringAffectingAnnotations
import nl.kute.log.log
import nl.kute.reflection.error.SyntheticClassException
import nl.kute.reflection.hasImplementedToString
import nl.kute.reflection.simplifyClassName
import nl.kute.util.SetCache
import nl.kute.util.asHexString
import nl.kute.util.identityHash
import nl.kute.util.identityHashHex
import nl.kute.util.lineEnd
import nl.kute.util.throwableAsString
import kotlin.math.max
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

internal typealias AsStringHandler = (Any) -> String

/** Abstract base class for implementing classes that want to expose an [asString] method */
public abstract class AsStringProducer {

    /**
     * Facade method allowing subclasses to access the (private) [nl.kute.core.asString] method with additional parameters
     * [propertyNamesToExclude] and [nameValues]
     * @param
     */
    @Suppress("RedundantModalityModifier")
    protected final fun <T : Any?> T?.objectAsString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>): String =
        asString(propertyNamesToExclude, *nameValues)

    /** @return The resulting [String] */
    public abstract fun asString(): String

    abstract override fun toString(): String

}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[AsStringClassOption] to override the default.
 * * Want more control of what is included or not? See [AsStringBuilder]
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[AsStringOption] and other
 * (other annotations, see package `nl.kute.core.annotation.modify`)
 * @see [AsStringBuilder]
 */
public fun Any?.asString(): String = asString(emptyStringList)

/**
 * Convenient [asString] alternative, with only the provided properties.
 * * String value of individual properties is capped at 500; see @[AsStringClassOption] to override the default.
 * * Want more options? See [AsStringBuilder]
 *
 * > This method builds an [AsStringBuilder] object on each call, which is marginally less efficient
 * > than defining a reusable immutable [AsStringBuilder] as a class instance value.
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[AsStringOption] and other
 * (other annotations, see package `nl.kute.core.annotation.modify`)
 * @see [AsStringBuilder]
 */
public fun <T: Any?> T.asString(vararg props: KProperty1<T, *>): String =
    asStringBuilder().withOnlyProperties(*props).asString()

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see @[AsStringOption] to override the default
 *
 * This method allows you to exclude any properties by name, including inaccessible private ones.
 * @param propertyNamesToExclude properties that you don't want to be included in the result,
 * e.g. inaccessible (private) properties of a class being [asString]-ed
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations;
 * for these annotations, e.g. @[AsStringOption] and other (other annotations, see package `nl.kute.core.annotation.modify`)
 * @see [AsStringBuilder]
 */
private fun <T : Any?> T?.asString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>, forceAsString: Boolean = false): String {
    fun Any.asStringRecursive() = "$recursivePrefxix${this::class.simplifyClassName()}$recursivePostfix"

    try {
        val obj = this ?: return defaultNullString
        val objectCategory = AsStringObjectCategory.resolveObjectCategory(obj)
        // If no stack guarding needed, we can handle the object here already
        if (objectCategory.hasHandler() && !objectCategory.guardStack) {
            return objectCategory.handler!!(obj)
        } else {
            try {
                // Check if we were already busy processing this object
                val isRecursiveCall = !objectsStackGuard.get().addIfNotPresent(obj)
                if (isRecursiveCall) {
                    // avoid endless loop
                    return obj.asStringRecursive()
                }
                if (objectCategory.hasHandler() && objectCategory.guardStack) {
                    // stack guarding performed, handle it
                    return objectCategory.handler!!(obj)
                }
                val objClass = obj::class
                val useToString = !forceAsString
                        && !objClass.java.isSynthetic
                        && objClass.simpleName != null // null with a category of classes that kotlin's reflection does not support
                        && objClass !in forceClassAsStringCache
                        && objClass.hasImplementedToString()
                        && obj.toStringPreference() == PREFER_TOSTRING

                if (useToString) {
                    fun isToStringCallingAsString(string: String): Boolean {
                        // if we are here AND a toString() method is something like this:
                        //       `override fun toString() = asString()`
                        // we ran into recursion because toString() calls asString()
                        return string.startsWith(recursivePrefxix) && string == obj.asStringRecursive()
                    }

                    return obj.toString().let {
                        // Calling `asString()` from `toString()` is fine on itself, but not in case of PREFER_TOSTRING.
                        // That results in an remark on recursion only.
                        // We want to return something more meaningful; so call asString() instead
                        if (isToStringCallingAsString(it)) {
                            // cache it, so we won't run into useless recursion next time
                            forceClassAsStringCache.add(objClass)
                            // remove the entry in the stack, we are going to making a fresh start here
                            objectsStackGuard.get().remove(obj)
                            obj.asString(emptyStringList, forceAsString = true)
                        } else {
                            it
                        }
                    }
                }
                // no handler, so custom object, let's process it, with all params provided
                try {
                    val annotationsByProperty: Map<KProperty<*>, Set<Annotation>> =
                        objClass.propertiesWithAsStringAffectingAnnotations()
                            .filterNot { propertyNamesToExclude.contains(it.key.name) }
                            .filterNot { entry -> entry.value.any { annotation -> annotation is AsStringOmit } }
                    val named = nameValues
                        .filterNot { nameValue ->
                            nameValue is PropertyValue<*>
                                    && nameValue.asStringAffectingAnnotations.any { it is AsStringOmit }
                        }
                    val nameValueSeparator =
                        if (annotationsByProperty.isEmpty() || named.isEmpty()) "" else valueSeparator
                    return annotationsByProperty
                        .entries.joinToString(
                            separator = valueSeparator,
                            prefix = "${obj.objectIdentity()}$propertyListPrefix",
                            limit = stringJoinMaxCount
                        ) { entry ->
                            val prop = entry.key
                            val annotationSet = entry.value
                            "${prop.name}=${getPropValueString(prop, annotationSet)}"
                        } + named.joinToString(
                            prefix = nameValueSeparator,
                            separator = valueSeparator,
                            postfix = propertyListSuffix,
                            limit = stringJoinMaxCount
                        ) {
                        "${it.name}=${it.value.asString()}"
                    }
                } catch (e: SyntheticClassException) {
                    // Kotlin's reflection can't handle synthetic classes, like for lambda, callable reference etc.
                    // (more details, see KDoc of SyntheticClassException)
                    return obj.syntheticClassObjectAsString()
                } catch (e: Exception) {
                    log(
                        "ERROR: Exception ${e.javaClass.name.simplifyClassName()} occurred when retrieving string value" +
                                " for object of class ${this.javaClass};$lineEnd${e.throwableAsString(50)}"
                    )
                    return obj.asStringFallBack()
                } catch (t: Throwable) {
                    log(
                        "FATAL ERROR: Throwable ${t.javaClass.name.simplifyClassName()} occurred when retrieving string value" +
                                " for object of class ${this.javaClass};$lineEnd${t.throwableAsString(50)}"
                    )
                    return obj.asStringFallBack()
                }
            } finally {
                objectsStackGuard.get().remove(obj)
            }
        }
    } catch (e: Exception) {
        // Should not happen!
        // It's probably a secondary exception somewhere. Not much more we can do here
        e.printStackTrace()
        return ""
    }
}

private val emptyStringList: List<String> = listOf()

private const val propertyListPrefix = "("
private const val valueSeparator: String = ", "
private const val propertyListSuffix = ")"

private const val recursivePrefxix =  "recursive: "
private const val recursivePostfix = "$propertyListPrefix...$propertyListSuffix"

private val classPrefix = Regex("^class ")

/** To be used as fallback return value in case of exception within [asString] */
@JvmSynthetic // avoid access from external Java code
internal fun Any?.asStringFallBack(): String {
    // mimics the Java toString() output when toString() would not be overridden
    return if (this == null) defaultNullString else {
        "${this::class.simplifyClassName()}@${this.identityHashHex}"
            .replace(classPrefix, "")
    }
}

/**
 * Helper class to keep track of objects being processed in [asString].
 * > **NB:** Technically it is not a stack (by no means), but functionally you may think of it like a stack
 * > of objects being processed in a single call to [asString].
 *
 * > @Developer: take care when setting _breakpoints_ in class [ObjectsStackGuard]!
 *
 * > Even simply showing an object (with recursive references) in the debugger will implicitly call `toString()`,
 * > **this may already influence the content of the [objectsMap]** (so may influence result of [asString]);
 * > or may even cause stack overflow.
 */
private class ObjectsStackGuard {

    /**
     * Keeps track of the number of objects being processed in a single call to [asString].
     * @param obj The object
     * @param count The number of times this exact [obj] is currently part of processing this single call to [asString]
     */
    class ObjectCounter(@AsStringOmit val obj: Any, var count: Int) {
        fun incrementAndGet() = run { ++count; this }
        fun decrementAndGet() = run { --count; this }

        // toString and asString to avoid that showing this in the debugger will blow up the stack
        // Make sure not to include the obj itself in the toString(), that may cause stack overflow!!
        override fun toString(): String = "recursive depth: $count (identity: ${obj.identityHashHex} class: ${obj::class.simplifyClassName()})"

        @Suppress("unused")
        fun asString(): String = this.toString()
    }

    // Only to be used within ThreadLocal, so non-thread safe map is OK
    private val objectsMap = object : HashMap<Int, ObjectCounter>() {
        // toString and asString to avoid that showing this in the debugger will blow up the stack
        override fun toString(): String =
            this.entries.associate { it.key.asHexString to it.value }.asString()

        @Suppress("unused")
        fun asString(): String = this.toString()
    }

    /**
     * Counts the number of objects in the "stack".
     * * The [size] must always be `0` before and after an outbound call to [asString]
     * * Do **not** use it in production code! The [size] should be retrieved **for test purposes only**;
     *   typically to verify proper functioning of the [ObjectsStackGuard] and the [asString] code that depends on it.
     * * The performance is linear, not constant or logarithmic
     * * Outside of tests there is no functional need to call this value, apart from curiosity.
     * @see [getObjectsStackSize]
     */
    val size: Int
        get() = objectsMap.map { it.value.count }.sum()

    /**
     * Calling this method should **never** be necessary.
     * The objectsMap **must** be **empty** before **and** after any outbound call to [asString].
     */
    @Suppress("unused")
    fun clear() = objectsMap.clear()

    private fun <T : Any> get(obj: T): ObjectCounter? =
        objectsMap[obj.identityHash].let {
            if (it?.obj === obj) it else null
        }

    /** @return `true` if [obj] is newly added; `false` if it was present already (like [Set]`.add` behaviour) */
    fun <T : Any> addIfNotPresent(obj: T): Boolean =
        get(obj).let { objectCounter ->
            objectCounter?.incrementAndGet() ?: ObjectCounter(obj, 1)
                .also { objectsMap[obj.identityHash] = ObjectCounter(obj, 1) }
    }.count <= 1

    /**
     * "Removes" an object from the "stack", either by decrementing the [count], or, if the [count]
     *  would be 0 after decrementing, by removing the [objectsMap] entry.
     */
    fun <T : Any> remove(obj: T) =
        get(obj)?.let { objectCounter ->
            if (objectCounter.count == 1) {
                objectsMap.remove(obj.identityHash)
            } else {
                objectsMap[System.identityHashCode(obj)]!!.decrementAndGet()
            }
        }
}

/**
 * Technically not a stack implementation, but the term "stack" gives a good indication of the purpose.
 *
 * Keeps track of objects being processed within [asString], to avoid endless loops in case of recursive
 * (mutually referencing or self-referencing) objects.
 *
 * **The thread's [objectsStackGuard] must always empty before and after an outbound call to [asString].**
 * @see [getObjectsStackSize]
 */
private val objectsStackGuard: ThreadLocal<ObjectsStackGuard> =
    ThreadLocal.withInitial(::ObjectsStackGuard)

/**
 * To be used within tests **only**.
 *
 * Must always return 0 before and after an outbound call to [asString].
 * If non-zero, something is seriously wrong in the implementation!
 * > Non-zero return value might signal memory leaks or even chance of [OutOfMemoryError]!
 * @return The size of the object stack
 */
@JvmSynthetic // avoid access from external Java code
internal fun getObjectsStackSize() = max(objectsStackGuard.get().size, 0)

/** Cache for classes that have [asString] forced (due to recursion issues with [toString]) */
private val forceClassAsStringCache = SetCache<KClass<*>>()

@Suppress("unused")
private val configChangeCallback = { forceClassAsStringCache.reset() }
    .also { callback -> AsStringClassOption::class.subscribeConfigChange(callback) }
