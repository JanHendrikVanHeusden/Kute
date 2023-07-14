package nl.kute.util

import org.apache.commons.lang3.RandomStringUtils
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

    @Test
    fun hexStringToInt() {
        assertThat(hexStringToInt("0")).isZero
        assertThat(hexStringToInt("1")).isEqualTo(1)
        assertThat(hexStringToInt("a")).isEqualTo(10)
        assertThat(hexStringToInt("1")).isEqualTo(1)
        assertThat(hexStringToInt("7fffffff")).isEqualTo(Int.MAX_VALUE)
        assertThat(hexStringToInt("fffffff0")).isEqualTo(-16)
        assertThat(hexStringToInt("80000000")).isEqualTo(Int.MIN_VALUE)

        repeat(100) {
            val input = RandomStringUtils.random(7, "0123456789abcdef").trimStart('0').ifBlank { "0" }
            val actual: Int = hexStringToInt(input)
            val checkValue: String = Integer.toHexString(actual)
            assertThat(checkValue)
                .`as`("Parsing hex $actual back to decimal should yield the same value $input, but was $checkValue")
                .isEqualTo(input)
        }
    }

    @Test
    fun hexStringToLong() {
        assertThat(hexStringToLong("0")).isZero
        assertThat(hexStringToLong("1")).isEqualTo(1)
        assertThat(hexStringToLong("a")).isEqualTo(10)
        assertThat(hexStringToLong("1")).isEqualTo(1)
        assertThat(hexStringToLong("7fffffffffffffff")).isEqualTo(Long.MAX_VALUE)
        assertThat(hexStringToLong("fffffffffffffff0")).isEqualTo(-16)
        assertThat(hexStringToLong("8000000000000000")).isEqualTo(Long.MIN_VALUE)

        repeat(100) {
            val input = RandomStringUtils.random(15, "0123456789abcdef").trimStart('0').ifBlank { "0" }
            val actual: Long = hexStringToLong(input)
            val checkValue: String = java.lang.Long.toHexString(actual)
            assertThat(checkValue)
                .`as`("Parsing hex $actual back to decimal should yield the same value $input, but was $checkValue")
                .isEqualTo(input)
        }
    }
}