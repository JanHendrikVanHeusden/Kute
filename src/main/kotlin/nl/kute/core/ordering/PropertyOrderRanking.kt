package nl.kute.core.ordering

import nl.kute.core.property.PropertyValueInfo
import nl.kute.core.property.PropertyValueInformation

// TODO: kdoc
public interface PropertyOrderRanking {
    public val rank: UShort
}

// TODO: kdoc
public abstract class AbstractPropertyOrderRanking(internal open val propertyValueInfo: PropertyValueInfo?) : PropertyOrderRanking

// TODO: kdoc
public class LengthPropertyRanking(propertyValueInfo: PropertyValueInfo): AbstractPropertyOrderRanking(propertyValueInfo) {
    override val rank: UShort = ValueLengthRanking.getRank(propertyValueInfo.stringValueLength).rank
}

// TODO: kdoc
public class TypeAndLengthPropertyRanking(override val propertyValueInfo: PropertyValueInfo): AbstractPropertyOrderRanking(propertyValueInfo) {
    override val rank: UShort = calcRank()
    private fun calcRank(): UShort {
        val baseTypeRank: UShort = if (propertyValueInfo.isBaseType && !propertyValueInfo.isCharSequence) 0u else 10u
        val lengthRank = ValueLengthRanking.getRank(propertyValueInfo.stringValueLength).rank + baseTypeRank
        return lengthRank.toUShort()
    }
}

// TODO: kdoc
public class NoOpPropertyRanking private constructor(propertyValueInfo: PropertyValueInfo? = null) : AbstractPropertyOrderRanking(propertyValueInfo) {
    override val rank: UShort = 0u
    public companion object {
        // TODO: kdoc
        public val instance: NoOpPropertyRanking = NoOpPropertyRanking()
        // TODO: kdoc
        public val rankProvider: (PropertyValueInformation?) -> PropertyOrderRanking = { _ -> instance }
    }

    override fun equals(other: Any?): Boolean =
        this === other || (other != null && this::class == other::class)
    override fun hashCode(): Int = 0
}
