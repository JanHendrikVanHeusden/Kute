//[Kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[NamedSupplier](index.md)

# NamedSupplier

class [NamedSupplier](index.md)&lt;[V](index.md)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [ValueSupplier](../-value-supplier/index.md)&lt;[V](index.md)?&gt;) : [AbstractNameValue](../-abstract-name-value/index.md)&lt;[V](index.md)?&gt; 

A [NameValue](../-name-value/index.md) implementation where the value to be resolved is provided by means of a [ValueSupplier](../-value-supplier/index.md) lambda.

- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md)

[NamedSupplier](index.md) is intended for situations where the supplied value needs to be evaluated on each access, both state changes and reassignment. E.g.:

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
   Usage of [NamedSupplier](index.md) in a pre-built [nl.kute.asstring.core.AsStringBuilder](../../nl.kute.asstring.core/-as-string-builder/index.md) is good practice, as reassignment is reflected
- 
   Annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) are taken in account only if these are part of the supplied object. Annotations in the outer context (e.g. in the above example: annotations within the class that holds the `aDate` variable) are not taken in account.
- 
   If annotations in the outer context are to be honoured, consider using [NamedProp](../-named-prop/index.md)
- 
   The `supplier` is weakly referenced only, so the [NamedSupplier](index.md) does not prevent garbage collection of the supplier, or it's supplied value.

#### Parameters

jvm

| | |
|---|---|
| name | The name to identify this [NamedSupplier](index.md)'s value |
| supplier | The lambda to supply the [value](value.md) on request |

#### See also

| |
|---|
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md) |

## Constructors

| | |
|---|---|
| [NamedSupplier](-named-supplier.md) | [jvm]<br>constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), callable: [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)&lt;[V](index.md)?&gt;)<br>Secondary constructor, mainly for usage from Java.<br>constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [ValueSupplier](../-value-supplier/index.md)&lt;[V](index.md)?&gt;) |

## Properties

| Name | Summary |
|---|---|
| [name](name.md) | [jvm]<br>open override val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | [jvm]<br>open override val [value](value.md): [V](index.md)?<br>The value of this [NameValue](../-name-value/index.md) |
