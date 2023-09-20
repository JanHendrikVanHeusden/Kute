//[kute](../../../index.md)/[nl.kute.asstring.core](../index.md)/[AsStringBuilder](index.md)/[withAlsoNamed](with-also-named.md)

# withAlsoNamed

[jvm]\
fun [withAlsoNamed](with-also-named.md)(vararg nameValues: [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)&lt;*&gt;): [AsStringBuilder](index.md)

Allows adding additional related [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s, that provide property-like named values.

This allows to add arbitrary values to the [asString](as-string.md) output, e.g. unrelated properties, calculated values, etc.

For usage of these, see the subclasses of [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)

#### Parameters

jvm

| | |
|---|---|
| nameValues | The [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md)s to add to the output of [asString](as-string.md) |

#### See also

| |
|---|
| [NameValue](../../nl.kute.asstring.namedvalues/-name-value/index.md) |
| [NamedSupplier](../../nl.kute.asstring.namedvalues/-named-supplier/index.md) |
| [NamedProp](../../nl.kute.asstring.namedvalues/-named-prop/index.md) |
| [NamedValue](../../nl.kute.asstring.namedvalues/-named-value/index.md) |
