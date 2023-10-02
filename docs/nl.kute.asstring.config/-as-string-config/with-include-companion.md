---
title: withIncludeCompanion
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withIncludeCompanion](with-include-companion.html)



# withIncludeCompanion



[jvm]\
fun [withIncludeCompanion](with-include-companion.html)(includeCompanion: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringClassOption.includeCompanion](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-companion.html).



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
| [AsStringClassOption.includeCompanion](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-companion.html) |



