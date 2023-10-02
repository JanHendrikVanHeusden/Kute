---
title: pattern
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.modify](../index.html)/[AsStringReplace](index.html)/[pattern](pattern.html)



# pattern



[jvm]\
val [pattern](pattern.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)



#### Parameters


jvm

| | |
|---|---|
| pattern | The expression to replace, either as regular expression (when [isRegexpPattern](is-regexp-pattern.html) is `true`; default), or as a literal (when [isRegexpPattern](is-regexp-pattern.html) is `false`). Each occurrence will be replaced by [replacement](replacement.html).     * Invalid regular expression will result in an empty String being returned. |




