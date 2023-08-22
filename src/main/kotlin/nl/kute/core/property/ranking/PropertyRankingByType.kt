package nl.kute.core.property.ranking

/**
 * Provides ranking for ordering properties in [nl.kute.core.asString] output, based
 * on [PropertyValueMetaData.returnType].
 * Intended mainly to keep known basic types with not too long String-representations ordered first
 * > E.g. [Number], [java.util.Date], [Char], [Boolean], [java.time.temporal.Temporal] etc.; see [nl.kute.core.isBaseType]
 * @see [ValueLengthRanking]
 */
public open class PropertyRankingByType private constructor(): PropertyRanking() {
    /** @return A low value if it's a base-type and not a [CharSequence]; a high value otherwise */
    public override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int {
        return if (propertyValueMetaData.isBaseType && !propertyValueMetaData.isCharSequence) 0
        else 10
    }

    override fun instance(): PropertyRankingByType = instance

    public companion object {
        /** Singleton instance of [PropertyRankingByType] */
        public val instance: PropertyRankingByType = PropertyRankingByType()
    }
}

@Suppress("unused") // construct instance to have it registered
private val propertyRankingByType = PropertyRankingByType.instance
    .also { it.register() }