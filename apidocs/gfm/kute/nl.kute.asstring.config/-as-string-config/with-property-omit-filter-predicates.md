//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.md)

# withPropertyOmitFilterPredicates

[jvm]\
fun [withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.md)(predicate: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [PropertyMeta](../../nl.kute.asstring.property.meta/-property-meta/index.md)&gt;): [AsStringConfig](index.md)

Sets the new [PropertyOmitFilter](../../nl.kute.asstring.property.filter/-property-omit-filter/index.md)s to be applied.

- 
   Is essentially a wrapper for [withPropertyOmitFilters](with-property-omit-filters.md)

Mainly for use from **Java**, where [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html) parameter is easier than lambdas (in [withPropertyOmitFilters](with-property-omit-filters.md)) For **Kotlin**, [withPropertyOmitFilters](with-property-omit-filters.md) is recommended (fore ease of use)

- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the values as an application-wide default.
- 
   Any previously existing filters will be removed by [applyAsDefault](apply-as-default.md).

#### See also

| |
|---|
| [AsStringConfig.withPropertyOmitFilters](with-property-omit-filters.md) |
| [AsStringConfig.getPropertyOmitFilters](get-property-omit-filters.md) |
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
