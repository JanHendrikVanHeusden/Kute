---
title: withSurroundPropValue
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withSurroundPropValue](with-surround-prop-value.html)



# withSurroundPropValue



[jvm]\
fun [withSurroundPropValue](with-surround-prop-value.html)(surroundPropValue: [PropertyValueSurrounder](../../nl.kute.asstring.annotation.option/-property-value-surrounder/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringOption.surroundPropValue](../../nl.kute.asstring.annotation.option/-as-string-option/surround-prop-value.html).



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
| [AsStringOption.surroundPropValue](../../nl.kute.asstring.annotation.option/-as-string-option/surround-prop-value.html) |



