package nl.kute.core.ordering

import nl.kute.core.ordering.ValueLengthRanking.L
import nl.kute.core.ordering.ValueLengthRanking.M
import nl.kute.core.ordering.ValueLengthRanking.XL
import nl.kute.core.property.PropertyValueMetaData
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

/**
 * Provides ranking for ordering properties in [nl.kute.core.asString] output,
 * based on T-shirt sizing by means of [ValueLengthRanking]
 * > The ranking is not based on exact lengths, that would give a really unstable ordering;
 * > so it's using length categories by some more or less arbitrary common sense length categories
 * @see [ValueLengthRanking]
 */
public open class PropertyRankingByLength private constructor(): PropertyRanking() {
    /** @return A numeric rank based on [PropertyValueMetaData.stringValueLength] and T-shirt sizes as of [ValueLengthRanking] */
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int =
        ValueLengthRanking.getRank(propertyValueMetaData.stringValueLength).rank

    override fun instance(): PropertyRankingByLength = instance

    public companion object {
        /** Singleton instance of [PropertyRankingByLength] */
        public val instance: PropertyRankingByLength = PropertyRankingByLength()
    }
}
@Suppress("unused") // construct instance to have it registered
private val propertyRankingByLength = PropertyRankingByLength.instance
    .also { it.register() }

/**
 * Provides ranking for ordering properties in [nl.kute.core.asString] output, based
 * on [PropertyValueMetaData.returnType].
 * Intended mainly to keep known basic types with not too long String-representations ordered first
 * > E.g. [Number], [java.util.Date], [Char], [Boolean], [java.time.temporal.Temporal] etc.; see [nl.kute.core.isBaseType]
 * @see [ValueLengthRanking]
 */
public open class PropertyRankingByType private constructor(): PropertyRanking() {
    /** @return A low value if it's a base-type and not a [CharSequence]; a high value otherwise */
    public override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int {
        return if (propertyValueMetaData.isBaseType && !propertyValueMetaData.isCharSequence) 0
        else 10
    }

    override fun instance(): PropertyRankingByType = instance

    public companion object {
        /** Singleton instance of [PropertyRankingByType] */
        public val instance: PropertyRankingByType = PropertyRankingByType()
    }
}
@Suppress("unused") // construct instance to have it registered
private val propertyRankingByType = PropertyRankingByType.instance
    .also { it.register() }

@Suppress("KDocMissingDocumentation")
public val propNamesCategorizing: Array<String> = arrayOf("code", "type", "category", "order", "kind")
@Suppress("KDocMissingDocumentation")
public val propNamesNumberSuffix: Array<String> = arrayOf("num", "no", "nr", "number", "numer", "numero")
@Suppress("KDocMissingDocumentation")
public val propNamesLink: Array<String> = arrayOf("url", "uri", "link")
@Suppress("KDocMissingDocumentation")
public val propNamesInformative: Array<String> = arrayOf("text", "txt", "tekst", "desc", "info", "expl")
@Suppress("KDocMissingDocumentation")
public val propNamesDocument: Array<String> = arrayOf("file", "report", "doc", "content")
@Suppress("KDocMissingDocumentation")
public val propNamesContentFormat: Array<String> = arrayOf("xml", "csv", "yaml", "yml", "axon", "html", "htm", "protobuf")

/**
 * Provides ranking for ordering properties in [nl.kute.core.asString] output, based on common property names and suffixes,
 * combined with value lengths.
 * > *Common names / suffixes include, for instance, `id`, `...Id`, `code`, `type`, `uuid`, `json`, `xml`, `text`, `desc`, etc.*
 *
 * It is intended to demonstrate usage of naming conventions (together with lengths) as a means of ranking
 * properties and thus their ordering.
 * * Names and suffixes usually case-insensitive
 * * For sure it won't fit **your** naming conventions. So the implementation is given "as is"!
 *    * You are encouraged to roll your own ranking, if you feel like it!
 * * Of course it can be combined it with other provided implementations, e.g. [PropertyRankingByLength] or [PropertyRankingByLength].
 */
public open class PropertyRankingByCommonNames private constructor(): PropertyRanking() {

    /** @return A numeric rank based on [PropertyValueMetaData.stringValueLength] and T-shirt sizes as of [ValueLengthRanking] */
    public override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int {
        val propName = propertyValueMetaData.propertyName
        val propNameLower = propertyValueMetaData.propertyName.lowercase()
        val propSizeRank = ValueLengthRanking.getRank(propertyValueMetaData.stringValueLength)
        return when {
            propNameLower == "id" -> 0
            propName.endsWith("Id") -> 5
            propNameLower.endsWith("_id") -> 5
            propNameLower.endsWith("uuid") -> 15
            propSizeRank <= M && propNameLower.contains("ident") -> 20
            propSizeRank <= M && propName.endsWith("Ref") -> 20
            propSizeRank <= M && propNameLower.endsWith("_ref") -> 20
            propSizeRank <= L && propNamesCategorizing.any { propNameLower.contains(it) } -> 30
            propSizeRank <= L && propNamesNumberSuffix.any { propNameLower.endsWith(it) } -> 30
            propSizeRank <= XL && propNamesLink.any { propNameLower.endsWith(it) } -> 40
            propSizeRank <= L && propNameLower.contains("sql") -> 50
            propSizeRank <= M -> 40
            propNameLower.contains("json") -> 60
            propNamesDocument.any { propNameLower.contains(it) } -> 80
            propNamesContentFormat.any { propNameLower.contains(it) } -> 80
            propNameLower.contains("xml") -> 80
            propNamesInformative.any { propNameLower.contains(it) } -> if (propSizeRank <= L)  50 else 100
            else -> 40
        }
    }

    override fun instance(): PropertyRankingByCommonNames = instance

    public companion object {
        /** Singleton instance of [PropertyRankingByCommonNames] */
        public val instance: PropertyRankingByCommonNames = PropertyRankingByCommonNames()
    }
}

@Suppress("unused") // construct instance to have it registered
private val propertyRankingByCommonNames = PropertyRankingByCommonNames.instance
    .also { it.register() }

/**
 * Class to explicitly specify that properties need not be ordered
 */
public class NoOpPropertyRanking private constructor() : PropertyRanking() {

    /** @return A constant value regardless of [propertyValueMetaData] input;
     * so effectively, it will not contribute to ordering
     */
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int = 0

    override fun instance(): NoOpPropertyRanking = instance

    public companion object {
        /** Singleton instance of [NoOpPropertyRanking] */
        public val instance: NoOpPropertyRanking = NoOpPropertyRanking()
    }
}

@Suppress("unused") // construct instance to have it registered
private val noOpPropertyRanking = NoOpPropertyRanking.instance
    .also { it.register() }

// endregion