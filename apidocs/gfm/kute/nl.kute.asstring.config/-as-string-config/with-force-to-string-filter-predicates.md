//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withForceToStringFilterPredicates](with-force-to-string-filter-predicates.md)

# withForceToStringFilterPredicates

[jvm]\
fun [withForceToStringFilterPredicates](with-force-to-string-filter-predicates.md)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [ClassMeta](../../nl.kute.asstring.property.meta/-class-meta/index.md)&gt;): [AsStringConfig](index.md)

Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md)s to be applied.

- 
   Is essentially a wrapper for [withForceToStringFilters](with-force-to-string-filters.md)

Mainly for use from **Java**, where [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) parameter is easier than lambdas (in [withPropertyOmitFilters](with-property-omit-filters.md)) For **Kotlin**, [withPropertyOmitFilters](with-property-omit-filters.md) is recommended (for ease of use)

- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the values as an application-wide default.
- 
   Any previously existing filters will be removed by [applyAsDefault](apply-as-default.md).

#### See also

| |
|---|
| [AsStringConfig.withForceToStringFilters](with-force-to-string-filters.md) |
| [AsStringConfig.getForceToStringFilters](get-force-to-string-filters.md) |
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
