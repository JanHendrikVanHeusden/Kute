//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyRankable](index.md)/[getRank](get-rank.md)

# getRank

[jvm]\
abstract fun [getRank](get-rank.md)(propertyValueMetaData: [PropertyValueMetaData](../-property-value-meta-data/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMetaData](get-rank.md).

The rank should be deterministic (i.e. always return the same value given same input)

#### Return

A numeric rank based on / associated with the given [propertyValueMetaData](get-rank.md)
