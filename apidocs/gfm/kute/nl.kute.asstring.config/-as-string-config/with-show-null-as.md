//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withShowNullAs](with-show-null-as.md)

# withShowNullAs

[jvm]\
fun [withShowNullAs](with-show-null-as.md)(showNullAs: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringConfig](index.md)

Sets the new default value for [AsStringOption.showNullAs](../../nl.kute.asstring.annotation.option/-as-string-option/show-null-as.md).

- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the value being set to the [AsStringOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.md) application-wide default.

#### Return

the config builder (`this`)

#### See also

| |
|---|
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
| [AsStringOption.DefaultOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.md) |
| [AsStringOption.showNullAs](../../nl.kute.asstring.annotation.option/-as-string-option/show-null-as.md) |
