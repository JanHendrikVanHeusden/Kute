//[Kute](../../../index.md)/[nl.kute.asstring.property.ranking](../index.md)/[PropertyValueInfoComparator](index.md)

# PropertyValueInfoComparator

[jvm]\
class [PropertyValueInfoComparator](index.md)(rankables: [PropertyRankable](../-property-rankable/index.md)&lt;*&gt; = emptyArray()) : [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt; 

[Comparator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparator/index.html) for comparing or sorting [PropertyValueMetaData](../-property-value-meta-data/index.md) objects by the given rankables, where the first rankables instance has the highest weight (more or less like SQL multi-column sorting).

**NB:***The comparison is* ***not*** *consistent with equality of* [*PropertyValueMetaData*](../-property-value-meta-data/index.md)*! I.e., non-equal values can (by design) result in equal outcome of the comparison.*

*So don't use this* [*Comparator*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparator/index.html) *for* [*toSortedSet*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html) *or* [*toSortedMap*](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html)*: you may lose entries, as these skip keys when equal sort order.*

## Constructors

| | |
|---|---|
| [PropertyValueInfoComparator](-property-value-info-comparator.md) | [jvm]<br>constructor(vararg rankables: [PropertyRankable](../-property-rankable/index.md)&lt;*&gt; = emptyArray()) |

## Functions

| Name | Summary |
|---|---|
| [compare](compare.md) | [jvm]<br>open override fun [compare](compare.md)(meta1: [PropertyValueMetaData](../-property-value-meta-data/index.md)?, meta2: [PropertyValueMetaData](../-property-value-meta-data/index.md)?): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Compare the given [PropertyValueMetaData](compare.md) and [meta2](compare.md) by subsequently applying each [PropertyRankable.getRank](../-property-rankable/get-rank.md) until a non-zero compare result is found, or until the rankables are exhausted. |
| [reversed](index.md#208665987%2FFunctions%2F-1216412040) | [jvm]<br>open fun [reversed](index.md#208665987%2FFunctions%2F-1216412040)(): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt; |
| [thenComparing](index.md#1867086357%2FFunctions%2F-1216412040) | [jvm]<br>open fun [thenComparing](index.md#1867086357%2FFunctions%2F-1216412040)(p0: [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;in [PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt;): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt;<br>open fun &lt;[U](index.md#-956714852%2FFunctions%2F-1216412040) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[U](index.md#-956714852%2FFunctions%2F-1216412040)&gt;&gt; [thenComparing](index.md#-956714852%2FFunctions%2F-1216412040)(p0: [Function](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html)&lt;in [PropertyValueMetaData](../-property-value-meta-data/index.md)?, out [U](index.md#-956714852%2FFunctions%2F-1216412040)&gt;): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt;<br>open fun &lt;[U](index.md#1302379890%2FFunctions%2F-1216412040) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [thenComparing](index.md#1302379890%2FFunctions%2F-1216412040)(p0: [Function](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html)&lt;in [PropertyValueMetaData](../-property-value-meta-data/index.md)?, out [U](index.md#1302379890%2FFunctions%2F-1216412040)&gt;, p1: [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;in [U](index.md#1302379890%2FFunctions%2F-1216412040)&gt;): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt; |
| [thenComparingDouble](index.md#1450016452%2FFunctions%2F-1216412040) | [jvm]<br>open fun [thenComparingDouble](index.md#1450016452%2FFunctions%2F-1216412040)(p0: [ToDoubleFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/ToDoubleFunction.html)&lt;in [PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt;): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt; |
| [thenComparingInt](index.md#-561260000%2FFunctions%2F-1216412040) | [jvm]<br>open fun [thenComparingInt](index.md#-561260000%2FFunctions%2F-1216412040)(p0: [ToIntFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/ToIntFunction.html)&lt;in [PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt;): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt; |
| [thenComparingLong](index.md#1748179236%2FFunctions%2F-1216412040) | [jvm]<br>open fun [thenComparingLong](index.md#1748179236%2FFunctions%2F-1216412040)(p0: [ToLongFunction](https://docs.oracle.com/javase/8/docs/api/java/util/function/ToLongFunction.html)&lt;in [PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt;): [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)&lt;[PropertyValueMetaData](../-property-value-meta-data/index.md)?&gt; |
