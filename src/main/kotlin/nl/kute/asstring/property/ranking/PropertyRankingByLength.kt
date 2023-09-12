package nl.kute.asstring.property.ranking

import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.core.asString
import nl.kute.asstring.property.meta.PropertyValueMetaData
import nl.kute.asstring.property.ranking.ValueLengthRanking.S
import nl.kute.asstring.property.ranking.ValueLengthRanking.XXL
import java.util.EnumSet

/**
 * Provides ranking for ordering properties in [nl.kute.asstring.core.asString] output,
 * based on T-shirt sizing by means of [ValueLengthRanking]
 * > The ranking is not based on exact lengths, that would give an unstable ordering;
 * > so the ranking is based on length categories by some more or less arbitrary length categories
 * @see [ValueLengthRanking]
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
@AsStringClassOption(includeCompanion = false)
public open class PropertyRankingByLength private constructor(): PropertyRanking() {
    /** @return A numeric rank based on [PropertyValueMetaData.stringValueLength] and T-shirt sizes as of [ValueLengthRanking] */
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int =
        ValueLengthRanking.getRank(propertyValueMetaData.stringValueLength).rank

    public companion object {
        /** Singleton instance of [PropertyRankingByLength] */
        @Suppress("unused") // will be called reflectively
        @AsStringOmit
        public var instance: PropertyRankingByLength = PropertyRankingByLength()
    }
}

/**
 * [ValueLengthRanking] provides a somewhat arbitrary classification of value lengths, ranging from [S] to [XXL],
 * each with a [rank] and a [lengthRange].
 * > The [lengthRange]s fully cover the [Int] values from `0` to [Int.MAX_VALUE], without overlap.
 * @param [rank] The relative weight associated with the [ValueLengthRanking], ranging up from [S] to [XXL]
 * @param [lengthRange] The range (min inclusive, max inclusive) associated with the [ValueLengthRanking]
 * @see [PropertyRankingByLength]
 */
@AsStringClassOption(includeIdentityHash = false, includeCompanion = false)
public enum class ValueLengthRanking(public val rank: Int, public val lengthRange: IntRange) {
    /** Ranking for Strings of lengths 0..25 (inclusive) */
    S(10, 0..25),
    /** Ranking for Strings of lengths `26..50` (inclusive) */
    M(20, S.lengthRange.last + 1..50),
    /** Ranking for Strings of lengths `51..100` (inclusive) */
    L(30, M.lengthRange.last + 1..100),
    /** Ranking for Strings of lengths `101..200` (inclusive) */
    XL(40, L.lengthRange.last + 1..200),
    /** Ranking for Strings of lengths `200` or greater */
    XXL(50, XL.lengthRange.last + 1..Int.MAX_VALUE)
    ;

    public companion object {
        /** The full [Set] of [ValueLengthRanking]s */
        private val valueLengthRankings: EnumSet<ValueLengthRanking> = EnumSet.allOf(ValueLengthRanking::class.java)

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

    public override fun toString(): String = asString()
}
