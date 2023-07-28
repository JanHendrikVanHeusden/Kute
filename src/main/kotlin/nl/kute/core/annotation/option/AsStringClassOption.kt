package nl.kute.core.annotation.option

import nl.kute.config.initialAsStringClassOption
import nl.kute.config.initialIncludeIdentityHash
import nl.kute.config.notifyConfigChange
import nl.kute.config.subscribeConfigChange
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.simplifyClassName
import nl.kute.util.identityHashHex
import nl.kute.util.ifNull
import java.lang.annotation.Inherited
import java.util.concurrent.ConcurrentHashMap
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

/**
 * The [AsStringClassOption] annotation can be placed on classes only.
 *
 * Besides that, the [AsStringClassOption.defaultOption] is used as a default
 * when no explicit [AsStringClassOption] annotation is applied.
 *
 * It allows specifying how property values are to be parsed in the [nl.kute.core.asString] return value.
 * @param includeIdentityHash Should the identity hash be included?
 * If included, the identity hash is `@` followed by the hex representation as of [System.identityHashCode]
 * (up to 8 hex characters), identical to the hex string seen in non-overridden [toString] output.
 * @param preferToString If a [toString] method is implemented
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringClassOption(
    val includeIdentityHash: Boolean = initialIncludeIdentityHash,
    // TODO: tests
    val preferToString: ToStringPreference = USE_ASSTRING
) {

    /** Static holder for [defaultOption] */
    public companion object DefaultOption {
        /**
         * [AsStringOption] to be used if no explicit [AsStringOption] annotation is specified.
         * > When changed, the property cache will be reset (cleared).
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
internal fun Any.getAsStringClassOption(): AsStringClassOption =
    this::class.let { kClass ->
        val cache = asStringClassOptionCache
        cache[kClass].ifNull {
            (this::class.annotationOfSubSuperHierarchy() ?: AsStringClassOption.defaultOption)
                .also { cache[kClass] = it }
        }
    }

private var asStringClassOptionCache: MutableMap<KClass<*>, AsStringClassOption> = ConcurrentHashMap()

/**
 * Resets the class level cache for [AsStringClassOption].
 * > This is typically needed when the [AsStringClassOption.defaultOption] is changed,
 *   to avoid inconsistent intermediate results.
 */
internal fun resetAsStringClassOptionCache() {
    // create a new map instead of clearing the old one, to avoid intermediate situations
    // while concurrently reading from / writing to the map, as these operations are not atomic typically
    asStringClassOptionCache = ConcurrentHashMap()
}

@Suppress("unused")
private val configChangeCallback = { resetAsStringClassOptionCache() }
    .also { callback -> AsStringClassOption::class.subscribeConfigChange(callback) }

// Mainly for testing purposes
internal val asStringClassOptionCacheSize
    @JvmSynthetic // avoid access from external Java code
    get() = asStringClassOptionCache.size

