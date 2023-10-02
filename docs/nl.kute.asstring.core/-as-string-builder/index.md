---
title: AsStringBuilder
---
//[kute](../../../index.html)/[nl.kute.asstring.core](../index.html)/[AsStringBuilder](index.html)



# AsStringBuilder

class [AsStringBuilder](index.html) : [AsStringProducer](../-as-string-producer/index.html)

Builder for more fine-grained specification of what is included/excluded in the output of [nl.kute.asstring.core.asString](../as-string.html), like additional values, filtering out properties (by reference or by name), etc.



An [AsStringBuilder](index.html) object is constructed by calling static method [asStringBuilder](-companion/as-string-builder.html).



Building it can be done explicitly by calling [AsStringBuilder.build](build.html), or implicitly by the first call to [AsStringBuilder.asString](as-string.html).



- 
   After being built, the [AsStringBuilder](index.html) is effectively immutable.
- 
   Without any further settings (e.g. [exceptProperties](except-properties.html), [withAlsoProperties](with-also-properties.html), alsoNamed etc.), a call to `MyClass.`[AsStringBuilder.asString](as-string.html) produces the same result as `MyClass.`[nl.kute.asstring.core.asString](../as-string.html)




*Usage:*



- 
   Recommended is to declare a property of type [AsStringBuilder](index.html) in your class.
- 
   Alternatively, an [AsStringBuilder](index.html) object can be constructed on the fly (typically in a [toString](to-string.html) method);     this is less efficient though.
- 
   On the [AsStringBuilder](index.html) object, call the `with*`- and `except*`-methods to specify what to include / exclude.
- - 
      Exclusion can also be accomplished (in a more static way & different semantics) with the [nl.kute.asstring.annotation.modify.AsStringOmit](../../nl.kute.asstring.annotation.modify/-as-string-omit/index.html) annotation




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
   An [AsStringBuilder](index.html) does not prevent garbage collection of the object it applies to (it uses weak reference).
- 
   For best performance, the [AsStringBuilder](index.html) object (or the [AsStringProducer](../-as-string-producer/index.html) object produced by the [build](build.html) method) may be stored as an instance variable within the class it applies to. This avoids the cost of building it over and over (like when it is part of a log-statement or of a `toString()` method).
- 
   Properties of this class (and any subclasses) are excluded from rendering by [nl.kute.asstring.core.asString](../as-string.html)




#### Parameters


jvm

| | |
|---|---|
| obj | The object to create the [AsStringBuilder](index.html) for (and to ultimately produce the obj.[asString](as-string.html)`()` for) |



## Types


| Name | Summary |
|---|---|
| [Companion](-companion/index.html) | [jvm]<br>object [Companion](-companion/index.html) |


## Functions


| Name | Summary |
|---|---|
| [asString](as-string.html) | [jvm]<br>open override fun [asString](as-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [build](build.html) | [jvm]<br>fun [build](build.html)(): [AsStringProducer](../-as-string-producer/index.html)<br>Build this [AsStringBuilder](index.html). |
| [exceptProperties](except-properties.html) | [jvm]<br>fun [exceptProperties](except-properties.html)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.html)<br>Restricts the output by excluding the object's properties listed in [props](except-properties.html). |
| [exceptPropertyNames](except-property-names.html) | [jvm]<br>fun [exceptPropertyNames](except-property-names.html)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.html)<br>Restricts the output by excluding the object's property names listed in [names](except-property-names.html). |
| [toString](to-string.html) | [jvm]<br>open override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [withAlsoNamed](with-also-named.html) | [jvm]<br>fun [withAlsoNamed](with-also-named.html)(vararg nameValues: [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)&lt;*&gt;): [AsStringBuilder](index.html)<br>Allows adding additional related [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s, that provide property-like named values. |
| [withAlsoProperties](with-also-properties.html) | [jvm]<br>fun [withAlsoProperties](with-also-properties.html)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.html)<br>Allows adding additional related properties, e.g. member objects, delegates etc. |
| [withOnlyProperties](with-only-properties.html) | [jvm]<br>fun [withOnlyProperties](with-only-properties.html)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.html)<br>Restricts the output to only the object's properties listed in [props](with-only-properties.html). |
| [withOnlyPropertyNames](with-only-property-names.html) | [jvm]<br>fun [withOnlyPropertyNames](with-only-property-names.html)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.html)<br>Restricts the output to only the object's property names listed in [names](with-only-property-names.html). |

