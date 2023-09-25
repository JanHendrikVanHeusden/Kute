package nl.kute.exception

import nl.kute.reflection.util.simplifyClassName
import nl.kute.util.lineEnd

/**
 * @param maxStackTraceLines The maximum number of stack trace lines to be included in the result; default = 5.
 *                           When 0 or negative, an empty String is returned.
 * @return The topmost [maxStackTraceLines] stack trace entries as a formatted String; max returned length is 1000
 */
@JvmSynthetic // avoid access from external Java code
internal fun Throwable.stackTraceLinesAsString(maxStackTraceLines: Int = 5): String =
    if (maxStackTraceLines <= 0) "" else this.stackTrace.take(maxStackTraceLines)
        .joinToString(separator = "$lineEnd\t at ", prefix = "\t at ", postfix = "$lineEnd\t...", limit = 1000)

/**
 * @param maxStackTraceLines The maximum number of stack trace lines to be included in the result; default = 5.
 *                           When 0 or negative, no stack trace info is included.
 * @return A compact String representation of the [Throwable] including the topmost [maxStackTraceLines] stack trace entries
 */
@JvmSynthetic // avoid access from external Java code
internal fun Throwable.throwableAsString(maxStackTraceLines: Int = 5): String =
    "${this::class.simplifyClassName()}: message=$message$lineEnd\t cause=${cause?.javaClass?.name?.simplifyClassName()}$lineEnd ${this.stackTraceLinesAsString(maxStackTraceLines)}"
