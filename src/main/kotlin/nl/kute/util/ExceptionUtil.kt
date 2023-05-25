package nl.kute.util

/** @return The topmost [maxStackTraceLines] stack trace entries as a formatted String; max returned length is 1000 */
fun Throwable.stackTraceLinesAsString(maxStackTraceLines: Int = 5): String =
    this.stackTrace.take(maxStackTraceLines).joinToString(separator = "$lineEnd\t at ", prefix = "\t at ", postfix = "$lineEnd\t..." , limit = 1000)

/** @return A compact String representation of the [Throwable] including the topmost [maxStackTraceLines] stack trace entries */
fun Throwable.asString(maxStackTraceLines: Int = 5): String =
    "${this.javaClass.simpleName}: message=$message$lineEnd\t cause=${cause?.javaClass?.simpleName}$lineEnd ${this.stackTraceLinesAsString(maxStackTraceLines)}"
