//[kute](../../../index.md)/[nl.kute.asstring.weakreference](../index.md)/[ObjectWeakReference](index.md)

# ObjectWeakReference

class [ObjectWeakReference](index.md)&lt;[T](index.md)&gt;(obj: [T](index.md)?) : [WeakReference](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html)&lt;[T](index.md)?&gt; 

[WeakReference](https://docs.oracle.com/javase/8/docs/api/java/lang/ref/WeakReference.html) implementation with [toString](to-string.md) overridden

#### Parameters

jvm

| | |
|---|---|
| obj | The referenced object. `null`-safe. |

## Constructors

| | |
|---|---|
| [ObjectWeakReference](-object-weak-reference.md) | [jvm]<br>constructor(obj: [T](index.md)?) |

## Functions

| Name | Summary |
|---|---|
| [clear](index.md#1185955492%2FFunctions%2F-1216412040) | [jvm]<br>open fun [clear](index.md#1185955492%2FFunctions%2F-1216412040)() |
| [enqueue](index.md#-1582683575%2FFunctions%2F-1216412040) | [jvm]<br>open fun [enqueue](index.md#-1582683575%2FFunctions%2F-1216412040)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [get](index.md#1424066235%2FFunctions%2F-1216412040) | [jvm]<br>open fun [get](index.md#1424066235%2FFunctions%2F-1216412040)(): [T](index.md)? |
| [isEnqueued](index.md#1222417347%2FFunctions%2F-1216412040) | [jvm]<br>open fun [isEnqueued](index.md#1222417347%2FFunctions%2F-1216412040)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toString](to-string.md) | [jvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
