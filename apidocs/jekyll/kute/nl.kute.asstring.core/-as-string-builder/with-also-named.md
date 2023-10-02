---
title: withAlsoNamed
---
//[kute](../../../index.html)/[nl.kute.asstring.core](../index.html)/[AsStringBuilder](index.html)/[withAlsoNamed](with-also-named.html)



# withAlsoNamed



[jvm]\
fun [withAlsoNamed](with-also-named.html)(vararg nameValues: [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)&lt;*&gt;): [AsStringBuilder](index.html)



Allows adding additional related [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s, that provide property-like named values.



This allows to add arbitrary values to the [asString](as-string.html) output, e.g. unrelated properties, calculated values, etc.



For usage of these, see the subclasses of [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)



#### Parameters


jvm

| | |
|---|---|
| nameValues | The [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html)s to add to the output of [asString](as-string.html) |



#### See also


| |
|---|
| [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.html) |
| [NamedSupplier](../../nl.kute.asstring.namedvalues/-named-supplier/index.html) |
| [NamedProp](../../nl.kute.asstring.namedvalues/-named-prop/index.html) |
| [NamedValue](../../nl.kute.asstring.namedvalues/-named-value/index.html) |



