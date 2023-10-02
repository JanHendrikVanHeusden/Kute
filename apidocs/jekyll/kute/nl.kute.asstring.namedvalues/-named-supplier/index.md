---
title: NamedSupplier
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[NamedSupplier](index.html)



# NamedSupplier

class [NamedSupplier](index.html)&lt;[V](index.html)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [ValueSupplier](../-value-supplier/index.html)&lt;[V](index.html)?&gt;) : [AbstractNameValue](../-abstract-name-value/index.html)&lt;[V](index.html)?&gt; 

A [NameValue](../-name-value/index.html) implementation where the value to be resolved is provided by means of a [ValueSupplier](../-value-supplier/index.html) lambda.



- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html)




[NamedSupplier](index.html) is intended for situations where the supplied value needs to be evaluated on each access, both state changes and reassignment. E.g.:

```kotlin
var aDate = Date()
val namedSupplier: NamedSupplier<Date> = NamedSupplier("aDate", { aDate })

// this state change *will* be reflected in the `namedSupplier`:
aDate.time = aDate.time + 3_600_000
println(namedSupplier.value) // yields date with new time
// this reassignment *will also* be reflected in the `namedSupplier`:
aDate = Date()
println(namedSupplier.value) // yields date after reassignment
```


- 
   Usage of [NamedSupplier](index.html) in a pre-built [nl.kute.asstring.core.AsStringBuilder](../../nl.kute.asstring.core/-as-string-builder/index.html) is good practice, as reassignment is reflected
- 
   Annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) are taken in account only if these are part of the supplied object. Annotations in the outer context (e.g. in the above example: annotations within the class that holds the `aDate` variable) are not taken in account.
- 
   If annotations in the outer context are to be honoured, consider using [NamedProp](../-named-prop/index.html)
- 
   The `supplier` is weakly referenced only, so the [NamedSupplier](index.html) does not prevent garbage collection of the supplier, or it's supplied value.




#### Parameters


jvm

| | |
|---|---|
| name | The name to identify this [NamedSupplier](index.html)'s value |
| supplier | The lambda to supply the [value](value.html) on request |



#### See also


| |
|---|
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html) |


## Constructors


| | |
|---|---|
| [NamedSupplier](-named-supplier.html) | [jvm]<br>constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), callable: [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)&lt;[V](index.html)?&gt;)<br>Secondary constructor, mainly for usage from Java.<br>constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [ValueSupplier](../-value-supplier/index.html)&lt;[V](index.html)?&gt;) |


## Properties


| Name | Summary |
|---|---|
| [name](name.html) | [jvm]<br>open override val [name](name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.html) | [jvm]<br>open override val [value](value.html): [V](index.html)?<br>The value of this [NameValue](../-name-value/index.html) |

