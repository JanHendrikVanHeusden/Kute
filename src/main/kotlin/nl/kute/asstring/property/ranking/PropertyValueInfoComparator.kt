package nl.kute.asstring.property.ranking

/**
 * [Comparator] for comparing or sorting [PropertyValueMetaData] objects by the given [rankables],
 * where the first [rankables] instance has the greatest weight (more or less like SQL multi-column sorting).
 *
 * **NB:** *The comparison is **not** consistent with equality of [PropertyValueMetaData]!
 * I.e., non-equal values can (by design) result in equal outcome of the comparison.*
 * > *So don't use this [Comparator] for [toSortedSet] or [toSortedMap]: you may lose entries,
 * these functions skip keys that have equal sort order (keeping last one only, usually).*
 */
internal class PropertyValueInfoComparator(private vararg val rankables: PropertyRankable<*> = emptyArray()) :
    Comparator<PropertyValueMetaData?> {

    /**
     * Compare the given [PropertyValueMetaData] [meta1] and [meta2] by subsequently applying each
     * [PropertyRankable.getRank] until a non-zero compare result is found, or until the [rankables] are exhausted.
     *
     * **NB:** *The comparison is **not** consistent with equality of [PropertyValueMetaData]!
     * See the KDoc of this class [PropertyValueInfoComparator] for details & usage warnings.*
     * @return The compare result of the first [rankables] that yields a non-zero compare result;
     * or `0` if none of the [rankables] yields a non-zero result
     */
    override fun compare(meta1: PropertyValueMetaData?, meta2: PropertyValueMetaData?): Int {
        if (meta1 == meta2) return 0
        if (meta1 == null) return -1
        if (meta2 == null) return 1
        rankables.forEach { rankable ->
            rankable.getRank(meta1).compareTo(rankable.getRank(meta2)).let {
                if (it != 0) {
                    return it
                }
            }
        }
        return 0
    }

}