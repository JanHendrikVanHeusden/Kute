@file:JvmName("TestHelper")

package nl.kute.test.helper

/** Convenience Regex-escaped String literal `\$`, for matching `$` characters in Regex String patterns. */
const val dollar = """\$"""

/**
 * Trims the first character off the String if that character `==` [charToTrim].
 * > `null` safe.
 * @return If the first character is [charToTrim], a new String with the first character of the input String trimmed;
 * otherwise, the original input String
 */
fun String?.trimFirstIf(charToTrim: Char): String? {
    return if (this == null) null else {
        if (this.startsWith(charToTrim)) this.drop(1)  else this
    }
}

/**
 * Trims the last character off the String if that character `==` [charToTrim].
 * > `null` safe.
 * @return If the last character is [charToTrim], a new String without the last character of the input String trimmed;
 * otherwise, the original input String
 */
fun String?.trimLastIf(charToTrim: Char): String? {
    return if (this == null) null else {
        if (this.endsWith(charToTrim)) this.dropLast(1) else this
    }
}

