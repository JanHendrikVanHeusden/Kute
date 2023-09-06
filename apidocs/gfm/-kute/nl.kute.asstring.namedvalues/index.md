//[Kute](../../index.md)/[nl.kute.asstring.namedvalues](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [AbstractNameValue](-abstract-name-value/index.md) | [jvm]<br>abstract class [AbstractNameValue](-abstract-name-value/index.md)&lt;[V](-abstract-name-value/index.md)&gt; : [NameValue](-name-value/index.md)&lt;[V](-abstract-name-value/index.md)&gt; <br>Abstract base class for classes to provide name / value combinations from various sources. |
| [NamedProp](-named-prop/index.md) | [jvm]<br>class [NamedProp](-named-prop/index.md)&lt;[T](-named-prop/index.md), [V](-named-prop/index.md)&gt;(val property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](-named-prop/index.md)&gt;, obj: [T](-named-prop/index.md)?) : [AbstractNameValue](-abstract-name-value/index.md)&lt;[V](-named-prop/index.md)&gt; , WeakReferencing&lt;[T](-named-prop/index.md)&gt; , PropertyValue&lt;[V](-named-prop/index.md)?&gt; <br>A [NameValue](-name-value/index.md) implementation where the value to be resolved is provided through the given [property](-named-prop/property.md) |
| [NamedSupplier](-named-supplier/index.md) | [jvm]<br>class [NamedSupplier](-named-supplier/index.md)&lt;[V](-named-supplier/index.md)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [Supplier](index.md#-531561431%2FClasslikes%2F-1216412040)&lt;[V](-named-supplier/index.md)?&gt;) : [AbstractNameValue](-abstract-name-value/index.md)&lt;[V](-named-supplier/index.md)?&gt; <br>A [NameValue](-name-value/index.md) implementation where the value to be resolved is provided by means of a [Supplier](index.md#-531561431%2FClasslikes%2F-1216412040) lambda. |
| [NamedValue](-named-value/index.md) | [jvm]<br>class [NamedValue](-named-value/index.md)&lt;[V](-named-value/index.md)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [V](-named-value/index.md)?) : [AbstractNameValue](-abstract-name-value/index.md)&lt;[V](-named-value/index.md)?&gt; <br>A [NameValue](-name-value/index.md) implementation where its value is provided directly, as parameter [value](-named-value/value.md). |
| [NameValue](-name-value/index.md) | [jvm]<br>interface [NameValue](-name-value/index.md)&lt;[V](-name-value/index.md)&gt;<br>Interface for classes to provide name / value combinations from various sources. |
| [Supplier](index.md#-531561431%2FClasslikes%2F-1216412040) | [jvm]<br>typealias [Supplier](index.md#-531561431%2FClasslikes%2F-1216412040)&lt;[T](index.md#-531561431%2FClasslikes%2F-1216412040)&gt; = () -&gt; [T](index.md#-531561431%2FClasslikes%2F-1216412040)?<br>Convenience type alias for `() -> T?` |

## Functions

| Name | Summary |
|---|---|
| [namedProp](named-prop.md) | [jvm]<br>fun &lt;[T](named-prop.md), [V](named-prop.md)&gt; [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](named-prop.md)?&gt;.[namedProp](named-prop.md)(obj: [T](named-prop.md)?): [NamedProp](-named-prop/index.md)&lt;[T](named-prop.md)?, [V](named-prop.md)?&gt;<br>Convenience method to construct a [NamedProp](-named-prop/index.md) |
| [namedSupplier](named-supplier.md) | [jvm]<br>fun &lt;[V](named-supplier.md)&gt; [Supplier](index.md#-531561431%2FClasslikes%2F-1216412040)&lt;[V](named-supplier.md)?&gt;.[namedSupplier](named-supplier.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [NameValue](-name-value/index.md)&lt;[V](named-supplier.md)?&gt;<br>Convenience method to construct a [NamedSupplier](-named-supplier/index.md) |
