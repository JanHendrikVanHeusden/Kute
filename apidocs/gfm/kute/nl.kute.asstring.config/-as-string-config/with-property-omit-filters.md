//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withPropertyOmitFilters](with-property-omit-filters.md)

# withPropertyOmitFilters

[jvm]\
fun [withPropertyOmitFilters](with-property-omit-filters.md)(vararg filters: [PropertyOmitFilter](../../nl.kute.asstring.property.filter/-property-omit-filter/index.md)): [AsStringConfig](index.md)

Sets the new [PropertyOmitFilter](../../nl.kute.asstring.property.filter/-property-omit-filter/index.md)s to be applied.

- 
   Properties for which one of more [filters](with-property-omit-filters.md) return `true` will be omitted in subsequent calls to [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md).
- 
   Any exceptions that may occur during evaluation of a [PropertyOmitFilter](../../nl.kute.asstring.property.filter/-property-omit-filter/index.md) are ignored, and:
- - 
      The exception will be logged
   - 
      The filter will be removed from the filter registry, to avoid further exceptions
- 
   Nothing will happen effectively until [applyAsDefault](apply-as-default.md) is called on the [AsStringConfig](index.md) object.
- 
   [applyAsDefault](apply-as-default.md) applies the values as an application-wide default.
- 
   Any previously existing filters will be removed by [applyAsDefault](apply-as-default.md).

#### See also

| |
|---|
| [AsStringConfig.getPropertyOmitFilters](get-property-omit-filters.md) |
| [AsStringConfig.applyAsDefault](apply-as-default.md) |
