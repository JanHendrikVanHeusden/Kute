---
title: withForceToStringFilters
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)/[withForceToStringFilters](with-force-to-string-filters.html)



# withForceToStringFilters



[jvm]\
fun [withForceToStringFilters](with-force-to-string-filters.html)(vararg filters: [ClassMetaFilter](../../nl.kute.asstring.core.filter/-class-meta-filter/index.html)): [AsStringConfig](index.html)



Sets the new [ClassMetaFilter](../../nl.kute.asstring.core.filter/-class-meta-filter/index.html)s to be applied.



- 
   Custom [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)es for which one of more [filters](with-force-to-string-filters.html) return `true` will have their toString called (instead of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html))
- 
   Any exceptions that may occur during evaluation of a [ClassMetaFilter](../../nl.kute.asstring.core.filter/-class-meta-filter/index.html) are ignored, and:
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



