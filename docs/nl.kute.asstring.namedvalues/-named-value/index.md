---
title: NamedValue
---
//[kute](../../../index.html)/[nl.kute.asstring.namedvalues](../index.html)/[NamedValue](index.html)



# NamedValue

class [NamedValue](index.html)&lt;[V](index.html)&gt;(val name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [V](index.html)?) : [AbstractNameValue](../-abstract-name-value/index.html)&lt;[V](index.html)?&gt; 

A [NameValue](../-name-value/index.html) implementation where its value is provided directly, as parameter [value](value.html).



- 
   Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output
- 
   Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html)




[NamedValue](index.html) is intended for situations where the value is provided once, and reassignment to the source value need not be reflected. State changes are reflected though. E.g.:

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
   Usage of [NamedValue](index.html) in a pre-built [nl.kute.asstring.core.AsStringBuilder](../../nl.kute.asstring.core/-as-string-builder/index.html) is not recommended, as reassignment is not reflected
- 
   If reassignment should be reflected, consider using [NamedSupplier](../-named-supplier/index.html) or [NamedProp](../-named-prop/index.html)
- 
   Annotations that affect output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) are taken in account only if these are part of the [value](value.html) object. Annotations in the outer context (e.g. in the above example: annotations within the class that holds the `aDate` variable) are not taken in account.
- 
   If annotations in the outer context are to be honoured, consider using [NamedProp](../-named-prop/index.html)
- 
   The [value](value.html) is weakly referenced only, so the [NamedValue](index.html) does not prevent garbage collection of the [value](value.html)




#### Parameters


jvm

| | |
|---|---|
| name | The name to identify this [NamedValue](index.html)'s value |
| value | The value of this [NamedValue](index.html) |



#### See also


| |
|---|
| [AsStringBuilder.withAlsoNamed](../../nl.kute.asstring.core/-as-string-builder/with-also-named.html) |


## Constructors


| | |
|---|---|
| [NamedValue](-named-value.html) | [jvm]<br>constructor(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [V](index.html)?) |


## Properties


| Name | Summary |
|---|---|
| [name](name.html) | [jvm]<br>open override val [name](name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [value](value.html) | [jvm]<br>open override val [value](value.html): [V](index.html)?<br>The value of this [NameValue](../-name-value/index.html) |

