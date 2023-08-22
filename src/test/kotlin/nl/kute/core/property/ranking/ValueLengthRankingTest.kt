package nl.kute.core.property.ranking

import nl.kute.core.property.ranking.ValueLengthRanking.L
import nl.kute.core.property.ranking.ValueLengthRanking.M
import nl.kute.core.property.ranking.ValueLengthRanking.S
import nl.kute.core.property.ranking.ValueLengthRanking.XL
import nl.kute.core.property.ranking.ValueLengthRanking.XXL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class ValueLengthRankingTest {

    @Test
    fun `getRank should give the expected rank for any positive integer`() {
        (0..250).forEach { valueLength ->
            val rank = ValueLengthRanking.getRank(valueLength)
            val expected = when {
                valueLength <= 25 -> S
                valueLength <= 50 -> M
                valueLength <= 100 -> L
                valueLength <= 200 -> XL
                else -> XXL
            }
            assertThat(rank).isSameAs(expected)
            assertThat(valueLength).isBetween(rank.lengthRange.first, rank.lengthRange.last)
        }
        assertThat(ValueLengthRanking.getRank(Int.MAX_VALUE)).isSameAs(XXL)
        (1..10).forEach { _ ->
            assertThat(ValueLengthRanking.getRank(Random.nextInt(250, Int.MAX_VALUE)))
                .isSameAs(XXL)
        }
    }

    @Test
    fun `getRank should return S for negative integer`() {
        // Should not happen, but if somehow a negative value would be entered, consider it as S
        assertThat(ValueLengthRanking.getRank(-1)).isSameAs(S)
        assertThat(ValueLengthRanking.getRank(Int.MIN_VALUE)).isSameAs(S)
        assertThat(ValueLengthRanking.getRank(Random.nextInt(Int.MIN_VALUE, -1))).isSameAs(S)
    }


}