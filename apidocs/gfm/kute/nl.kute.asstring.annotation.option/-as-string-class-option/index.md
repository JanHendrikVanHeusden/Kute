//[kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringClassOption](index.md)

# AsStringClassOption

@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html)])

@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)

annotation class [AsStringClassOption](index.md)(val includeIdentityHash: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = initialIncludeIdentityHash, val toStringPreference: [ToStringPreference](../-to-string-preference/index.md) = USE_ASSTRING, val includeCompanion: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = initialIncludeCompanion, val sortNamesAlphabetic: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = initialSortNamesAlphabetic, val propertySorters: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md)&lt;*&gt;&gt; = [])

- 
   The [AsStringClassOption](index.md) annotation controls what is included, and ordering of properties
- 
   The [AsStringClassOption](index.md) annotation can be placed on classes.

#### Parameters

jvm

| | |
|---|---|
| includeIdentityHash | Should the identity hash be included in output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md)? If included, the identity hash is represented as `@` followed by the hex representation as of [System.identityHashCode](https://docs.oracle.com/javase/8/docs/api/java/lang/System.html#identityHashCode-kotlin.Any-) (up to 8 hex characters), identical to the hex string seen in non-overridden toString output.<br>Default = `false` by [initialIncludeIdentityHash](../../nl.kute.asstring.core.defaults/initial-include-identity-hash.md) |
| toStringPreference | -     If [ToStringPreference.USE_ASSTRING](../-to-string-preference/-u-s-e_-a-s-s-t-r-i-n-g/index.md) applies (either as default or by annotation), [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) should dynamically resolve properties and values for custom classes, even if toString is implemented. [USE_ASSTRING](../-to-string-preference/-u-s-e_-a-s-s-t-r-i-n-g/index.md) is the default. -     If [ToStringPreference.PREFER_TOSTRING](../-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) applies (either as default or by annotation), and a toString method is implemented, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) should honour the class's toString, rather than dynamically resolving properties and values.<br>If [ToStringPreference.PREFER_TOSTRING](../-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.md) applies and recursion is detected in the toString implementation, [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) will fall back to dynamically resolving properties and values for that class. |
| includeCompanion | Should a companion object (if any) be included in the output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md)? A companion object will be included only if all of these apply:<br>1.     The class that contains the companion object is not private 2.     The companion object is public 3.     The companion object has at least 1 property<br>Default = `false` by [initialIncludeCompanion](../../nl.kute.asstring.core.defaults/initial-include-companion.md) |
| sortNamesAlphabetic | Should output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) be sorted alphabetically by property name (case-insensitive) in output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md)?<br>**NB:** This is a pre-sorting. If additional [propertySorters](property-sorters.md) are given, these will be applied after the alphabetic sort.<br>Default = `false` by [initialSortNamesAlphabetic](../../nl.kute.asstring.core.defaults/initial-sort-names-alphabetic.md) |
| propertySorters | One or more [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md) implementing classes can be specified to have properties sorted in output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md). Default is none (no explicit sorting order). These will be applied in order, like SQL multi-column sorting.<br>So if the 1st sorter yields an equal result for a pair of properties, the 2nd will be applied, and so on until a non-zero result is obtained, of until the [propertySorters](property-sorters.md) are exhausted.<br>-     This sorting is applied after alphabetic sorting is applied. The sorting is stable, so if the [propertySorters](property-sorters.md) yield an equal value, the alphabetic sorting is preserved. -     Usage of [propertySorters](property-sorters.md) may have a significant effect on CPU and memory footprint of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md). |

## Types

| Name | Summary |
|---|---|
| [DefaultOption](-default-option/index.md) | [jvm]<br>object [DefaultOption](-default-option/index.md)<br>Static holder for [defaultOption](-default-option/default-option.md) |

## Properties

| Name | Summary |
|---|---|
| [includeCompanion](include-companion.md) | [jvm]<br>val [includeCompanion](include-companion.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [includeIdentityHash](include-identity-hash.md) | [jvm]<br>val [includeIdentityHash](include-identity-hash.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [propertySorters](property-sorters.md) | [jvm]<br>val [propertySorters](property-sorters.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;out [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md)&lt;*&gt;&gt;&gt; |
| [sortNamesAlphabetic](sort-names-alphabetic.md) | [jvm]<br>val [sortNamesAlphabetic](sort-names-alphabetic.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toStringPreference](to-string-preference.md) | [jvm]<br>val [toStringPreference](to-string-preference.md): [ToStringPreference](../-to-string-preference/index.md) |
