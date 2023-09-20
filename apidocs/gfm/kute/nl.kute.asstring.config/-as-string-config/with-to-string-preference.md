//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withToStringPreference](with-to-string-preference.md)

# withToStringPreference

[jvm]\
fun [withToStringPreference](with-to-string-preference.md)(toStringPreference: [ToStringPreference](../../nl.kute.asstring.annotation.option/-to-string-preference/index.md)): [AsStringConfig](index.md)

Sets the new default value for [AsStringClassOption.toStringPreference](../../nl.kute.asstring.annotation.option/-as-string-class-option/to-string-preference.md).

- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the value being set to the [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.md) application-wide default.

#### Return

the config builder (`this`)

#### See also

| |
|---|
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
| [AsStringClassOption.toStringPreference](../../nl.kute.asstring.annotation.option/-as-string-class-option/to-string-preference.md) |
