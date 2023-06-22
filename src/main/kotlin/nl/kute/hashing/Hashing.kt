package nl.kute.hashing

/**
 * With Java 17, we would use `java.util.HexFormat`, like below.
 * But that's Java 17, and we want to be able to run on Java 11+
 */

import nl.kute.hashing.DigestMethod.CRC32C
import nl.kute.hashing.DigestMethod.JAVA_HASHCODE
import nl.kute.log.logWithCaller
import nl.kute.util.asString
import nl.kute.util.toByteArray
import nl.kute.util.toHex
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.zip.CRC32C as JavaUtilCRC32C

/**
 * Create a hex String based on the receiver's [hashCode] method. Null safe.
 * @return The receiver's [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`;
 *         or `null` if the receiver is `null`.
 */
internal fun Any?.hexHashCode(): String? = this?.let { hashCode().toByteArray().toHex() }

/**
 * Create a hex String based on the [input] object's [hashCode] method using `CRC32C`
 * @param input The [String] object to create as hash String for
 * @param charset The [Charset] of the [input] String
 * @return The [input]'s [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
private fun JavaUtilCRC32C.hashCrc32C(input: String, charset: Charset): String {
    this.update(input.toByteArray(charset))
    val result = this.value.toByteArray().toHex().trimStart('0')
    return if (result == "") "0" else result
}

/**
 * Create a hex String based on the [input] object's [hashCode] method using the receiver [MessageDigest]
 * @param input The [String] object to create as hash String for
 * @param charset The [Charset] of the [input] String
 * @return The [input]'s [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
private fun MessageDigest.hashByAlgorithm(input: String, charset: Charset): String {
    this.update(input.toByteArray(charset))
    return this.digest().toHex()
}

/**
 * Create a hash String for the given [input] String
 * @param input The [String] to create as hash for
 * @param digestMethod The [DigestMethod] to create the hash with
 * @param charset The [Charset] of the input [String]; default is [Charset.defaultCharset].
 * @return The hash String as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
internal fun hashString(input: String?, digestMethod: DigestMethod, charset: Charset = Charset.defaultCharset()): String? {
    return if (input == null) null
    else try {
        when (digestMethod) {
            JAVA_HASHCODE -> input.hexHashCode()

            CRC32C -> (CRC32C.instanceProvider!!.invoke() as JavaUtilCRC32C).hashCrc32C(input, charset)

            else -> (digestMethod.instanceProvider!!.invoke() as MessageDigest).hashByAlgorithm(input, charset)
        }
    } catch (t: Throwable) {
        // The property's value is probably sensitive, so make sure not to use the value in the error message
        logWithCaller(
            "Hashing.hashString()",
            "${t.javaClass.simpleName} occurred when hashing with digestMethod $digestMethod; exception: [${t.asString()}]"
        )
        null
    }
}

/**
 * Hash algorithms; typical usage within Kute is to avoid exposing sensitive or personally identifiable data
 * in logging etc.
 * * **Note that hashing data like this is *NOT* meant to be a security mechanism.**
 *     It is just a way to avoid that plain text data is exposed in logging etc.,
 *     and should only be used like that.
 *     Securing data would require much more hardening than this library is intended for.
 * * Given that, the hashing algorithms of this enum are selected for best performance, not for security.
 */
enum class DigestMethod(val instanceProvider: (() -> Any)? = null) {
    /**
     * Simply using the java hashCode.
     * Length is 8 when represented as a hex String.
     */
    JAVA_HASHCODE,

    /**
     * CRC32C is a fast hash methods with compact output (length 1 to 8), but security is nearly absent.
     * The resulting hex String may be `0`.
     */
    CRC32C({ JavaUtilCRC32C() }),

    /**
     * SHA1 is considered as one of the better performing hash methods; but not considered secure.
     * Length is 40 when represented as a hex String.
     */
    SHA1({ MessageDigest.getInstance("SHA-1") }),

    /**
     * MD5 is considered as one of the better performing hash methods; but not considered secure
     * Length is 32 when represented as a hex String.
     */
    MD5({ MessageDigest.getInstance("MD5") })
}
