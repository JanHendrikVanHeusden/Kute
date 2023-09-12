//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyRankingByCommonNames](index.md)

# PropertyRankingByCommonNames

@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(includeCompanion = true, toStringPreference = ToStringPreference.USE_ASSTRING)

open class [PropertyRankingByCommonNames](index.md) : [PropertyRanking](../-property-ranking/index.md)

Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output, based on common property names and suffixes, combined with value lengths.

*Common names / suffixes include, for instance,* `id`*,* `...Id`*,* `code`*,* `type`*,* `uuid`*,* `json`*,* `xml`*,* `text`*,* `desc`*, etc.*

It is intended to demonstrate usage of naming conventions (together with lengths and types) as a means of ranking properties and thus their ordering.

- 
   Names and suffixes usually case-insensitive
- 
   For sure it won't fit **your** naming conventions. So the implementation is given &quot;as is&quot;!
- - 
      You are encouraged to roll your own ranking, if you feel like it!
- 
   Of course it can be combined it with other provided implementations, e.g. [PropertyRankingByLength](../-property-ranking-by-length/index.md) or [PropertyRankingByLength](../-property-ranking-by-length/index.md).

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
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
