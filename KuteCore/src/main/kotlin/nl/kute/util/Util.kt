package nl.kute.util

/**
 * If [this] is not null, returns [this]. Otherwise, returns the result of calling [block].
 * Thanks to [https://gist.github.com/garyp](https://gist.github.com/garyp/f7436b3898582613da07ee10a3e652ad)
 */
internal inline fun <T> T?.ifNull(block: () -> T): T = this ?: block()