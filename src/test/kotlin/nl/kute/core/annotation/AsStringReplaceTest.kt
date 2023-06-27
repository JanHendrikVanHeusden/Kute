package nl.kute.core.annotation

import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.modify.replacePattern
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringReplaceTest {

    @Test
    fun `regex replacement should honour back reference capturing`() {
        // fake Maltese IBAN number
        val input = "MT52QCGK45148414861965929692444"
        val replacementSpec = AsStringReplace(
            pattern = """^([A-Z]{2})\d{2}([A-Z]{4}\d{3})\d+?(\d{3})$""",
            replacement = """$1\00$2*****$3"""
        )
        val replaced = replacementSpec.replacePattern(input)
        // just a note: replacement like this could also be accomplished
        // using 2 subsequent @AsStringMask annotations
        assertThat(replaced).isEqualTo("MT00QCGK451*****444")
    }

    @Test
    fun `invalid regex should cause empty return value and log exception`() {
        TODO("To be implemented yet")
    }

}