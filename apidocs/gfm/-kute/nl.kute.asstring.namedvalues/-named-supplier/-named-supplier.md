//[Kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[NamedSupplier](index.md)/[NamedSupplier](-named-supplier.md)

# NamedSupplier

[jvm]\
constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), callable: [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)&lt;[V](index.md)?&gt;)

Secondary constructor, mainly for usage from Java.

In Java, `() -> myValue` is translated to a [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html) when introduced as a local variable

#### Parameters

jvm

| | |
|---|---|
| name | The name to identify this [NamedSupplier](index.md)'s value |
| callable | The [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html) to supply the [value](value.md) on request |

[jvm]\
constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [Supplier](../-supplier/index.md)&lt;[V](index.md)?&gt;)

#### Parameters

jvm

| | |
|---|---|
| name | The name to identify this [NamedSupplier](index.md)'s value |
| supplier | The lambda to supply the [value](value.md) on request |
