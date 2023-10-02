---
title: getRank
---
//[kute](../../../../index.html)/[nl.kute.asstring.property.ranking](../../index.html)/[ValueLengthRanking](../index.html)/[Companion](index.html)/[getRank](get-rank.html)



# getRank



[jvm]\
fun [getRank](get-rank.html)(length: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?): [ValueLengthRanking](../index.html)



Get the [ValueLengthRanking](../index.html) where the [length](get-rank.html) fits into the [ValueLengthRanking.lengthRange](../length-range.html)



#### Return



The [ValueLengthRanking](../index.html) associated with the length;



when [length](get-rank.html) has an unexpected value (`null` or negative), [S](../-s/index.html) is returned



#### Parameters


jvm

| | |
|---|---|
| length | The length to retrieve the [ValueLengthRanking](../index.html) for |




