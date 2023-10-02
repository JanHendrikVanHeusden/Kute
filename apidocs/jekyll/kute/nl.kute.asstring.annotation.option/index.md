---
title: nl.kute.asstring.annotation.option
---
//[kute](../../index.html)/[nl.kute.asstring.annotation.option](index.html)



# Package-level declarations



## Types


| Name | Summary |
|---|---|
| [AsStringClassOption](-as-string-class-option/index.html) | [jvm]<br>@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html)])<br>@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)<br>annotation class [AsStringClassOption](-as-string-class-option/index.html)(val includeIdentityHash: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = initialIncludeIdentityHash, val toStringPreference: [ToStringPreference](-to-string-preference/index.html) = USE_ASSTRING, val includeCompanion: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = initialIncludeCompanion, val sortNamesAlphabetic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = initialSortNamesAlphabetic, val propertySorters: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../nl.kute.asstring.property.ranking/-property-rankable/index.html)&lt;*&gt;&gt; = []) |
| [AsStringOption](-as-string-option/index.html) | [jvm]<br>@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html), [AnnotationTarget.PROPERTY](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-p-r-o-p-e-r-t-y/index.html), [AnnotationTarget.FUNCTION](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-u-n-c-t-i-o-n/index.html)])<br>@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)<br>annotation class [AsStringOption](-as-string-option/index.html)(val showNullAs: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = initialShowNullAs, val surroundPropValue: [PropertyValueSurrounder](-property-value-surrounder/index.html) = NONE, val propMaxStringValueLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = initialMaxStringValueLength, val elementsLimit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = initialElementsLimit)<br>The [AsStringOption](-as-string-option/index.html) annotation can be placed: |
| [PropertyValueSurrounder](-property-value-surrounder/index.html) | [jvm]<br>enum [PropertyValueSurrounder](-property-value-surrounder/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[PropertyValueSurrounder](-property-value-surrounder/index.html)&gt; <br>Enum to define prefixes and postfixes, to distinguish property value Strings from other text. E.g. |
| [ToStringPreference](-to-string-preference/index.html) | [jvm]<br>enum [ToStringPreference](-to-string-preference/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ToStringPreference](-to-string-preference/index.html)&gt; <br>Preference for whether to use toString (insofar implemented) or [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) (with dynamic property & value resolution) for custom classes that have toString implemented. |

