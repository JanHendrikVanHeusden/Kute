@file:JvmName("KuteLog")

package nl.kute.log

import nl.kute.util.asString
import java.util.function.Consumer

internal val stdOutLogger: (String) -> Unit = { msg: String -> println(msg) }
internal val loggerWithCaller: (String, String) -> Unit = { caller, msg: String -> logger("$caller - $msg") }

fun resetStdOutLogger() {
    logger = stdOutLogger
}

/**
 * By default (wen no logger set explicitly), Kute logs error messages to std out (using println()).
 *
 * A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.
 * > To be used from within Kotlin.
 * > For use from Java code: see [setLogConsumer]
 *
 * Typical usage (Kotlin) would be:
 * ```
 * private val kuteLogger = LoggerFactory().getLogger("nl.kute")
 * nl.kute.log.logger = { msg -> kuteLogger.error(msg) }
 * ```
 */
var logger: (String) -> Unit = stdOutLogger
    set(newLogger) {
        try {
            // basic test to assert that the new logger works (not causing an exception)
            newLogger("")
            field = newLogger // when no exception occurred
        } catch (e: Exception) {
            field.invoke(
                """Tried to set logger, but logger caused exception ${e::class}.
                | logger will not be changed!
                | ${e.asString()}
                | """.trimMargin()
            )
        }
    }

fun Any?.log(msg: String) = loggerWithCaller("${this?.javaClass ?: ""}", msg)
fun logWithCaller(caller: String, msg: String) = loggerWithCaller(caller, msg)

/**
 * By default (wen no logger set explicitly), Kute logs error messages to std out (using println()).
 *
 * A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.
 * > To be used from within Java.
 * > For use from Kotlin code: see [logger]
 *
 * More convenient in Java than hassling with [Unit] (as you would with [logger])
 * > Typical usage (Java) would be:
 * ```
 * Logger myLogger = LoggerFactory().getLogger("nl.kute")
 * Consumer<String> kuteErrorlogger = msg -> myLogger.error(msg);
 * nl.kute.log.setLogConsumer(kuteErrorLogger);
 * ```
 */
fun setLogConsumer(aLogger: Consumer<String>) {
    logger = { msg: String -> aLogger.accept(msg) }
}
