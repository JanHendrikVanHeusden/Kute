package nl.kute.asstring.property.ranking

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.core.asString
import nl.kute.log.log
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentHashMap.newKeySet
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Interface to provide ranking for ordering properties in [nl.kute.asstring.core.asString] output.
 * > **NB:** This interface is sealed, so it can not be implemented directly.
 * > Concrete implementations should extend [PropertyRanking] instead.
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
 * This interface is sealed, so external code can not implement it.
 * Concrete implementations should extend [PropertyRanking] instead.
 * @see [PropertyRanking]
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
@AsStringClassOption(toStringPreference = USE_ASSTRING)
public sealed interface PropertyRankable<out T: PropertyRankable<T>> {

    /**
     * Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMetaData].
     * > The rank should be deterministic (i.e. always return the same value given same input)
     * @return A numeric rank based on / associated with the given [propertyValueMetaData]
     */
    public fun getRank(propertyValueMetaData: PropertyValueMetaData): Int

    /**
     * Register this concrete [PropertyRankable] class to allow using it for ordering properties
     * in [nl.kute.asstring.core.asString] output
     * */
    public fun register() {
        registerPropertyRankingClass(this::class to this)
    }
}

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

@JvmSynthetic // avoid access from external Java code
private val propertyRankingRegistryByClass: MutableMap<KClass<out PropertyRankable<*>>, PropertyRankable<*>> =
    ConcurrentHashMap()

@JvmSynthetic // avoid access from external Java code
private val unusablePropertyRankingClasses: MutableSet<KClass<out PropertyRankable<*>>> = newKeySet()

@JvmSynthetic // avoid access from external Java code
internal fun <T: PropertyRankable<T>> registerPropertyRankingClass(classInstancePair: Pair<KClass<out T>, T?>) {
    if (classInstancePair.second == null) unusablePropertyRankingClasses.add(classInstancePair.first)
    else propertyRankingRegistryByClass[classInstancePair.first] = classInstancePair.second!!
}

@JvmSynthetic // avoid access from external Java code
internal fun <T: PropertyRankable<T>> KClass<out T>.getPropertyRankableInstance(): T? {

    @Suppress("UNCHECKED_CAST")
    return (propertyRankingRegistryByClass[this] as T?)
        ?: this.factorPropertyRankable()?.
        also { it.register() }
            .ifNull {
                if (!unusablePropertyRankingClasses.contains(this)) {
                    log("Unable to instantiate $this: can't use it for ordering properties")
                    unusablePropertyRankingClasses.add(this)
                }
                null
            }
}

private fun <T: PropertyRankable<T>> KClass<out T>.factorPropertyRankable(): T? =
    this.getInstance() ?: this.constructInstance()

private fun <T: PropertyRankable<T>> KClass<out T>.getInstance(): T? {
    return try {
        val companionObjectClass: KClass<*> = this.companionObject ?: return null
        val instanceProp = companionObjectClass.memberProperties.firstOrNull {
            it.name == "instance"
                    && it.returnType.classifier == this
                    // Should be val, not var
                    && it !is KMutableProperty<*>
        } ?: return null
        @Suppress("UNCHECKED_CAST")
        return instanceProp
            .also { it.isAccessible = true }
            .getter
            .also { it.isAccessible = true }
            .call(this.companionObjectInstance!!) as T?
    } catch (e: InterruptedException) {
        throw e
    } catch (e: Exception) {
        // ignore
         null
    }
}

private fun <T: PropertyRankable<T>> KClass<out T>.constructInstance(): T? {
    return try {
        this.constructors.firstOrNull { it.parameters.isEmpty() }
            ?.also { it.isAccessible = true }
            ?.call()
    } catch (e: InterruptedException) {
        throw e
    } catch (e: Exception) {
        // ignore
        null
    }
}
