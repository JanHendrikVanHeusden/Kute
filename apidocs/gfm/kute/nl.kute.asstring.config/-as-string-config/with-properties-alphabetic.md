//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withPropertiesAlphabetic](with-properties-alphabetic.md)

# withPropertiesAlphabetic

[jvm]\
fun [withPropertiesAlphabetic](with-properties-alphabetic.md)(sortNamesAlphabetic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.md)

Sets the new default value for [AsStringClassOption.sortNamesAlphabetic](../../nl.kute.asstring.annotation.option/-as-string-class-option/sort-names-alphabetic.md).

- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the value being set to the [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.md) application-wide default.

**NB:** This is a pre-sorting. If additional [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md) are given, these will be applied after the alphabetic sort. That sorting is stable, so if sorters yield an equal value, the alphabetic ordering is preserved.

#### Return

the config builder (`this`)

#### See also

| |
|---|
| [AsStringConfig.withPropertySorters](with-property-sorters.md) |
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
| [AsStringClassOption.sortNamesAlphabetic](../../nl.kute.asstring.annotation.option/-as-string-class-option/sort-names-alphabetic.md) |
