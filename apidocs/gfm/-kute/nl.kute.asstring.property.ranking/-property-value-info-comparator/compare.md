//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyValueInfoComparator](index.md)/[compare](compare.md)

# compare

[jvm]\
open override fun [compare](compare.md)(meta1: [PropertyValueMetaData](../-property-value-meta-data/index.md)?, meta2: [PropertyValueMetaData](../-property-value-meta-data/index.md)?): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Compare the given [PropertyValueMetaData](compare.md) and [meta2](compare.md) by subsequently applying each [PropertyRankable.getRank](../-property-rankable/get-rank.md) until a non-zero compare result is found, or until the rankables are exhausted.

**NB:***The comparison is* ***not*** *consistent with equality of* [*PropertyValueMetaData*](../-property-value-meta-data/index.md)*! See the KDoc of this class* [*PropertyValueInfoComparator*](index.md) *for details* & *usage warnings.*

#### Return

The compare result of the first rankables that yields a non-zero compare result; or `0` if none of the rankables yields a non-zero result
