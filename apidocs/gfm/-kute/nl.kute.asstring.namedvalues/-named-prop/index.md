//[Kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[NamedProp](index.md)

# NamedProp

class [NamedProp](index.md)&lt;[T](index.md), [V](index.md)&gt;(val property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](index.md)&gt;, obj: [T](index.md)?) : [AbstractNameValue](../-abstract-name-value/index.md)&lt;[V](index.md)&gt; , WeakReferencing&lt;[T](index.md)&gt; , PropertyValue&lt;[V](index.md)?&gt; 

A [NameValue](../-name-value/index.md) implementation where the value to be resolved is provided through the given [property](property.md)

- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md)
- 
   [NamedProp](index.md) is a wrapper for a [property](property.md), with a weak reference [objectReference](object-reference.md) to the object the [property](property.md) is to be associated with.

[NamedProp](index.md) is intended for situations where the [property](property.md)'s value needs to be evaluated on each access, where:

- 
   All annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) are taken into account (this sets it apart it from [NamedValue](../-named-value/index.md) and [NamedProp](index.md))
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
   Usage of [NamedProp](index.md) in a pre-built [nl.kute.asstring.core.AsStringBuilder](../../nl.kute.asstring.core/-as-string-builder/index.md) is good practice, as reassignment is reflected
- 
   All annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) are taken in account: these that are part of the supplied object as well as annotations in the outer context (e.g. in the above example: annotations within the class that holds the `aDate` variable).
- 
   The object the property is associated with is weakly referenced only, so the [NamedProp](index.md) does not prevent garbage collection of the [property](property.md) or it's supplied value.

The property is of the more general type [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html) (specifically, not [kotlin.reflect.KProperty0](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property0/index.html) or [kotlin.reflect.KProperty1](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property1/index.html)). [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html) allows more flexibility in values to be handled, but it also allows incoherent calls, e.g.: `NamedProp(objOfaClass, AnotherClass::someProperty)`

[NamedProp](index.md) is resilient for such incoherent calls, but the resulting [value](value.md) may be `null`, and a log message may be issued on construction of the [NamedProp](index.md).

#### Parameters

jvm

| | |
|---|---|
| obj | The object on which the [property](property.md) is to be resolved |
| property | The property to retrieve the value with. |

#### See also

| |
|---|
| [namedProp](../named-prop.md) |
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md) |

## Constructors

| | |
|---|---|
| [NamedProp](-named-prop.md) | [jvm]<br>constructor(property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](index.md)&gt;, obj: [T](index.md)?) |

## Properties

| Name | Summary |
|---|---|
| [asStringAffectingAnnotations](as-string-affecting-annotations.md) | [jvm]<br>open override val [asStringAffectingAnnotations](as-string-affecting-annotations.md): [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt;<br>The annotations that may affect the [property](property.md)'s String representation |
| [name](name.md) | [jvm]<br>open override val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name to identify this [NameValue](../-name-value/index.md) |
| [objectReference](object-reference.md) | [jvm]<br>open override val [objectReference](object-reference.md): [ObjectWeakReference](../../nl.kute.asstring.weakreference/-object-weak-reference/index.md)&lt;[T](index.md)?&gt;<br>The [ObjectWeakReference](../../nl.kute.asstring.weakreference/-object-weak-reference/index.md) referencing the object that is to provide the value (i.e., that will eventually produce the [NamedValue.value](../-named-value/value.md)) |
| [property](property.md) | [jvm]<br>open override val [property](property.md): [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;[V](index.md)&gt; |
| [propertyCoherentWithObject](property-coherent-with-object.md) | [jvm]<br>val [propertyCoherentWithObject](property-coherent-with-object.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Indicates whether the [property](property.md)'s value can be resolved on the given object |
| [value](value.md) | [jvm]<br>override val [value](value.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?<br>The [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) representation of the [property](property.md) value, with annotations taken into account |
