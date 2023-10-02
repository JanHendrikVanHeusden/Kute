---
title: withPropertySorters
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withPropertySorters](with-property-sorters.html)



# withPropertySorters



[jvm]\
fun [withPropertySorters](with-property-sorters.html)(vararg propertySorters: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html)&lt;*&gt;&gt;): [AsStringConfig](index.html)



Sets the new default value for [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html).



- 
   Any exceptions that may occur during evaluation of a [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html) are ignored, and:
- 
   The exception will be logged
- 
   The [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html) will be removed from the registry, to avoid further exceptions
- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.html) is called on the [AsStringConfig](index.html) object.
- 
   [applyAsDefault](apply-as-default.html) applies the value being set to the [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.html) application-wide default.




**NB:** This sorting is applied after the alphabetic ordering (see: [withPropertiesAlphabetic](with-properties-alphabetic.html)). The sorting is stable, so if the [propertySorters](with-property-sorters.html) yield an equal value, the alphabetic ordering is preserved.



#### Return



the config builder (`this`)



#### See also


| |
|---|
| [AsStringConfig.withPropertiesAlphabetic](with-properties-alphabetic.html) |
| [AsStringConfig.applyAsDefault](apply-as-default.html) |
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html) |



