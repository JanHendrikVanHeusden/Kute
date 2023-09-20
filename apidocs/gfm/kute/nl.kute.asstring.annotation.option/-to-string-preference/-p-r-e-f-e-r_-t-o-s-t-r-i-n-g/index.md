//[kute](../../../../index.md)/[nl.kute.asstring.annotation.option](../../index.md)/[ToStringPreference](../index.md)/[PREFER_TOSTRING](index.md)

# PREFER_TOSTRING

[PREFER_TOSTRING](index.md)

When [PREFER_TOSTRING](index.md) applies, [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.md) will call the object's toString method on custom classes, provided that the class or any of its superclasses has overridden toString. If no overridden toString is present, the properties will be resolved dynamically (like with [USE_ASSTRING](../-u-s-e_-a-s-s-t-r-i-n-g/index.md)).

Characteristics:

- 
   Less maintenance-friendly: new or renamed properties of your custom classes will not be automatically reflected for classes with a home-made toString implementation
- - 
      If no toString implemented, [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.md) will be used
- 
   Limited protection against errors due to recursion / self reference / mutual references (only when no home-made toString implementation)
- 
   Honours existing toString implementations of your custom classes
- 
   Does not honour annotations like [nl.kute.asstring.annotation.modify.AsStringMask](../../../nl.kute.asstring.annotation.modify/-as-string-mask/index.md) etc. when a home made toString implementation is present
- 
   Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)s etc. only when no home-made toString implementation

#### See also

| |
|---|
| [ToStringPreference.USE_ASSTRING](../-u-s-e_-a-s-s-t-r-i-n-g/index.md) |

## Properties

| Name | Summary |
|---|---|
| [name](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040) | [jvm]<br>val [name](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040) | [jvm]<br>val [ordinal](../../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
