---
title: withElementsLimit
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withElementsLimit](with-elements-limit.html)



# withElementsLimit



[jvm]\
fun [withElementsLimit](with-elements-limit.html)(elementsLimit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringOption.elementsLimit](../../nl.kute.asstring.annotation.option/-as-string-option/elements-limit.html).



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
| [AsStringOption.elementsLimit](../../nl.kute.asstring.annotation.option/-as-string-option/elements-limit.html) |



