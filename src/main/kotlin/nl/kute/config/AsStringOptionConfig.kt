package nl.kute.config

import nl.kute.core.annotation.option.AsStringOption

/** Initial default value for how to represent `null` in the [nl.kute.core.asString] */
public const val initialDefaultNullString: String = "null"

/** Initial default value for the maximum length **per property** in the [nl.kute.core.asString] output */
public const val initialDefaultMaxStringValueLength: Int = 500

/** Initial default options for the output of [nl.kute.core.asString] */
public val initialDefaultAsStringOption: AsStringOption =
    AsStringOption(initialDefaultNullString, initialDefaultMaxStringValueLength)

/**
 * Builder class, intended to apply the [AsStringOption] as a default (see [AsStringOptionBuilder.applyAsDefault]).
 */
public class AsStringOptionBuilder {

    private val currentDefaultOption = AsStringOption.defaultAsStringOption
    private val currentDefaultNullStr = currentDefaultOption.showNullAs
    private val currentDefaultPropMaxLength = currentDefaultOption.propMaxStringValueLength

    private var newAsStringOption: AsStringOption = currentDefaultOption

    /**
     * Assigns the [AsStringOption] that is built to [AsStringOption.defaultAsStringOption], as the new application default
     *  > This operation will reset (clear) the property cache, if necessary
     * @return the newly applied default [AsStringOption]
     */
    public fun applyAsDefault(): AsStringOption {
        AsStringOption.defaultAsStringOption = newAsStringOption
        return newAsStringOption
    }

    /**
     * Builds and returns the [AsStringOption] that is built, but does not apply it.
     * @return the newly built [AsStringOption]
     */
    public fun build(): AsStringOption {
        return newAsStringOption
    }

    private fun setNewDefaultOption(newAsStringOption: AsStringOption) {
        this.newAsStringOption = newAsStringOption
    }

    private fun setNewDefaultOption(showNullAs: String = currentDefaultNullStr, propMaxLength: Int = currentDefaultPropMaxLength) {
        setNewDefaultOption(AsStringOption(showNullAs, propMaxLength))
    }

    /**
     * Sets the new value for [AsStringOption.showNullAs]
     * @return the builder (`this`)
     */
    public fun showNullAs(showNullAs: String): AsStringOptionBuilder {
        setNewDefaultOption(showNullAs = showNullAs)
        return this
    }

    /**
     * Sets the new value for [AsStringOption.propMaxStringValueLength]
     * @return the builder (`this`)
     */
    public fun maxPropertyStringLength(propMaxLength: Int): AsStringOptionBuilder {
        setNewDefaultOption(propMaxLength = propMaxLength)
        return this
    }
}

/** Convenience method to retrieve [AsStringOption.defaultAsStringOption]'s [AsStringOption.showNullAs] */
internal val defaultNullString: String
    @JvmSynthetic // avoid access from external Java code
    get() = AsStringOption.defaultAsStringOption.showNullAs

/** Convenience method to retrieve [AsStringOption.defaultAsStringOption]'s [AsStringOption.propMaxStringValueLength] */
internal val defaultMaxStringValueLength: Int
    @JvmSynthetic // avoid access from external Java code
    get() = AsStringOption.defaultAsStringOption.propMaxStringValueLength

/**
 * Reset config options: resets [AsStringOption.defaultAsStringOption] to [initialDefaultAsStringOption].
 * Mainly for testing purposes.
 *  > This operation will reset (clear) the property cache, if necessary
 */
@JvmSynthetic
internal fun restoreInitialDefaultAsStringOption(): AsStringOption =
    initialDefaultAsStringOption.also { AsStringOption.defaultAsStringOption = it }
