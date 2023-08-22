package nl.kute.core.property.ranking

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@JvmSynthetic // avoid access from external Java code
internal val propertyRankingRegistryByClass: MutableMap<KClass<out PropertyRankable<*>>, PropertyRankable<*>> = ConcurrentHashMap()

@JvmSynthetic // avoid access from external Java code
internal fun <T: PropertyRankable<T>> registerPropertyRankingClass(classInstancePair: Pair<KClass<out T>, T>) {
    propertyRankingRegistryByClass[classInstancePair.first] = classInstancePair.second
}

/**
 * Interface to provide ranking for ordering properties in [nl.kute.core.asString] output
 * @see [nl.kute.core.annotation.option.AsStringClassOption.propertySorters]
 */
public interface PropertyRankable<out T: PropertyRankable<T>> {

    /**
     * Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMetaData].
     * > The rank should be deterministic (i.e. always return the same value given same input)
     * @return A numeric rank based on / associated with the given [propertyValueMetaData]
     */
    public fun getRank(propertyValueMetaData: PropertyValueMetaData): Int

    /** @return An instance (*`singleton`* or *`singleton`-like*) of this concrete [PropertyRankable] */
    public fun instance(): T

    /** Register this concrete [PropertyRankable] class to allow using it for ordering properties in [nl.kute.core.asString] output */
    public fun register() {
        registerPropertyRankingClass(this::class to this.instance())
    }
}

/**
 * Convenience abstract base class to provide ranking for ordering properties in [nl.kute.core.asString] output.
 * * Features basic [equals] and [hashCode] implementation, based on class of `this`
 * @see [nl.kute.core.annotation.option.AsStringClassOption.propertySorters]
 */
public abstract class PropertyRanking : PropertyRankable<PropertyRanking> {

    override fun equals(other: Any?): Boolean =
        this === other || (other != null && this::class == other::class)
    override fun hashCode(): Int = this::class.hashCode()

    final override fun register() {
        super.register()
    }
}

// region ~ Concrete implementations of PropertyRanking

// endregion