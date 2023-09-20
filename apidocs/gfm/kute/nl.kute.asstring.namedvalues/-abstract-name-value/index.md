//[kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[AbstractNameValue](index.md)

# AbstractNameValue

abstract class [AbstractNameValue](index.md)&lt;[V](index.md)&gt; : [NameValue](../-name-value/index.md)&lt;[V](index.md)&gt; 

Abstract base class for classes to provide name / value combinations from various sources.

- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md)

#### Inheritors

| |
|---|
| [NamedProp](../-named-prop/index.md) |
| [NamedSupplier](../-named-supplier/index.md) |
| [NamedValue](../-named-value/index.md) |

## Constructors

| | |
|---|---|
| [AbstractNameValue](-abstract-name-value.md) | [jvm]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [name](../-name-value/name.md) | [jvm]<br>abstract val [name](../-name-value/name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name to identify this [NameValue](../-name-value/index.md) |
| [value](../-name-value/value.md) | [jvm]<br>abstract val [value](../-name-value/value.md): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?<br>The value of this [NameValue](../-name-value/index.md) |
