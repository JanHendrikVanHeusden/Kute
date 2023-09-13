package nl.kute.util

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

class StringUtilTest {

    @Test
    fun `test joinIfNotEmpty`() {
        assertThat(joinIfNotEmpty("-", "1", "2", "3")).isEqualTo("1-2-3")
        assertThat(joinIfNotEmpty("", "1", "2", "3")).isEqualTo("123")
        assertThat(joinIfNotEmpty("x", "1", "", "3")).isEqualTo("1x3")
        assertThat(joinIfNotEmpty("xYz", "abc", "def", "gHi")).isEqualTo("abcxYzdefxYzgHi")
        assertThat(joinIfNotEmpty("")).isEmpty()
        assertThat(joinIfNotEmpty("sep")).isEmpty()
        assertThat(joinIfNotEmpty("-", "1")).isEqualTo("1")
        assertThat(joinIfNotEmpty("-", "", "2")).isEqualTo("2")
        assertThat(joinIfNotEmpty(" ", " ", "", " ")).isEqualTo("   ")
    }

    @Test
    fun `test joinIfNotEmpty with random strings`() {
        // arrange
        val stringArgs = mutableListOf<String>().also { list ->
            repeat(10) {
                list.add(RandomStringUtils.random(Random.nextInt(1, 20)))
            }
        }.toTypedArray()
        val separator = RandomStringUtils.random(3)
        // act, assert
        assertThat(joinIfNotEmpty(separator, *stringArgs))
            .isEqualTo(stringArgs.joinToString(separator = separator))
    }

    @Test
    fun `test takeAndEllipse`() {
        val nullStr: String? = null
        assertThat(nullStr.takeAndEllipse(1)).isNull()

        val randomStr10 = RandomStringUtils.random(10)
        assertThat(randomStr10.takeAndEllipse(-1)).isEqualTo(randomStr10)
        assertThat(randomStr10.takeAndEllipse(Int.MIN_VALUE)).isEqualTo(randomStr10)

        assertThat(randomStr10.takeAndEllipse(10)).isEqualTo(randomStr10)
        assertThat(randomStr10.takeAndEllipse(12)).isEqualTo(randomStr10)
        assertThat(randomStr10.takeAndEllipse(Int.MAX_VALUE)).isEqualTo(randomStr10)

        (0..9).forEach {
            assertThat(randomStr10.takeAndEllipse(it)).isEqualTo(randomStr10.take(it) + "...")
        }
    }
}