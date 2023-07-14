package nl.kute.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import kotlin.random.Random

class HexUtilTest {

    @Test
    fun intAsHexString() {
        val nullInt: Int? = null
        assertThat(nullInt.asHexString).isEqualTo("0")
        assertThat(0.asHexString).isEqualTo("0")
        assertThat(1.asHexString).isEqualTo("1")
        assertThat(10.asHexString).isEqualTo("a")
        assertThat(16.asHexString).isEqualTo("10")
        assertThat(Int.MAX_VALUE.asHexString).isEqualTo("7fffffff")
        assertThat((-16).asHexString).isEqualTo("fffffff0")
        assertThat(Int.MIN_VALUE.asHexString).isEqualTo("80000000")

        repeat(100) {
            val input = Random.nextInt()
            val actual: String = input.asHexString
            val checkValue: Int = Integer.parseUnsignedInt(actual, 16)
            assertThat(checkValue)
                .`as`("Parsing hex $actual back to decimal should yield the same value $input, but was $checkValue")
                .isEqualTo(input)
        }
    }

    @Test
    fun longAsHexString() {
        val nullLong: Long? = null
        assertThat(nullLong.asHexString).isEqualTo("0")
        assertThat(0L.asHexString).isEqualTo("0")
        assertThat(1L.asHexString).isEqualTo("1")
        assertThat(10L.asHexString).isEqualTo("a")
        assertThat(16L.asHexString).isEqualTo("10")
        assertThat(Long.MAX_VALUE.asHexString).isEqualTo("7fffffffffffffff")
        assertThat((-16L).asHexString).isEqualTo("fffffffffffffff0")
        assertThat(Long.MIN_VALUE.asHexString).isEqualTo("8000000000000000")

        repeat(100) {
            val input = Random.nextLong()
            val actual: String = input.asHexString
            val checkValue: Long = java.lang.Long.parseUnsignedLong(actual, 16)
            assertThat(checkValue)
                .`as`("Parsing hex $actual back to decimal should yield the same value $input, but was $checkValue")
                .isEqualTo(input)
        }
    }

}