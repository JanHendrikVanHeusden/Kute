package nl.kute.core.property.ranking

/**
 * Class to explicitly specify that properties need not be ordered
 */
public class NoOpPropertyRanking private constructor() : PropertyRanking() {

    /** @return A constant value regardless of [propertyValueMetaData] input;
     * so effectively, it will not contribute to ordering
     */
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int = 0

    public companion object {
        /** Singleton instance of [NoOpPropertyRanking] */
        public val instance: NoOpPropertyRanking = NoOpPropertyRanking()
    }
}
