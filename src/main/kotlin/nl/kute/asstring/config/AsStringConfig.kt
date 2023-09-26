package nl.kute.asstring.config

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.core.ClassFilter
import nl.kute.asstring.core.PropertyOmitFilter
import nl.kute.asstring.core.defaults.initialAsStringClassOption
import nl.kute.asstring.core.defaults.initialAsStringOption
import nl.kute.asstring.core.forceToStringClassRegistry
import nl.kute.asstring.core.propertyOmitFilterRegistry
import nl.kute.asstring.property.meta.PropertyMeta
import nl.kute.asstring.property.ranking.PropertyRankable
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate
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

    private var propertyOmitFilters: Array<out PropertyOmitFilter> =
        propertyOmitFilterRegistry.entries().toTypedArray()

    private var forceToStringFilters: Array<out ClassFilter> =
        forceToStringClassRegistry.entries().toTypedArray()

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
     * Sets the new default value for [AsStringClassOption.toStringPreference].
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
     * * Any exceptions that may occur during evaluation of a [PropertyRankable] are ignored, and:
     *   * The exception will be logged
     *   * The [PropertyRankable] will be removed from the registry, to avoid further exceptions
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
     * Sets the new [PropertyOmitFilter]s to be applied.
     * * Properties for which one of more [filters] return **`true`** will be omitted in subsequent calls to [nl.kute.asstring.core.asString].
     * * Any exceptions that may occur during evaluation of a [PropertyOmitFilter] are ignored, and:
     *    * The exception will be logged
     *    * The filter will be removed from the filter registry, to avoid further exceptions
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the values as an application-wide default.
     * * Any previously existing filters will be removed by [applyAsDefault].
     * @see getPropertyOmitFilters
     * @see [applyAsDefault]
     */
    public fun withPropertyOmitFilters(vararg filters: PropertyOmitFilter): AsStringConfig {
        propertyOmitFilters = filters
        return this
    }

    /**
     * Sets the new [ClassFilter]s to be applied.
     * * Custom [KClass]es for which one of more [filters] return **`true`** will have their [toString] called
     *   (instead of [nl.kute.asstring.core.asString])
     * * Any exceptions that may occur during evaluation of a [ClassFilter] are ignored, and:
     *    * The exception will be logged
     *    * The filter will be removed from the filter registry, to avoid further exceptions
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the values as an application-wide default.
     * * Any previously existing filters will be removed by [applyAsDefault].
     * @see getPropertyOmitFilters
     * @see [applyAsDefault]
     */
    public fun withForceToStringFilters(vararg filters: ClassFilter): AsStringConfig {
        forceToStringFilters = filters
        return this
    }

    /**
     * Sets the new [PropertyOmitFilter]s to be applied.
     * * Is essentially a wrapper for [withPropertyOmitFilters]
     *
     * > Mainly for use from **Java**, where [Predicate] parameter is easier than lambdas (in [withPropertyOmitFilters])
     * > For **Kotlin**, [withPropertyOmitFilters] is recommended (fore ease of use)
     *
     * * Nothing will happen effectively until [applyAsDefault] is called on the [AsStringConfig] object.
     * * [applyAsDefault] applies the values as an application-wide default.
     * * Any previously existing filters will be removed by [applyAsDefault].
     * @see withPropertyOmitFilters
     * @see getPropertyOmitFilters
     * @see [applyAsDefault]
     */
    public fun withPropertyOmitFilterPredicates(predicate: Predicate<in PropertyMeta>): AsStringConfig {
        return withPropertyOmitFilters({ m -> predicate.test(m) })
    }

    /**
     * @return The currently effective [PropertyOmitFilter]s
     * @see [withPropertyOmitFilters]
     */
    public fun getPropertyOmitFilters(): Collection<PropertyOmitFilter> =
        propertyOmitFilterRegistry.getEntryMap().keys

    /**
     * @return The currently effective [ClassFilter]s
     * @see [withForceToStringFilters]
     */
    public fun getForceToStringFilters(): Collection<PropertyOmitFilter> =
        forceToStringClassRegistry.getEntryMap().keys

    /** @return The currently effective default [AsStringOption] */
    public fun getAsStringOptionDefault(): AsStringOption = AsStringOption.defaultOption

    /** @return The currently effective default [AsStringClassOption] */
    public fun getAsStringClassOptionDefault(): AsStringClassOption = AsStringClassOption.defaultOption

    /**
     * Assigns the [AsStringOption] and [AsStringClassOption] being built, to [AsStringOption.defaultOption]
     * and [AsStringClassOption.defaultOption], respectively, as the new application defaults.
     *  > This operation will reset (clear) the property cache and other caches, if necessary.
     */
    public fun applyAsDefault() {
        AsStringOption.defaultOption = newDefaultAsStringOption
        AsStringClassOption.defaultOption = newDefaultAsStringClassOption
        propertyOmitFilterRegistry.replaceAll(*propertyOmitFilters)
        forceToStringClassRegistry.replaceAll(*forceToStringFilters)
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
