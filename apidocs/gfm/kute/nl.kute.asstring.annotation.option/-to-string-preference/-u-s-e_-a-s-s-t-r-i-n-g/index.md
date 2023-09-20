//[kute](../../../../index.md)/[nl.kute.asstring.annotation.option](../../index.md)/[ToStringPreference](../index.md)/[USE_ASSTRING](index.md)

# USE_ASSTRING

[USE_ASSTRING](index.md)

When [USE_ASSTRING](index.md) applies, [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.md) will dynamically resolve properties and values for custom classes, even if the class or any of its superclasses has toString overridden.

[USE_ASSTRING](index.md) is **recommended** unless:

- 
   specific requirements apply to toString methods
- 
   toString methods have been created with additional value, e.g. additional values     for tracing objects in logs, etc.
- - 
      NB: Such values can also be added to [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.md); see [nl.kute.asstring.core.AsStringBuilder](../../../nl.kute.asstring.core/-as-string-builder/index.md)

Characteristics:

- 
   Maintenance-friendly: new or renamed properties of your custom classes are automatically reflected in the [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.md) output
- 
   Full protection against errors due to recursion / self reference / mutual references ([StackOverflowError](https://docs.oracle.com/javase/8/docs/api/java/lang/StackOverflowError.html)s)
- 
   Bypasses existing toString implementations of your custom classes
- 
   Fully honours annotations like [nl.kute.asstring.annotation.modify.AsStringMask](../../../nl.kute.asstring.annotation.modify/-as-string-mask/index.md) etc.
- 
   Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)s etc.

#### See also

| |
|---|
| [ToStringPreference.PREFER_TOSTRING](../-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) |

## Properties

| Name | Summary |
|---|---|
| [name](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040) | [jvm]<br>val [name](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040) | [jvm]<br>val [ordinal](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
