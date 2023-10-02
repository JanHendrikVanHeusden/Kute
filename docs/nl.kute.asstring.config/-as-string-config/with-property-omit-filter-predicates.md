---
title: withPropertyOmitFilterPredicates
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.html)



# withPropertyOmitFilterPredicates



[jvm]\
fun [withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.html)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [PropertyMeta](../../nl.kute.asstring.property.meta/-property-meta/index.html)&gt;): [AsStringConfig](index.html)



Sets the new [PropertyMetaFilter](../../nl.kute.asstring.core.filter/-property-meta-filter/index.html)s to be applied.



- 
   Is essentially a wrapper for [withPropertyOmitFilters](with-property-omit-filters.html)




Mainly for use from **Java**, where [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) parameter is easier than lambdas (in [withPropertyOmitFilters](with-property-omit-filters.html)) For **Kotlin**, [withPropertyOmitFilters](with-property-omit-filters.html) is recommended (for ease of use)



- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.html) is called on the [AsStringConfig](index.html) object.
- 
   [applyAsDefault](apply-as-default.html) applies the values as an application-wide default.
- 
   Any previously existing filters will be removed by [applyAsDefault](apply-as-default.html).




#### See also


| |
|---|
| [AsStringConfig.withPropertyOmitFilters](with-property-omit-filters.html) |
| [AsStringConfig.getPropertyOmitFilters](get-property-omit-filters.html) |
| [AsStringConfig.applyAsDefault](apply-as-default.html) |



