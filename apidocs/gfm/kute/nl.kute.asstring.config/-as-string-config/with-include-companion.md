//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withIncludeCompanion](with-include-companion.md)

# withIncludeCompanion

[jvm]\
fun [withIncludeCompanion](with-include-companion.md)(includeCompanion: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.md)

Sets the new default value for [AsStringClassOption.includeCompanion](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-companion.md).

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
| [AsStringClassOption.includeCompanion](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-companion.md) |
