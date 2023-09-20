//[kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyRankable](index.md)/[getRank](get-rank.md)

# getRank

[jvm]\
abstract fun [getRank](get-rank.md)(propertyValueMeta: [PropertyValueMeta](../../nl.kute.asstring.property.meta/-property-value-meta/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMeta](get-rank.md).

The rank should be deterministic (i.e. always return the same value given same input)

#### Return

A numeric rank based on / associated with the given [propertyValueMeta](get-rank.md)
