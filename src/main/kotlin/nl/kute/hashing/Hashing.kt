package nl.kute.hashing

import nl.kute.hashing.DigestMethod.CRC32C
import nl.kute.hashing.DigestMethod.JAVA_HASHCODE
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.HexFormat
import java.util.zip.CRC32C as JavaUtilCRC32C

private val hexFormat: HexFormat = HexFormat.of()

/**
 * Create a hex String based on the [input] object's [hashCode] method
 * @param input The [Any] object to create as hash String for
 * @return The [input]'s [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
fun javaHashString(input: Any): String = hexFormat.toHexDigits(input.hashCode())

private fun JavaUtilCRC32C.hashCrc32C(input: String, charset: Charset): String {
    this.update(input.toByteArray(charset))
    val result = hexFormat.toHexDigits(this.value).trimStart('0')
    return if (result == "") "0" else result
}

private fun MessageDigest.hashByAlgorithm(input: String, charset: Charset): String? {
    this.update(input.toByteArray(charset))
    return hexFormat.formatHex(this.digest())
}

/**
 * Create a hash String for the given [input] String
 * @param input The [String] to create as hash for
 * @param digestMethod The [DigestMethod] to create the hash with
 * @param charset The [Charset] of the input [String]; default is [Charset.defaultCharset].
 * @return The hash String as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
fun hashString(input: String?, digestMethod: DigestMethod, charset: Charset = Charset.defaultCharset()): String? {
    return if (input == null) null
    else try {
        when (digestMethod) {
            JAVA_HASHCODE -> javaHashString(input)

            CRC32C -> (CRC32C.instanceProvider!!.invoke() as JavaUtilCRC32C).hashCrc32C(input, charset)

            else -> (digestMethod.instanceProvider!!.invoke() as MessageDigest).hashByAlgorithm(input, charset)
        }
    } catch (t: Throwable) {
        // no logging framework present, so we only can use standard output
        println("${t.javaClass.simpleName} occurred when hashing with digestMethod $digestMethod; exception message = [${t.message}]")
        null
    }
}

/**
 * Hash algorithms; typical usage is to avoid exposing sensitive or personally identifiable data in logging etc.
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
