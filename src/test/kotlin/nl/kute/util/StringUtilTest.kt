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

    }
}