---
title: PropertyRankable
---
//[kute](../../../index.html)/[nl.kute.asstring.property.ranking](../index.html)/[PropertyRankable](index.html)



# PropertyRankable





@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(toStringPreference = ToStringPreference.USE_ASSTRING)



interface [PropertyRankable](index.html)&lt;out [T](index.html) : [PropertyRankable](index.html)&lt;[T](index.html)&gt;&gt;

Interface to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output.



**NB:** This interface is sealed, so it can not be implemented directly. Concrete implementations should extend [PropertyRanking](../-property-ranking/index.html) instead.



**In order to be used for** ***property ranking*** (see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html)), the concrete class must either (in this order of prevalence):



- 
   Be pre-instantiated, by having a concrete [PropertyRanking](../-property-ranking/index.html)-subclass object constructed
- 
   Allow reflective instantiation, by one of the following methods:




1. 
   Have a reachable (`public`) companion object with a `val` property named `instance` that returns an instance of the concrete [PropertyRankable](index.html) subclass.
2. 
   Have a no-arg constructor that is reachable (`public`) or that can be set accessible reflectively by means of [kotlin.reflect.KProperty.isAccessible](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect.jvm/index.html)




This interface is sealed, so external code can not implement it. Concrete implementations should extend [PropertyRanking](../-property-ranking/index.html) instead.



#### See also


| |
|---|
| [PropertyRanking](../-property-ranking/index.html) |
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html) |


#### Inheritors


| |
|---|
| [PropertyRanking](../-property-ranking/index.html) |


## Functions


| Name | Summary |
|---|---|
| [getRank](get-rank.html) | [jvm]<br>abstract fun [getRank](get-rank.html)(propertyValueMeta: [PropertyValueMeta](../../nl.kute.asstring.property.meta/-property-value-meta/index.html)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMeta](get-rank.html). |
| [register](register.html) | [jvm]<br>open fun [register](register.html)()<br>Register this concrete [PropertyRankable](index.html) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) output |

