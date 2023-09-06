//[Kute](../../index.md)/[nl.kute.asstring.property.ranking](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [NoOpPropertyRanking](-no-op-property-ranking/index.md) | [jvm]<br>class [NoOpPropertyRanking](-no-op-property-ranking/index.md) : [PropertyRanking](-property-ranking/index.md)<br>Class to explicitly specify that properties need not be ordered |
| [PropertyRankable](-property-rankable/index.md) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(toStringPreference = ToStringPreference.USE_ASSTRING)<br>interface [PropertyRankable](-property-rankable/index.md)&lt;out [T](-property-rankable/index.md) : [PropertyRankable](-property-rankable/index.md)&lt;[T](-property-rankable/index.md)&gt;&gt;<br>Interface to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.md) output. |
| [PropertyRanking](-property-ranking/index.md) | [jvm]<br>abstract class [PropertyRanking](-property-ranking/index.md) : [PropertyRankable](-property-rankable/index.md)&lt;[PropertyRanking](-property-ranking/index.md)&gt; <br>Abstract base class to provide ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.md) output. |
| [PropertyRankingByCommonNames](-property-ranking-by-common-names/index.md) | [jvm]<br>open class [PropertyRankingByCommonNames](-property-ranking-by-common-names/index.md) : [PropertyRanking](-property-ranking/index.md)<br>Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.md) output, based on common property names and suffixes, combined with value lengths. |
| [PropertyRankingByLength](-property-ranking-by-length/index.md) | [jvm]<br>open class [PropertyRankingByLength](-property-ranking-by-length/index.md) : [PropertyRanking](-property-ranking/index.md)<br>Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.md) output, based on T-shirt sizing by means of [ValueLengthRanking](-value-length-ranking/index.md) |
| [PropertyRankingByType](-property-ranking-by-type/index.md) | [jvm]<br>open class [PropertyRankingByType](-property-ranking-by-type/index.md) : [PropertyRanking](-property-ranking/index.md)<br>Provides ranking for ordering properties in [nl.kute.asstring.core.asString](../nl.kute.asstring.core/as-string.md) output, based on [PropertyValueMetaData.returnType](-property-value-meta-data/return-type.md). Intended mainly to keep known basic types with not too long `toString()`-representations ordered first |
| [PropertyValueMeta](-property-value-meta/index.md) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(toStringPreference = ToStringPreference.PREFER_TOSTRING)<br>class [PropertyValueMeta](-property-value-meta/index.md)(propertyValue: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?, objectClass: [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;*&gt;?, property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)&lt;*&gt;, val stringValueLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)?) : [PropertyValueMetaData](-property-value-meta-data/index.md)<br>Metadata about a property and the property's value |
| [PropertyValueMetaData](-property-value-meta-data/index.md) | [jvm]<br>interface [PropertyValueMetaData](-property-value-meta-data/index.md)<br>Interface for metadata about a property and the property's value |
| [ValueLengthRanking](-value-length-ranking/index.md) | [jvm]<br>@[AsStringClassOption](../nl.kute.asstring.annotation.option/-as-string-class-option/index.md)(includeIdentityHash = false, includeCompanion = false)<br>enum [ValueLengthRanking](-value-length-ranking/index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ValueLengthRanking](-value-length-ranking/index.md)&gt; <br>[ValueLengthRanking](-value-length-ranking/index.md) provides a somewhat arbitrary classification of value lengths, ranging from [S](-value-length-ranking/-s/index.md) to [XXL](-value-length-ranking/-x-x-l/index.md), each with a [rank](-value-length-ranking/rank.md) and a [lengthRange](-value-length-ranking/length-range.md). |