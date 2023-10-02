---
title: ClassMeta
---
//[kute](../../../index.html)/[nl.kute.asstring.property.meta](../index.html)/[ClassMeta](index.html)



# ClassMeta

interface [ClassMeta](index.html)

Metadata about a [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)



#### Inheritors


| |
|---|
| [PropertyMeta](../-property-meta/index.html) |


## Properties


| Name | Summary |
|---|---|
| [objectClass](object-class.html) | [jvm]<br>abstract val [objectClass](object-class.html): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;<br>[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html) that comprises the property |
| [objectClassName](object-class-name.html) | [jvm]<br>abstract val [objectClassName](object-class-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>Simplified name of the [objectClass](object-class.html) |
| [packageName](package-name.html) | [jvm]<br>abstract val [packageName](package-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? |


## Functions


| Name | Summary |
|---|---|
| [equals](equals.html) | [jvm]<br>abstract operator override fun [equals](equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.html) | [jvm]<br>abstract override fun [hashCode](hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.html) | [jvm]<br>abstract override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

