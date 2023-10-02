---
title: nl.kute.asstring.namedvalues
---
//[kute](../../index.html)/[nl.kute.asstring.namedvalues](index.html)



# Package-level declarations



## Types


| Name | Summary |
|---|---|
| [AbstractNameValue](-abstract-name-value/index.html) | [jvm]<br>abstract class [AbstractNameValue](-abstract-name-value/index.html)&lt;[V](-abstract-name-value/index.html)&gt; : [NameValue](-name-value/index.html)&lt;[V](-abstract-name-value/index.html)&gt; <br>Abstract base class for classes to provide name / value combinations from various sources. |
| [NamedProp](-named-prop/index.html) | [jvm]<br>class [NamedProp](-named-prop/index.html)&lt;[T](-named-prop/index.html), [V](-named-prop/index.html)&gt;(val property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](-named-prop/index.html)&gt;, obj: [T](-named-prop/index.html)?) : [AbstractNameValue](-abstract-name-value/index.html)&lt;[V](-named-prop/index.html)&gt; , WeakReferencing&lt;[T](-named-prop/index.html)&gt; , PropertyValue&lt;[V](-named-prop/index.html)?&gt; <br>A [NameValue](-name-value/index.html) implementation where the value to be resolved is provided through the given [property](-named-prop/property.html) |
| [NamedSupplier](-named-supplier/index.html) | [jvm]<br>class [NamedSupplier](-named-supplier/index.html)&lt;[V](-named-supplier/index.html)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [ValueSupplier](-value-supplier/index.html)&lt;[V](-named-supplier/index.html)?&gt;) : [AbstractNameValue](-abstract-name-value/index.html)&lt;[V](-named-supplier/index.html)?&gt; <br>A [NameValue](-name-value/index.html) implementation where the value to be resolved is provided by means of a [ValueSupplier](-value-supplier/index.html) lambda. |
| [NamedValue](-named-value/index.html) | [jvm]<br>class [NamedValue](-named-value/index.html)&lt;[V](-named-value/index.html)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [V](-named-value/index.html)?) : [AbstractNameValue](-abstract-name-value/index.html)&lt;[V](-named-value/index.html)?&gt; <br>A [NameValue](-name-value/index.html) implementation where its value is provided directly, as parameter [value](-named-value/value.html). |
| [NameValue](-name-value/index.html) | [jvm]<br>interface [NameValue](-name-value/index.html)&lt;[V](-name-value/index.html)&gt;<br>Interface for classes to provide name / value combinations from various sources. |
| [ValueSupplier](-value-supplier/index.html) | [jvm]<br>typealias [ValueSupplier](-value-supplier/index.html)&lt;[T](-value-supplier/index.html)&gt; = () -&gt; [T](-value-supplier/index.html)?<br>Convenience type alias for `() -> T?` |


## Functions


| Name | Summary |
|---|---|
| [namedProp](named-prop.html) | [jvm]<br>fun &lt;[T](named-prop.html), [V](named-prop.html)&gt; [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](named-prop.html)?&gt;.[namedProp](named-prop.html)(obj: [T](named-prop.html)?): [NamedProp](-named-prop/index.html)&lt;[T](named-prop.html)?, [V](named-prop.html)?&gt;<br>Convenience method to construct a [NamedProp](-named-prop/index.html) |
| [namedSupplier](named-supplier.html) | [jvm]<br>fun &lt;[V](named-supplier.html)&gt; [ValueSupplier](-value-supplier/index.html)&lt;[V](named-supplier.html)?&gt;.[namedSupplier](named-supplier.html)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [NamedSupplier](-named-supplier/index.html)&lt;[V](named-supplier.html)?&gt;<br>Convenience method to construct a [NamedSupplier](-named-supplier/index.html) |

