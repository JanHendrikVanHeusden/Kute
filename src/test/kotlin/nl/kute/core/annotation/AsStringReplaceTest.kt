package nl.kute.core.annotation

import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.modify.replacePattern
import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AsStringReplaceTest {

// Note:
// The replacement of regular expressions is done simply by means of String.replace(Regex(pattern), replacement)
// A couple of tests in this class address some Regex specific stuff, just to assert / demo that it works.
// It's not the intention of course to fully test all Regex possibilities.

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        resetStdOutLogger()
    }

    @Test
    fun `regex replacement should honour back reference capturing by number`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = """^([A-Z]{2})\d{2}([A-Z]{4}\d{3})\d+?(\d{3})$""",
            replacement = """$1\00$2*****$3""",
            isRegexpPattern = true
        )
        // act
        val replaced = replacementSpec.replacePattern(input)

        // assert
        // just a note: replacement like this could also be accomplished using 2 subsequent
        // @AsStringMask annotations; done that way, the String length is preserved
        assertThat(replaced).isEqualTo("MT00QCGK451*****444")
    }

    @Test
    fun `regex replacement should honour back reference capturing by name`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = """^(?<country>[A-Z]{2}).+$""",
            replacement = "\${country}",
        )
        // act
        val replaced = replacementSpec.replacePattern(input)

        // assert
        assertThat(replaced).isEqualTo("MT")
    }

    @Test
    fun `AsStringReplace should default to regex replacement`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = """\d+$""",
            replacement = """*"""
        )
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEqualTo("MT52QCGK*")
    }

    @Test
    fun `regex replacement should replace all matching occurrences`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = """\d""",
            replacement = "-"
        )
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEqualTo("MT--QCGK-----------------------")
    }

    @Test
    fun `regex replacement should remove all matching occurrences when no replacement specified`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = """\d"""
        )
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEqualTo("MTQCGK")
    }

    @Test
    fun `literal replacement should replace all matching occurrences`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = "1",
            replacement = "#",
            isRegexpPattern = false
        )
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEqualTo("MT52QCGK45#484#486#965929692444")
    }

    @Test
    fun `literal replacement should remove all matching occurrences when no replacement specified`() {
        // arrange
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = "1",
            isRegexpPattern = false
        )
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEqualTo("MT52QCGK45484486965929692444")
    }

    @Test
    fun `invalid regex should cause empty return value and log exception`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }
        val input = "something"
        val replacementSpec = AsStringReplace(pattern = "(")
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEmpty()
        assertThat(logMsg).contains("PatternSyntaxException occurred")
    }

    @Test
    fun `invalid regex replacement should cause empty return value and log exception`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }
        val input = "something"
        val replacementSpec = AsStringReplace(pattern = "some", replacement = "$1")
        // act
        val replaced = replacementSpec.replacePattern(input)
        // assert
        assertThat(replaced).isEmpty()
        assertThat(logMsg)
            .contains("Exception occurred")
            .contains("No group 1")
    }
}