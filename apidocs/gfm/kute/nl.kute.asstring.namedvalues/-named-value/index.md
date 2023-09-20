//[kute](../../../index.md)/[nl.kute.asstring.namedvalues](../index.md)/[NamedValue](index.md)

# NamedValue

class [NamedValue](index.md)&lt;[V](index.md)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [V](index.md)?) : [AbstractNameValue](../-abstract-name-value/index.md)&lt;[V](index.md)?&gt; 

A [NameValue](../-name-value/index.md) implementation where its value is provided directly, as parameter [value](value.md).

- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md)

[NamedValue](index.md) is intended for situations where the value is provided once, and reassignment to the source value need not be reflected. State changes are reflected though. E.g.:

```kotlin
var aDate = Date()
val namedValue: NamedValue<Date> = NamedValue("aDate", aDate)

// this state change *will* be reflected in the `namedValue`:
aDate.time = aDate.time + 3_600_000
println(namedValue.value) // yields date with new time
// this reassignment *will not* be reflected in the `namedValue`:
aDate = Date()
println(namedValue.value) // yields date with new time, but not the latest value of aDate
```

- 
   Usage of [NamedValue](index.md) in a pre-built [nl.kute.asstring.core.AsStringBuilder](../../nl.kute.asstring.core/-as-string-builder/index.md) is not recommended, as reassignment is not reflected
- 
   If reassignment should be reflected, consider using [NamedSupplier](../-named-supplier/index.md) or [NamedProp](../-named-prop/index.md)
- 
   Annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) are taken in account only if these are part of the [value](value.md) object. Annotations in the outer context (e.g. in the above example: annotations within the class that holds the `aDate` variable) are not taken in account.
- 
   If annotations in the outer context are to be honoured, consider using [NamedProp](../-named-prop/index.md)
- 
   The [value](value.md) is weakly referenced only, so the [NamedValue](index.md) does not prevent garbage collection of the [value](value.md)

#### Parameters

jvm

| | |
|---|---|
| name | The name to identify this [NamedValue](index.md)'s value |
| value | The value of this [NamedValue](index.md) |

#### See also

| |
|---|
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.md) |

## Constructors

| | |
|---|---|
| [NamedValue](-named-value.md) | [jvm]<br>constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [V](index.md)?) |

## Properties

| Name | Summary |
|---|---|
| [name](name.md) | [jvm]<br>open override val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.md) | [jvm]<br>open override val [value](value.md): [V](index.md)?<br>The value of this [NameValue](../-name-value/index.md) |
