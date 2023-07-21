package nl.kute.test.helper

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition

/** Convenience Regex-escaped String literal `\$`, for matching `$` characters in Regex String patterns. */
const val dollar = """\$"""

/**
 * @return AssertJ [Condition] to assert that the exact number of [theChar] characters in a String
 * is equal to [expectedCount].
 */
fun containsExactCharCount(theChar: Char, expectedCount: Int): Condition<String> {
    var actualCount = 0
    return object: Condition<String>() {
        override fun matches(string: String): Boolean {
            actualCount = string.count { it == theChar }
            return actualCount == expectedCount
        }
    }.`as` { "a String containing exactly $expectedCount `$theChar` character(s), but it contains $actualCount of it" }
}

/**
 * @return AssertJ [Condition] to assert that the exact number of `=` characters in a String
 * is equal to [expectedCount].
 * > Typically usage is to assert the number of properties captured in a [nl.kute.core.asString] return value,
 * > by counting the `=` characters.
 */
internal fun equalSignCount(expectedCount: Int): Condition<String> =
    containsExactCharCount('=', expectedCount)

fun containsExhaustiveInAnyOrder(
    vararg strings: String,
    ignoreChars: String = " ",
    ignorePrefix: String = "",
    ignoreSuffix: String = ""): Condition<String> {

    var strippedString: String
    var remainingString: String
    var errorMessage: String? = null
    return object: Condition<String>() {
        override fun matches(string: String): Boolean {
            strippedString = string.removePrefix(ignorePrefix).removeSuffix(ignoreSuffix)
            try {
                assertThat(strippedString).contains(*strings)
            } catch (e: AssertionError) {
                errorMessage = e.message
                throw e
            }
            remainingString = strippedString
            strings.forEach { remainingString = remainingString.replace(it, "") }
            if (ignoreChars.isNotEmpty()) {
                ignoreChars.forEach {
                    remainingString = remainingString.replace(it.toString(), "")
                }
                if (remainingString.isNotEmpty()) {
                    errorMessage =
                        "a String containing exactly ${strings.contentDeepToString()}" +
                                " (with prefix and suffix ignored, if specified), but it also contains " +
                                if (remainingString.isBlank()) "${remainingString.length} whitespace characters"
                                else "the characters of `$remainingString`"
                }
            }
            return remainingString.isEmpty()
        }
    }.`as` { errorMessage }
}
