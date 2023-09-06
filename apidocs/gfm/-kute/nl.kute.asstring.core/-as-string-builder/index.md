//[Kute](../../../index.md)/[nl.kute.asstring.core](../index.md)/[AsStringBuilder](index.md)

# AsStringBuilder

class [AsStringBuilder](index.md) : [AsStringProducer](../-as-string-producer/index.md)

Builder for more fine-grained specification of what is included/excluded in the output of [nl.kute.asstring.core.asString](../as-string.md), like additional values, filtering out properties (by reference or by name), etc.

An [AsStringBuilder](index.md) object is constructed by calling static method [asStringBuilder](-companion/as-string-builder.md).

Building it can be done explicitly by calling [AsStringBuilder.build](build.md), or implicitly by the first call to [AsStringBuilder.asString](as-string.md).

- 
   After being built, the [AsStringBuilder](index.md) is effectively immutable.
- 
   Without any further settings (e.g. [exceptProperties](except-properties.md), [withAlsoProperties](with-also-properties.md), alsoNamed etc.), a call to `MyClass.`[AsStringBuilder.asString](as-string.md) produces the same result as `MyClass.`[nl.kute.asstring.core.asString](../as-string.md)

*Usage:*

- 
   Recommended is to declare a property of type [AsStringBuilder](index.md) in your class.
- 
   Alternatively, an [AsStringBuilder](index.md) object can be constructed on the fly (typically in a [toString](to-string.md) method);     this is less efficient though.
- 
   On the [AsStringBuilder](index.md) object, call the `with*`- and `except*`-methods to specify what to include / exclude.
- - 
      Exclusion can also be accomplished (in a more static way & different semantics) with the [nl.kute.asstring.annotation.modify.AsStringOmit](../../nl.kute.asstring.annotation.modify/-as-string-omit/index.md) annotation

E.g.

```kotlin
class MyClass(val myProp1: Prop1Type, val myProp2: Prop2Type, val unimportantProp: Unimportant, myParam: ParamClass) {
  // using static method to construct it
  val asStringBuilder = asStringBuilder()
      .exceptProperties(::unimportantProp)
      .withAlsoNamed(NamedValue("myParam", myParam))
  // ... more code
 }
```

- 
   An [AsStringBuilder](index.md) does not prevent garbage collection of the object it applies to (it uses weak reference).
- 
   For best performance, the [AsStringBuilder](index.md) object (or the [AsStringProducer](../-as-string-producer/index.md) object produced by the [build](build.md) method) may be stored as an instance variable within the class it applies to. This avoids the cost of building it over and over (like when it is part of a log-statement or of a `toString()` method).
- 
   Properties of this class (and any subclasses) are excluded from rendering by [nl.kute.asstring.core.asString](../as-string.md)

#### Parameters

jvm

| | |
|---|---|
| obj | The object to create the [AsStringBuilder](index.md) for (and to ultimately produce the obj.[asString](as-string.md)`()` for) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [jvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [asString](as-string.md) | [jvm]<br>open override fun [asString](as-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [build](build.md) | [jvm]<br>fun [build](build.md)(): [AsStringProducer](../-as-string-producer/index.md)<br>Build this [AsStringBuilder](index.md). |
| [exceptProperties](except-properties.md) | [jvm]<br>fun [exceptProperties](except-properties.md)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.md)<br>Restricts the output by excluding the object's properties listed in [props](except-properties.md). |
| [exceptPropertyNames](except-property-names.md) | [jvm]<br>fun [exceptPropertyNames](except-property-names.md)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.md)<br>Restricts the output by excluding the object's property names listed in [names](except-property-names.md). |
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [withAlsoNamed](with-also-named.md) | [jvm]<br>fun [withAlsoNamed](with-also-named.md)(vararg nameValues: [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)&lt;*&gt;): [AsStringBuilder](index.md)<br>Allows adding additional related [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s, that provide property-like named values. |
| [withAlsoProperties](with-also-properties.md) | [jvm]<br>fun [withAlsoProperties](with-also-properties.md)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.md)<br>Allows adding additional related properties, e.g. member objects, delegates etc. |
| [withOnlyProperties](with-only-properties.md) | [jvm]<br>fun [withOnlyProperties](with-only-properties.md)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.md)<br>Restricts the output to only the object's properties listed in [props](with-only-properties.md). |
| [withOnlyPropertyNames](with-only-property-names.md) | [jvm]<br>fun [withOnlyPropertyNames](with-only-property-names.md)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.md)<br>Restricts the output to only the object's property names listed in [names](with-only-property-names.md). |
