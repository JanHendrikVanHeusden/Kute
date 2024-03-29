package nl.kute.asstring.annotation.option

import nl.kute.asstring.annotation.additionalAnnotations
import nl.kute.asstring.annotation.findAnnotation
import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.config.notifyConfigChange
import nl.kute.asstring.config.subscribeConfigChange
import nl.kute.asstring.core.defaults.initialAsStringClassOption
import nl.kute.asstring.core.defaults.initialIncludeCompanion
import nl.kute.asstring.core.defaults.initialIncludeIdentityHash
import nl.kute.asstring.core.defaults.initialSortNamesAlphabetic
import nl.kute.asstring.property.ranking.PropertyRankable
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.util.simplifyClassName
import nl.kute.retain.MapCache
import nl.kute.util.identityHashHex
import nl.kute.util.ifNull
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

/**
 * * The [AsStringClassOption] annotation controls what is included, and ordering of properties
 * * The [AsStringClassOption] annotation can be placed on classes.
 *
 * @param includeIdentityHash Should the identity hash be included in output of [nl.kute.asstring.core.asString]?
 * If included, the identity hash is represented as `@` followed by the hex representation as of [System.identityHashCode]
 * (up to 8 hex characters), identical to the hex string seen in non-overridden [toString] output.
 * > Default = `false` by [initialIncludeIdentityHash]
 *
 * @param toStringPreference
 * * If [ToStringPreference.USE_ASSTRING] applies (either as default or by annotation), [nl.kute.asstring.core.asString] should
 *   dynamically resolve properties and values for custom classes, even if [toString] is implemented.
 *   [USE_ASSTRING] is the default.
 * * If [ToStringPreference.PREFER_TOSTRING] applies (either as default or by annotation), and a [toString] method is
 *   implemented, [nl.kute.asstring.core.asString] should honour the class's [toString], rather than dynamically resolving
 *   properties and values.
 *   > If [ToStringPreference.PREFER_TOSTRING] applies and recursion is detected in the [toString] implementation,
 *   > [nl.kute.asstring.core.asString] will fall back to dynamically resolving properties and values for that class.
 *
 * @param includeCompanion Should a companion object (if any) be included in the output of [nl.kute.asstring.core.asString]?
 * A companion object will be included only if all of these apply:
 *  1. The class that contains the companion object is not private
 *  2. The companion object is public
 *  3. The companion object has at least 1 property
 * > Default = `false` by [initialIncludeCompanion]
 * @param sortNamesAlphabetic Should output of [nl.kute.asstring.core.asString] be sorted alphabetically
 * by property name (case-insensitive) in output of [nl.kute.asstring.core.asString]?
 *
 * **NB:** This is a pre-sorting. If additional [propertySorters] are given, these will be applied after the alphabetic sort.
 * > Default = `false` by [initialSortNamesAlphabetic]
 * @param propertySorters One or more [PropertyRankable] implementing classes can be specified to have properties sorted
 * in output of [nl.kute.asstring.core.asString]. Default is none (no explicit sorting order).
 * These will be applied in order, like SQL multi-column sorting.
 * > So if the 1st sorter yields an equal result for a pair of properties, the 2nd will be applied, and so on until a non-zero
 * > result is obtained, of until the [propertySorters] are exhausted.
 *
 * * This sorting is applied after alphabetic sorting is applied.
 *  The sorting is stable, so if the [propertySorters] yield an equal value, the alphabetic sorting is preserved.
 * * Usage of [propertySorters] may have effect on CPU and memory footprint of [nl.kute.asstring.core.asString].
 * * Any exceptions that may occur during evaluation of a [PropertyRankable] are ignored, and:
 *   * The exception will be logged
 *   * The [PropertyRankable] will be removed from the registry, to avoid further exceptions
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringClassOption(
    val includeIdentityHash: Boolean = initialIncludeIdentityHash,
    val toStringPreference: ToStringPreference = USE_ASSTRING,
    val includeCompanion: Boolean = initialIncludeCompanion,
    val sortNamesAlphabetic: Boolean = initialSortNamesAlphabetic,
    vararg val propertySorters: KClass<out PropertyRankable<*>> = []
) {

    /** Static holder for [defaultOption] */
    public companion object DefaultOption {
        /**
         * [AsStringClassOption] to be used as default if no explicit [AsStringClassOption] annotation is specified.
         * > On change (see [nl.kute.asstring.config.AsStringConfig]), the property cache will be reset (cleared).
         */
        @Volatile
        public var defaultOption: AsStringClassOption = initialAsStringClassOption
            @JvmSynthetic // avoid access from external Java code
            internal set(newDefault) {
                if (newDefault != field) {
                    field = newDefault
                    // not using Observable delegate here, old/new values are not needed, simple notification will do
                    AsStringClassOption::class.notifyConfigChange()
                }
            }
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun Any.objectIdentity(asStringClassOption: AsStringClassOption): String {
    val className = this::class.simplifyClassName()
    return if (asStringClassOption.includeIdentityHash) {
        "$className@${this.identityHashHex}"
    } else {
        className
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.asStringClassOption(): AsStringClassOption =
    this.let { kClass ->
        // referring to inner cache (so asStringClassOptionCache.cache)
        // because of possible race conditions when resetting the cache
        asStringClassOptionCache.cache.let { theCache ->
            theCache[kClass].ifNull {
                (this.annotationOfSubSuperHierarchy() ?:
                additionalAnnotations[this]?.findAnnotation() ?:
                AsStringClassOption.defaultOption)
                    .also { theCache[kClass] = it }
            }
        }
    }

/**
 * Cache for the [AsStringClassOption] setting that applies to the given class, either by class annotation
 * or, if not annotated with [AsStringClassOption], by [AsStringClassOption.defaultOption].
 * > This cache will be reset (cleared) when [AsStringClassOption.defaultOption] is changed.
 */
@JvmSynthetic // avoid access from external Java code
internal var asStringClassOptionCache = MapCache<KClass<*>, AsStringClassOption>()
    .also {
        @Suppress("UNCHECKED_CAST")
        (AsStringClassOption::class as KClass<Annotation>).subscribeConfigChange { it.reset() }
    }
