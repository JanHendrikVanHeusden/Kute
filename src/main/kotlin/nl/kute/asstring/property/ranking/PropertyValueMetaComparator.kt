package nl.kute.asstring.property.ranking

import nl.kute.asstring.property.meta.PropertyValueMeta
import nl.kute.asstring.property.ranking.impl.NoOpPropertyRanking
import nl.kute.exception.handleException
import nl.kute.exception.throwableAsString
import nl.kute.logging.log
import nl.kute.reflection.util.simplifyClassName
import nl.kute.util.lineEnd

/**
 * [Comparator] for comparing or sorting [PropertyValueMeta] objects by the given [rankables],
 * where the first [rankables] instance has the greatest weight (more or less like SQL multi-column sorting).
 *
 * **NB:** *The comparison is **not** consistent with equality of [PropertyValueMeta]!
 * I.e., non-equal values can (by design) result in equal outcome of the comparison.*
 * > *So don't use this [Comparator] for [toSortedSet] or [toSortedMap]: you may lose entries,
 * these functions skip keys that have equal sort order (keeping last one only, usually).*
 */
internal class PropertyValueMetaComparator(private vararg val rankables: PropertyRankable<*> = emptyArray()) :
    Comparator<PropertyValueMeta?> {

    /**
     * Compare the given [PropertyValueMeta] [meta1] and [meta2] by subsequently applying each
     * [PropertyRankable.getRank] until a non-zero compare result is found, or until the [rankables] are exhausted.
     * > When an exception occurs when evaluating a [PropertyRankable.getRank]:
     *  * the exception will be handled
     *  * the [PropertyRankable] will effectively be removed from the registry
     *  * the [compare] method will return 0
     *  > See [evaluateRank]
     *
     * **NB:** *The comparison is **not** consistent with equality of [PropertyValueMeta]!
     * See the KDoc of this class [PropertyValueMetaComparator] for details & usage warnings.*
     *
     * @return The compare result of the first [rankables] that yields a non-zero compare result;
     * or `0` if none of the [rankables] yields a non-zero result
     */
    override fun compare(meta1: PropertyValueMeta?, meta2: PropertyValueMeta?): Int {
        if (meta1 == meta2) return 0
        if (meta1 == null) return -1
        if (meta2 == null) return 1
        rankables.forEach { rankable ->
            try {
                rankable.evaluateRank(meta1).compareTo(rankable.evaluateRank(meta2)).let { compareResult ->
                    if (compareResult != 0) {
                        return compareResult
                    }
                }
            } catch (e: Exception) {
                // already logged, ignore, let it return 0
            }
        }
        return 0
    }

    // wrapper around getRank() to log exception
    private fun PropertyRankable<*>.evaluateRank(meta: PropertyValueMeta) : Int =
        try {
            this.getRank(meta)
        } catch (e: Exception) {
            // Exceptions that occur by sorting may happen extremely frequently.
            // So to avoid that the logging flows over, we will replace the entry in the registry by a noop rankable
            // So when next property ordering occurs, no exception should occur anymore
            handleException(e) {
                propertyRankingRegistryByClass[this::class] = NoOpPropertyRanking.instance
                val className = this::class.simplifyClassName()
                log("$className threw exception while evaluating $meta." +
                            "The $className will be removed from the registry (so not used anymore):$lineEnd ${e.throwableAsString()}"
                )
            }
            throw e
        }
}