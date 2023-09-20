//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withPropertySorters](with-property-sorters.md)

# withPropertySorters

[jvm]\
fun [withPropertySorters](with-property-sorters.md)(vararg propertySorters: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md)&lt;*&gt;&gt;): [AsStringConfig](index.md)

Sets the new default value for [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md).

- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the value being set to the [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.md) application-wide default.

**NB:** This sorting is applied after the alphabetic ordering (see: [withPropertiesAlphabetic](with-properties-alphabetic.md)). The sorting is stable, so if the [propertySorters](with-property-sorters.md) yield an equal value, the alphabetic ordering is preserved.

#### Return

the config builder (`this`)

#### See also

| |
|---|
| [AsStringConfig.withPropertiesAlphabetic](with-properties-alphabetic.md) |
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md) |
