//[kute](../../../index.md)/[nl.kute.asstring.property.meta](../index.md)/[ClassMeta](index.md)

# ClassMeta

interface [ClassMeta](index.md)

Metadata about a [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)

#### Inheritors

| |
|---|
| [PropertyMeta](../-property-meta/index.md) |

## Properties

| Name | Summary |
|---|---|
| [objectClass](object-class.md) | [jvm]<br>abstract val [objectClass](object-class.md): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;<br>[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html) that comprises the property |
| [objectClassName](object-class-name.md) | [jvm]<br>abstract val [objectClassName](object-class-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Simplified name of the [objectClass](object-class.md) |
| [packageName](package-name.md) | [jvm]<br>abstract val [packageName](package-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>abstract operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>abstract override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [jvm]<br>abstract override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
