---
title: withIncludeIdentityHash
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withIncludeIdentityHash](with-include-identity-hash.html)



# withIncludeIdentityHash



[jvm]\
fun [withIncludeIdentityHash](with-include-identity-hash.html)(includeHash: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.html)



Sets the new default value for [AsStringClassOption.includeIdentityHash](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-identity-hash.html).



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
| [AsStringClassOption.includeIdentityHash](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-identity-hash.html) |



