//[Kute](../../index.md)/[nl.kute.asstring.core](index.md)/[asString](as-string.md)

# asString

[jvm]\
fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[asString](as-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

Mimics the format of Kotlin data class's [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) method (insofar not modified by annotations).

- 
   Super-class properties are included
- 
   Private properties are included (but not in subclasses)
- 
   String value of individual properties is capped at 500; see [AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.md) to override the default.
- 
   Want more control of what is included or not? See [AsStringBuilder](-as-string-builder/index.md)

The output of [asString](as-string.md) can be controlled / modified by using these annotations:

- 
   `@`[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.md) to control what is included, and ordering of properties (at class level)
- 
   `@`[AsStringOption](../nl.kute.asstring.annotation.option/-as-string-option/index.md) to control how properties are rendered (e.g. delimiters, max. lengths, how to represent `null`s, etc.)
- 
   `@`[nl.kute.asstring.annotation.modify.AsStringOmit](../nl.kute.asstring.annotation.modify/-as-string-omit/index.md) to exclude individual properties from [asString](as-string.md) output
- 
   `@`[nl.kute.asstring.annotation.modify.AsStringMask](../nl.kute.asstring.annotation.modify/-as-string-mask/index.md) to mask properties, typically to keep private or sensitive data out of the [asString](as-string.md) output
- 
   `@`[nl.kute.asstring.annotation.modify.AsStringReplace](../nl.kute.asstring.annotation.modify/-as-string-replace/index.md)
- 
   `@`[nl.kute.asstring.annotation.modify.AsStringHash](../nl.kute.asstring.annotation.modify/-as-string-hash/index.md)

#### Return

A String representation of the receiver object, including class name and property names + values; adhering to related annotations

#### See also

| |
|---|
| [AsStringBuilder](-as-string-builder/index.md) |

[jvm]\
fun &lt;[T](as-string.md)&gt; [T](as-string.md).[asString](as-string.md)(vararg props: [KProperty1](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property1/index.html)&lt;[T](as-string.md), *&gt;): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

Convenient [asString](as-string.md) overload, with only the provided properties.

- 
   See also the KDoc of [asString](as-string.md) for comprehensive explanation, e.g. on usage of annotations.
- 
   Want more control of what is included or not? See [AsStringBuilder](-as-string-builder/index.md)

This method builds an [AsStringBuilder](-as-string-builder/index.md) object on each call, which is marginally less efficient than defining a reusable immutable [AsStringBuilder](-as-string-builder/index.md) as a class instance value.

#### Return

A String representation of the receiver object, including class name and property names + values; adhering to related annotations

#### See also

| |
|---|
| [asString](as-string.md) |
| [AsStringBuilder](-as-string-builder/index.md) |
