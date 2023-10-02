---
title: exceptProperties
---
//[kute](../../../index.html)/[nl.kute.asstring.core](../index.html)/[AsStringBuilder](index.html)/[exceptProperties](except-properties.html)



# exceptProperties



[jvm]\
fun [exceptProperties](except-properties.html)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.html)



Restricts the output by excluding the object's properties listed in [props](except-properties.html).



*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s added by [withAlsoNamed](with-also-named.html) are not excluded.



#### Parameters


jvm

| | |
|---|---|
| props | The list of properties to be excluded in the output of [asString](as-string.html). |




