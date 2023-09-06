//[Kute](../../../index.md)/[nl.kute.asstring.core](../index.md)/[AsStringBuilder](index.md)/[withOnlyPropertyNames](with-only-property-names.md)

# withOnlyPropertyNames

[jvm]\
fun [withOnlyPropertyNames](with-only-property-names.md)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.md)

Restricts the output to only the object's property names listed in [names](with-only-property-names.md).

*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s added by [withAlsoNamed](with-also-named.md) are not filtered out.

#### Parameters

jvm

| | |
|---|---|
| names | The restrictive list of property names to be included in the output of [asString](as-string.md). |
