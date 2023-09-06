//[Kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withMaxPropertyStringLength](with-max-property-string-length.md)

# withMaxPropertyStringLength

[jvm]\
fun [withMaxPropertyStringLength](with-max-property-string-length.md)(propMaxLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.md)

Sets the new default value for [AsStringOption.propMaxStringValueLength](../../nl.kute.asstring.annotation.option/-as-string-option/prop-max-string-value-length.md).

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
| [AsStringOption.propMaxStringValueLength](../../nl.kute.asstring.annotation.option/-as-string-option/prop-max-string-value-length.md) |
