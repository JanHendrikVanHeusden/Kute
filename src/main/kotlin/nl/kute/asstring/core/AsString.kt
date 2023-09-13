@file:JvmName("AsString")
@file:Suppress("SameParameterValue")

package nl.kute.asstring.core

import nl.kute.asstring.annotation.additionalAnnotations
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.annotation.option.asStringClassOption
import nl.kute.asstring.config.subscribeConfigChange
import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.core.defaults.defaultNullString
import nl.kute.asstring.namedvalues.NameValue
import nl.kute.asstring.namedvalues.PropertyValue
import nl.kute.asstring.property.filter.propertyOmitFilter
import nl.kute.asstring.property.getPropValueString
import nl.kute.asstring.property.meta.PropertyMetaData
import nl.kute.asstring.property.propertiesWithAsStringAffectingAnnotations
import nl.kute.asstring.property.ranking.NoOpPropertyRanking
import nl.kute.asstring.property.ranking.PropertyRankable
import nl.kute.asstring.property.ranking.PropertyValueMetaComparator
import nl.kute.asstring.property.ranking.getPropertyRankableInstance
import nl.kute.log.log
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.error.SyntheticClassException
import nl.kute.reflection.property.propertyReturnTypesToOmit
import nl.kute.reflection.util.hasImplementedToString
import nl.kute.reflection.util.retrieveCompanionObjectInstance
import nl.kute.reflection.util.simplifyClassName
import nl.kute.util.MapCache
import nl.kute.util.asHexString
import nl.kute.util.identityHash
import nl.kute.util.identityHashHex
import nl.kute.util.joinIfNotEmpty
import nl.kute.util.lineEnd
import nl.kute.util.throwableAsString
import kotlin.math.max
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

// region ~ AsStringProducer

/**
 * Abstract base class for implementing classes that want to expose an [asString] method
 * * Properties of this class's subclasses are excluded from rendering by [asString]
 */
public abstract class AsStringProducer {
// Must be in same file as private fun `objectAsString()` (to call the private method)

    /**
     * Facade method allowing subclasses to access the (private) [nl.kute.asstring.core.asString] method with additional parameters
     * [propertyNamesToExclude] and [nameValues]
     * @param
     */
    @Suppress("RedundantModalityModifier")
    protected final fun <T : Any?> T?.objectAsString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>): String =
        asString(propertyNamesToExclude, *nameValues)

    /** @return The resulting [String] */
    public abstract fun asString(): String

    abstract override fun toString(): String

    private companion object {
        init {
            // Properties of this type should not be rendered by asString()
            propertyReturnTypesToOmit.add(AsStringProducer::class)
        }
    }
}

// endregion

// region ~ asString

/**
 * Mimics the format of Kotlin data class's [toString] method (insofar not modified by annotations).
 * * Super-class properties are included
 * * Private properties are included (but not in subclasses)
 * * String value of individual properties is capped at 500; see [AsStringClassOption] to override the default.
 * * Want more control of what is included or not? See [AsStringBuilder]
 *
 * The output of [asString] can be controlled / modified by using these annotations:
 * * `@`[AsStringClassOption] to control what is included, and ordering of properties (at class level)
 * * `@`[AsStringOption] to control how properties are rendered (e.g. delimiters, max. lengths, how to represent `null`s, etc.)
 * * `@`[nl.kute.asstring.annotation.modify.AsStringOmit] to exclude individual properties from [asString] output
 * * `@`[nl.kute.asstring.annotation.modify.AsStringMask] to mask properties, typically to keep private or sensitive data out
 *   of the [asString] output
 * * `@`[nl.kute.asstring.annotation.modify.AsStringReplace]
 * * `@`[nl.kute.asstring.annotation.modify.AsStringHash]
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations
 * @see [AsStringBuilder]
 */
public fun Any?.asString(): String = asString(emptyStringList)

@JvmSynthetic // avoid access from external Java code
// Specifically to be used for collections, arrays etc.
internal fun Any?.asString(elementsLimit: Int? = null): String = asString(emptyStringList, elementsLimit = elementsLimit)

