package nl.kute.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
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
    fun `identityHashHex should remove leading zeros`() {
        var foundLeadingZero = false
        var counter = 0
        // When trying hard enough, we should encounter a value with leading 0
        // Typically within 5 to 50 attempts.
        while (!foundLeadingZero && ++counter <= 1000) {
            val obj = Any()
            val identityHashHex = obj.identityHashHex
            if (identityHashHex.length < 8) {
                foundLeadingZero = true
                // println("succeeded after $counter attempts")
            }
        }
        assumeThat(foundLeadingZero)
            .`as`("It is reasonably safe to assume that System.identityHashCode / identityHashHex" +
                    " will produce identity hashes with leading zeros (hex representation shorter than 8)." +
                    " But the implementation is JVM dependent, hence no guarantees there.\n" +
                    " So failing will be ignored (hence `assumeThat()`, not `assertThat()`.")
            .isTrue
    }
}