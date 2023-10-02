---
title: nl.kute.asstring.property.ranking
---
//[kute](../../index.html)/[nl.kute.asstring.property.ranking](index.html)



# Package-level declarations



## Types


| Name | Summary |
|---|---|
| [NoOpPropertyRanking](-no-op-property-ranking/index.html) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeCompanion = false)<br>class [NoOpPropertyRanking](-no-op-property-ranking/index.html) : [PropertyRanking](-property-ranking/index.html)<br>Class to explicitly specify that properties need not be ordered |
| [PropertyRankable](-property-rankable/index.html) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(toStringPreference = ToStringPreference.USE_ASSTRING)<br>interface [PropertyRankable](-property-rankable/index.html)&lt;out [T](-property-rankable/index.html) : [PropertyRankable](-property-rankable/index.html)&lt;[T](-property-rankable/index.html)&gt;&gt;<br>Interface to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output. |
| [PropertyRanking](-property-ranking/index.html) | [jvm]<br>abstract class [PropertyRanking](-property-ranking/index.html) : [PropertyRankable](-property-rankable/index.html)&lt;[PropertyRanking](-property-ranking/index.html)&gt; <br>Abstract base class to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output. |
| [PropertyRankingByCommonNames](-property-ranking-by-common-names/index.html) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeCompanion = true, toStringPreference = ToStringPreference.USE_ASSTRING)<br>open class [PropertyRankingByCommonNames](-property-ranking-by-common-names/index.html) : [PropertyRanking](-property-ranking/index.html)<br>Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output, based on common property names and suffixes, combined with value lengths. |
| [PropertyRankingByStringValueLength](-property-ranking-by-string-value-length/index.html) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeCompanion = false)<br>open class [PropertyRankingByStringValueLength](-property-ranking-by-string-value-length/index.html) : [PropertyRanking](-property-ranking/index.html)<br>Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output, based on T-shirt sizing by means of [ValueLengthRanking](-value-length-ranking/index.html) |
| [PropertyRankingByType](-property-ranking-by-type/index.html) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeCompanion = false)<br>open class [PropertyRankingByType](-property-ranking-by-type/index.html) : [PropertyRanking](-property-ranking/index.html)<br>Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.html) output, based on PropertyValueMeta.returnType. Intended mainly to keep known basic types with not too long `toString()`-representations ordered first |
| [ValueLengthRanking](-value-length-ranking/index.html) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.html)(includeIdentityHash = false, includeCompanion = false)<br>enum [ValueLengthRanking](-value-length-ranking/index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ValueLengthRanking](-value-length-ranking/index.html)&gt; <br>[ValueLengthRanking](-value-length-ranking/index.html) provides a somewhat arbitrary classification of value lengths, ranging from [S](-value-length-ranking/-s/index.html) to [XXL](-value-length-ranking/-x-x-l/index.html), each with a [rank](-value-length-ranking/rank.html) and a [lengthRange](-value-length-ranking/length-range.html). |

