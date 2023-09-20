//[kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringClassOption](index.md)/[includeCompanion](include-companion.md)

# includeCompanion

[jvm]\
val [includeCompanion](include-companion.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

#### Parameters

jvm

| | |
|---|---|
| includeCompanion | Should a companion object (if any) be included in the output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md)? A companion object will be included only if all of these apply:<br>1.     The class that contains the companion object is not private 2.     The companion object is public 3.     The companion object has at least 1 property<br>Default = `false` by [initialIncludeCompanion](../../nl.kute.asstring.core.defaults/initial-include-companion.md) |
