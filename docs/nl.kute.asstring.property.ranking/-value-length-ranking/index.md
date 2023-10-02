---
title: ValueLengthRanking
---
//[kute](../../../index.html)/[nl.kute.asstring.property.ranking](../index.html)/[ValueLengthRanking](index.html)



# ValueLengthRanking





@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeIdentityHash = false, includeCompanion = false)



enum [ValueLengthRanking](index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ValueLengthRanking](index.html)&gt; 

[ValueLengthRanking](index.html) provides a somewhat arbitrary classification of value lengths, ranging from [S](-s/index.html) to [XXL](-x-x-l/index.html), each with a [rank](rank.html) and a [lengthRange](length-range.html).



The [lengthRange](length-range.html)s fully cover the [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) values from `0` to [Int.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-a-x_-v-a-l-u-e.html), without overlap.



#### Parameters


jvm

| | |
|---|---|
| rank | The relative weight associated with the [ValueLengthRanking](index.html), ranging up from [S](-s/index.html) to [XXL](-x-x-l/index.html) |
| lengthRange | The range (min inclusive, max inclusive) associated with the [ValueLengthRanking](index.html) |



#### See also


| |
|---|
| [PropertyRankingByStringValueLength](../-property-ranking-by-string-value-length/index.html) |


## Entries


| | |
|---|---|
| [S](-s/index.html) | [jvm]<br>[S](-s/index.html)<br>Ranking for Strings of lengths 0..25 (inclusive) |
| [M](-m/index.html) | [jvm]<br>[M](-m/index.html)<br>Ranking for Strings of lengths `26..50` (inclusive) |
| [L](-l/index.html) | [jvm]<br>[L](-l/index.html)<br>Ranking for Strings of lengths `51..100` (inclusive) |
| [XL](-x-l/index.html) | [jvm]<br>[XL](-x-l/index.html)<br>Ranking for Strings of lengths `101..200` (inclusive) |
| [XXL](-x-x-l/index.html) | [jvm]<br>[XXL](-x-x-l/index.html)<br>Ranking for Strings of lengths `200` or greater |


## Types


| Name | Summary |
|---|---|
| [Companion](-companion/index.html) | [jvm]<br>object [Companion](-companion/index.html) |


## Properties


| Name | Summary |
|---|---|
| [entries](entries.html) | [jvm]<br>val [entries](entries.html): [EnumEntries](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.enums/-enum-entries/index.html)&lt;[ValueLengthRanking](index.html)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [lengthRange](length-range.html) | [jvm]<br>val [lengthRange](length-range.html): [IntRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-int-range/index.html) |
| [name](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-372974862%2FProperties%2F863300109) | [jvm]<br>val [name](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-372974862%2FProperties%2F863300109): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-739389684%2FProperties%2F863300109) | [jvm]<br>val [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-739389684%2FProperties%2F863300109): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [rank](rank.html) | [jvm]<br>val [rank](rank.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |


## Functions


| Name | Summary |
|---|---|
| [toString](to-string.html) | [jvm]<br>open override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [valueOf](value-of.html) | [jvm]<br>fun [valueOf](value-of.html)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [ValueLengthRanking](index.html)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.html) | [jvm]<br>fun [values](values.html)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[ValueLengthRanking](index.html)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |

