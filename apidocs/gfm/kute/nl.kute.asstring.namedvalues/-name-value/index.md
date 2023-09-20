//[kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[NameValue](index.md)

# NameValue

interface [NameValue](index.md)&lt;[V](index.md)&gt;

Interface for classes to provide name / value combinations from various sources.

- 
   This interface is **sealed**, so it can not be implemented directly by external classes. Please extend [AbstractNameValue](../-abstract-name-value/index.md) instead.
- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md)

#### See also

| |
|---|
| [AbstractNameValue](../-abstract-name-value/index.md) |
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md) |

#### Inheritors

| |
|---|
| [AbstractNameValue](../-abstract-name-value/index.md) |

## Properties

| Name | Summary |
|---|---|
| [name](name.md) | [jvm]<br>abstract val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name to identify this [NameValue](index.md) |
| [value](value.md) | [jvm]<br>abstract val [value](value.md): [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?<br>The value of this [NameValue](index.md) |
