---
title: AsStringConfig
---
//[kute](../../../index.html)/[nl.kute.asstring.config](../index.html)/[AsStringConfig](index.html)



# AsStringConfig

class [AsStringConfig](index.html)

Builder-like class, to prepare and apply newly set values as defaults for [AsStringOption](../../nl.kute.asstring.annotation.option/-as-string-option/index.html) / [AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html).



- 
   Various `with*`-methods are available to set the desired defaults
- 
   [applyAsDefault](apply-as-default.html)`()` must be called to make the new values effective as default
- 
   [AsStringConfig](index.html) is permissive; no validation is applied to input values




The current (and newly set) defaults can be accessed as [AsStringOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.html) and [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.html)



#### See also


| |
|---|
| [AsStringConfig.applyAsDefault](apply-as-default.html) |


## Constructors


| | |
|---|---|
| [AsStringConfig](-as-string-config.html) | [jvm]<br>constructor()<br>As an alternative, one can use static method [asStringConfig](../as-string-config.html) instead |


## Functions


| Name | Summary |
|---|---|
| [applyAsDefault](apply-as-default.html) | [jvm]<br>fun [applyAsDefault](apply-as-default.html)()<br>Assigns the [AsStringOption](../../nl.kute.asstring.annotation.option/-as-string-option/index.html) and [AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html) being built, to [AsStringOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-option/-default-option/default-option.html) and [AsStringClassOption.defaultOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/-default-option/default-option.html), respectively, as the new application defaults. |
| [getAsStringClassOptionDefault](get-as-string-class-option-default.html) | [jvm]<br>fun [getAsStringClassOptionDefault](get-as-string-class-option-default.html)(): [AsStringClassOption](../../nl.kute.asstring.annotation.option/-as-string-class-option/index.html) |
| [getAsStringOptionDefault](get-as-string-option-default.html) | [jvm]<br>fun [getAsStringOptionDefault](get-as-string-option-default.html)(): [AsStringOption](../../nl.kute.asstring.annotation.option/-as-string-option/index.html) |
| [getForceToStringFilters](get-force-to-string-filters.html) | [jvm]<br>fun [getForceToStringFilters](get-force-to-string-filters.html)(): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.html), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)&gt; |
| [getPropertyOmitFilters](get-property-omit-filters.html) | [jvm]<br>fun [getPropertyOmitFilters](get-property-omit-filters.html)(): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.html), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)&gt; |
| [withElementsLimit](with-elements-limit.html) | [jvm]<br>fun [withElementsLimit](with-elements-limit.html)(elementsLimit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringOption.elementsLimit](../../nl.kute.asstring.annotation.option/-as-string-option/elements-limit.html). |
| [withForceToStringFilterPredicates](with-force-to-string-filter-predicates.html) | [jvm]<br>fun [withForceToStringFilterPredicates](with-force-to-string-filter-predicates.html)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [ClassMeta](../../nl.kute.asstring.property.meta/-class-meta/index.html)&gt;): [AsStringConfig](index.html)<br>Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.html)s to be applied. |
| [withForceToStringFilters](with-force-to-string-filters.html) | [jvm]<br>fun [withForceToStringFilters](with-force-to-string-filters.html)(vararg filters: [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.html)): [AsStringConfig](index.html)<br>Sets the new [ClassMetaFilter](../../nl.kute.asstring.core/-class-meta-filter/index.html)s to be applied. |
| [withIncludeCompanion](with-include-companion.html) | [jvm]<br>fun [withIncludeCompanion](with-include-companion.html)(includeCompanion: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringClassOption.includeCompanion](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-companion.html). |
| [withIncludeIdentityHash](with-include-identity-hash.html) | [jvm]<br>fun [withIncludeIdentityHash](with-include-identity-hash.html)(includeHash: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringClassOption.includeIdentityHash](../../nl.kute.asstring.annotation.option/-as-string-class-option/include-identity-hash.html). |
| [withMaxPropertyStringLength](with-max-property-string-length.html) | [jvm]<br>fun [withMaxPropertyStringLength](with-max-property-string-length.html)(propMaxLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringOption.propMaxStringValueLength](../../nl.kute.asstring.annotation.option/-as-string-option/prop-max-string-value-length.html). |
| [withPropertiesAlphabetic](with-properties-alphabetic.html) | [jvm]<br>fun [withPropertiesAlphabetic](with-properties-alphabetic.html)(sortNamesAlphabetic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringClassOption.sortNamesAlphabetic](../../nl.kute.asstring.annotation.option/-as-string-class-option/sort-names-alphabetic.html). |
| [withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.html) | [jvm]<br>fun [withPropertyOmitFilterPredicates](with-property-omit-filter-predicates.html)(vararg predicates: [Predicate](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)&lt;in [PropertyMeta](../../nl.kute.asstring.property.meta/-property-meta/index.html)&gt;): [AsStringConfig](index.html)<br>Sets the new [PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.html)s to be applied. |
| [withPropertyOmitFilters](with-property-omit-filters.html) | [jvm]<br>fun [withPropertyOmitFilters](with-property-omit-filters.html)(vararg filters: [PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.html)): [AsStringConfig](index.html)<br>Sets the new [PropertyMetaFilter](../../nl.kute.asstring.core/-property-meta-filter/index.html)s to be applied. |
| [withPropertySorters](with-property-sorters.html) | [jvm]<br>fun [withPropertySorters](with-property-sorters.html)(vararg propertySorters: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html)&lt;*&gt;&gt;): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringClassOption.propertySorters](../../nl.kute.asstring.annotation.option/-as-string-class-option/property-sorters.html). |
| [withShowNullAs](with-show-null-as.html) | [jvm]<br>fun [withShowNullAs](with-show-null-as.html)(showNullAs: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringOption.showNullAs](../../nl.kute.asstring.annotation.option/-as-string-option/show-null-as.html). |
| [withSurroundPropValue](with-surround-prop-value.html) | [jvm]<br>fun [withSurroundPropValue](with-surround-prop-value.html)(surroundPropValue: [PropertyValueSurrounder](../../nl.kute.asstring.annotation.option/-property-value-surrounder/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringOption.surroundPropValue](../../nl.kute.asstring.annotation.option/-as-string-option/surround-prop-value.html). |
| [withToStringPreference](with-to-string-preference.html) | [jvm]<br>fun [withToStringPreference](with-to-string-preference.html)(toStringPreference: [ToStringPreference](../../nl.kute.asstring.annotation.option/-to-string-preference/index.html)): [AsStringConfig](index.html)<br>Sets the new default value for [AsStringClassOption.toStringPreference](../../nl.kute.asstring.annotation.option/-as-string-class-option/to-string-preference.html). |

