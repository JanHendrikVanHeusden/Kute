---
title: elementsLimit
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.option](../index.html)/[AsStringOption](index.html)/[elementsLimit](elements-limit.html)



# elementsLimit



[jvm]\
val [elementsLimit](elements-limit.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)



#### Parameters


jvm

| | |
|---|---|
| elementsLimit | limits the number of elements of collection like properties to be represented in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) return value. Default = 50 (by [initialElementsLimit](../../nl.kute.asstring.core.defaults/initial-elements-limit.html))<br>-     Applies to repeating values, e.g. for [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)s, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)s, [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)s -     When capped, the resulting String is appended with ellipsis `...` -     negative values mean: [initialElementsLimit](../../nl.kute.asstring.core.defaults/initial-elements-limit.html) (default value). -     **NB**: The String representation is also capped by [propMaxStringValueLength](prop-max-string-value-length.html) |




