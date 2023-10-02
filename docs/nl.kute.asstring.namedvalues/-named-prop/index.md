---
title: NamedProp
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[NamedProp](index.html)



# NamedProp

class [NamedProp](index.html)&lt;[T](index.html), [V](index.html)&gt;(val property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](index.html)&gt;, obj: [T](index.html)?) : [AbstractNameValue](../-abstract-name-value/index.html)&lt;[V](index.html)&gt; , WeakReferencing&lt;[T](index.html)&gt; , PropertyValue&lt;[V](index.html)?&gt; 

A [NameValue](../-name-value/index.html) implementation where the value to be resolved is provided through the given [property](property.html)



- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html)
- 
   [NamedProp](index.html) is a wrapper for a [property](property.html), with a weak reference [objectReference](object-reference.html) to the object the [property](property.html) is to be associated with.




[NamedProp](index.html) is intended for situations where the [property](property.html)'s value needs to be evaluated on each access, where:



- 
   All annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) are taken into account (this sets it apart it from [NamedValue](../-named-value/index.html) and [NamedProp](index.html))
- 
   Both state change and reassignment are reflected




E.g.:

```kotlin
class MyClass(var aDate: Date = Date())
val myObject1 = MyClass(Date().also { it.time = it.time - 86_400_000 }) // yesterday
val myObject2 = MyClass(myObject1.aDate) // also yesterday

val myClass1NamedProp = NamedProp(MyClass::aDate, myObject1).also { println(it.value) /* yesterday */}
val myObject2NamedProp = NamedProp(myObject2::aDate, myObject2).also { println(it.value) /* yesterday */}

myObject1.aDate = Date() // today
myObject2.aDate = myObject1.aDate // today
println(myClass1NamedProp.value) // today
println(myObject2NamedProp.value) // today
```


- 
   Usage of [NamedProp](index.html) in a pre-built [nl.kute.asstring.core.AsStringBuilder](../../nl.kute.asstring.core/-as-string-builder/index.html) is good practice, as reassignment is reflected
- 
   All annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) are taken in account: these that are part of the supplied object as well as annotations in the outer context (e.g. in the above example: annotations within the class that holds the `aDate` variable).
- 
   The object the property is associated with is weakly referenced only, so the [NamedProp](index.html) does not prevent garbage collection of the [property](property.html) or it's supplied value.




The property is of the more general type [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html) (specifically, not [kotlin.reflect.KProperty0](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property0/index.html) or [kotlin.reflect.KProperty1](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property1/index.html)). [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html) allows more flexibility in values to be handled, but it also allows incoherent calls, e.g.: `NamedProp(objOfaClass, AnotherClass::someProperty)`



[NamedProp](index.html) is resilient for such incoherent calls, but the resulting [value](value.html) may be `null`, and a log message may be issued on construction of the [NamedProp](index.html).



#### Parameters


jvm

| | |
|---|---|
| obj | The object on which the [property](property.html) is to be resolved |
| property | The property to retrieve the value with. |



#### See also


| |
|---|
| [namedProp](../named-prop.html) |
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html) |


## Constructors


| | |
|---|---|
| [NamedProp](-named-prop.html) | [jvm]<br>constructor(property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](index.html)&gt;, obj: [T](index.html)?) |


## Properties


| Name | Summary |
|---|---|
| [asStringAffectingAnnotations](as-string-affecting-annotations.html) | [jvm]<br>open override val [asStringAffectingAnnotations](as-string-affecting-annotations.html): [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt;<br>The annotations that may affect the [property](property.html)'s String representation |
| [name](name.html) | [jvm]<br>open override val [name](name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name to identify this [NameValue](../-name-value/index.html) |
| [objectReference](object-reference.html) | [jvm]<br>open override val [objectReference](object-reference.html): [ObjectWeakReference](../../nl.kute.asstring.weakreference/-object-weak-reference/index.html)&lt;[T](index.html)?&gt;<br>The [ObjectWeakReference](../../nl.kute.asstring.weakreference/-object-weak-reference/index.html) referencing the object that is to provide the value (i.e., that will eventually produce the [NamedValue.value](../-named-value/value.html)) |
| [property](property.html) | [jvm]<br>open override val [property](property.html): [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](index.html)&gt; |
| [propertyCoherentWithObject](property-coherent-with-object.html) | [jvm]<br>val [propertyCoherentWithObject](property-coherent-with-object.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Indicates whether the [property](property.html)'s value can be resolved on the given object |
| [value](value.html) | [jvm]<br>override val [value](value.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>The [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) representation of the [property](property.html) value, with annotations taken into account |

