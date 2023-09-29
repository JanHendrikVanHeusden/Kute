//[kute](../../../index.md)/[nl.kute.asstring.config](../index.md)/[AsStringConfig](index.md)

# AsStringConfig

class [AsStringConfig](index.md)

Builder-like class, to prepare and apply newly set values as defaults for [AsStringOption](../../nl.kute.asstring.annotation.option/-as-string-option/index.md) / [AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md).

- 
   Various `with*`-methods are available to set the desired defaults
- 
   [applyAsDefault](apply-as-default.md)`()` must be called to make the new values effective as default
- 
   [AsStringConfig](index.md) is permissive; no validation is applied to input values

The current (and newly set) defaults can be accessed as [AsStringOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.md) and [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.md)

#### See also

| |
|---|
| [AsStringConfig.applyAsDefault](apply-as-default.md) |

## Constructors

| | |
|---|---|
| [AsStringConfig](-as-string-config.md) | [jvm]<br>constructor()<br>As an alternative, one can use static method [asStringConfig](../as-string-config.md) instead |

## Functions

| Name | Summary |
|---|---|
| [applyAsDefault](apply-as-default.md) | [jvm]<br>fun [applyAsDefault](apply-as-default.md)()<br>Assigns the [AsStringOption](../../nl.kute.asstring.annotation.option/-as-string-option/index.md) and [AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md) being built, to [AsStringOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.md) and [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.md), respectively, as the new application defaults. |
| [getAsStringClassOptionDefault](get-as-string-class-option-default.md) | [jvm]<br>fun [getAsStringClassOptionDefault](get-as-string-class-option-default.md)(): [AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.md) |
| [getAsStringOptionDefault](get-as-string-option-default.md) | [jvm]<br>fun [getAsStringOptionDefault](get-as-string-option-default.md)(): [AsStringOption](../../nl.kute.asstring.annotation.option/-as-string-option/index.md) |
| [getForceToStringFilters](get-force-to-string-filters.md) | [jvm]<br>fun [getForceToStringFilters](get-force-to-string-filters.md)(): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)&gt; |
| [getPropertyOmitFilters](get-property-omit-filters.md) | [jvm]<br>fun [getPropertyOmitFilters](get-property-omit-filters.md)(): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.md), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)&gt; |
| [withElementsLimit](with-elements-limit.md) | [jvm]<br>fun [withElementsLimit](with-elements-limit.md)(elementsLimit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringOption.elementsLimit](../../nl.kute.asstring.annotation.option/-as-string-option/elements-limit.md). |
| [withForceToStringFilterPredicates](with-force-to-string-filter-predicates.md) | [jvm]<br>fun [withForceToStringFilterPredicates](with-force-to-string-filter-predicates.md)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [ClassMeta](../../nl.kute.asstring.property.meta/-class-meta/index.md)&gt;): [AsStringConfig](index.md)<br>Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md)s to be applied. |
| [withForceToStringFilters](with-force-to-string-filters.md) | [jvm]<br>fun [withForceToStringFilters](with-force-to-string-filters.md)(vararg filters: [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md)): [AsStringConfig](index.md)<br>Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.md)s to be applied. |
| [withIncludeCompanion](with-include-companion.md) | [jvm]<br>fun [withIncludeCompanion](with-include-companion.md)(includeCompanion: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringClassOption.includeCompanion](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-companion.md). |
| [withIncludeIdentityHash](with-include-identity-hash.md) | [jvm]<br>fun [withIncludeIdentityHash](with-include-identity-hash.md)(includeHash: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringClassOption.includeIdentityHash](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-identity-hash.md). |
| [withMaxPropertyStringLength](with-max-property-string-length.md) | [jvm]<br>fun [withMaxPropertyStringLength](with-max-property-string-length.md)(propMaxLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringOption.propMaxStringValueLength](../../nl.kute.asstring.annotation.option/-as-string-option/prop-max-string-value-length.md). |
| [withPropertiesAlphabetic](with-properties-alphabetic.md) | [jvm]<br>fun [withPropertiesAlphabetic](with-properties-alphabetic.md)(sortNamesAlphabetic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringClassOption.sortNamesAlphabetic](../../nl.kute.asstring.annotation.option/-as-string-class-option/sort-names-alphabetic.md). |
| [withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.md) | [jvm]<br>fun [withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.md)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [PropertyMeta](../../nl.kute.asstring.property.meta/-property-meta/index.md)&gt;): [AsStringConfig](index.md)<br>Sets the new [PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.md)s to be applied. |
| [withPropertyOmitFilters](with-property-omit-filters.md) | [jvm]<br>fun [withPropertyOmitFilters](with-property-omit-filters.md)(vararg filters: [PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.md)): [AsStringConfig](index.md)<br>Sets the new [PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.md)s to be applied. |
| [withPropertySorters](with-property-sorters.md) | [jvm]<br>fun [withPropertySorters](with-property-sorters.md)(vararg propertySorters: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md)&lt;*&gt;&gt;): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.md). |
| [withShowNullAs](with-show-null-as.md) | [jvm]<br>fun [withShowNullAs](with-show-null-as.md)(showNullAs: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringOption.showNullAs](../../nl.kute.asstring.annotation.option/-as-string-option/show-null-as.md). |
| [withSurroundPropValue](with-surround-prop-value.md) | [jvm]<br>fun [withSurroundPropValue](with-surround-prop-value.md)(surroundPropValue: [PropertyValueSurrounder](../../nl.kute.asstring.annotation.option/-property-value-surrounder/index.md)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringOption.surroundPropValue](../../nl.kute.asstring.annotation.option/-as-string-option/surround-prop-value.md). |
| [withToStringPreference](with-to-string-preference.md) | [jvm]<br>fun [withToStringPreference](with-to-string-preference.md)(toStringPreference: [ToStringPreference](../../nl.kute.asstring.annotation.option/-to-string-preference/index.md)): [AsStringConfig](index.md)<br>Sets the new default value for [AsStringClassOption.toStringPreference](../../nl.kute.asstring.annotation.option/-as-string-class-option/to-string-preference.md). |
