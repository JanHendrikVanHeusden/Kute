package nl.kute.core.ordering

import nl.kute.config.stringJoinMaxCount
import nl.kute.core.property.PropertyValueInformation
import nl.kute.core.property.getPropValueString
import kotlin.reflect.KProperty

@JvmSynthetic // avoid access from external Java code
internal fun Map<KProperty<*>, Set<Annotation>>.joinAndSortToString(
    rankProvider: ((PropertyValueInformation?) -> PropertyOrderRanking)?,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1
): String {
    return if (rankProvider == null || rankProvider == NoOpPropertyRanking.rankProvider) {
        this.entries.joinToString(
            separator = separator,
            prefix = prefix,
            limit = stringJoinMaxCount
        ) { entry ->
            val prop = entry.key
            val annotationSet = entry.value
            "${prop.name}=${getPropValueString(prop, annotationSet).first}"
        }
    } else {
        this.entries.asSequence()
            .map { entry ->
                val prop = entry.key
                val annotationSet = entry.value
                getPropValueString(prop, annotationSet)
            }.sortedBy { rankProvider(it.second).rank }
            .map { "${it.second!!.propertyName}=${it.first}" }
            .joinToString(
                separator = separator,
                prefix = prefix,
                limit = stringJoinMaxCount
            )
    }
}
