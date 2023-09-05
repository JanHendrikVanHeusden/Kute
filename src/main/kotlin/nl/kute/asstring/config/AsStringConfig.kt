package nl.kute.asstring.config

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.core.defaults.initialAsStringClassOption
import nl.kute.asstring.core.defaults.initialAsStringOption
import nl.kute.asstring.property.ranking.PropertyRankable
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Builder-like class, to prepare and apply newly set values as defaults
 * for [AsStringOption] / [AsStringClassOption].
 * * Various `with*`-methods are available to set the desired defaults
 * * [applyAsDefault]`()` must be called to make the new values effective as default
 * * [AsStringConfig] is permissive; no validation is applied to input values
 * > The current (and newly set) defaults can be accessed as [AsStringOption.defaultOption] and
 * [AsStringClassOption.defaultOption]
 * @constructor As an alternative, one can use static method [asStringConfig] instead
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
        elementsLimit: Int = newDefaultAsStringOption.elementsLimit
    ) {

        setNewDefaultAsStringOption(
            AsStringOption(
                showNullAs = showNullAs,
                surroundPropValue = surroundPropValue,
                propMaxStringValueLength = propMaxLength,
                elementsLimit = elementsLimit
            )
        )
    }

    private fun setNewDefaultAsStringClassOption(newAsStringClassOption: AsStringClassOption) {
        this.newDefaultAsStringClassOption = newAsStringClassOption
    }

    private fun setNewDefaultAsStringClassOption(
        includeIdentityHash: Boolean = newDefaultAsStringClassOption.includeIdentityHash,
        toStringPreference: ToStringPreference = newDefaultAsStringClassOption.toStringPreference,
        includeCompanion: Boolean = newDefaultAsStringClassOption.includeCompanion,
        sortNamesAlphabetic: Boolean = newDefaultAsStringClassOption.sortNamesAlphabetic,
        vararg propertySorters: KClass<out PropertyRankable<*>> = newDefaultAsStringClassOption.propertySorters
    ) {
        setNewDefaultAsStringClassOption(
            AsStringClassOption(
                includeIdentityHash = includeIdentityHash,
                toStringPreference = toStringPreference,
                includeCompanion = includeCompanion,
                sortNamesAlphabetic = sortNamesAlphabetic,
                propertySorters = propertySorters
            )
        )
    }

    /**
     * Sets the new default value for [AsStringOption.showNullAs].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringOption.defaultOption]
     * @see [AsStringOption.showNullAs]
     */
    public fun withShowNullAs(showNullAs: String): AsStringConfig {
        setNewDefaultAsStringOption(showNullAs = showNullAs)
        return this
    }

    /**
     * Sets the new default value for [AsStringOption.surroundPropValue].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringOption.surroundPropValue]
     */
    public fun withSurroundPropValue(surroundPropValue: PropertyValueSurrounder): AsStringConfig {
        setNewDefaultAsStringOption(surroundPropValue = surroundPropValue)
        return this
    }

    /**
     * Sets the new default value for [AsStringOption.propMaxStringValueLength].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringOption.propMaxStringValueLength]
     */
    public fun withMaxPropertyStringLength(propMaxLength: Int): AsStringConfig {
        setNewDefaultAsStringOption(propMaxLength = propMaxLength)
        return this
    }

    /**
     * Sets the new default value for [AsStringOption.elementsLimit].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringOption.elementsLimit]
     */
    public fun withElementsLimit(elementsLimit: Int): AsStringConfig {
        setNewDefaultAsStringOption(elementsLimit = elementsLimit)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.includeIdentityHash].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringClassOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringClassOption.includeIdentityHash]
     */
    public fun withIncludeIdentityHash(includeHash: Boolean): AsStringConfig {
        setNewDefaultAsStringClassOption(includeIdentityHash = includeHash)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.includeIdentityHash].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringClassOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringClassOption.toStringPreference]
     */
    public fun withToStringPreference(toStringPreference: ToStringPreference): AsStringConfig {
        setNewDefaultAsStringClassOption(toStringPreference = toStringPreference)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.includeCompanion].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringClassOption.defaultOption]
     *  application-wide default.
     * @return the config builder (`this`)
     * @see [applyAsDefault]
     * @see [AsStringClassOption.includeCompanion]
     */
    public fun withIncludeCompanion(includeCompanion: Boolean): AsStringConfig {
        setNewDefaultAsStringClassOption(includeCompanion = includeCompanion)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.sortNamesAlphabetic].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringClassOption.defaultOption]
     *  application-wide default.
     *
     * > **NB:** This is a pre-sorting. If additional [AsStringClassOption.propertySorters] are given,
     *   these will be applied after the alphabetic sort. That sorting is stable, so if sorters yield an equal value,
     *   the alphabetic ordering is preserved.
     * @return the config builder (`this`)
     * @see [withPropertySorters]
     * @see [applyAsDefault]
     * @see [AsStringClassOption.sortNamesAlphabetic]
     */
    public fun withPropertiesAlphabetic(sortNamesAlphabetic: Boolean): AsStringConfig {
        setNewDefaultAsStringClassOption(sortNamesAlphabetic = sortNamesAlphabetic)
        return this
    }

    /**
     * Sets the new default value for [AsStringClassOption.propertySorters].
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the value being set to the [AsStringClassOption.defaultOption]
     *  application-wide default.
     *
     * > **NB:** This sorting is applied after the alphabetic ordering (see: [withPropertiesAlphabetic]).
     * The sorting is stable, so if the [propertySorters] yield an equal value, the alphabetic ordering is preserved.
     *
     * @return the config builder (`this`)
     * @see [withPropertiesAlphabetic]
     * @see [applyAsDefault]
     * @see [AsStringClassOption.propertySorters]
     */
    public fun withPropertySorters(vararg propertySorters: KClass<out PropertyRankable<*>>): AsStringConfig {
        setNewDefaultAsStringClassOption(propertySorters = propertySorters)
        return this
    }

    /**
     * Assigns the [AsStringOption] and [AsStringClassOption] being built, to [AsStringOption.defaultOption]
     * and [AsStringClassOption.defaultOption], respectively, as the new application defaults.
     *  > This operation will reset (clear) the property cache and other caches, if necessary.
     */
    public fun applyAsDefault() {
        AsStringOption.defaultOption = newDefaultAsStringOption
        AsStringClassOption.defaultOption = newDefaultAsStringClassOption
    }

}

/** Static convenience method to construct an [AsStringConfig] object */
public fun asStringConfig(): AsStringConfig = AsStringConfig()


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
    configChangeSubscriptions[this]?.forEach { callback ->
        callback.invoke()
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
