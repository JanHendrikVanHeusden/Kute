@file:JvmName("HexUtils")

package nl.kute.util
/**
 * Most of this code would not be needed in Java 17+ (would use HexFormat class in Java 17+).
 * But we want Kute to be Java 11+ compatible...
*/

/**
 * Create a hex String based on the receiver's [hashCode] method. Null safe.
 * > Mainly for testing purposes
 * @return The receiver's [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`;
 *         or `null` if the receiver is `null`.
 */
@JvmSynthetic // avoid access from external Java code
internal fun Any?.hexHashCode(): String? = this?.let { hashCode().asHexString }

/**
 * Converts an [Int] to a lower-case unsigned hex string, with leading zeros stripped.
 * Returns "0" when [Int] receiver is `null`.
 * > NB: Kotlin's `Int.toString(16)` is signed, produces a leading minus sign for negative values;
 * >     that's not what we want!
 */
internal val Int?.asHexString: String
    @JvmSynthetic // avoid access from external Java code
    get() = if (this == null) "0"
    else Integer.toHexString(this).ifBlank { "0" }

/**
 * Converts a [Long] to a lower-case unsigned hex string.
 * > NB: Kotlin's `Long.toString(16)` is signed, produces a leading minus sign for negative values;
 * >     that's not what we want!
 */
internal val Long?.asHexString: String
    @JvmSynthetic // avoid access from external Java code
    get() = if (this == null) "0"
    else java.lang.Long.toHexString(this).ifBlank { "0" }

/**
 * Convert a [ByteArray] to a hex String
 * > Not very readable code, but much faster most other ways in Java 11
 * * Thanks to [StackOverflow: how to convert a byte array to a hex-string](https://stackoverflow.com/a/24267654)
 * @return the receiver [ByteArray], converted to a hex String, lower case.
 */
@JvmSynthetic // avoid access from external Java code
internal fun ByteArray.byteArrayToHex(): String {
    val retVal = ByteArray(this.size * 2)
    for (j: Int in this.indices) {
        val v: Int = this[j].toInt() and 0xFF
        // ushr (unsigned shift right) is what >>> is in Java
        retVal[j * 2] = hexChars[v ushr 4].code.toByte()
        retVal[j * 2 + 1] = hexChars[v and 0x0F].code.toByte()
    }
    return String(retVal)
}
private val hexChars = "0123456789abcdef".toCharArray()
