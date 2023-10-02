---
title: withMaxPropertyStringLength
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withMaxPropertyStringLength](with-max-property-string-length.html)



# withMaxPropertyStringLength



[jvm]\
fun [withMaxPropertyStringLength](with-max-property-string-length.html)(propMaxLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringOption.propMaxStringValueLength](../../nl.kute.asstring.annotation.option/-as-string-option/prop-max-string-value-length.html).



- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.html) is called on the [AsStringConfig](index.html) object.
- 
   [applyAsDefault](apply-as-default.html) applies the value being set to the [AsStringOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.html) application-wide default.




#### Return



the config builder (`this`)



#### See also


| |
|---|
| [AsStringConfig.applyAsDefault](apply-as-default.html) |
| [AsStringOption.propMaxStringValueLength](../../nl.kute.asstring.annotation.option/-as-string-option/prop-max-string-value-length.html) |



