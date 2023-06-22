package nl.kute.core.annotation

import nl.kute.core.annotation.modify.AsStringMask
import nl.kute.core.annotation.modify.mask
import org.apache.commons.lang3.RandomUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TestAsStringMask {

    // String of length ~335k
    private val longString = mutableListOf("abcdefghijklmnopqrstuvwxyz1234567890-=`~!@#$%^&€()_+{}[];:',./<>?")
        .also { list -> repeat(5000) { list.add(list[0]) } }.joinToString()

    @Test
    fun `test masking start and end positions`() {
        val maskDefault = AsStringMask()
        assertThat(maskDefault.mask("12345")).isEqualTo("*****")

        val maskLast = AsStringMask(startMaskAt = -1)
        assertThat(maskLast.mask("shown")).isEqualTo("show*")

        val maskLeft = AsStringMask(startMaskAt = 0, endMaskAt = -3)
        assertThat(maskLeft.mask("1234567890")).isEqualTo("*******890")

        val maskLeftBut1 = AsStringMask(startMaskAt = 1, endMaskAt = -3)
        assertThat(maskLeftBut1.mask("1234567890")).isEqualTo("1******890")

        val maskOverlap = AsStringMask(startMaskAt = 5, endMaskAt = -7)
        assertThat(maskOverlap.mask("1234567890"))
            .`as`("when start of mask beyond end of mask, everything should be masked")
            .isEqualTo("**********")

        val maskEnd0 = AsStringMask(endMaskAt = 0)
        assertThat(maskEnd0.mask("1234567890"))
            .`as`("when end of mask 0, everything should be masked")
            .isEqualTo("**********")

        val maskEnd1 = AsStringMask(endMaskAt = 1)
        assertThat(maskEnd1.mask("1234567890")).isEqualTo("*234567890")

        val maskStartBeforeStrEnd = AsStringMask(startMaskAt = 9)
        assertThat(maskStartBeforeStrEnd.mask("1234567890")).isEqualTo("123456789*")

        val maskStartAtStrEnd = AsStringMask(startMaskAt = 10)
        assertThat(maskStartAtStrEnd.mask("1234567890"))
            .`as`("when start of mask at highest index or beyond, everything should be masked")
            .isEqualTo("**********")

        val maskStartBeyondStrEnd = AsStringMask(startMaskAt = 11)
        assertThat(maskStartBeyondStrEnd.mask("1234567890"))
            .`as`("when start of mask at highest index or beyond, everything should be masked")
            .isEqualTo("**********")

        val maskAllNegative = AsStringMask(startMaskAt = -8, endMaskAt = -4)
        assertThat(maskAllNegative.mask("1234567890")).isEqualTo("12****7890")

        val maskExtremes = AsStringMask(startMaskAt = Int.MIN_VALUE, endMaskAt = Int.MAX_VALUE)
        assertThat(maskExtremes.mask("1234567890")).isEqualTo("**********")

        val maskWeirdExtremes = AsStringMask(startMaskAt = Int.MAX_VALUE, endMaskAt = Int.MIN_VALUE)
        assertThat(maskWeirdExtremes.mask("1234567890"))
            .`as`("When out of range values, everything should be masked")
            .isEqualTo("**********")

        val maskWeirdStart = AsStringMask(startMaskAt = Int.MIN_VALUE)
        assertThat(maskWeirdStart.mask("1234567890"))
            .`as`("When out of range start of mask, everything should be masked")
            .isEqualTo("**********")

        val maskWeirdEnd = AsStringMask(endMaskAt = Int.MAX_VALUE)
        assertThat(maskWeirdEnd.mask("1234567890"))
            .`as`("When out of range end of mask, everything should be masked")
            .isEqualTo("**********")

        val maskWithVeryLong = AsStringMask(startMaskAt = 2, endMaskAt = -5)
        with(maskWithVeryLong.mask(longString)!!) {
            assertThat(this)
                .hasSameSizeAs(longString)
                .startsWith(longString.take(2))
                .endsWith(longString.takeLast(3))
            assertThat(this.take(2).takeLast(1)).isNotEqualTo("*")
            assertThat(this.take(3).takeLast(1)).isEqualTo("*")
            assertThat(this.takeLast(5).take(1)).isNotEqualTo("*")
            assertThat(this.takeLast(6).take(1)).isEqualTo("*")
            repeat(10) {
                val index = RandomUtils.nextInt(3, longString.length - 6)
                assertThat(this[index]).isEqualTo('*')
            }
        }

    }

    @Test
    fun `test masking nulls`() {
        val maskDefault = AsStringMask()
        assertThat(maskDefault.mask(null)).isEqualTo("****")

        val maskNullsTrue = AsStringMask(maskNulls = true)
        assertThat(maskNullsTrue.mask(null)).isEqualTo("****")

        val maskNullsFalse = AsStringMask(maskNulls = false)
        assertThat(maskNullsFalse.mask(null)).isNull()

        val maskDifferentChar = AsStringMask(mask = '#')
        assertThat(maskDifferentChar.mask(null)).isEqualTo("####")
    }

    @Test
    fun `test masking with min max length`() {
        val maskMax5 = AsStringMask(maxLength = 5)
        assertThat(maskMax5.mask(longString)).isEqualTo("*****")

        val maskMin8 = AsStringMask(minLength = 8)
        assertThat(maskMin8.mask("abc")).isEqualTo("********")

        val maskConflictingLengths = AsStringMask(minLength = 8, maxLength = 3)
        assertThat(maskConflictingLengths.mask("12345"))
            .`as`("when conflicting, min length should be applied")
            .isEqualTo("********")
    }

    @Test
    fun `test masking with min max length and start, end`() {
        var theMask = AsStringMask(maxLength = 12, startMaskAt = 5, endMaskAt = 9)
        assertThat(theMask.mask("12345678901234567890")).isEqualTo("12345****012")

        theMask = AsStringMask(maxLength = 7, startMaskAt = 5, endMaskAt = 9)
        assertThat(theMask.mask("12345678901234567890"))
            .`as`("Masking should be applied first, then min/max length")
            .isEqualTo("12345**")

        theMask = AsStringMask(maxLength = 7, startMaskAt = 5, endMaskAt = -11)
        assertThat(theMask.mask("12345678901234567890"))
            .`as`("Masking should be applied first, then min/max length")
            .isEqualTo("12345**")
    }

    @Test
    fun `test different masking chars`() {
        // includes some multi-byte chars
        val someForeignChars = listOf('⒂', '¥', '¬')
        val someChars = (0..32).map { (it * it * it).toChar() }.toList()

        val str20 = "⒂¥¬ab cde 123 789_%@"

        someChars + someForeignChars + ('A'..'z').toList().forEach {
            val asStringMask = AsStringMask(mask = it)
            assertThat(asStringMask.mask(str20)).isEqualTo(it.toString().repeat(20))
        }
    }

}