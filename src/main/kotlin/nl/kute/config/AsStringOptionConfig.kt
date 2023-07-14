package nl.kute.config

import nl.kute.core.annotation.option.AsStringOption

/** Initial default value for how to represent `null` in the [nl.kute.core.asString] */
public const val initialDefaultNullString: String = "null"

/** Initial default value for the maximum length **per property** in the [nl.kute.core.asString] output */
public const val initialDefaultMaxStringValueLength: Int = 500

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.core.asString] output */
public const val initialDefaultIncludeIdentityHash: Boolean = false

/** Initial default options for the output of [nl.kute.core.asString] */
public val initialDefaultAsStringOption: AsStringOption =
    AsStringOption(initialDefaultNullString, initialDefaultMaxStringValueLength, initialDefaultIncludeIdentityHash)

/**
 * Builder class, intended to apply the [AsStringOption] as a default (see [AsStringOptionBuilder.applyAsDefault]).
 */
public class AsStringOptionBuilder {

    private val currentDefaultOption = AsStringOption.defaultAsStringOption
    private val currentDefaultNullStr = currentDefaultOption.showNullAs
    private val currentDefaultPropMaxLength = currentDefaultOption.propMaxStringValueLength
    private val currentDefaultIncludeHash = currentDefaultOption.includeIdentityHash

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

    private fun setNewDefaultOption(showNullAs: String = currentDefaultNullStr, propMaxLength: Int = currentDefaultPropMaxLength, includeHash: Boolean = currentDefaultIncludeHash) {
        setNewDefaultOption(AsStringOption(showNullAs,  propMaxLength, includeHash))
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

    /**
     * Sets the new value for [AsStringOption.includeIdentityHash]
     * @return the builder (`this`)
     */
    public fun includeIdentityHash(includeHash: Boolean): AsStringOptionBuilder {
        setNewDefaultOption(includeHash = includeHash)
        return this
    }
}

/** Convenience method to retrieve [AsStringOption.defaultAsStringOption]'s [AsStringOption.showNullAs] */
internal val defaultNullString: String
    get() = AsStringOption.defaultAsStringOption.showNullAs

/** Convenience method to retrieve [AsStringOption.defaultAsStringOption]'s [AsStringOption.propMaxStringValueLength] */
internal val defaultMaxStringValueLength: Int
    get() = AsStringOption.defaultAsStringOption.propMaxStringValueLength

/** Convenience method to retrieve [AsStringOption.defaultAsStringOption]'s [AsStringOption.includeIdentityHash] */
internal val defaultIncludeIdentityHash: Boolean
    get() = AsStringOption.defaultAsStringOption.includeIdentityHash

/**
 * Reset config options: resets [AsStringOption.defaultAsStringOption] to [initialDefaultAsStringOption]
 *  > This operation will reset (clear) the property cache, if necessary
 */
public fun restoreInitialDefaultAsStringOption(): AsStringOption =
    initialDefaultAsStringOption.also { AsStringOption.defaultAsStringOption = it }
