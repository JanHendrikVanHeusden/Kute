---
title: withPropertyOmitFilters
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withPropertyOmitFilters](with-property-omit-filters.html)



# withPropertyOmitFilters



[jvm]\
fun [withPropertyOmitFilters](with-property-omit-filters.html)(vararg filters: [PropertyMetaFilter](../../nl.kute.asstring.core.filter/-property-meta-filter/index.html)): [AsStringConfig](index.html)



Sets the new [PropertyMetaFilter](../../nl.kute.asstring.core.filter/-property-meta-filter/index.html)s to be applied.



- 
   Properties for which one of more [filters](with-property-omit-filters.html) return `true` will be omitted in subsequent calls to [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html).
- 
   Any exceptions that may occur during evaluation of a [PropertyMetaFilter](../../nl.kute.asstring.core.filter/-property-meta-filter/index.html) are ignored, and:
- - 
      The exception will be logged
   - 
      The filter will be removed from the filter registry, to avoid further exceptions
- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.html) is called on the [AsStringConfig](index.html) object.
- 
   [applyAsDefault](apply-as-default.html) applies the values as an application-wide default.
- 
   Any previously existing filters will be removed by [applyAsDefault](apply-as-default.html).




#### See also


| |
|---|
| [AsStringConfig.getPropertyOmitFilters](get-property-omit-filters.html) |
| [AsStringConfig.applyAsDefault](apply-as-default.html) |



