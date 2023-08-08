package nl.kute.core.ordering

import nl.kute.core.ordering.ValueLengthRanking.L
import nl.kute.core.ordering.ValueLengthRanking.M
import nl.kute.core.ordering.ValueLengthRanking.XL
import nl.kute.core.property.PropertyValueInformation
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal val propertyRankingRegistryByClass: MutableMap<KClass<out PropertyRanking>, PropertyRanking> = ConcurrentHashMap()

public fun registerPropertyRankingClass(classInstancePair: Pair<KClass<out PropertyRanking>, PropertyRanking>) {
    propertyRankingRegistryByClass[classInstancePair.first] = classInstancePair.second
}

// TODO: kdoc
public interface PropertyRanking {
    // TODO: kdoc
    public fun getRank(propertyValueInfo: PropertyValueInformation): Short

    // TODO: kdoc
    public fun instance() : PropertyRanking
}

// TODO: kdoc
public abstract class AbstractPropertyRanking : PropertyRanking {
    init {
        @Suppress("LeakingThis")
        registerPropertyRankingClass(this::class to this)
    }
    override fun equals(other: Any?): Boolean =
        this === other || (other != null && this::class == other::class)
    override fun hashCode(): Int = 0
}

// region ~ Concrete implementations of PropertyRanking

// TODO: kdoc
public class PropertyRankingByLength private constructor(): AbstractPropertyRanking() {
    override fun getRank(propertyValueInfo: PropertyValueInformation): Short =
        ValueLengthRanking.getRank(propertyValueInfo.stringValueLength).rank

    override fun instance(): PropertyRanking = instance

    public companion object {
        // TODO: kdoc
        public val instance: PropertyRankingByLength = PropertyRankingByLength()
    }
}
@Suppress("unused") // construct instance to have it registered
private val propertyRankingByLength = PropertyRankingByLength.instance

// TODO: kdoc
public class PropertyRankingByType private constructor(): AbstractPropertyRanking() {
    public override fun getRank(propertyValueInfo: PropertyValueInformation): Short {
        return if (propertyValueInfo.isBaseType && !propertyValueInfo.isCharSequence) 0
        else ValueLengthRanking.maxRank
    }

    override fun instance(): PropertyRanking = instance

    public companion object {
        // TODO: kdoc
        public val instance: PropertyRankingByType = PropertyRankingByType()
    }
}
@Suppress("unused") // construct instance to have it registered
private val propertyRankingByType = PropertyRankingByType.instance

// TODO: kdoc
public class PropertyRankingByTypeAndLength private constructor(): AbstractPropertyRanking() {
    public override fun getRank(propertyValueInfo: PropertyValueInformation): Short {
        val typeRank: Short = PropertyRankingByType.instance.getRank(propertyValueInfo)
        return (typeRank + PropertyRankingByLength.instance.getRank(propertyValueInfo)).toShort()
    }

    override fun instance(): PropertyRanking = instance

    public companion object {
        // TODO: kdoc
        public val instance: PropertyRankingByTypeAndLength = PropertyRankingByTypeAndLength()
    }
}
@Suppress("unused") // construct instance to have it registered
private val propertyRankingByTypeAndLength = PropertyRankingByTypeAndLength.instance

private val propNamesCategorizing = arrayOf("code", "type", "category", "order", "kind")
private val propNamesNumberSuffix = arrayOf("num", "no", "nr", "number", "numer", "numero")
private val propNamesLink = arrayOf("url", "uri", "link")
private val propNamesInformative = arrayOf("text", "desc", "info", "expl")
private val propNamesDocument = arrayOf("file", "report", "doc", "content")
private val propNamesContentFormat = arrayOf("xml", "csv", "yaml", "yml", "axon", "html", "htm", "protobuf")

// TODO: kdoc
public class PropertyRankingByCommonNames private constructor(): AbstractPropertyRanking() {
    public override fun getRank(propertyValueInfo: PropertyValueInformation): Short {
        val propName = propertyValueInfo.propertyName
        val propNameLower = propertyValueInfo.propertyName.lowercase()
        val propSizeRank = ValueLengthRanking.getRank(propertyValueInfo.stringValueLength)
        return when {
            propNameLower == "id" -> 0
            propName.endsWith("Id") -> 5
            propNameLower.endsWith("_id") -> 5
            propNameLower.endsWith("uuid") -> 15
            propSizeRank <= M && propNameLower.contains("ident") -> 20
            propSizeRank <= M && propName.endsWith("Ref") -> 20
            propSizeRank <= M && propNameLower.endsWith("_ref") -> 20
            propSizeRank <= L && propNamesCategorizing.any { propNameLower.contains(it) } -> 50
            propSizeRank <= L && propNamesNumberSuffix.any { propNameLower.endsWith(it) } -> 50
            propSizeRank <= XL && propNamesLink.any { propNameLower.endsWith(it) } -> 70
            propSizeRank <= L && propNameLower.contains("sql") -> 80
            propSizeRank <= M -> 70
            propNameLower.contains("json") -> 100
            propNamesDocument.any { propNameLower.contains(it) } -> 300
            propNamesContentFormat.any { propNameLower.contains(it) } -> 300
            propNameLower.contains("xml") -> 300
            propNamesInformative.any { propNameLower.contains(it) } -> if (propSizeRank <= L)  80 else 500
            else -> 70
        }
    }

    override fun instance(): PropertyRanking = instance

    public companion object {
        // TODO: kdoc
        public val instance: PropertyRankingByCommonNames = PropertyRankingByCommonNames()
    }
}

@Suppress("unused") // construct instance to have it registered
private val propertyRankingByCommonNames = PropertyRankingByCommonNames.instance

// TODO: kdoc
public class NoOpPropertyRanking private constructor() : AbstractPropertyRanking() {
    override fun getRank(propertyValueInfo: PropertyValueInformation): Short = 0

    override fun instance(): PropertyRanking = instance

    public companion object {
        // TODO: kdoc
        public val instance: NoOpPropertyRanking = NoOpPropertyRanking()
    }
}

// endregion