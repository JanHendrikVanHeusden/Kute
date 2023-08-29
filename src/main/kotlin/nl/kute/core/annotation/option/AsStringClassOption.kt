package nl.kute.core.annotation.option

import nl.kute.config.initialAsStringClassOption
import nl.kute.config.initialIncludeIdentityHash
import nl.kute.config.initialSortNamesAlphabetic
import nl.kute.config.notifyConfigChange
import nl.kute.config.subscribeConfigChange
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.core.property.ranking.PropertyRankable
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.simplifyClassName
import nl.kute.util.MapCache
import nl.kute.util.identityHashHex
import nl.kute.util.ifNull
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

/**
 * The [AsStringClassOption] annotation can be placed on classes only.
 *
 * Besides that, the [AsStringClassOption.defaultOption] is used as a default
 * when no explicit [AsStringClassOption] annotation is applied.
 *
 * It allows specifying how property values are to be parsed in the [nl.kute.core.asString] return value.
 * @param includeIdentityHash Should the identity hash be included in output of [nl.kute.core.asString]?
 * If included, the identity hash is represented as `@` followed by the hex representation as of [System.identityHashCode]
 * (up to 8 hex characters), identical to the hex string seen in non-overridden [toString] output.
 * @param toStringPreference
 * * If [ToStringPreference.USE_ASSTRING] applies (either as default or by annotation), [nl.kute.core.asString] should
 *   dynamically resolve properties and values for custom classes, even if [toString] is implemented.
 * * If [ToStringPreference.PREFER_TOSTRING] applies (either as default or by annotation), and a [toString] method is
 *   implemented, [nl.kute.core.asString] should honour the class's [toString], rather than dynamically resolving
 *   properties and values.
 *   > If [ToStringPreference.PREFER_TOSTRING] applies and recursion is detected in the [toString] implementation,
 *   > [nl.kute.core.asString] will fall back to dynamically resolving properties and values for that class.
 * @param sortNamesAlphabetic Should output of [nl.kute.core.asString] be sorted alphabetically by property name in output
 * of [nl.kute.core.asString].?
 * > **NB:** This is a pre-sorting. If additional [propertySorters] are given, these will be applied after the alphabetic sort.
 * @param propertySorters One or more [PropertyRankable] implementing classes can be specified to have properties sorted
 * in output of [nl.kute.core.asString].
 * These will be applied in order, like SQL multi-column sorting.
 * > So if the 1st sorter yields an equal result for a pair of properties, the 2nd will be applied, and so on until a non-zero
 * > result is obtained, of until the [propertySorters] are exhausted.
 * * **NB:** Usage of [propertySorters] has a significant effect on CPU and memory footprint of [nl.kute.core.asString].
 *
 * > **NB:** This sorting is applied after alphabetic sorting is applied.
 * The sorting is stable, so if the [propertySorters] yield an equal value, the alphabetic sorting is preserved.
 *
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringClassOption(
    val includeIdentityHash: Boolean = initialIncludeIdentityHash,
    val toStringPreference: ToStringPreference = USE_ASSTRING,
    val sortNamesAlphabetic: Boolean = initialSortNamesAlphabetic,
    vararg val propertySorters: KClass<out PropertyRankable<*>> = []
) {

    /** Static holder for [defaultOption] */
    public companion object DefaultOption {
        /**
         * [AsStringOption] to be used as default if no explicit [AsStringOption] annotation is specified.
         * > On change (see [nl.kute.config.AsStringConfig]), the property cache will be reset (cleared).
         */
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
    nullableAsStringClassOption().ifNull {
        AsStringClassOption.defaultOption
            // referring to inner cache (so asStringClassOptionCache.cache)
            // because of possible race conditions when resetting the cache
            .also { asStringClassOptionCache.cache[this] = it }
    }

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.nullableAsStringClassOption(): AsStringClassOption? =
    asStringClassOptionCache.cache.let { theCache ->
        theCache[this].ifNull {
            this.annotationOfSubSuperHierarchy<AsStringClassOption>()
                // referring to inner cache (so asStringClassOptionCache.cache)
                // because of possible race conditions when resetting the cache
                ?.also { theCache[this] = it }
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
