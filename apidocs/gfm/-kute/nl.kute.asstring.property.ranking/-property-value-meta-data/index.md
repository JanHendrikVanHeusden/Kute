//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyValueMetaData](index.md)

# PropertyValueMetaData

interface [PropertyValueMetaData](index.md)

Interface for metadata about a property and the property's value

#### Inheritors

| |
|---|
| [PropertyValueMeta](../-property-value-meta/index.md) |

## Properties

| Name | Summary |
|---|---|
| [isBaseType](is-base-type.md) | [jvm]<br>abstract val [isBaseType](is-base-type.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.md)? |
| [isCharSequence](is-char-sequence.md) | [jvm]<br>abstract val [isCharSequence](is-char-sequence.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html) type? E.g. [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [StringBuilder](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/index.html), [java.nio.CharBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/CharBuffer.html), etc. |
| [isCollectionLike](is-collection-like.md) | [jvm]<br>abstract val [isCollectionLike](is-collection-like.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a collection-like type, e.g. [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html), [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html), [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html), [CharArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html) and other `*Array`s, etc. |
| [isNull](is-null.md) | [jvm]<br>abstract val [isNull](is-null.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's value `null`? |
| [objectClassName](object-class-name.md) | [jvm]<br>abstract val [objectClassName](object-class-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Class name of the class that comprises the property |
| [propertyName](property-name.md) | [jvm]<br>abstract val [propertyName](property-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The property's name |
| [returnType](return-type.md) | [jvm]<br>abstract val [returnType](return-type.md): [KType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-type/index.html)<br>The [KProperty.returnType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/return-type.html) of the property |
| [stringValueLength](string-value-length.md) | [jvm]<br>abstract val [stringValueLength](string-value-length.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?<br>The property's value length (as retrieved with the associated object) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>abstract operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>abstract override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [jvm]<br>abstract override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
