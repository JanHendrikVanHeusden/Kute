//[kute](../../../index.md)/[nl.kute.asstring.annotation.modify](../index.md)/[AsStringReplace](index.md)/[pattern](pattern.md)

# pattern

[jvm]\
val [pattern](pattern.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

#### Parameters

jvm

| | |
|---|---|
| pattern | The expression to replace, either as regular expression (when [isRegexpPattern](is-regexp-pattern.md) is `true`; default), or as a literal (when [isRegexpPattern](is-regexp-pattern.md) is `false`). Each occurrence will be replaced by [replacement](replacement.md).     * Invalid regular expression will result in an empty String being returned. |
