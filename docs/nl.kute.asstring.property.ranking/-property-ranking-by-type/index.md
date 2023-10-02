---
title: PropertyRankingByType
---
//[kute](../../../index.html)/[nl.kute.asstring.property.ranking](../index.html)/[PropertyRankingByType](index.html)



# PropertyRankingByType





@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeCompanion = false)



open class [PropertyRankingByType](index.html) : [PropertyRanking](../-property-ranking/index.html)

Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output, based on PropertyValueMeta.returnType. Intended mainly to keep known basic types with not too long `toString()`-representations ordered first



E.g. [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html), [java.util.Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html), [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), [java.time.temporal.Temporal](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/Temporal.html) etc.; see [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.html)



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
| [toString](../-property-ranking/to-string.html) | [jvm]<br>open override fun [toString](../-property-ranking/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

