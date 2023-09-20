//[kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringClassOption](index.md)/[propertySorters](property-sorters.md)

# propertySorters

[jvm]\
val [propertySorters](property-sorters.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;out [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md)&lt;*&gt;&gt;&gt;

#### Parameters

jvm

| | |
|---|---|
| propertySorters | One or more [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.md) implementing classes can be specified to have properties sorted in output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md). Default is none (no explicit sorting order). These will be applied in order, like SQL multi-column sorting.<br>So if the 1st sorter yields an equal result for a pair of properties, the 2nd will be applied, and so on until a non-zero result is obtained, of until the [propertySorters](property-sorters.md) are exhausted.<br>-     This sorting is applied after alphabetic sorting is applied. The sorting is stable, so if the [propertySorters](property-sorters.md) yield an equal value, the alphabetic sorting is preserved. -     Usage of [propertySorters](property-sorters.md) may have a significant effect on CPU and memory footprint of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md). |
