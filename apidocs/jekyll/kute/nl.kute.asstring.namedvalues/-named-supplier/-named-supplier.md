---
title: NamedSupplier
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[NamedSupplier](index.html)/[NamedSupplier](-named-supplier.html)



# NamedSupplier



[jvm]\
constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), callable: [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html)&lt;[V](index.html)?&gt;)



Secondary constructor, mainly for usage from Java.



In Java, `() -> myValue` is translated to a [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html) when introduced as a local variable



#### Parameters


jvm

| | |
|---|---|
| name | The name to identify this [NamedSupplier](index.html)'s value |
| callable | The [Callable](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Callable.html) to supply the [value](value.html) on request |





[jvm]\
constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), supplier: [ValueSupplier](../-value-supplier/index.html)&lt;[V](index.html)?&gt;)



#### Parameters


jvm

| | |
|---|---|
| name | The name to identify this [NamedSupplier](index.html)'s value |
| supplier | The lambda to supply the [value](value.html) on request |




