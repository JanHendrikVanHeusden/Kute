//[kute](../../../../index.md)/[nl.kute.asstring.property.ranking](../../index.md)/[ValueLengthRanking](../index.md)/[Companion](index.md)/[getRank](get-rank.md)

# getRank

[jvm]\
fun [getRank](get-rank.md)(length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?): [ValueLengthRanking](../index.md)

Get the [ValueLengthRanking](../index.md) where the [length](get-rank.md) fits into the [ValueLengthRanking.lengthRange](../length-range.md)

#### Return

The [ValueLengthRanking](../index.md) associated with the length;

when [length](get-rank.md) has an unexpected value (`null` or negative), [S](../-s/index.md) is returned

#### Parameters

jvm

| | |
|---|---|
| length | The length to retrieve the [ValueLengthRanking](../index.md) for |