/**
 * Convenient [asString] overload, with only the provided properties.
 * * See also the KDoc of [asString] for comprehensive explanation, e.g. on usage of annotations.
 * * Want more control of what is included or not? See [AsStringBuilder]
 *
 * > This method builds an [AsStringBuilder] object on each call, which is marginally less efficient
 * > than defining a reusable immutable [AsStringBuilder] as a class instance value.
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations
 * @see [asString]
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
private fun <T : Any> T?.asString(propertyNamesToExclude: Collection<String>, vararg nameValues: NameValue<*>, elementsLimit: Int? = null): String {
    val obj = this ?: return defaultNullString
    try {
        try {
            val objectCategory = AsStringObjectCategory.resolveObjectCategory(obj)
            // If no stack guarding needed, we can handle the object here already
            if (!objectCategory.guardStack && objectCategory.hasAnyHandler()) {
                return handleObject(obj, objectCategory, elementsLimit)
            }
            if (objectCategory.hasHandler() && !objectCategory.guardStack) {
                return objectCategory.handler!!(obj)
            } else {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val objClass: KClass<T> = obj::class as KClass<T>
                    // Check recursion: are we were already busy processing this object?
                    val isRecursiveCall = !objectsStackGuard.get().addIfNotPresent(obj)
                    if (isRecursiveCall) {
                        // if recursive (and maybe PREFER_TOSTRING), reset it to USE_ASSTRING
                        useToStringByClass[objClass] = USE_ASSTRING
                        // avoid endless loop
                        return "$recursivePrefix${objClass.simplifyClassName()}$recursivePostfix"
                    }
                    if (objectCategory.hasAnyHandler()) {
                        return handleObject(obj, objectCategory, elementsLimit)
                    }
                    val hasAdditionalParameters = propertyNamesToExclude.isNotEmpty() || nameValues.isNotEmpty()
                    if (!hasAdditionalParameters) {
                        val (toStringPref, firstTime) = objClass.toStringHandling()
                        if (toStringPref == PREFER_TOSTRING) {
                            obj.tryToString(firstTime)?.let { return it }
                        }
                    }

                    // not preferring toString() & no handler, so custom object to dynamically resolve
                    // let's process it, with all params provided
                    try {
                        var annotationsByProperty: Map<KProperty<*>, Set<Annotation>> =
                            objClass.propertiesWithAsStringAffectingAnnotations()
                                .filterNot { propertyNamesToExclude.contains(it.key.name) }
                                .filterNot { entry -> entry.value.any { annotation -> annotation is AsStringOmit } }

                        if (propertyOmitFilter.hasFilter()) {
                            // todo: cache propertyMetaData by property
                            annotationsByProperty = annotationsByProperty.filterKeys { prop ->
                                propertyOmitFilter.getFilters()
                                    .all { filter -> !filter(PropertyMetaData(prop, objClass)) }
                            }
                        }
                        val named: List<NameValue<*>> = nameValues
                            .filterNot { nameValue ->
                                nameValue is PropertyValue<*>
                                        && nameValue.asStringAffectingAnnotations.any { it is AsStringOmit }
                            }
                        val asStringClassOption = objClass.asStringClassOption()
                        val rankProviders = asStringClassOption.propertySorters
                            .mapNotNull { it.getPropertyRankableInstance() }
                            .toTypedArray()
                        return "${obj.objectIdentity()}$propertyListPrefix" +
                        joinIfNotEmpty(
                            separator = valueSeparator,
                            annotationsByProperty.joinToStringWithOrderRank(
                                obj = obj,
                                rankProviders = rankProviders,
                                separator = valueSeparator,
                                limit = stringJoinMaxCount
                            ),
                            objClass.companionAsString(),
                            named.joinToString(
                                separator = valueSeparator,
                                limit = stringJoinMaxCount
                            ) {
                                "${it.name}=${it.value.asString()}"
                            }
                        ) + propertyListSuffix
                    } catch (e: SyntheticClassException) {
                        // Kotlin's reflection can't handle synthetic classes, like for lambda, callable reference etc.
                        // (more details, see KDoc of SyntheticClassException)
                        return obj.syntheticClassObjectAsString()
                    }
                } finally {
                    objectsStackGuard.get().remove(obj)
                }
            }
        } catch (e: ConcurrentModificationException) {
            // Seems to be an unsafe Collection, Map, Array, etc.
            // Try to create a defensive copy, and return asString() of that.
            // This is tried once only; if unsuccessful, return fallback string
            log("Warning: Non-thread safe collection/map was modified concurrently!\n ${e.javaClass.name.simplifyClassName()} occurred when retrieving string value" +
                    " for object of class ${this.javaClass};$lineEnd${e.throwableAsString(50)}")
            return obj.cloneCollectionLikeStuff()?.asString(propertyNamesToExclude, *nameValues, elementsLimit = elementsLimit)
                ?: obj.asStringFallBack()
        } catch (e: InterruptedException) {
            throw e
        } catch (e: Exception) {
            log("ERROR: Exception ${e.javaClass.name.simplifyClassName()} occurred when retrieving string value" +
                    " for object of class ${this.javaClass};$lineEnd${e.throwableAsString(50)}")
            return obj.asStringFallBack()
        }
    } catch (e: InterruptedException) {
        throw e
    } catch (e: Exception) {
        // Should not happen!
        // It's probably a secondary exception somewhere. Not much more we can do here
        e.printStackTrace()
        return ""
    }
}

private fun Map<KProperty<*>, Set<Annotation>>.joinToStringWithOrderRank(
    obj: Any,
    vararg rankProviders: PropertyRankable<*> = emptyArray(),
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    limit: Int
): String {
    val sortNamesAlphabetic = this.size > 1 && obj::class.asStringClassOption().sortNamesAlphabetic
    val props: Map<KProperty<*>, Set<Annotation>> =
        if (sortNamesAlphabetic) this.entries.sortedBy { it.key.name.lowercase() }.associate { it.key to it.value }
        else this
    return if (this.size <= 1 || !rankProviders.hasEffectiveRankProvider()) {
        // no ranking/ordering of properties required, just process the values
        props.entries.joinToString(
            separator = separator,
            prefix = prefix,
            limit = limit
        ) { entry ->
            val prop = entry.key
            val annotationSet = entry.value
            "${prop.name}=${obj.getPropValueString(prop, annotationSet).second}"
        }
    } else {
        // ranking/ordering required
        // retrieve the String values and map them against the property metadata
        val metaDataStringMap = props.entries
            .map { entry -> obj.getPropValueString(entry.key, entry.value) }
            .associate { it.first!! to it.second }
        // We cannot use toSortedMap() here: toSortedMap() only keeps the last entry when equal outcome
        // of the comparator, so toSortedMap() may drop entries when sorting :-(
        //
        // So instead, we're going to sort keys (property metadata), then re-associate them with the value Strings.
        val sortedKeys = metaDataStringMap.keys.sortedWith(PropertyValueMetaComparator(*rankProviders))
        sortedKeys.associateWith { metaDataStringMap[it] }
            .map { "${it.key.propertyName}=${it.value}" }
            .joinToString(
                separator = separator,
                prefix = prefix,
                limit = limit
            )
    }
}

internal fun Array<out PropertyRankable<*>>.hasEffectiveRankProvider() =
    this.isNotEmpty() && !this.all { it == NoOpPropertyRanking.instance }

internal fun (Array<out KClass<out PropertyRankable<*>>>)?.hasEffectiveRankProvider(): Boolean =
    !this.isNullOrEmpty() && !this.all { it::class == NoOpPropertyRanking::class }

// endregion

// region ~ toString vs AsString preference

/** @return Is this class feasible for reflection, and if so, does it have [toString] implemented? */
private fun KClass<*>.feasibleForToString() =
    // simpleName is null for a bunch of classes not supported by kotlin's reflection
    // (many of these do not override toString anyway)
    !this.java.isSynthetic && this.simpleName != null && this.hasImplementedToString()

/**
 * Is this class feasible for using [toString] from [asString], and if so, does it prefer [toString]?
 * @return
 *  * If feasible for [toString], the class's [ToStringPreference]
 *  * Or [USE_ASSTRING] if not feasible for [toString]
 */
private fun KClass<*>.toStringFeasibility() =
    if (!this.feasibleForToString()) USE_ASSTRING else this.toStringPreference()

/**
 * Determines the [ToStringPreference] and whether it is the first time we handle this class.
 * > Also adds the found [ToStringPreference] to the cache, if absent
 */
private fun KClass<*>.toStringHandling(): Pair<ToStringPreference, Boolean> {
    // referring to inner cache (so useToStringByClass.cache)
    // because of possible race conditions when resetting the cache
    val theCache = useToStringByClass.cache
    theCache[this].let { cachedPref ->
        val firstTime = cachedPref == null
        return if (firstTime) Pair(toStringFeasibility(), firstTime).also { theCache[this] = it.first }
        else Pair(cachedPref!!, firstTime)
    }
}

/**
 * @return
 * * If not [firstTime], simply returns [toString] of the receiver
 * * If [firstTime]:
 */
private fun <T : Any> T.tryToString(firstTime: Boolean): String? {
    // Did we see this class before and did toString() complete without recursion? If so, go ahead
    if (!firstTime) return this.toString()

    // Calling `asString()` from `toString()` is fine on itself, but not in case of PREFER_TOSTRING:
    // that would only result in a remark on recursion.
    // We want to return something more meaningful; so call asString() instead in case of recursion.
    this.toString().let { toStringResult ->
        // If we are here, value from `useToStringByClass` should normally be true.
        // It may have been set to USE_ASSTRING though, due to recursion. So check again, to avoid endless loops
        val useToString = useToStringByClass[this::class]!!
        return if (useToString == PREFER_TOSTRING) {
            toStringResult
        } else {
            // Probably recursion detected - this will not end successfully when happening inside toString()
            // Remove the entry from the stack, we are going to making a fresh start here, now with asString()
            objectsStackGuard.get().remove(this)
            return null
        }
    }
}

