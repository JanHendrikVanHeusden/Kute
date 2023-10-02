---
title: withShowNullAs
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withShowNullAs](with-show-null-as.html)



# withShowNullAs



[jvm]\
fun [withShowNullAs](with-show-null-as.html)(showNullAs: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringOption.showNullAs](../../nl.kute.asstring.annotation.option/-as-string-option/show-null-as.html).



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
| [AsStringOption.DefaultOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.html) |
| [AsStringOption.showNullAs](../../nl.kute.asstring.annotation.option/-as-string-option/show-null-as.html) |



