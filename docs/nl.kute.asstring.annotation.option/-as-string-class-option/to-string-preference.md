---
title: toStringPreference
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.option](../index.html)/[AsStringClassOption](index.html)/[toStringPreference](to-string-preference.html)



# toStringPreference



[jvm]\
val [toStringPreference](to-string-preference.html): [ToStringPreference](../-to-string-preference/index.html)



#### Parameters


jvm

| | |
|---|---|
| toStringPreference | -     If [ToStringPreference.USE_ASSTRING](../-to-string-preference/-u-s-e_-a-s-s-t-r-i-n-g/index.html) applies (either as default or by annotation), [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) should dynamically resolve properties and values for custom classes, even if toString is implemented. [USE_ASSTRING](../-to-string-preference/-u-s-e_-a-s-s-t-r-i-n-g/index.html) is the default. -     If [ToStringPreference.PREFER_TOSTRING](../-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.html) applies (either as default or by annotation), and a toString method is implemented, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) should honour the class's toString, rather than dynamically resolving properties and values.<br>If [ToStringPreference.PREFER_TOSTRING](../-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.html) applies and recursion is detected in the toString implementation, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) will fall back to dynamically resolving properties and values for that class. |