/**
 * Registry for preference whether [asString] should call [toString] for the given class
 * * If `true`, the class may be processed with [toString]
 * * If `false`, the class should be processed by dynamically resolving properties and values
 * > This registry will be reset (cleared) when [AsStringClassOption.defaultOption] is changed.
 */
@JvmSynthetic // avoid access from external Java code
internal val useToStringByClass = MapCache<KClass<*>, ToStringPreference>()
    .also {
        @Suppress("UNCHECKED_CAST")
        (AsStringClassOption::class as KClass<Annotation>).subscribeConfigChange { it.reset() }
    }

// endregion

// region ~ Constants, helper methods etc.

/** Limits the overall number of properties values or [nl.kute.asstring.namedvalues.NamedValue]s to be joined together */
public const val stringJoinMaxCount: Int = 1000

private val emptyStringList: List<String> = listOf()

private const val propertyListPrefix = "("
private const val valueSeparator: String = ", "
private const val propertyListSuffix = ")"

private const val recursivePrefix =  "recursive: "
private const val recursivePostfix = "$propertyListPrefix...$propertyListSuffix"

private val classPrefix = Regex("^class ")

private fun handleObject(obj: Any, category: AsStringObjectCategory, limit: Int?): String {
    return if (category.hasHandlerWithSize()) category.handlerWithSize!!(obj, limit)
    else category.handler!!(obj)
}

/** To be used as fallback return value in case of exception within [asString] */
@JvmSynthetic // avoid access from external Java code
internal fun Any?.asStringFallBack(): String {
    // mimics the Java toString() output when toString() would not be overridden
    return if (this == null) defaultNullString else {
        "${this::class.simplifyClassName()}@${this.identityHashHex}"
            .replace(classPrefix, "")
    }
}

/** Tries to clone a [Collection], [Array], [Map], etc., typically as a defensive copy */
private fun Any.cloneCollectionLikeStuff(): Any? =
    try {
        when (this) {
            is List<*> -> {
                java.util.List.copyOf(this)
            }
            is Collection<*> -> {
                this.toList()
            }
            is Map<*, *> -> {
                this.toMap()
            }
            else -> {
                null
            }
        }
    } catch (e: InterruptedException) {
        throw e
    } catch (e: Exception) {
        // ignore
        null
}

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.companionAsString(): String {
    if (!this.asStringClassOption().includeCompanion) return ""
    try {
        val companionObjectInstance = this.retrieveCompanionObjectInstance()
        companionObjectInstance?.let { companionInstance ->
            // Do not include the companion object if it doesn't have any properties
            if (this.companionObject?.memberProperties?.isEmpty() == true) {
                return ""
            }
            // When rendering a companion object, we should also take into account the class level
            // annotations of the enclosing class.
            // But we can't retrieve the enclosing class from the companion.
            // So we pre-register the enclosing class's annotations with the companion class, to be retrieved further downstream
            if (!additionalAnnotations.contains(companionInstance)) {
                registerHolderClassAnnotations(companionInstance)
            }
            return "companion: " + this.companionObjectInstance.asString()
        }
    } catch (e: InterruptedException) {
        throw e
    } catch (e: Exception) {
        log("ERROR: Exception ${e.javaClass.name.simplifyClassName()} occurred when retrieving string value" +
                " for companion object of class ${this};$lineEnd${e.throwableAsString(50)}")
    }
    return ""
}

private fun KClass<*>.registerHolderClassAnnotations(companionInstance: Any) {
    val annotationSet = mutableSetOf<Annotation>()
    this.annotationOfSubSuperHierarchy<AsStringOption>()?.let { annotationSet.add(it) }
    this.annotationOfSubSuperHierarchy<AsStringClassOption>()?.let { annotationSet.add(it) }
    if (annotationSet.isNotEmpty()) {
        additionalAnnotations[companionInstance::class] = annotationSet
    }
}

// endregion

// region ~ Objects stack guard

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
// Must be in same file as fun asString(), we really don't want to expose ObjectsStackGuard

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

// endregion
