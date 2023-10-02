---
title: PropertyMeta
---
//[kute](../../../index.html)/[nl.kute.asstring.property.meta](../index.html)/[PropertyMeta](index.html)



# PropertyMeta

interface [PropertyMeta](index.html) : [ClassMeta](../-class-meta/index.html)

Metadata about a property



#### Inheritors


| |
|---|
| [PropertyValueMeta](../-property-value-meta/index.html) |


## Properties


| Name | Summary |
|---|---|
| [isBaseType](is-base-type.html) | [jvm]<br>abstract val [isBaseType](is-base-type.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType](../../nl.kute.asstring.core/is-base-type.html)? |
| [isCharSequence](is-char-sequence.html) | [jvm]<br>abstract val [isCharSequence](is-char-sequence.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html) type? E.g. [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [StringBuilder](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-string-builder/index.html), [java.nio.CharBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/CharBuffer.html), etc. |
| [isCollectionLike](is-collection-like.html) | [jvm]<br>abstract val [isCollectionLike](is-collection-like.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the property's return type a collection-like type, e.g. [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html), [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html), [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html), [CharArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html) and other `*Array`s, etc. |
| [objectClass](../-class-meta/object-class.html) | [jvm]<br>abstract val [objectClass](../-class-meta/object-class.html): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;<br>[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html) that comprises the property |
| [objectClassName](../-class-meta/object-class-name.html) | [jvm]<br>abstract val [objectClassName](../-class-meta/object-class-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Simplified name of the [objectClass](../-class-meta/object-class.html) |
| [packageName](../-class-meta/package-name.html) | [jvm]<br>abstract val [packageName](../-class-meta/package-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |
| [property](property.html) | [jvm]<br>abstract val [property](property.html): [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;<br>The property this [PropertyMeta](index.html) is about |
| [propertyName](property-name.html) | [jvm]<br>abstract val [propertyName](property-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The [property](property.html)'s name |
| [returnType](return-type.html) | [jvm]<br>abstract val [returnType](return-type.html): [KType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-type/index.html)<br>The [kotlin.reflect.KProperty.returnType](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/return-type.html) of the property |


## Functions


| Name | Summary |
|---|---|
| [equals](equals.html) | [jvm]<br>abstract operator override fun [equals](equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.html) | [jvm]<br>abstract override fun [hashCode](hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.html) | [jvm]<br>abstract override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

