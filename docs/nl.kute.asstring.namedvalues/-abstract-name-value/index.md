---
title: AbstractNameValue
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[AbstractNameValue](index.html)



# AbstractNameValue

abstract class [AbstractNameValue](index.html)&lt;[V](index.html)&gt; : [NameValue](../-name-value/index.html)&lt;[V](index.html)&gt; 

Abstract base class for classes to provide name / value combinations from various sources.



- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html)




#### Inheritors


| |
|---|
| [NamedProp](../-named-prop/index.html) |
| [NamedSupplier](../-named-supplier/index.html) |
| [NamedValue](../-named-value/index.html) |


## Constructors


| | |
|---|---|
| [AbstractNameValue](-abstract-name-value.html) | [jvm]<br>constructor() |


## Properties


| Name | Summary |
|---|---|
| [name](../-name-value/name.html) | [jvm]<br>abstract val [name](../-name-value/name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name to identify this [NameValue](../-name-value/index.html) |
| [value](../-name-value/value.html) | [jvm]<br>abstract val [value](../-name-value/value.html): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?<br>The value of this [NameValue](../-name-value/index.html) |

