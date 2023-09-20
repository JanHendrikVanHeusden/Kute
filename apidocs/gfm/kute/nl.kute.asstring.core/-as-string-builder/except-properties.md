//[kute](../../../index.md)/[nl.kute.asstring.core](../index.md)/[AsStringBuilder](index.md)/[exceptProperties](except-properties.md)

# exceptProperties

[jvm]\
fun [exceptProperties](except-properties.md)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.md)

Restricts the output by excluding the object's properties listed in [props](except-properties.md).

*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s added by [withAlsoNamed](with-also-named.md) are not excluded.

#### Parameters

jvm

| | |
|---|---|
| props | The list of properties to be excluded in the output of [asString](as-string.md). |
