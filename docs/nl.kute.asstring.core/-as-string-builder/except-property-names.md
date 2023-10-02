---
title: exceptPropertyNames
---
//[kute](../../../index.html)/[nl.kute.asstring.core](../index.html)/[AsStringBuilder](index.html)/[exceptPropertyNames](except-property-names.html)



# exceptPropertyNames



[jvm]\
fun [exceptPropertyNames](except-property-names.html)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.html)



Restricts the output by excluding the object's property names listed in [names](except-property-names.html).



*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s added by [withAlsoNamed](with-also-named.html) are not excluded.



#### Parameters


jvm

| | |
|---|---|
| names | The list of property names to be excluded in the output of [asString](as-string.html). |




