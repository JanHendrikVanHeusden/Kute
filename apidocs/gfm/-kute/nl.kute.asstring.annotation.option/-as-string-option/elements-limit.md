//[Kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringOption](index.md)/[elementsLimit](elements-limit.md)

# elementsLimit

[jvm]\
val [elementsLimit](elements-limit.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

#### Parameters

jvm

| | |
|---|---|
| elementsLimit | limits the number of elements of collection like properties to be represented in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) return value. Default = 50 (by [initialElementsLimit](../../nl.kute.asstring.core.defaults/initial-elements-limit.md))<br>-     Applies to repeating values, e.g. for [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)s, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)s, [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)s -     When capped, the resulting String is appended with ellipsis `...` -     negative values mean: [initialElementsLimit](../../nl.kute.asstring.core.defaults/initial-elements-limit.md) (default value). -     **NB**: The String representation is also capped by [propMaxStringValueLength](prop-max-string-value-length.md) |
