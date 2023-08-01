package nl.kute.config

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Builder-like class, to prepare and apply newly set values as defaults
 * for [AsStringOption] / [AsStringClassOption]
 * @see [AsStringConfig.applyAsDefault]
 */
public class AsStringConfig {

    private var newDefaultAsStringOption: AsStringOption = AsStringOption.defaultOption
    private var newDefaultAsStringClassOption: AsStringClassOption = AsStringClassOption.defaultOption

    private fun setNewDefaultAsStringOption(newAsStringOption: AsStringOption) {
        this.newDefaultAsStringOption = newAsStringOption
    }

    private fun setNewDefaultAsStringOption(
        showNullAs: String = newDefaultAsStringOption.showNullAs,
        propMaxLength: Int = newDefaultAsStringOption.propMaxStringValueLength) {

        setNewDefaultAsStringOption(AsStringOption(showNullAs, propMaxLength))
    }

    private fun setNewDefaultAsStringClassOption(newAsStringClassOption: AsStringClassOption) {
        this.newDefaultAsStringClassOption = newAsStringClassOption
    }

    private fun setNewDefaultAsStringClassOption(
        includeIdentityHash: Boolean = newDefaultAsStringClassOption.includeIdentityHash,
        toStringPreference: ToStringPreference = newDefaultAsStringClassOption.toStringPreference
        ) {
        setNewDefaultAsStringClassOption(AsStringClassOption(includeIdentityHash, toStringPreference))
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
        setNewDefaultAsStringOption(showNullAs = showNullAs)
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
        setNewDefaultAsStringOption(propMaxLength = propMaxLength)
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
        setNewDefaultAsStringClassOption(includeIdentityHash = includeHash)
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
    public fun withToStringPreference(toStringPreference: ToStringPreference): AsStringConfig {
        setNewDefaultAsStringClassOption(toStringPreference = toStringPreference)
        return this
    }

    /**
     * Assigns the [AsStringOption] that is built to [AsStringOption.defaultOption],
     * as the new application default.
     *  > This operation will reset (clear) the property cache, if necessary
     * @return the newly applied default [AsStringOption]
     */
    public fun applyAsDefault() {
        AsStringOption.defaultOption = newDefaultAsStringOption
        AsStringClassOption.defaultOption = newDefaultAsStringClassOption
    }

}

/** Limit to the number of elements to be joined together */
@JvmSynthetic // avoid access from external Java code
internal const val stringJoinMaxCount: Int = 200

/** Initial default value for how to represent `null` in the [nl.kute.core.asString] */
public const val initialNullString: String = "null"

/** Initial default value for the maximum length **per property** in the [nl.kute.core.asString] output */
public const val initialMaxStringValueLength: Int = 500

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.core.asString] output */
public const val initialIncludeIdentityHash: Boolean = false

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.core.asString] output */
public val initialToStringPreference: ToStringPreference = USE_ASSTRING

/** Initial default options for the output of [nl.kute.core.asString] */
@JvmSynthetic // avoid access from external Java code
internal val initialAsStringOption: AsStringOption =
    AsStringOption(initialNullString, initialMaxStringValueLength)

/** Initial default options for the output of [nl.kute.core.asString] */
@JvmSynthetic // avoid access from external Java code
internal val initialAsStringClassOption: AsStringClassOption =
    AsStringClassOption(initialIncludeIdentityHash, initialToStringPreference)

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
internal fun KClass<*>.subscribeConfigChange(callback: () -> Unit) {
    configChangeSubscriptions[this].ifNull {
        mutableListOf<() -> Unit>().also { configChangeSubscriptions[this] = it }
    }.add(callback)
}

private val configChangeSubscriptions:
        MutableMap<KClass<*>, MutableList<() -> Unit>> = ConcurrentHashMap()
