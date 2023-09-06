//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyValueMeta](index.md)

# PropertyValueMeta

[jvm]\
@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(toStringPreference = ToStringPreference.PREFER_TOSTRING)

class [PropertyValueMeta](index.md)(propertyValue: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, objectClass: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;, val stringValueLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?) : [PropertyValueMetaData](../-property-value-meta-data/index.md)

Metadata about a property and the property's value

## Constructors

| | |
|---|---|
| [PropertyValueMeta](-property-value-meta.md) | [jvm]<br>constructor(propertyValue: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, objectClass: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;, stringValueLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?) |

## Properties

| Name | Summary |
|---|---|
| [isBaseType](is-base-type.md) | [jvm]<br>open override val [isBaseType](is-base-type.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.md)? |
| [isCharSequence](is-char-sequence.md) | [jvm]<br>open override val [isCharSequence](is-char-sequence.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html) type? E.g. [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [StringBuilder](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/index.html), [java.nio.CharBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/CharBuffer.html), etc. |
| [isCollectionLike](is-collection-like.md) | [jvm]<br>open override val [isCollectionLike](is-collection-like.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a collection-like type, e.g. [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html), [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html), [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html), [CharArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html) and other `*Array`s, etc. |
| [isNull](is-null.md) | [jvm]<br>open override val [isNull](is-null.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's value `null`? |
| [objectClassName](object-class-name.md) | [jvm]<br>open override val [objectClassName](object-class-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Class name of the class that comprises the property |
| [propertyName](property-name.md) | [jvm]<br>open override val [propertyName](property-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The property's name |
| [returnType](return-type.md) | [jvm]<br>open override val [returnType](return-type.md): [KType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-type/index.html)<br>The [KProperty.returnType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/return-type.html) of the property |
| [stringValueLength](string-value-length.md) | [jvm]<br>open override val [stringValueLength](string-value-length.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?<br>The property's value length (as retrieved with the associated object) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
