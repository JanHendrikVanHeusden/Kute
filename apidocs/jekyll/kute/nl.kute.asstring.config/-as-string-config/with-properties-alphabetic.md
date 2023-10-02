---
title: withPropertiesAlphabetic
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withPropertiesAlphabetic](with-properties-alphabetic.html)



# withPropertiesAlphabetic



[jvm]\
fun [withPropertiesAlphabetic](with-properties-alphabetic.html)(sortNamesAlphabetic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringClassOption.sortNamesAlphabetic](../../nl.kute.asstring.annotation.option/-as-string-class-option/sort-names-alphabetic.html).



- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.html) is called on the [AsStringConfig](index.html) object.
- 
   [applyAsDefault](apply-as-default.html) applies the value being set to the [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.html) application-wide default.




**NB:** This is a pre-sorting. If additional [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html) are given, these will be applied after the alphabetic sort. That sorting is stable, so if sorters yield an equal value, the alphabetic ordering is preserved.



#### Return



the config builder (`this`)



#### See also


| |
|---|
| [AsStringConfig.withPropertySorters](with-property-sorters.html) |
| [AsStringConfig.applyAsDefault](apply-as-default.html) |
| [AsStringClassOption.sortNamesAlphabetic](../../nl.kute.asstring.annotation.option/-as-string-class-option/sort-names-alphabetic.html) |



