@file:JvmName("KuteLog")

package nl.kute.log

import nl.kute.exception.handleException
import nl.kute.exception.throwableAsString
import java.util.function.Consumer

/** Logs message [msg] to [loggerWithCaller], prefixed by the receiver's class name  */
public fun Any?.log(msg: Any?): Unit = try {
    loggerWithCaller("${this?.javaClass ?: ""}", msg)
} catch (e: Exception) {
    handleException(e) {
        e.printStackTrace() // not much else we can do
    }
}

/** Logs message [msg] to [loggerWithCaller], prefixed by the [caller] String */
@JvmSynthetic // avoid access from external Java code
internal fun logWithCaller(caller: String, msg: Any?): Unit = try {
    loggerWithCaller(caller, msg)
} catch (e: Exception) {
    handleException(e) {
        e.printStackTrace() // not much else we can do
    }
}

/** When no other [logger] is set, this logger is used, which simply outputs `msg` to std out (using [println]) */
@JvmSynthetic // avoid access from external Java code
internal val stdOutLogger: (Any?) -> Unit = { msg: Any? -> println(msg) }

/** Logger that outputs `msg`, prefixed with the `caller` String, to the current [logger] */
@JvmSynthetic // avoid access from external Java code
internal val loggerWithCaller: (String, Any?) -> Unit = { caller, msg: Any? -> logger("$caller - $msg") }

/**
 * Static [logger].
 * By default, wen no other logger set explicitly, Kute uses [stdOutLogger] to output to std out (using `println()`).
 *
 * A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.
 * * To be used in **Kotlin**.
 * * In **Java** code, use [KuteLogConsumer.setLogConsumer] instead (more convenient in Java)
 *
 * Typical usage for **Kotlin** with an SLF4J compatible logger might be:
 * ```
 * private val kuteLogger = Logger.getLogger("nl.kute")
 * nl.kute.log.logger = { msg -> kuteLogger.error(msg) }
 * ```
 * @see [KuteLogConsumer.setLogConsumer]
 */
public var logger: (String?) -> Unit = stdOutLogger
    set(newLogger) {
        try {
            if (newLogger != stdOutLogger) {
                // basic test to assert that the new logger works (not causing an exception)
                newLogger("")
            }
            field = newLogger // when no exception occurred
        } catch (e: Exception) {
            handleException(e) {
                field.invoke(
                    """Tried to set logger, but logger caused exception ${e::class}.
                | logger will not be changed!
                | ${e.throwableAsString()}
                | """.trimMargin()
                )
            }
        }
    }

/** Resets the logger to [stdOutLogger]; mainly for testing purposes */
internal fun resetStdOutLogger() {
    logger = stdOutLogger
}

/** To be used in **Java** code to redirect Kute's log output to a consumer
 * > Typically to a SLFJ (or equivalent) log method */
public class KuteLogConsumer {

    public companion object {

        /**
         * [setLogConsumer] can be used in **Java** code to set a different logger.
         * > By default, wen no other logger set explicitly, Kute uses [stdOutLogger] to output to std out (using `println()`).
         *
         * A different logger (typically SLF4J etc.) can be injected to have it send error logs to your logging framework.
         * * To be used from within **Java**
         * * In **Kotlin** code, use [logger] instead
         *
         * More convenient in **Java** than hassling with [Unit] (as you would with [logger])
         * > Typical usage for Java with an *SLF4J* compatible logger would be:
         * ```
         * Logger myLogger = Logger.getLogger("nl.kute")
         * Consumer<String> kuteErrorLogger = msg -> myLogger.error(msg);
         * nl.kute.log.KuteLogConsumer.setLogConsumer(kuteErrorLogger);
         * ```
         * @see [logger]
         */
        @JvmStatic
        public fun setLogConsumer(aLogger: Consumer<String?>) {
            logger = { msg: String? -> aLogger.accept(msg) }
        }
    }
}
