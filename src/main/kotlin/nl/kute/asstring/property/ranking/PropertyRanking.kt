package nl.kute.asstring.property.ranking

import nl.kute.asstring.core.asString

/**
 * Abstract base class to provide ranking for ordering properties in [nl.kute.asstring.core.asString] output.
 * * Features basic [toString], [equals], and [hashCode] implementations.
 * * On construction, it automatically registers the concrete class to  be used for ordering properties.
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