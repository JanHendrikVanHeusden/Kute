package nl.kute.util

/** The identity hashCode as returned by [System.identityHashCode]. Returns `0` for `null` */
internal val Any?.identityHash: Int
    @JvmSynthetic // avoid access from external Java code
    get() = System.identityHashCode(this)

/** The identity hashCode as returned by [System.identityHashCode], as hex String. Returns `0` for `null` */
internal val Any?.identityHashHex: String
    @JvmSynthetic // avoid access from external Java code
    get() = this?.identityHash?.asHexString ?: "0"
