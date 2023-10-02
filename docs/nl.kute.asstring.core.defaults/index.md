---
title: nl.kute.asstring.core.defaults
---
//[kute](../../index.html)/[nl.kute.asstring.core.defaults](index.html)



# Package-level declarations



## Properties


| Name | Summary |
|---|---|
| [initialElementsLimit](initial-elements-limit.html) | [jvm]<br>const val [initialElementsLimit](initial-elements-limit.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 50<br>Initial default value for limiting the number of elements of a collection to be parsed |
| [initialIncludeCompanion](initial-include-companion.html) | [jvm]<br>const val [initialIncludeCompanion](initial-include-companion.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false<br>Initial default value for the choice whether to include the object's companion (if any) in the [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialIncludeIdentityHash](initial-include-identity-hash.html) | [jvm]<br>const val [initialIncludeIdentityHash](initial-include-identity-hash.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false<br>Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialMaxStringValueLength](initial-max-string-value-length.html) | [jvm]<br>const val [initialMaxStringValueLength](initial-max-string-value-length.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 500<br>Initial default value for the maximum length **per property** in the [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialPropertySorters](initial-property-sorters.html) | [jvm]<br>val [initialPropertySorters](initial-property-sorters.html): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[PropertyRankable](../nl.kute.asstring.property.ranking/-property-rankable/index.html)&lt;*&gt;&gt;&gt;<br>Initial default value for [PropertyRankable](../nl.kute.asstring.property.ranking/-property-rankable/index.html) property ranking classes to be used for sorting properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialShowNullAs](initial-show-null-as.html) | [jvm]<br>const val [initialShowNullAs](initial-show-null-as.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Initial default value for how to represent `null` in the [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialSortNamesAlphabetic](initial-sort-names-alphabetic.html) | [jvm]<br>const val [initialSortNamesAlphabetic](initial-sort-names-alphabetic.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false<br>Initial default value for the choice whether the properties should be pre-sorted alphabetically in  [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialSurroundPropValue](initial-surround-prop-value.html) | [jvm]<br>val [initialSurroundPropValue](initial-surround-prop-value.html): [PropertyValueSurrounder](../nl.kute.asstring.annotation.option/-property-value-surrounder/index.html)<br>Initial default value for prefix/postfix property value Strings in the [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |
| [initialToStringPreference](initial-to-string-preference.html) | [jvm]<br>val [initialToStringPreference](initial-to-string-preference.html): [ToStringPreference](../nl.kute.asstring.annotation.option/-to-string-preference/index.html)<br>Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output |

