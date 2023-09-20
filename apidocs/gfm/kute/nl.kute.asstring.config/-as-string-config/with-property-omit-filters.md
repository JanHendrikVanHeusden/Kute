//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withPropertyOmitFilters](with-property-omit-filters.md)

# withPropertyOmitFilters

[jvm]\
fun [withPropertyOmitFilters](with-property-omit-filters.md)(vararg filters: [PropertyMetaFilter](../../nl.kute.asstring.property.filter/-property-meta-filter/index.md)): [AsStringConfig](index.md)

Sets the new [PropertyMetaFilter](../../nl.kute.asstring.property.filter/-property-meta-filter/index.md)s to be applied.

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
