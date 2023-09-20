//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withIncludeIdentityHash](with-include-identity-hash.md)

# withIncludeIdentityHash

[jvm]\
fun [withIncludeIdentityHash](with-include-identity-hash.md)(includeHash: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.md)

Sets the new default value for [AsStringClassOption.includeIdentityHash](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-identity-hash.md).

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
| [AsStringClassOption.includeIdentityHash](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-identity-hash.md) |
