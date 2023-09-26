@file:JvmName("ExceptionHandling")

package nl.kute.exception

import nl.kute.observe.Action
import java.lang.Exception
import java.util.concurrent.CancellationException

@JvmSynthetic // avoid access from external Java code
internal inline fun handleException(exception: Exception, onException: Action = {}) {
    when (exception) {
        is InterruptedException -> throw exception
        is CancellationException -> throw exception
        else -> {
            onException()
        }
    }
}

@JvmSynthetic // avoid access from external Java code
internal inline fun <T> handleWithReturn(exception: Exception, returnValue: T, onException: Action = {}): T {
    handleException(exception, onException)
    return returnValue
}
