//[Kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[ToStringPreference](index.md)

# ToStringPreference

[jvm]\
enum [ToStringPreference](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ToStringPreference](index.md)&gt; 

Preference for whether to use toString (insofar implemented) or [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) (with dynamic property & value resolution) for custom classes that have toString implemented.

- 
   See the KDoc for [USE_ASSTRING](-u-s-e_-a-s-s-t-r-i-n-g/index.md) / [PREFER_TOSTRING](-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) for characteristics of each.

[USE_ASSTRING](-u-s-e_-a-s-s-t-r-i-n-g/index.md) is **recommended** unless:

- 
   specific requirements apply to toString methods
- 
   toString methods have been created with additional value, e.g. additional values     for tracing objects in logs, etc.

## Entries

| | |
|---|---|
| [USE_ASSTRING](-u-s-e_-a-s-s-t-r-i-n-g/index.md) | [jvm]<br>[USE_ASSTRING](-u-s-e_-a-s-s-t-r-i-n-g/index.md)<br>When [USE_ASSTRING](-u-s-e_-a-s-s-t-r-i-n-g/index.md) applies, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) will dynamically resolve properties and values for custom classes, even if the class or any of its superclasses has toString overridden. |
| [PREFER_TOSTRING](-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) | [jvm]<br>[PREFER_TOSTRING](-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md)<br>When [PREFER_TOSTRING](-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) applies, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) will call the object's toString method on custom classes, provided that the class or any of its superclasses has overridden toString. If no overridden toString is present, the properties will be resolved dynamically (like with [USE_ASSTRING](-u-s-e_-a-s-s-t-r-i-n-g/index.md)). |

## Properties

| Name | Summary |
|---|---|
| [entries](entries.md) | [jvm]<br>val [entries](entries.md): [EnumEntries](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.enums/-enum-entries/index.html)&lt;[ToStringPreference](index.md)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040) | [jvm]<br>val [name](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-372974862%2FProperties%2F-1216412040): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040) | [jvm]<br>val [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.md#-739389684%2FProperties%2F-1216412040): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [valueOf](value-of.md) | [jvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [ToStringPreference](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [jvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[ToStringPreference](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
