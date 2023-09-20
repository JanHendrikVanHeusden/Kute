//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withElementsLimit](with-elements-limit.md)

# withElementsLimit

[jvm]\
fun [withElementsLimit](with-elements-limit.md)(elementsLimit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.md)

Sets the new default value for [AsStringOption.elementsLimit](../../nl.kute.asstring.annotation.option/-as-string-option/elements-limit.md).

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
| [AsStringOption.elementsLimit](../../nl.kute.asstring.annotation.option/-as-string-option/elements-limit.md) |
