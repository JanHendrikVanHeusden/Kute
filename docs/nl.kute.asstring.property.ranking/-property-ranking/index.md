---
title: PropertyRanking
---
//[kute](../../../index.html)/[nl.kute.asstring.property.ranking](../index.html)/[PropertyRanking](index.html)



# PropertyRanking

abstract class [PropertyRanking](index.html) : [PropertyRankable](../-property-rankable/index.html)&lt;[PropertyRanking](index.html)&gt; 

Abstract base class to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output.



- 
   Features basic [toString](to-string.html), [equals](equals.html), and [hashCode](hash-code.html) implementations.
- 
   On construction, it automatically registers the concrete class to  be used for ordering properties.




#### See also


| |
|---|
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html) |


#### Inheritors


| |
|---|
| [NoOpPropertyRanking](../-no-op-property-ranking/index.html) |
| [PropertyRankingByCommonNames](../-property-ranking-by-common-names/index.html) |
| [PropertyRankingByStringValueLength](../-property-ranking-by-string-value-length/index.html) |
| [PropertyRankingByType](../-property-ranking-by-type/index.html) |


## Constructors


| | |
|---|---|
| [PropertyRanking](-property-ranking.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [equals](equals.html) | [jvm]<br>open operator override fun [equals](equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [getRank](../-property-rankable/get-rank.html) | [jvm]<br>abstract fun [getRank](../-property-rankable/get-rank.html)(propertyValueMeta: [PropertyValueMeta](../../nl.kute.asstring.property.meta/-property-value-meta/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMeta](../-property-rankable/get-rank.html). |
| [hashCode](hash-code.html) | [jvm]<br>open override fun [hashCode](hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [register](register.html) | [jvm]<br>override fun [register](register.html)()<br>Register this concrete [PropertyRankable](../-property-rankable/index.html) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output. |
| [toString](to-string.html) | [jvm]<br>open override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

