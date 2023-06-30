package nl.kute.util

/** The identity hashCode as returned by [System.identityHashCode]. Returns `0` for `null` */
internal val Any?.identityHash: Int
    get() = System.identityHashCode(this)

/** The identity hashCode as returned by [System.identityHashCode], as hex String. Returns `0` for `null` */
internal val Any?.identityHashHex: String
    get() = this?.identityHash?.asHexString?.replace(leadingZeroRegex, "") ?: "0"
private val leadingZeroRegex = Regex("^0+")
