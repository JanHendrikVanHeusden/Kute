//[Kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringClassOption](index.md)/[toStringPreference](to-string-preference.md)

# toStringPreference

[jvm]\
val [toStringPreference](to-string-preference.md): [ToStringPreference](../-to-string-preference/index.md)

#### Parameters

jvm

| | |
|---|---|
| toStringPreference | -     If [ToStringPreference.USE_ASSTRING](../-to-string-preference/-u-s-e_-a-s-s-t-r-i-n-g/index.md) applies (either as default or by annotation), [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) should dynamically resolve properties and values for custom classes, even if toString is implemented. [USE_ASSTRING](../-to-string-preference/-u-s-e_-a-s-s-t-r-i-n-g/index.md) is the default. -     If [ToStringPreference.PREFER_TOSTRING](../-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) applies (either as default or by annotation), and a toString method is implemented, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) should honour the class's toString, rather than dynamically resolving properties and values.<br>If [ToStringPreference.PREFER_TOSTRING](../-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) applies and recursion is detected in the toString implementation, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) will fall back to dynamically resolving properties and values for that class. |
