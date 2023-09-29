//[kute](../../../index.md)/[nl.kute.asstring.property.meta](../index.md)/[PropertyValueMeta](index.md)

# PropertyValueMeta

[jvm]\
interface [PropertyValueMeta](index.md) : [PropertyMeta](../-property-meta/index.md)

Metadata about a property and the property's value

## Properties

| Name | Summary |
|---|---|
| [isBaseType](../-property-meta/is-base-type.md) | [jvm]<br>abstract val [isBaseType](../-property-meta/is-base-type.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.md)? |
| [isCharSequence](../-property-meta/is-char-sequence.md) | [jvm]<br>abstract val [isCharSequence](../-property-meta/is-char-sequence.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html) type? E.g. [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [StringBuilder](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/index.html), [java.nio.CharBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/CharBuffer.html), etc. |
| [isCollectionLike](../-property-meta/is-collection-like.md) | [jvm]<br>abstract val [isCollectionLike](../-property-meta/is-collection-like.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a collection-like type, e.g. [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html), [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html), [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html), [CharArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html) and other `*Array`s, etc. |
| [isNull](is-null.md) | [jvm]<br>abstract val [isNull](is-null.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the value of the actual instance of this property `null`? |
| [objectClass](../-class-meta/object-class.md) | [jvm]<br>abstract val [objectClass](../-class-meta/object-class.md): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;<br>[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html) that comprises the property |
| [objectClassName](../-class-meta/object-class-name.md) | [jvm]<br>abstract val [objectClassName](../-class-meta/object-class-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Simplified name of the [objectClass](../-class-meta/object-class.md) |
| [packageName](../-class-meta/package-name.md) | [jvm]<br>abstract val [packageName](../-class-meta/package-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |
| [property](../-property-meta/property.md) | [jvm]<br>abstract val [property](../-property-meta/property.md): [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;<br>The property this [PropertyMeta](../-property-meta/index.md) is about |
| [propertyName](../-property-meta/property-name.md) | [jvm]<br>abstract val [propertyName](../-property-meta/property-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The [property](../-property-meta/property.md)'s name |
| [returnType](../-property-meta/return-type.md) | [jvm]<br>abstract val [returnType](../-property-meta/return-type.md): [KType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-type/index.html)<br>The [kotlin.reflect.KProperty.returnType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/return-type.html) of the property |
| [stringValueLength](string-value-length.md) | [jvm]<br>abstract val [stringValueLength](string-value-length.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?<br>The value of the actual instance of this property |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>abstract operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>abstract override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [jvm]<br>abstract override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
