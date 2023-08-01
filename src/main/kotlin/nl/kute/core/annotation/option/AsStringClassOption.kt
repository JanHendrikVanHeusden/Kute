package nl.kute.core.annotation.option

import nl.kute.config.initialAsStringClassOption
import nl.kute.config.initialIncludeIdentityHash
import nl.kute.config.notifyConfigChange
import nl.kute.config.subscribeConfigChange
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
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
 * @param includeIdentityHash Should the identity hash be included?
 * If included, the identity hash is `@` followed by the hex representation as of [System.identityHashCode]
 * (up to 8 hex characters), identical to the hex string seen in non-overridden [toString] output.
 * @param toStringPreference
 * * If [ToStringPreference.USE_ASSTRING] applies (either as default or by annotation), [nl.kute.core.asString] should
 *   dynamically resolve properties and values for custom classes, even if [toString] is implemented.
 * * If [ToStringPreference.PREFER_TOSTRING] applies (either as default or by annotation), and a [toString] method is
 *   implemented, [nl.kute.core.asString] should honour the class's [toString], rather than dynamically resolving
 *   properties and values.
 *   > If [ToStringPreference.PREFER_TOSTRING] applies and recursion is detected in the [toString] implementation,
 *   > [nl.kute.core.asString] will fall back to dynamically resolving properties and values for that class.
 */
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringClassOption(
    val includeIdentityHash: Boolean = initialIncludeIdentityHash,
    val toStringPreference: ToStringPreference = USE_ASSTRING
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
internal fun KClass<*>.getAsStringClassOption(): AsStringClassOption =
    this.let { kClass ->
        val cache = asStringClassOptionCache
        cache[kClass].ifNull {
            (this.annotationOfSubSuperHierarchy() ?: AsStringClassOption.defaultOption)
                .also { cache[kClass] = it }
        }
    }

@JvmSynthetic // avoid access from external Java code
internal var asStringClassOptionCache = MapCache<KClass<*>, AsStringClassOption>()

@Suppress("unused")
private val configChangeCallback = { asStringClassOptionCache.reset() }
    .also { callback -> AsStringClassOption::class.subscribeConfigChange(callback) }
