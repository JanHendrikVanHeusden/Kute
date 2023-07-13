package nl.kute.util

/**
 * * If [this] is not null, returns [this].
 * * Otherwise, returns the result of calling [block].
 *
 * This function can be used in two ways:
 * 1. [block] provides a fallback value when [this] is null.
 * 2. [block] provides an early return from the enclosing method when [this] is null:
 *     ```
 *     fun foo() {
 *         functionThatMightReturnNull().ifNull {
 *             log.warn("Got a null value")
 *             return
 *         }.doSomething()
 *     }
 *     ```
 *  Thanks to [gist of https://gist.github.com/garyp](https://gist.github.com/garyp/f7436b3898582613da07ee10a3e652ad)
 */
@JvmSynthetic // avoid access from external Java code
internal inline fun <T> T?.ifNull(block: () -> T): T =
    this ?: block()
