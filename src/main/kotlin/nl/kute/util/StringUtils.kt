package nl.kute.util

/** @see [System.lineSeparator] */
@JvmSynthetic // avoid access from external Java code
internal val lineEnd: String = System.lineSeparator()

/**
 * Comparable to [joinToString], but empty [strings] entries are skipped (no additional separator there)
 * @return Non-empty [strings] separated by [separator]
 */
@JvmSynthetic // avoid access from external Java code
internal fun joinIfNotEmpty(separator: String, vararg strings: String): String =
    strings.asSequence().filterNot { it.isEmpty() }.joinToString(separator = separator)

/**
 * Takes the first [n] characters of a String and, if capped, appends ellipsis `...`
 * @return
 * * `null` if this String is `null`
 * * If this String is not longer than [n], the original String.
 * * Otherwise, if this String is longer than [n], a string containing the first [n] characters from this string,
 *  followed by ellipsis `...`
 * @throws [IllegalArgumentException] if [n] is negative.
 */
@JvmSynthetic // avoid access from external Java code
internal fun String?.takeAndEllipse(n: Int): String? =
    this?.take(n)?.let { if (it.length == this.length) this else "$it..." }