@file:JvmName("KuteLog")
package nl.kute.log

import java.util.function.Consumer

internal val stdOutLogger: (String) -> Unit = { msg: String -> println(msg) }
internal val loggerWithCaller: (String, String) -> Unit = { caller, msg: String -> logger("$caller - $msg") }

fun resetStdOutLogger() {
    logger = stdOutLogger
}

/**
 * Can be used to inject a logger - to be used from within Kotlin
 * > For use from Java code: see [setLogConsumer]
 */
var logger: (String) -> Unit = stdOutLogger

fun Any.log(msg: String) = loggerWithCaller("${this::class}", msg)
fun logWithCaller(caller: String, msg: String) = loggerWithCaller(caller, msg)

/**
 * Can be used to inject a logger - to be used from within Java
 * > More convenient in Java than hassling with [Unit] (as you would with [logger])
 */
fun setLogConsumer(aLogger: Consumer<String>) {
    logger = { msg: String -> aLogger.accept(msg) }
}
