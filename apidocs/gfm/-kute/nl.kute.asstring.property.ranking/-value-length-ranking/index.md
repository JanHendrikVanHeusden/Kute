//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[ValueLengthRanking](index.md)

# ValueLengthRanking

@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(includeIdentityHash = false, includeCompanion = false)

enum [ValueLengthRanking](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ValueLengthRanking](index.md)&gt; 

[ValueLengthRanking](index.md) provides a somewhat arbitrary classification of value lengths, ranging from [S](-s/index.md) to [XXL](-x-x-l/index.md), each with a [rank](rank.md) and a [lengthRange](length-range.md).

The [lengthRange](length-range.md)s fully cover the [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) values from `0` to [Int.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-a-x_-v-a-l-u-e.html), without overlap.

#### Parameters

jvm

| | |
|---|---|
| rank | The relative weight associated with the [ValueLengthRanking](index.md), ranging up from [S](-s/index.md) to [XXL](-x-x-l/index.md) |
| lengthRange | The range (min inclusive, max inclusive) associated with the [ValueLengthRanking](index.md) |

#### See also

| |
|---|
| [PropertyRankingByStringValueLength](../-property-ranking-by-string-value-length/index.md) |

## Entries

| | |
|---|---|
| [S](-s/index.md) | [jvm]<br>[S](-s/index.md)<br>Ranking for Strings of lengths 0..25 (inclusive) |
| [M](-m/index.md) | [jvm]<br>[M](-m/index.md)<br>Ranking for Strings of lengths `26..50` (inclusive) |
| [L](-l/index.md) | [jvm]<br>[L](-l/index.md)<br>Ranking for Strings of lengths `51..100` (inclusive) |
| [XL](-x-l/index.md) | [jvm]<br>[XL](-x-l/index.md)<br>Ranking for Strings of lengths `101..200` (inclusive) |
| [XXL](-x-x-l/index.md) | [jvm]<br>[XXL](-x-x-l/index.md)<br>Ranking for Strings of lengths `200` or greater |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [entries](entries.md) | [jvm]<br>val [entries](entries.md): [EnumEntries](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.enums/-enum-entries/index.html)&lt;[ValueLengthRanking](index.md)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [lengthRange](length-range.md) | [jvm]<br>val [lengthRange](length-range.md): [IntRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-int-range/index.html) |
| [name](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040) | [jvm]<br>val [name](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040) | [jvm]<br>val [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [rank](rank.md) | [jvm]<br>val [rank](rank.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [valueOf](value-of.md) | [jvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [ValueLengthRanking](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [jvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[ValueLengthRanking](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
