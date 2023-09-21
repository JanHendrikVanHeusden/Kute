//[kute](../../index.md)/[nl.kute.asstring.core](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [AsStringBuilder](-as-string-builder/index.md) | [jvm]<br>class [AsStringBuilder](-as-string-builder/index.md) : [AsStringProducer](-as-string-producer/index.md)<br>Builder for more fine-grained specification of what is included/excluded in the output of [nl.kute.asstring.core.asString](as-string.md), like additional values, filtering out properties (by reference or by name), etc. |
| [AsStringProducer](-as-string-producer/index.md) | [jvm]<br>abstract class [AsStringProducer](-as-string-producer/index.md)<br>Abstract base class for implementing classes that want to expose an [asString](-as-string-producer/as-string.md) method |

## Properties

| Name | Summary |
|---|---|
| [stringJoinMaxCount](string-join-max-count.md) | [jvm]<br>const val [stringJoinMaxCount](string-join-max-count.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1000<br>Limits the overall number of properties values or [nl.kute.asstring.namedvalues.NamedValue](../nl.kute.asstring.namedvalues/-named-value/index.md)s to be joined together |

## Functions

| Name | Summary |
|---|---|
| [asString](as-string.md) | [jvm]<br>fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[asString](as-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Mimics the format of Kotlin data class's [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) method (insofar not modified by annotations).<br>[jvm]<br>fun &lt;[T](as-string.md)&gt; [T](as-string.md).[asString](as-string.md)(vararg props: [KProperty1](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property1/index.html)&lt;[T](as-string.md), *&gt;): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Convenient [asString](as-string.md) overload, with only the provided properties. |
| [isBaseType](is-base-type.md) | [jvm]<br>fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?.[isBaseType](is-base-type.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is the object's type considered a base type? |