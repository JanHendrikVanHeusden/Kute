---
title: withForceToStringFilterPredicates
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withForceToStringFilterPredicates](with-force-to-string-filter-predicates.html)



# withForceToStringFilterPredicates



[jvm]\
fun [withForceToStringFilterPredicates](with-force-to-string-filter-predicates.html)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [ClassMeta](../../nl.kute.asstring.property.meta/-class-meta/index.html)&gt;): [AsStringConfig](index.html)



Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.html)s to be applied.



- 
   Is essentially a wrapper for [withForceToStringFilters](with-force-to-string-filters.html)




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
| [AsStringConfig.withForceToStringFilters](with-force-to-string-filters.html) |
| [AsStringConfig.getForceToStringFilters](get-force-to-string-filters.html) |
| [AsStringConfig.applyAsDefault](apply-as-default.html) |



