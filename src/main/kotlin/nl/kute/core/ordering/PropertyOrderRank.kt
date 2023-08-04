package nl.kute.core.ordering

import nl.kute.core.property.PropertyValueInfo

// TODO: kdoc
public interface PropertyOrderRank {
    public val rank: UShort
}

// TODO: kdoc
public abstract class PropertyOrderRanking(internal open val propertyValueInfo: PropertyValueInfo?) : PropertyOrderRank

// TODO: kdoc
public class NoOpPropertyRank(propertyValueInfo: PropertyValueInfo? = null): PropertyOrderRanking(propertyValueInfo) {
    override val rank: UShort = 0u
}

// TODO: kdoc
public class LengthPropertyRank(propertyValueInfo: PropertyValueInfo): PropertyOrderRanking(propertyValueInfo) {
    override val rank: UShort = ValueLengthRank.getRank(propertyValueInfo.stringValueLength).rank
}

// TODO: kdoc
public class TypeAndLengthPropertyRank(override val propertyValueInfo: PropertyValueInfo): PropertyOrderRanking(propertyValueInfo) {
    override val rank: UShort = calcRank()
    private fun calcRank(): UShort {
        val baseTypeRank: UShort = if (propertyValueInfo.isBaseType && !propertyValueInfo.isCharSequence) 0u else 10u
        val lengthRank = ValueLengthRank.getRank(propertyValueInfo.stringValueLength).rank + baseTypeRank
        return lengthRank.toUShort()
    }
}
