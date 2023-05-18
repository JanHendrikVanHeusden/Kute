package nl.kute.hashing

import nl.kute.hashing.DigestMethod.CRC32C
import nl.kute.hashing.DigestMethod.JAVA_HASHCODE
import nl.kute.hashing.ThreadDigestHelper.Companion.threadCrc32c
import nl.kute.hashing.ThreadDigestHelper.Companion.threadDigestByAlgorithm
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.HexFormat
import java.util.zip.CRC32C as javaUtilCRC32C

val hexFormat: HexFormat = HexFormat.of()

/**
 * Create a hex String based on the [input] object's [hashCode] method
 * @param input The [Any] object to create as hash String for
 * @return The [input]'s [hashCode] as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
fun javaHashString(input: Any): String = hexFormat.toHexDigits(input.hashCode())

/**
 * Create a hash String for the given [input] String
 * @param input The [String] to create as hash for
 * @param digestMethod The [DigestMethod] to create the hash with
 * @param charset The [Charset] of the input [String]; default is [Charset.defaultCharset].
 * @return The hash String as a hexadecimal String, consisting of characters `0..9`, `a..f`
 */
fun hashString(input: String?, digestMethod: DigestMethod, charset: Charset = Charset.defaultCharset()): String? {
    if (input == null) {
        return null
    }
    return when (digestMethod) {
        JAVA_HASHCODE ->
            javaHashString(input)

        CRC32C -> {
            with(threadCrc32c.get()) {
                hashCrc32C(input, charset)
            }
        }
        else -> {
            require(!digestMethod.algorithm.isNullOrBlank()) { "No Digest algorithm specified" }
            with(threadDigestByAlgorithm[digestMethod.algorithm]!!.get()) {
                this.update(input.toByteArray(charset))
                hexFormat.formatHex(this.digest())
            }
        }
    }
}

@Synchronized // Crc32C is not thread safe
// FIXME: make coroutine safe
private fun javaUtilCRC32C.hashCrc32C(input: String, charset: Charset): String {
    this.update(input.toByteArray(charset))
    val result = hexFormat.toHexDigits(this.value).trimStart('0')
    return if (result == "") "0" else result
}


/**
 * Hash algorithms; typical usage is to avoid exposing sensitive or personally identifiable data in logging etc.
 * * **Note that hashing data like this is *NOT* meant to be a security mechanism.**
 *     It is just a way to avoid that plain text data is exposed in logging etc.,
 *     and should only be used like that.
 *     Securing data would require much more hardening than this library is intended for.
 * * Given that, the hashing algorithms of this enum are selected for best performance, not for security.
 */
enum class DigestMethod(val algorithm: String? = null) {
    /**
     * Simply using the java hashCode.
     * Length is 8 when represented as a hex String.
     */
    JAVA_HASHCODE(null),

    /**
     * CRC32C is a fast hash methods with compact output (length 1 to 8), but security is nearly absent.
     * The resulting hex String may be `0`.
     */
    CRC32C(null),

    /**
     * SHA1 is considered as one of the better performing hash methods; but not considered secure.
     * Length is 40 when represented as a hex String.
     */
    SHA1("SHA-1"),

    /**
     * MD5 is considered as one of the better performing hash methods; but not considered secure
     * Length is 32 when represented as a hex String.
     */
    MD5("MD5")
}

private class ThreadDigestHelper {

    /**
     * [MessageDigest] and [javaUtilCRC32C] are not thread safe.
     * So we keep a [ThreadLocal] copy to isolate threads from each other.
     */
    companion object {
        val threadDigestByAlgorithm: MutableMap<String, ThreadLocal<MessageDigest>> = mutableMapOf()
        val threadCrc32c: ThreadLocal<javaUtilCRC32C> = ThreadLocal.withInitial { javaUtilCRC32C() }

        @Synchronized
        // FIXME: make coroutine safe
        private fun messageDigestThreadLocal(algorithm: String): ThreadLocal<MessageDigest> =
            ThreadLocal.withInitial { MessageDigest.getInstance(algorithm) }

        init {
            DigestMethod.values().map { it.algorithm }.filter { !it.isNullOrBlank() }.forEach {
                threadDigestByAlgorithm[it!!] = messageDigestThreadLocal(it)
            }
        }
    }

}
