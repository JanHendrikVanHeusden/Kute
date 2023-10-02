---
title: withOnlyProperties
---
//[kute](../../../index.html)/[nl.kute.asstring.core](../index.html)/[AsStringBuilder](index.html)/[withOnlyProperties](with-only-properties.html)



# withOnlyProperties



[jvm]\
fun [withOnlyProperties](with-only-properties.html)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.html)



Restricts the output to only the object's properties listed in [props](with-only-properties.html).



*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s added by [withAlsoNamed](with-also-named.html) are not filtered out.



#### Parameters


jvm

| | |
|---|---|
| props | The restrictive list of properties to be included in the output of [asString](as-string.html). |




