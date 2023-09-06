//[Kute](../../index.md)/[nl.kute.asstring.core](index.md)/[isBaseType](is-base-type.md)

# isBaseType

[jvm]\
fun [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html).[isBaseType](is-base-type.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Is the object's type considered a base type?

Currently, the following types are considered base types:

- 
   Primitives / primitive wrappers
- 
   Elementary Java types like [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html), [CharSequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-sequence/index.html), [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html), [java.util.Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html), [java.time.temporal.Temporal](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/Temporal.html) and their subclasses
- 
   The Kotlin unsigned types ([UByte](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-byte/index.html), [UShort](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-short/index.html), [UInt](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-int/index.html), [ULong](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-u-long/index.html))
