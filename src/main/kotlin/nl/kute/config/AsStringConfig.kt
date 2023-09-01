package nl.kute.config

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.PropertyValueSurrounder
import nl.kute.core.annotation.option.PropertyValueSurrounder.NONE
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.core.property.ranking.PropertyRankable
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Builder-like class, to prepare and apply newly set values as defaults
 * for [AsStringOption] / [AsStringClassOption].
 * * [applyAsDefault]`()` must be called to make the new values effective as default.
 * * [AsStringConfig] is permissive; no validation is applied to input values.
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
        surroundPropValue: PropertyValueSurrounder = newDefaultAsStringOption.surroundPropValue,
        propMaxLength: Int = newDefaultAsStringOption.propMaxStringValueLength,
        elementsLimit: Int = newDefaultAsStringOption.elementsLimit) {

        setNewDefaultAsStringOption(AsStringOption(
            showNullAs = showNullAs,
            surroundPropValue = surroundPropValue,
            propMaxStringValueLength = propMaxLength,
            elementsLimit = elementsLimit
        ))
    }

    private fun setNewDefaultAsStringClassOption(newAsStringClassOption: AsStringClassOption) {
        this.newDefaultAsStringClassOption = newAsStringClassOption
    }

    private fun setNewDefaultAsStringClassOption(
        includeIdentityHash: Boolean = newDefaultAsStringClassOption.includeIdentityHash,
        toStringPreference: ToStringPreference = newDefaultAsStringClassOption.toStringPreference,
        sortNamesAlphabetic: Boolean = newDefaultAsStringClassOption.sortNamesAlphabetic,
        vararg propertySorters: KClass<out PropertyRankable<*>> = newDefaultAsStringClassOption.propertySorters
        ) {
        setNewDefaultAsStringClassOption(AsStringClassOption(
            includeIdentityHash = includeIdentityHash,
            toStringPreference = toStringPreference,
            sortNamesAlphabetic = sortNamesAlphabetic,
            propertySorters = propertySorters
        ))
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
     * Sets the new default value for [AsStringOption.surroundPropValue].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringOption] annotation is present.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     */
    public fun withSurroundPropValue(surroundPropValue: PropertyValueSurrounder): AsStringConfig {
        setNewDefaultAsStringOption(surroundPropValue = surroundPropValue)
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
     * Sets the new default value for [AsStringOption.elementsLimit].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringOption] annotation is present.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     */
    public fun withElementsLimit(elementsLimit: Int): AsStringConfig {
        setNewDefaultAsStringOption(elementsLimit = elementsLimit)
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
     * Sets the new default value for [AsStringClassOption.sortNamesAlphabetic].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringClassOption] annotation is present.
     *
     * > **NB:** This is a pre-sorting. If additional [AsStringClassOption.propertySorters] are given,
     *   these will be applied after the alphabetic sort. That sorting is stable, so if sorters yield an equal value,
     *   the alphabetic ordering is preserved.
     * @see [withPropertySorters]
     */
    public fun withPropertiesAlphabetic(sortNamesAlphabetic: Boolean): AsStringConfig {
        setNewDefaultAsStringClassOption(sortNamesAlphabetic = sortNamesAlphabetic)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.propertySorters].
     *
     * After being applied, this value is used as an application-wide default
     * when no [AsStringClassOption] annotation is present.
     *
     * > **NB:** This sorting is applied after the alphabetic ordering (see: [withPropertiesAlphabetic]).
     * The sorting is stable, so if the [propertySorters] yield an equal value, the alphabetic ordering is preserved.
     *
     * @see [withPropertiesAlphabetic]
     */    public fun withPropertySorters(vararg propertySorters: KClass<out PropertyRankable<*>>): AsStringConfig {
        setNewDefaultAsStringClassOption(propertySorters = propertySorters)
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

/** Limits the overall number of properties or [nl.kute.core.namedvalues.NamedValue]s to be joined together */
public const val stringJoinMaxCount: Int = 1000

/** Initial default value for limiting the number of elements of a collection to be parsed */
public const val initialElementsLimit: Int = 50

/** Initial default value for how to represent `null` in the [nl.kute.core.asString] output */
public const val initialShowNullAs: String = "null"

/** Initial default value for prefix/postfix property value Strings in the [nl.kute.core.asString] output */
public val initialSurroundPropValue: PropertyValueSurrounder = NONE

/** Initial default value for the maximum length **per property** in the [nl.kute.core.asString] output */
public const val initialMaxStringValueLength: Int = 500

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.core.asString] output */
public const val initialIncludeIdentityHash: Boolean = false

/** Initial default value for the choice whether the properties should be pre-sorted alphabetically in  [nl.kute.core.asString] output */
public const val initialSortNamesAlphabetic: Boolean = false

/** Initial default value for [PropertyRankable] property ranking classes to be used for sorting properties in [nl.kute.core.asString] output */
public val initialPropertySorters: Array<KClass<PropertyRankable<*>>> = arrayOf()

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.core.asString] output */
public val initialToStringPreference: ToStringPreference = USE_ASSTRING

/** Initial default options for the output of [nl.kute.core.asString] */
@JvmSynthetic // avoid access from external Java code
internal val initialAsStringOption: AsStringOption = AsStringOption()

/** Initial default options for the output of [nl.kute.core.asString] */
@JvmSynthetic // avoid access from external Java code
internal val initialAsStringClassOption: AsStringClassOption = AsStringClassOption()

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
    configChangeSubscriptions[this]?.forEach {
            callback -> callback.invoke()
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun KClass<Annotation>.subscribeConfigChange(callback: () -> Unit) {
    configChangeSubscriptions[this].ifNull {
        mutableListOf<() -> Unit>().also { configChangeSubscriptions[this] = it }
    }.add(callback)
}

private val configChangeSubscriptions:
        MutableMap<KClass<*>, MutableList<() -> Unit>> = ConcurrentHashMap()
