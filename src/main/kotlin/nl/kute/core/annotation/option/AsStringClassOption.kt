package nl.kute.core.annotation.option

import nl.kute.config.initialDefaultAsStringClassOption
import nl.kute.config.initialDefaultIncludeIdentityHash
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
 * Besides that, the [AsStringClassOption.defaultAsStringClassOption] is used as a default
 * when no explicit [AsStringClassOption] annotation is applied.
 *
 * It allows specifying how property values are to be parsed in the [nl.kute.core.asString] return value.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringClassOption(val includeIdentityHash: Boolean = initialDefaultIncludeIdentityHash) {
    public companion object DefaultOption {
        /**
         * [AsStringOption] to be used if no explicit [AsStringOption] annotation is specified.
         * > When changed, the property cache will be reset (cleared).
         */
        public var defaultAsStringClassOption: AsStringClassOption = initialDefaultAsStringClassOption
            @JvmSynthetic // avoid access from external Java code
            internal set(newDefault) {
                if (newDefault != field) {
                    field = newDefault
                    // Clearing the cache is necessary because the property cache typically references the old and obsolete settings.
                    resetAsStringClassOptionCache()
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
            (this::class.annotationOfSubSuperHierarchy() ?: AsStringClassOption.defaultAsStringClassOption)
                .also { cache[kClass] = it }
        }
    }

private var asStringClassOptionCache: MutableMap<KClass<*>, AsStringClassOption> = ConcurrentHashMap()

/**
 * Resets the class level cache for [AsStringClassOption].
 * > This is typically needed when the [AsStringClassOption.defaultAsStringClassOption] is changed,
 *   to avoid inconsistent intermediate results.
 */
internal fun resetAsStringClassOptionCache() {
    // create a new map instead of clearing the old one, to avoid intermediate situations
    // while concurrently reading from / writing to the map, as these operations may not be atomic
    asStringClassOptionCache = ConcurrentHashMap()
}

// Mainly for testing purposes
internal val asStringClassOptionCacheSize
    @JvmSynthetic // avoid access from external Java code
    get() = asStringClassOptionCache.size

