---
title: propertySorters
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.option](../index.html)/[AsStringClassOption](index.html)/[propertySorters](property-sorters.html)



# propertySorters



[jvm]\
val [propertySorters](property-sorters.html): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;out [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;out [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html)&lt;*&gt;&gt;&gt;



#### Parameters


jvm

| | |
|---|---|
| propertySorters | One or more [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html) implementing classes can be specified to have properties sorted in output of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html). Default is none (no explicit sorting order). These will be applied in order, like SQL multi-column sorting.<br>So if the 1st sorter yields an equal result for a pair of properties, the 2nd will be applied, and so on until a non-zero result is obtained, of until the [propertySorters](property-sorters.html) are exhausted.<br>-     This sorting is applied after alphabetic sorting is applied. The sorting is stable, so if the [propertySorters](property-sorters.html) yield an equal value, the alphabetic sorting is preserved. -     Usage of [propertySorters](property-sorters.html) may have effect on CPU and memory footprint of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html). -     Any exceptions that may occur during evaluation of a [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html) are ignored, and: -     The exception will be logged -     The [PropertyRankable](../../nl.kute.asstring.property.ranking/-property-rankable/index.html) will be removed from the registry, to avoid further exceptions |




