package nl.kute.util


/**
 * @param maxStackTraceLines The maximum number of stack trace lines to be included in the result; default = 5.
 *                           When 0 or negative, an empty String is returned.
 * @return The topmost [maxStackTraceLines] stack trace entries as a formatted String; max returned length is 1000
 */
internal fun Throwable.stackTraceLinesAsString(maxStackTraceLines: Int = 5): String =
    if (maxStackTraceLines <= 0) "" else this.stackTrace.take(maxStackTraceLines)
        .joinToString(separator = "$lineEnd\t at ", prefix = "\t at ", postfix = "$lineEnd\t...", limit = 1000)

/**
 * @param maxStackTraceLines The maximum number of stack trace lines to be included in the result; default = 5.
 *                           When 0 or negative, no stack trace info is included.
 * @return A compact String representation of the [Throwable] including the topmost [maxStackTraceLines] stack trace entries
 */
internal fun Throwable.asString(maxStackTraceLines: Int = 5): String =
    "${this.javaClass.simpleName}: message=$message$lineEnd\t cause=${cause?.javaClass?.simpleName}$lineEnd ${this.stackTraceLinesAsString(maxStackTraceLines)}"
