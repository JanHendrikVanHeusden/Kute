package nl.kute.test.helper

import org.assertj.core.api.AbstractStringAssert
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
 * Assertion that the given input String:
 *  * is prefixed / suffixed by [withPrefix] / [withSuffix], if not blank,
 *  * contains **only** the [strings] and the prefix and suffix,
 *  * with [ignoreChars] ignored *after* the [strings] and [withPrefix] / [withSuffix] are matched.
 * > The assertion is exhaustive, meaning that when [withPrefix], [withSuffix] and [strings] are removed from the
 * > input String, and all characters of [ignoreChars] are removed, the remaining String should be empty.
 * * The values of [withPrefix] and [withSuffix] may or may not also be present in the [strings] arguments; this does
 *   not affect the assertion result.
 * @return if the assertion succeeds, the resulting [AbstractStringAssert];
 * otherwise an [AssertionError] is thrown
 */
fun AbstractStringAssert<*>.containsExhaustiveInAnyOrder(
    vararg strings: String,
    ignoreChars: String = "",
    withPrefix: String = "",
    withSuffix: String = ""): AbstractStringAssert<*> =
    this.`is`(containsExhaustiveInAnyOrderCondition(*strings,
        ignoreChars = ignoreChars,
        withPrefix = withPrefix,
        withSuffix = withSuffix)
    )

private fun containsExhaustiveInAnyOrderCondition(
    vararg strings: String,
    ignoreChars: String = "",
    withPrefix: String = "",
    withSuffix: String = ""
): Condition<String> {

    var remainingString: String
    var errorMessage: String? = null
    return object: Condition<String>() {
        override fun matches(string: String): Boolean {
            try {
                assertThat(string)
                    .startsWith(withPrefix)
                    .endsWith(withSuffix)
                    .contains(*(if (strings.isEmpty()) arrayOf("") else strings))
            } catch (e: AssertionError) {
                errorMessage = e.message
                throw e
            }
            remainingString = string.removePrefix(withPrefix).removeSuffix(withSuffix)
            strings.forEach { remainingString = remainingString.replace(it, "") }
            if (ignoreChars.isNotEmpty()) {
                ignoreChars.forEach {
                    remainingString = remainingString.replace(it.toString(), "")
                }
            }
            if (remainingString.isNotEmpty()) {
                val ignoreCharMsg = if (ignoreChars.isEmpty()) "" else ", and with characters of `$ignoreChars` ignored"
                errorMessage =
                    "a String containing exactly ${strings.contentDeepToString()}" +
                            " (with prefix and suffix, if specified$ignoreCharMsg), but it also contains " +
                            if (remainingString.isBlank()) "${remainingString.length} whitespace characters"
                            else "the characters of `$remainingString`"
            }

            return remainingString.isEmpty()
        }
    }.`as` { errorMessage }
}
