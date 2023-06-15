@file:JvmName("KuteLog")

package nl.kute.log

import nl.kute.util.asString
import java.util.function.Consumer

/** Logs message [msg], prefixed by the receiver's class name */
fun Any?.log(msg: String) = loggerWithCaller("${this?.javaClass ?: ""}", msg)

/** Logs message [msg], prefixed by the [caller] String */
fun logWithCaller(caller: String, msg: String) = loggerWithCaller(caller, msg)

/** When no other [logger] is set, this logger is used, which simply outputs `msg` to std out (using [println]) */
internal val stdOutLogger: (String) -> Unit = { msg: String -> println(msg) }

/** Logger that outputs `msg`, prefixed with the `caller` String, to the current [logger] */
internal val loggerWithCaller: (String, String) -> Unit = { caller, msg: String -> logger("$caller - $msg") }

/**
 * Static [logger].
 * By default (wen no other logger set explicitly), Kute uses [stdOutLogger] to output to std out (using `println()`).
 *
 * A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.
 * > To be used from within Kotlin.
 * > For use from Java code: see [setLogConsumer]
 *
 * Typical usage (Kotlin) would be:
 * ```
 * private val kuteLogger = Logger.getLogger("nl.kute")
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

/**
 * Static [logger].
 * By default (wen no other logger set explicitly), Kute uses [stdOutLogger] to output to std out (using `println()`).
 *
 * A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.
 * > To be used from within Java.
 * > For use from Kotlin code: see [logger]
 *
 * More convenient in Java than hassling with [Unit] (as you would with [logger])
 * > Typical usage (Java) would be:
 * ```
 * Logger myLogger = Logger.getLogger("nl.kute")
 * Consumer<String> kuteErrorlogger = msg -> myLogger.error(msg);
 * nl.kute.log.setLogConsumer(kuteErrorLogger);
 * ```
 */
fun setLogConsumer(aLogger: Consumer<String>) {
    logger = { msg: String -> aLogger.accept(msg) }
}

/** Resets the logger to [stdOutLogger] */
internal fun resetStdOutLogger() {
    logger = stdOutLogger
}
