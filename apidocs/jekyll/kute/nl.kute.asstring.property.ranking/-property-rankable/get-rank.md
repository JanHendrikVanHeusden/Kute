---
title: getRank
---
//[kute](../../../index.html)/[nl.kute.asstring.property.ranking](../index.html)/[PropertyRankable](index.html)/[getRank](get-rank.html)



# getRank



[jvm]\
abstract fun [getRank](get-rank.html)(propertyValueMeta: [PropertyValueMeta](../../nl.kute.asstring.property.meta/-property-value-meta/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)



Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMeta](get-rank.html).



The rank should be deterministic (i.e. always return the same value given same input)



#### Return



A numeric rank based on / associated with the given [propertyValueMeta](get-rank.html)




