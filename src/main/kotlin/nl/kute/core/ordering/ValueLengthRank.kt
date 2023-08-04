package nl.kute.core.ordering

import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.core.namedvalues.namedProp
import java.util.EnumSet

/**
 * [ValueLengthRank] provides a somewhat arbitrary classification of value lengths, ranging from [S] to [XXL],
 * each with a [rank] and a [lengthRange].
 * > The [lengthRange]s fully cover the [Int] values from `0` to [Int.MAX_VALUE], but do not overlap.
 * @param [rank] The relative weight associated with the [ValueLengthRank], ranging up from [S] to [XXL]
 * @param [lengthRange] The range (min inclusive, max inclusive) associated with the [ValueLengthRank]
 */
@Suppress("KDocMissingDocumentation") // names [S] to [XXL] should be descriptive enough
@AsStringClassOption(toStringPreference = ToStringPreference.USE_ASSTRING, includeIdentityHash = false)
public enum class ValueLengthRank(public val rank: UShort, public val lengthRange: IntRange) {
    S(10u, 0..25),
    M(20u, S.lengthRange.last + 1..50),
    L(30u, M.lengthRange.last + 1..100),
    XL(40u, L.lengthRange.last + 1..200),
    XXL(50u, XL.lengthRange.last + 1..Int.MAX_VALUE)
    ;

    public companion object {
        /** The full [Set] of [ValueLengthRank]s */
        public val valueLengthRanks: EnumSet<ValueLengthRank> = EnumSet.allOf(ValueLengthRank::class.java)

        /**
         * Get the [ValueLengthRank] where the [length] fits into the [ValueLengthRank.lengthRange]
         * @param [length] The length to retrieve the [ValueLengthRank] for
         */
        public fun getRank(length: Int?): ValueLengthRank =
            valueLengthRanks.firstOrNull { (length ?: 0) in it.lengthRange } ?: S
    }

    private val asStringBuilder = asStringBuilder()
        .withOnlyProperties()
        .withAlsoNamed(namedProp(this::name), namedProp(this::rank), namedProp(this::lengthRange))
        .build()

    public override fun toString(): String = asStringBuilder.asString()
}