//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[NoOpPropertyRanking](index.md)

# NoOpPropertyRanking

@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(includeCompanion = false)

class [NoOpPropertyRanking](index.md) : [PropertyRanking](../-property-ranking/index.md)

Class to explicitly specify that properties need not be ordered

#### See also

| |
|---|
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [equals](../-property-ranking/equals.md) | [jvm]<br>open operator override fun [equals](../-property-ranking/equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [getRank](get-rank.md) | [jvm]<br>open override fun [getRank](get-rank.md)(propertyValueMetaData: [PropertyValueMetaData](../../nl.kute.asstring.property.meta/-property-value-meta-data/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [hashCode](../-property-ranking/hash-code.md) | [jvm]<br>open override fun [hashCode](../-property-ranking/hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [register](../-property-ranking/register.md) | [jvm]<br>override fun [register](../-property-ranking/register.md)()<br>Register this concrete [PropertyRankable](../-property-rankable/index.md) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output. |
| [toString](../-property-ranking/to-string.md) | [jvm]<br>open override fun [toString](../-property-ranking/to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
