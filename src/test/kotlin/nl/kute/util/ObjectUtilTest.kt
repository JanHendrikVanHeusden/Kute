package nl.kute.util

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ObjectUtilTest {

    @Test
    fun `identityHash should return 0 for null`() {
        assertThat(null.identityHash).isZero
    }

    @Test
    fun `identityHash should return System identityHashCode`() {
        val obj = Any()
        assertThat(obj.identityHash).isEqualTo(System.identityHashCode(obj))
    }

    @Test
    fun `identityHashHex should return 0 for null`() {
        assertThat(null.identityHashHex).isEqualTo("0")
    }

    @Test
    @Disabled("Result depends on JVM, so no way to guarantee that it will produce" +
            " any identity hashes with leading zeros." +
            " So not running automatically. Run this test manually if needed" +
            " (it is reasonably safe though to assume it will produce leading zeros).")
    fun `identityHashHex should remove leading zeros`() {
        var foundLeadingZero = false
        var counter = 0
        // When trying hard enough, we should encounter a value with leading 0
        // Typically within 5 to 50 attempts.
        while (!foundLeadingZero && counter <= 10000) {
            val randomString = RandomStringUtils.randomAlphabetic(5)
            counter++
            val identityHashHex = randomString.identityHashHex
            if (identityHashHex.length < 8) {
                foundLeadingZero = true
                println("succeeded after $counter attempts")
            }
        }
        assertThat(foundLeadingZero).isTrue
    }
}