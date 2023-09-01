package nl.kute.asstring.property.ranking

/**
 * Provides ranking for ordering properties in [nl.kute.asstring.core.asString] output, based
 * on [PropertyValueMetaData.returnType].
 * Intended mainly to keep known basic types with not too long String-representations ordered first
 * > E.g. [Number], [java.util.Date], [Char], [Boolean], [java.time.temporal.Temporal] etc.; see [nl.kute.asstring.core.isBaseType]
 * @see [ValueLengthRanking]
 */
public open class PropertyRankingByType private constructor(): PropertyRanking() {
    /** @return A low value if it's a base-type and not a [CharSequence]; a high value otherwise */
    public override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int {
        return if (propertyValueMetaData.isBaseType && !propertyValueMetaData.isCharSequence) 0
        else 10
    }

    public companion object {
        /** Singleton instance of [PropertyRankingByType] */
        @Suppress("unused") // will be called reflectively
        public val instance: PropertyRankingByType = PropertyRankingByType()
    }
}