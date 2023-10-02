---
title: withToStringPreference
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withToStringPreference](with-to-string-preference.html)



# withToStringPreference



[jvm]\
fun [withToStringPreference](with-to-string-preference.html)(toStringPreference: [ToStringPreference](../../nl.kute.asstring.annotation.option/-to-string-preference/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringClassOption.toStringPreference](../../nl.kute.asstring.annotation.option/-as-string-class-option/to-string-preference.html).



- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.html) is called on the [AsStringConfig](index.html) object.
- 
   [applyAsDefault](apply-as-default.html) applies the value being set to the [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.html) application-wide default.




#### Return



the config builder (`this`)



#### See also


| |
|---|
| [AsStringConfig.applyAsDefault](apply-as-default.html) |
| [AsStringClassOption.toStringPreference](../../nl.kute.asstring.annotation.option/-as-string-class-option/to-string-preference.html) |



