package nl.kute.hashing

import nl.kute.hashing.DigestMethod.CRC32C
import nl.kute.hashing.DigestMethod.JAVA_HASHCODE
import nl.kute.log.logWithCaller
import nl.kute.reflection.simplifyClassName
import nl.kute.util.asHexString
import nl.kute.util.throwableAsString
import nl.kute.util.byteArrayToHex
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.zip.CRC32C as JavaUtilCRC32C

/**
 * Create a hex String based on the [input] object's [hashCode] method using `CRC32C`
 * @param input The [String] object to create as hash String for
 * @param charset The [Charset] of the [input] String
 * @return The [input]'s [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
private fun JavaUtilCRC32C.hashCrc32C(input: String, charset: Charset): String {
    this.update(input.toByteArray(charset))
    val result = this.value.asHexString
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
    return this.digest().byteArrayToHex()
}

/**
 * Create a hash String for the given [input] String
 * @param input The [String] to create as hash for
 * @param digestMethod The [DigestMethod] to create the hash with
 * @param charset The [Charset] of the input [String]; default is [Charset.defaultCharset].
 * @return The hash String as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
@JvmSynthetic // avoid access from external Java code
internal fun hashString(input: String?, digestMethod: DigestMethod, charset: Charset = Charset.defaultCharset()): String? {
    return if (input == null) null
    else try {
        when (digestMethod) {
            JAVA_HASHCODE -> input.hashCode().asHexString

            CRC32C -> (CRC32C.instanceProvider!!.invoke() as JavaUtilCRC32C).hashCrc32C(input, charset)

            else -> (digestMethod.instanceProvider!!.invoke() as MessageDigest).hashByAlgorithm(input, charset)
        }
    } catch (t: Throwable) {
        // The property's value is probably sensitive, so make sure not to use the value in the error message
        logWithCaller(
            "Hashing.hashString()",
            "${t.javaClass.name.simplifyClassName()} occurred when hashing with digestMethod $digestMethod; exception: [${t.throwableAsString()}]"
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
 *     > Securing data would require much more hardening than this library is intended for.
 * * Given that, the hashing algorithms of this enum are selected for practicality: compact output (at most 40)
 *   and performance; not in the first place for security.
 */
public enum class DigestMethod(public val instanceProvider: (() -> Any)? = null) {
    /**
     * Simply using the java [hashCode].
     * Length is 1 to 8 when represented as a hex String. Security is nearly absent
     * The resulting hex String may be `0`.
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
