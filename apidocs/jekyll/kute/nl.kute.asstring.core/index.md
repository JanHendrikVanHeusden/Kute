---
title: nl.kute.asstring.core
---
//[kute](../../index.html)/[nl.kute.asstring.core](index.html)



# Package-level declarations



## Types


| Name | Summary |
|---|---|
| [AsStringBuilder](-as-string-builder/index.html) | [jvm]<br>class [AsStringBuilder](-as-string-builder/index.html) : [AsStringProducer](-as-string-producer/index.html)<br>Builder for more fine-grained specification of what is included/excluded in the output of [nl.kute.asstring.core.asString](as-string.html), like additional values, filtering out properties (by reference or by name), etc. |
| [AsStringProducer](-as-string-producer/index.html) | [jvm]<br>abstract class [AsStringProducer](-as-string-producer/index.html)<br>Abstract base class for implementing classes that want to expose an [asString](-as-string-producer/as-string.html) method |
| [ClassMetaFilter](-class-meta-filter/index.html) | [jvm]<br>typealias [ClassMetaFilter](-class-meta-filter/index.html) = ([ClassMeta](../nl.kute.asstring.property.meta/-class-meta/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Alias for type `(`[ClassMeta](../nl.kute.asstring.property.meta/-class-meta/index.html)`)` ->[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [PropertyMetaFilter](-property-meta-filter/index.html) | [jvm]<br>typealias [PropertyMetaFilter](-property-meta-filter/index.html) = ([PropertyMeta](../nl.kute.asstring.property.meta/-property-meta/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Alias for type `(`[PropertyMeta](../nl.kute.asstring.property.meta/-property-meta/index.html)`)` ->[Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |


## Properties


| Name | Summary |
|---|---|
| [stringJoinMaxCount](string-join-max-count.html) | [jvm]<br>const val [stringJoinMaxCount](string-join-max-count.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1000<br>Limits the overall number of properties values or [nl.kute.asstring.namedvalues.NamedValue](../nl.kute.asstring.namedvalues/-named-value/index.html)s to be joined together |


## Functions


| Name | Summary |
|---|---|
| [asString](as-string.html) | [jvm]<br>fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[asString](as-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Mimics the format of Kotlin data class's [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) method (insofar not modified by annotations).<br>[jvm]<br>fun &lt;[T](as-string.html)&gt; [T](as-string.html).[asString](as-string.html)(vararg props: [KProperty1](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property1/index.html)&lt;[T](as-string.html), *&gt;): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Convenient [asString](as-string.html) overload, with only the provided properties. |
| [isBaseType](is-base-type.html) | [jvm]<br>fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[isBaseType](is-base-type.html)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the object's type considered a base type? |

