@file:JvmName("StringUtils")

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
 * Takes the first [n] characters of a String and, if capped, appends ellipsis `...
 * @receiver A [String]`?` (so null allowed)
 * @return
 * * `null` if `this` [String]`?` is `null`
 * * If [n] `< 0`, the original [String]`?`
 * * If `this` [String] is not longer than [n], the original String.
 * * Otherwise, if `this` [String] is longer than [n],
 *  a [String] containing the first [n] characters from this [String], followed by ellipsis `...`
 */
@JvmSynthetic // avoid access from external Java code
internal fun String?.takeAndEllipse(n: Int): String? =
    if (n < 0) this
    else this?.take(n)?.let { if (it.length == this.length) this else "$it..." }