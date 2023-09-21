//[kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyRanking](index.md)

# PropertyRanking

abstract class [PropertyRanking](index.md) : [PropertyRankable](../-property-rankable/index.md)&lt;[PropertyRanking](index.md)&gt; 

Abstract base class to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output.

- 
   Features basic [toString](to-string.md), [equals](equals.md), and [hashCode](hash-code.md) implementations.
- 
   On construction, it automatically registers the concrete class to  be used for ordering properties.

#### See also

| |
|---|
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md) |

#### Inheritors

| |
|---|
| [NoOpPropertyRanking](../-no-op-property-ranking/index.md) |
| [PropertyRankingByCommonNames](../-property-ranking-by-common-names/index.md) |
| [PropertyRankingByStringValueLength](../-property-ranking-by-string-value-length/index.md) |
| [PropertyRankingByType](../-property-ranking-by-type/index.md) |

## Constructors

| | |
|---|---|
| [PropertyRanking](-property-ranking.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [getRank](../-property-rankable/get-rank.md) | [jvm]<br>abstract fun [getRank](../-property-rankable/get-rank.md)(propertyValueMeta: [PropertyValueMeta](../../nl.kute.asstring.property.meta/-property-value-meta/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMeta](../-property-rankable/get-rank.md). |
| [hashCode](hash-code.md) | [jvm]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [register](register.md) | [jvm]<br>override fun [register](register.md)()<br>Register this concrete [PropertyRankable](../-property-rankable/index.md) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output. |
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |