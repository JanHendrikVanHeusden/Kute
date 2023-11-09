package nl.kute.asstring.property.ranking

import nl.kute.asstring.core.asString

/**
 * Abstract base class to provide ranking for ordering properties in [nl.kute.asstring.core.asString] output.
 * * Features basic [toString], [equals], and [hashCode] implementations.
 * * On construction, it automatically registers the concrete class to  be used for ordering properties.
 *
 * **In order to be used for *property ranking*** (see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]),
 * the concrete class must either (in this order of prevalence):
 * * Be pre-instantiated, by having a concrete [PropertyRanking]-subclass object constructed
 * * Allow reflective instantiation, by one of the following methods:
 *   1. Have a reachable (`public`) companion object with a `val` property named **`instance`** that returns
 *   an instance of the concrete [PropertyRankable] subclass.
 *   2. Have a no-arg constructor that is reachable (`public`) or that can be set accessible reflectively
 *   by means of [kotlin.reflect.KProperty.isAccessible]
 *
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
public abstract class PropertyRanking : PropertyRankable<PropertyRanking> {

    init {
        register()
    }

    /**
     * Register this concrete [PropertyRankable] class to allow using it for ordering properties in
     * [nl.kute.asstring.core.asString] output.
     * > This method is called automatically when the class (or a subclass) is constructed;
     * so there is no need to call it explicitly.
     */
    public final override fun register() {
        super.register()
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other != null && this::class == other::class)
    override fun hashCode(): Int = this::class.hashCode()

    override fun toString(): String = asString()
}