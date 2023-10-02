---
title: USE_ASSTRING
---
//[kute](../../../../index.html)/[nl.kute.asstring.annotation.option](../../index.html)/[ToStringPreference](../index.html)/[USE_ASSTRING](index.html)



# USE_ASSTRING



[USE_ASSTRING](index.html)



When [USE_ASSTRING](index.html) applies, [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.html) will dynamically resolve properties and values for custom classes, even if the class or any of its superclasses has toString overridden.



[USE_ASSTRING](index.html) is **recommended** unless:



- 
   specific requirements apply to toString methods
- 
   toString methods have been created with additional value, e.g. additional values     for tracing objects in logs, etc.
- - 
      NB: Such values can also be added to [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.html); see [nl.kute.asstring.core.AsStringBuilder](../../../nl.kute.asstring.core/-as-string-builder/index.html)




Characteristics:



- 
   Maintenance-friendly: new or renamed properties of your custom classes are automatically reflected in the [nl.kute.asstring.core.asString](../../../nl.kute.asstring.core/as-string.html) output
- 
   Full protection against errors due to recursion / self reference / mutual references ([StackOverflowError](https://docs.oracle.com/javase/8/docs/api/java/lang/StackOverflowError.html)s)
- 
   Bypasses existing toString implementations of your custom classes
- 
   Fully honours annotations like [nl.kute.asstring.annotation.modify.AsStringMask](../../../nl.kute.asstring.annotation.modify/-as-string-mask/index.html) etc.
- 
   Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)s etc.




#### See also


| |
|---|
| [ToStringPreference.PREFER_TOSTRING](../-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.html) |


## Properties


| Name | Summary |
|---|---|
| [name](../../../nl.kute.hashing/-digest-method/-m-d5/index.html#-372974862%2FProperties%2F863300109) | [jvm]<br>val [name](../../../nl.kute.hashing/-digest-method/-m-d5/index.html#-372974862%2FProperties%2F863300109): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../../nl.kute.hashing/-digest-method/-m-d5/index.html#-739389684%2FProperties%2F863300109) | [jvm]<br>val [ordinal](../../../nl.kute.hashing/-digest-method/-m-d5/index.html#-739389684%2FProperties%2F863300109): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

