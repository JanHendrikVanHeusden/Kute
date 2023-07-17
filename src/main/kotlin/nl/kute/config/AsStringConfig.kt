package nl.kute.config

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

private typealias ConfigChangeCallback = () -> Unit

/**
 * Builder-like class, to prepare and apply newly set values as defaults
 * for [AsStringOption] / [AsStringClassOption]
 * @see [AsStringConfig.applyAsDefault]
 */
public class AsStringConfig {

    private var newAsStringOption: AsStringOption = AsStringOption.defaultOption
    private var newDefaultClassOption: AsStringClassOption = AsStringClassOption.defaultOption

    private fun setNewDefaultOption(newAsStringOption: AsStringOption) {
        this.newAsStringOption = newAsStringOption
    }

    private fun setNewDefaultOption(
        showNullAs: String = AsStringOption.defaultOption.showNullAs,
        propMaxLength: Int = AsStringOption.defaultOption.propMaxStringValueLength) {

        setNewDefaultOption(AsStringOption(showNullAs, propMaxLength))
    }

    /**
     * Sets the new default value for [AsStringOption.showNullAs].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringOption] annotation is present.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     */
    public fun withShowNullAs(showNullAs: String): AsStringConfig {
        setNewDefaultOption(showNullAs = showNullAs)
        return this
    }

    /**
     * Sets the new default value for [AsStringOption.propMaxStringValueLength].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringOption] annotation is present.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     */
    public fun withMaxPropertyStringLength(propMaxLength: Int): AsStringConfig {
        setNewDefaultOption(propMaxLength = propMaxLength)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.includeIdentityHash].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringClassOption] annotation is present.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     */
    public fun withIncludeIdentityHash(includeHash: Boolean): AsStringConfig {
        newDefaultClassOption = AsStringClassOption(includeHash)
        return this
    }

    /**
     * Assigns the [AsStringOption] that is built to [AsStringOption.defaultOption],
     * as the new application default.
     *  > This operation will reset (clear) the property cache, if necessary
     * @return the newly applied default [AsStringOption]
     */
    public fun applyAsDefault() {
        AsStringOption.defaultOption = newAsStringOption
        AsStringClassOption.defaultOption = newDefaultClassOption
    }

}

/** Initial default value for how to represent `null` in the [nl.kute.core.asString] */
public const val initialNullString: String = "null"

/** Initial default value for the maximum length **per property** in the [nl.kute.core.asString] output */
public const val initialMaxStringValueLength: Int = 500

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.core.asString] output */
public const val initialIncludeIdentityHash: Boolean = false

/** Initial default options for the output of [nl.kute.core.asString] */
@JvmSynthetic
internal val initialAsStringOption: AsStringOption =
    AsStringOption(initialNullString, initialMaxStringValueLength)

/** Initial default options for the output of [nl.kute.core.asString] */
@JvmSynthetic
internal val initialAsStringClassOption: AsStringClassOption =
    AsStringClassOption(initialIncludeIdentityHash)

/** Convenience method to retrieve [AsStringOption.defaultOption]'s [AsStringOption.showNullAs] */
internal val defaultNullString: String
    @JvmSynthetic // avoid access from external Java code
    get() = AsStringOption.defaultOption.showNullAs

/** Convenience method to retrieve [AsStringOption.defaultOption]'s [AsStringOption.propMaxStringValueLength] */
internal val defaultMaxStringValueLength: Int
    @JvmSynthetic // avoid access from external Java code
    get() = AsStringOption.defaultOption.propMaxStringValueLength

/**
 * Reset config options: resets [AsStringOption.defaultOption] to [initialAsStringOption].
 * Mainly for testing purposes.
 *  > This operation will also reset (clear) the property cache, if necessary
 */
@JvmSynthetic // avoid access from external Java code
internal fun restoreInitialAsStringOption(): AsStringOption =
    initialAsStringOption.also { AsStringOption.defaultOption = it }

/**
 * Reset config options: resets [AsStringOption.defaultOption] to [initialAsStringOption].
 * Mainly for testing purposes.
 *  > This operation will also reset (clear) the cache for AsStringClasOption cache, if necessary.
 */
@JvmSynthetic // avoid access from external Java code
internal fun restoreInitialAsStringClassOption(): AsStringClassOption =
    initialAsStringClassOption.also { AsStringClassOption.defaultOption = it }

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.notifyConfigChange() {
    configChangeSubscriptions[this]?.forEach { callback -> callback.invoke() }
}

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.subscribeConfigChange(callback: ConfigChangeCallback) {
    configChangeSubscriptions[this].ifNull {
        mutableListOf<ConfigChangeCallback>().also { configChangeSubscriptions[this] = it }
    }.add(callback)
}

private val configChangeSubscriptions:
        MutableMap<KClass<*>, MutableList<ConfigChangeCallback>> = ConcurrentHashMap()
