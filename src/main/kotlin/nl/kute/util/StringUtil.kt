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
