package nl.kute.util

/** The identity hashCode as returned by [System.identityHashCode]; returns `0` for `null` */
internal val Any?.identityHash: Int
    get() = System.identityHashCode(this)

private val leadingZeroRegex = Regex("^0+")

internal val Any?.identityHashHex: String
    get() = this?.identityHash?.toByteArray()?.toHex()?.replace(leadingZeroRegex, "") ?: "0"
