package nl.kute.core.property.ranking

import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.core.namedvalues.namedProp
import java.util.EnumSet

/**
 * Provides ranking for ordering properties in [nl.kute.core.asString] output,
 * based on T-shirt sizing by means of [ValueLengthRanking]
 * > The ranking is not based on exact lengths, that would give a really unstable ordering;
 * > so it's using length categories by some more or less arbitrary common sense length categories
 * @see [ValueLengthRanking]
 */
public open class PropertyRankingByLength private constructor(): PropertyRanking() {
    /** @return A numeric rank based on [PropertyValueMetaData.stringValueLength] and T-shirt sizes as of [ValueLengthRanking] */
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int =
        ValueLengthRanking.getRank(propertyValueMetaData.stringValueLength).rank

    public companion object {
        /** Singleton instance of [PropertyRankingByLength] */
        @Suppress("unused") // will be called reflectively
        public var instance: PropertyRankingByLength = PropertyRankingByLength()
    }

}

/**
 * [ValueLengthRanking] provides a somewhat arbitrary classification of value lengths, ranging from [S] to [XXL],
 * each with a [rank] and a [lengthRange].
 * > The [lengthRange]s fully cover the [Int] values from `0` to [Int.MAX_VALUE], but do not overlap.
 * @param [rank] The relative weight associated with the [ValueLengthRanking], ranging up from [S] to [XXL]
 * @param [lengthRange] The range (min inclusive, max inclusive) associated with the [ValueLengthRanking]
 */
@Suppress("KDocMissingDocumentation") // names [S] to [XXL] should be descriptive enough
@AsStringClassOption(toStringPreference = ToStringPreference.USE_ASSTRING, includeIdentityHash = false)
public enum class ValueLengthRanking(public val rank: Int, public val lengthRange: IntRange) {
    S(10, 0..25),
    M(20, S.lengthRange.last + 1..50),
    L(30, M.lengthRange.last + 1..100),
    XL(40, L.lengthRange.last + 1..200),
    XXL(50, XL.lengthRange.last + 1..Int.MAX_VALUE)
    ;

    public companion object {
        /** The full [Set] of [ValueLengthRanking]s */
        public val valueLengthRankings: EnumSet<ValueLengthRanking> = EnumSet.allOf(ValueLengthRanking::class.java)

        /**
         * Get the [ValueLengthRanking] where the [length] fits into the [ValueLengthRanking.lengthRange]
         * @param [length] The length to retrieve the [ValueLengthRanking] for
         * @return The [ValueLengthRanking] associated with the length;
         * > when [length] has an unexpected value (`null` or negative), [S] is returned
         */
        public fun getRank(length: Int?): ValueLengthRanking =
            valueLengthRankings.firstOrNull { (length ?: 0) in it.lengthRange } ?: S

        /** The [ValueLengthRanking] with the lowest [rank] */
        public val minRank: Int = valueLengthRankings.minOf { it.rank }
        /** The [ValueLengthRanking] with the highest [rank] */
        public val maxRank: Int = valueLengthRankings.maxOf { it.rank }

        @JvmSynthetic // avoid access from external Java code
        internal fun String.getRank(): ValueLengthRanking = getRank(this.length)
    }

    private val asStringBuilder = asStringBuilder()
        .withOnlyProperties()
        .withAlsoNamed(namedProp(this::name), namedProp(this::rank), namedProp(this::lengthRange))
        .build()

    public override fun toString(): String = asStringBuilder.asString()
}