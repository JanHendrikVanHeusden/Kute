---
title: ObjectWeakReference
---
//[kute](../../../index.html)/[nl.kute.asstring.weakreference](../index.html)/[ObjectWeakReference](index.html)



# ObjectWeakReference

class [ObjectWeakReference](index.html)&lt;[T](index.html)&gt;(obj: [T](index.html)?) : [WeakReference](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html)&lt;[T](index.html)?&gt; 

[WeakReference](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html) implementation with [toString](to-string.html) overridden



#### Parameters


jvm

| | |
|---|---|
| obj | The referenced object. `null`-safe. |



## Constructors


| | |
|---|---|
| [ObjectWeakReference](-object-weak-reference.html) | [jvm]<br>constructor(obj: [T](index.html)?) |


## Functions


| Name | Summary |
|---|---|
| [clear](index.html#1185955492%2FFunctions%2F863300109) | [jvm]<br>open fun [clear](index.html#1185955492%2FFunctions%2F863300109)() |
| [enqueue](index.html#-1582683575%2FFunctions%2F863300109) | [jvm]<br>open fun [enqueue](index.html#-1582683575%2FFunctions%2F863300109)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [get](index.html#1424066235%2FFunctions%2F863300109) | [jvm]<br>open fun [get](index.html#1424066235%2FFunctions%2F863300109)(): [T](index.html)? |
| [isEnqueued](index.html#1222417347%2FFunctions%2F863300109) | [jvm]<br>open fun [isEnqueued](index.html#1222417347%2FFunctions%2F863300109)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toString](to-string.html) | [jvm]<br>open override fun [toString](to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

