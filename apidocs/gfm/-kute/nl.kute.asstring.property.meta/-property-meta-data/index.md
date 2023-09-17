//[Kute](../../../index.md)/[nl.kute.asstring.property.meta](../index.md)/[PropertyMetaData](index.md)

# PropertyMetaData

[jvm]\
@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(toStringPreference = ToStringPreference.PREFER_TOSTRING)

open class [PropertyMetaData](index.md)(val property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;, val objectClass: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;?) : [PropertyMeta](../-property-meta/index.md)

Metadata about a property

## Constructors

| | |
|---|---|
| [PropertyMetaData](-property-meta-data.md) | [jvm]<br>constructor(property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;, objectClass: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;?) |

## Properties

| Name | Summary |
|---|---|
| [isBaseType](is-base-type.md) | [jvm]<br>open override val [isBaseType](is-base-type.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.md)? |
| [isCharSequence](is-char-sequence.md) | [jvm]<br>open override val [isCharSequence](is-char-sequence.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html) type? E.g. [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [StringBuilder](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/index.html), [java.nio.CharBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/CharBuffer.html), etc. |
| [isCollectionLike](is-collection-like.md) | [jvm]<br>open override val [isCollectionLike](is-collection-like.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a collection-like type, e.g. [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html), [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html), [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html), [CharArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html) and other `*Array`s, etc. |
| [objectClass](object-class.md) | [jvm]<br>override val [objectClass](object-class.md): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;?<br>[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html) that comprises the property |
| [objectClassName](object-class-name.md) | [jvm]<br>open override val [objectClassName](object-class-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Simplified name of the [objectClass](object-class.md) that comprises the property |
| [property](property.md) | [jvm]<br>override val [property](property.md): [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;<br>The property this [PropertyMeta](../-property-meta/index.md) is about |
| [propertyName](property-name.md) | [jvm]<br>open override val [propertyName](property-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The [property](property.md)'s name |
| [returnType](return-type.md) | [jvm]<br>override val [returnType](return-type.md): [KType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-type/index.html)<br>The [kotlin.reflect.KProperty.returnType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/return-type.html) of the property |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [jvm]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [jvm]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
