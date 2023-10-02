---
title: PropertyRankingByCommonNames
---
//[kute](../../../index.html)/[nl.kute.asstring.property.ranking](../index.html)/[PropertyRankingByCommonNames](index.html)



# PropertyRankingByCommonNames





@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeCompanion = true, toStringPreference = ToStringPreference.USE_ASSTRING)



open class [PropertyRankingByCommonNames](index.html) : [PropertyRanking](../-property-ranking/index.html)

Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output, based on common property names and suffixes, combined with value lengths.



*Common names / suffixes include, for instance,* `id`*,* `...Id`*,* `code`*,* `type`*,* `uuid`*,* `json`*,* `xml`*,* `text`*,* `desc`*, etc.*



It is intended to demonstrate usage of naming conventions (together with lengths and types) as a means of ranking properties and thus their ordering.



- 
   Names and suffixes usually case-insensitive
- 
   For sure it won't fit **your** naming conventions. So the implementation is given &quot;as is&quot;!
- - 
      You are encouraged to roll your own ranking, if you feel like it!
- 
   Of course it can be combined it with other provided implementations, e.g. [PropertyRankingByStringValueLength](../-property-ranking-by-string-value-length/index.html).




#### See also


| |
|---|
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html) |


## Types


| Name | Summary |
|---|---|
| [Companion](-companion/index.html) | [jvm]<br>object [Companion](-companion/index.html) |


## Functions


| Name | Summary |
|---|---|
| [equals](../-property-ranking/equals.html) | [jvm]<br>open operator override fun [equals](../-property-ranking/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [getRank](get-rank.html) | [jvm]<br>open override fun [getRank](get-rank.html)(propertyValueMeta: [PropertyValueMeta](../../nl.kute.asstring.property.meta/-property-value-meta/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [hashCode](../-property-ranking/hash-code.html) | [jvm]<br>open override fun [hashCode](../-property-ranking/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [register](../-property-ranking/register.html) | [jvm]<br>override fun [register](../-property-ranking/register.html)()<br>Register this concrete [PropertyRankable](../-property-rankable/index.html) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output. |
| [toString](to-string.html) | [jvm]<br>open override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

