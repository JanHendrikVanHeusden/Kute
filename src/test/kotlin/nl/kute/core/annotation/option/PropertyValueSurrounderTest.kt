package nl.kute.core.annotation.option

import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.ALINEA_SIGNS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.ASTERISKS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.BRACES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.BROKEN_PIPES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.CARETS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.DIVISION_SIGNS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.DOUBLE_QUOTES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.GUILLEMETS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.HASH_SIGNS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.PARAGRAPHS
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.PARENTHESES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.PIPES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.SINGLE_QUOTES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.TILDES
import nl.kute.core.annotation.option.PropertyValueSurrounder.Companion.surroundBy
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PropertyValueSurrounderTest {

    @Test
    fun `non-null String should be surrounded by prefix and postfix`() {
        PropertyValueSurrounder.entries.forEach {
            val str = RandomStringUtils.random(10)
            assertThat(str.surroundBy(it))
                .`as`("$it should surround String by ${it.prefix} & ${it.postfix}")
                .isEqualTo(it.prefix + str + it.postfix)
        }
    }

    @Test
    fun `null String should be surrounded by prefix and postfix`() {
        val nullString: String? = null
        PropertyValueSurrounder.entries.forEach {
            assertThat(nullString.surroundBy(it))
                .`as`("$it should surround null by ${it.prefix} & ${it.postfix}")
                .isEqualTo(it.prefix + "null" + it.postfix)
        }
    }

    @Test
    fun `PropertyValueSurrounder names should be WYSIWIG`() {
        PropertyValueSurrounder.entries.filterNot { it.name.matches(Regex("[A-Z_]+")) }
            .forEach {
                assertThat(it.name)
                    .`as`("${it.name} should be ${it.prefix}${it.postfix}")
                    .isEqualTo(it.prefix + it.postfix)
            }
    }

    @Test
    fun `Java alternatives should match the corresponding values`() {
        assertThat(GUILLEMETS).isSameAs(PropertyValueSurrounder.`«»`)
        assertThat(CARETS).isSameAs(PropertyValueSurrounder.`^^`)
        assertThat(TILDES).isSameAs(PropertyValueSurrounder.`~~`)
        assertThat(BROKEN_PIPES).isSameAs(PropertyValueSurrounder.`¦¦`)
        assertThat(PIPES).isSameAs(PropertyValueSurrounder.`||`)
        assertThat(PARAGRAPHS).isSameAs(PropertyValueSurrounder.`§§`)
        assertThat(ALINEA_SIGNS).isSameAs(PropertyValueSurrounder.`¶¶`)
        assertThat(DIVISION_SIGNS).isSameAs(PropertyValueSurrounder.`÷÷`)
        assertThat(HASH_SIGNS).isSameAs(PropertyValueSurrounder.`##`)
        assertThat(SINGLE_QUOTES).isSameAs(PropertyValueSurrounder.`''`)
        assertThat(DOUBLE_QUOTES).isSameAs(PropertyValueSurrounder.`""`)
        assertThat(PARENTHESES).isSameAs(PropertyValueSurrounder.`()`)
        assertThat(BRACES).isSameAs(PropertyValueSurrounder.`{}`)
        assertThat(ASTERISKS).isSameAs(PropertyValueSurrounder.`**`)
    }

//    @Test
//    fun ``
}