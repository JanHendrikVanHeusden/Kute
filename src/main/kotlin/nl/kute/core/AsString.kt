@file:JvmName("AsString")
@file:Suppress("SameParameterValue", "SameParameterValue")

package nl.kute.core

import nl.kute.config.defaultNullString
import nl.kute.config.stringJoinMaxCount
import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.PropertyValue
import nl.kute.core.property.getPropValueString
import nl.kute.core.property.propertiesWithPrintModifyingAnnotations
import nl.kute.log.log
import nl.kute.reflection.error.SyntheticClassException
import nl.kute.reflection.simplifyClassName
import nl.kute.util.asHexString
import nl.kute.util.throwableAsString
import nl.kute.util.identityHash
import nl.kute.util.identityHashHex
import nl.kute.util.lineEnd
import kotlin.math.max
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

internal typealias AsStringHandler = (Any) -> String

//TODO: kdoc
public abstract class AsStringProducer {
    //TODO: kdoc
    @Suppress("RedundantModalityModifier")
    protected final fun <T : Any?> T?.objectAsString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>): String =
        asString(propertyNamesToExclude, *nameValues)

    //TODO: kdoc
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
private fun <T : Any?> T?.asString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>): String {
    try {
        val obj = this ?: return defaultNullString
        val objectCategory = AsStringObjectCategory.resolveObjectCategory(obj)
        // If no stack guarding needed, we can handle the object here already
        if (objectCategory.hasHandler() && !objectCategory.guardStack) {
            return objectCategory.handler!!(obj)
        } else {
            try {
                // Check if we were already busy processing this object
                objectsStackGuard.get().addIfNotPresent(obj).also { notPresent ->
                    if (!notPresent) {
                        // avoid endless loop
                        return "recursive: ${obj::class.simplifyClassName()}$propertyListPrefix...$propertyListSuffix"
                    }
                }
                if (objectCategory.hasHandler() && objectCategory.guardStack) {
                    // stack guarding performed, handle it
                    return objectCategory.handler!!(obj)
                }
                // no handler, so custom object, let's process it, with all params provided
                val objClass = obj::class
                try {
                    val annotationsByProperty: Map<KProperty<*>, Set<Annotation>> =
                        objClass.propertiesWithPrintModifyingAnnotations()
                            .filterNot { propertyNamesToExclude.contains(it.key.name) }
                            .filterNot { entry -> entry.value.any { annotation -> annotation is AsStringOmit } }
                    val named = nameValues
                        .filterNot { nameValue ->
                            nameValue is PropertyValue<*, *>
                                    && nameValue.printModifyingAnnotations.any { it is AsStringOmit }
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
                        "${it.name}=${it.valueString ?: defaultNullString}"
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
                objectsStackGuard.get().remove(this)
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

private val classPrefix = Regex("^class ")

/** To be used as fallback return value in case of exception within [asString] */
@JvmSynthetic // avoid access from external Java code
internal fun Any?.asStringFallBack(): String {
    // mimics the Java toString() output when toString() would not be overridden
    // this!!::class.java.interfaces.firstOrNull()?.toGenericString().simplifyClassName()
    return if (this == null) defaultNullString else {
        "${this::class.simplifyClassName()}@${this.identityHashHex}"
            .replace(classPrefix, "")
    }
}

/**
 * Helper class to keep track of objects being processed in [asString].
 *
 * > @Developer: take care when setting _breakpoints_ in class [ObjectsStackGuard]!
 *
 * > Even simply showing an object (with recursive references) in the debugger will implicitly call `toString()`,
 * > **this may already influence the content of the [objectsMap]** (so may influence result of [asString]);
 * > or may even cause stack overflow.
 */
private class ObjectsStackGuard {

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

    val size: Int
        get() = objectsMap.map { it.value.count }.sum()

    /**
     * Calling this method should **never** be necessary.
     * The objectsMap **must** be empty before and after any outbound call to [asString]
     */
    @Suppress("unused")
    fun clear() = objectsMap.clear()

    private fun <T : Any> get(obj: T): ObjectCounter? =
        objectsMap[obj.identityHash].let {
            if (it?.obj === obj) it else null
        }

    /** @return `true` if [obj] is newly added; `false` if it was present already (like [Set]`.add` behaviour */
    fun <T : Any> addIfNotPresent(obj: T): Boolean =
        get(obj).let { objectCounter ->
            objectCounter?.incrementAndGet() ?: ObjectCounter(obj, 1)
                .also { objectsMap[obj.identityHash] = ObjectCounter(obj, 1) }
    }.count <= 1

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
 * Technically not a stack implementation, but the term `stack` gives a good indication of the purpose.
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
 * To be used within tests.
 *
 * Must always return 0 before and after an outbound call to [asString].
 * If non-zero, something is seriously wrong in the implementation!
 * > Non-zero return value might signal memory leaks or even chance of [OutOfMemoryError]!
 * @return The size of the object stack
 */
@JvmSynthetic // avoid access from external Java code
internal fun getObjectsStackSize() = max(objectsStackGuard.get().size, 0)
