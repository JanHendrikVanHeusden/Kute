---
title: withOnlyPropertyNames
---
//[kute](../../../index.html)/[nl.kute.asstring.core](../index.html)/[AsStringBuilder](index.html)/[withOnlyPropertyNames](with-only-property-names.html)



# withOnlyPropertyNames



[jvm]\
fun [withOnlyPropertyNames](with-only-property-names.html)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.html)



Restricts the output to only the object's property names listed in [names](with-only-property-names.html).



*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s added by [withAlsoNamed](with-also-named.html) are not filtered out.



#### Parameters


jvm

| | |
|---|---|
| names | The restrictive list of property names to be included in the output of [asString](as-string.html). |




