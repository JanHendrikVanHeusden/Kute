//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyRankingByType](index.md)

# PropertyRankingByType

open class [PropertyRankingByType](index.md) : [PropertyRanking](../-property-ranking/index.md)

Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output, based on [PropertyValueMetaData.returnType](../-property-value-meta-data/return-type.md). Intended mainly to keep known basic types with not too long `toString()`-representations ordered first

E.g. [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html), [java.util.Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html), [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), [java.time.temporal.Temporal](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/Temporal.html) etc.; see [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.md)

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
| [getRank](get-rank.md) | [jvm]<br>open override fun [getRank](get-rank.md)(propertyValueMetaData: [PropertyValueMetaData](../-property-value-meta-data/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [hashCode](../-property-ranking/hash-code.md) | [jvm]<br>open override fun [hashCode](../-property-ranking/hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [register](../-property-ranking/register.md) | [jvm]<br>override fun [register](../-property-ranking/register.md)()<br>Register this concrete [PropertyRankable](../-property-rankable/index.md) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output. |
| [toString](../-property-ranking/to-string.md) | [jvm]<br>open override fun [toString](../-property-ranking/to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
