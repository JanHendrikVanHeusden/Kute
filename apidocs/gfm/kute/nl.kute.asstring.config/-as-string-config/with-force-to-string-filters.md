//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)/[withForceToStringFilters](with-force-to-string-filters.md)

# withForceToStringFilters

[jvm]\
fun [withForceToStringFilters](with-force-to-string-filters.md)(vararg filters: [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md)): [AsStringConfig](index.md)

Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md)s to be applied.

- 
   Custom [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)es for which one of more [filters](with-force-to-string-filters.md) return `true` will have their toString called (instead of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md))
- 
   Any exceptions that may occur during evaluation of a [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md) are ignored, and:
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
