//[Kute](../../../index.md)/[nl.kute.asstring.core](../index.md)/[AsStringBuilder](index.md)/[exceptPropertyNames](except-property-names.md)

# exceptPropertyNames

[jvm]\
fun [exceptPropertyNames](except-property-names.md)(vararg names: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringBuilder](index.md)

Restricts the output by excluding the object's property names listed in [names](except-property-names.md).

*NB:*[NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s added by [withAlsoNamed](with-also-named.md) are not excluded.

#### Parameters

jvm

| | |
|---|---|
| names | The list of property names to be excluded in the output of [asString](as-string.md). |
