//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyRankable](index.md)

# PropertyRankable

@[AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(toStringPreference = ToStringPreference.USE_ASSTRING)

interface [PropertyRankable](index.md)&lt;out [T](index.md) : [PropertyRankable](index.md)&lt;[T](index.md)&gt;&gt;

Interface to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output.

**NB:** This interface is sealed, so it can not be implemented directly. Concrete implementations should extend [PropertyRanking](../-property-ranking/index.md) instead.

**In order to be used for** ***property ranking*** (see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md)), the concrete class must either (in this order of prevalence):

- 
   Be pre-instantiated, by having a concrete [PropertyRanking](../-property-ranking/index.md)-subclass object constructed
- 
   Allow reflective instantiation, by one of the following methods:

1. 
   Have a reachable (`public`) companion object with a `val` property named `instance` that returns an instance of the concrete [PropertyRankable](index.md) subclass.
2. 
   Have a no-arg constructor that is reachable (`public`) or that can be set accessible reflectively by means of [kotlin.reflect.KProperty.isAccessible](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect.jvm/index.html)

This interface is sealed, so external code can not implement it. Concrete implementations should extend [PropertyRanking](../-property-ranking/index.md) instead.

#### See also

| |
|---|
| [PropertyRanking](../-property-ranking/index.md) |
| [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md) |

#### Inheritors

| |
|---|
| [PropertyRanking](../-property-ranking/index.md) |

## Functions

| Name | Summary |
|---|---|
| [getRank](get-rank.md) | [jvm]<br>abstract fun [getRank](get-rank.md)(propertyValueMetaData: [PropertyValueMetaData](../-property-value-meta-data/index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Provide a rank, typically to be used for sorting properties / values, based on the [propertyValueMetaData](get-rank.md). |
| [register](register.md) | [jvm]<br>open fun [register](register.md)()<br>Register this concrete [PropertyRankable](index.md) class to allow using it for ordering properties in [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) output |
