//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withSurroundPropValue](with-surround-prop-value.md)

# withSurroundPropValue

[jvm]\
fun [withSurroundPropValue](with-surround-prop-value.md)(surroundPropValue: [PropertyValueSurrounder](../../nl.kute.asstring.annotation.option/-property-value-surrounder/index.md)): [AsStringConfig](index.md)

Sets the new default value for [AsStringOption.surroundPropValue](../../nl.kute.asstring.annotation.option/-as-string-option/surround-prop-value.md).

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
| [AsStringOption.surroundPropValue](../../nl.kute.asstring.annotation.option/-as-string-option/surround-prop-value.md) |
