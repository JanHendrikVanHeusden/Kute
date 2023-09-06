//[Kute](../../../index.md)/[nl.kute.asstring.core](../index.md)/[AsStringBuilder](index.md)/[withOnlyProperties](with-only-properties.md)

# withOnlyProperties

[jvm]\
fun [withOnlyProperties](with-only-properties.md)(vararg props: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;): [AsStringBuilder](index.md)

Restricts the output to only the object's properties listed in [props](with-only-properties.md).

*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s added by [withAlsoNamed](with-also-named.md) are not filtered out.

#### Parameters

jvm

| | |
|---|---|
| props | The restrictive list of properties to be included in the output of [asString](as-string.md). |
