---
title: NameValue
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[NameValue](index.html)



# NameValue

interface [NameValue](index.html)&lt;[V](index.html)&gt;

Interface for classes to provide name / value combinations from various sources.



- 
   This interface is **sealed**, so it can not be implemented directly by external classes. Please extend [AbstractNameValue](../-abstract-name-value/index.html) instead.
- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html)




#### See also


| |
|---|
| [AbstractNameValue](../-abstract-name-value/index.html) |
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html) |


#### Inheritors


| |
|---|
| [AbstractNameValue](../-abstract-name-value/index.html) |


## Properties


| Name | Summary |
|---|---|
| [name](name.html) | [jvm]<br>abstract val [name](name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name to identify this [NameValue](index.html) |
| [value](value.html) | [jvm]<br>abstract val [value](value.html): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?<br>The value of this [NameValue](index.html) |

