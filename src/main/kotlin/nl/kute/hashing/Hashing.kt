package nl.kute.hashing

import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.CRC32C as javaUtilCRC32C

fun hash(input: String, digestMethod: DigestMethod): String {
    return  when (digestMethod) {
        DigestMethod.JAVA_HASHCODE -> {
            input.hashCode().toString(16)
        }
        DigestMethod.CRC32C -> {
            with(DigestHelper.threadCrc32c.get()) {
                this.update(input.toByteArray())
                this.value.toString(16)
            }
        }
        else -> {
            require(!digestMethod.algorithm.isNullOrBlank()) { "No Digest algorithm specified" }
            with(DigestHelper.threadDigestByAlgorithm.get(digestMethod.algorithm)!!.get()) {
                this.update(input.toByteArray())
                BigInteger(this.digest()).toString(16)
            }
        }
    }
}

private class DigestHelper {

    /**
     * [MessageDigest] and [javaUtilCRC32C] are not thread safe.
     * So we keep a [ThreadLocal] copy to isolate threads from each other.
     */
    companion object {
        val threadDigestByAlgorithm: MutableMap<String, ThreadLocal<MessageDigest>> = mutableMapOf()
        val threadCrc32c: ThreadLocal<javaUtilCRC32C> = ThreadLocal.withInitial { javaUtilCRC32C() }

        private fun messageDigestThreadLocal(algorithm: String): ThreadLocal<MessageDigest> =
            ThreadLocal.withInitial { MessageDigest.getInstance(algorithm) }

        init {
            DigestMethod.values().map { it.algorithm }.filter { !it.isNullOrBlank() } .forEach {
                threadDigestByAlgorithm[it!!] = messageDigestThreadLocal(it)
            }
        }
    }

}

/**
 * Hash algorithms to avoid exposing sensitive or personally identifiable data in logging etc.
 * * **Note that hashing data like this is *NOT* meant to be a security mechanism.**
 *     It is just a way to avoid that plain text data is exposed in logging etc.,
 *     and should only be used like that.
 *     Securing data would require much more hardening than this library can bring or even is intended for.
 * * Given that, the hashing algorithms of this enum are selected for best performance, not for security.
 */
enum class DigestMethod(val algorithm: String? = null) {
    /** Simply using the java hashCode. Length is 8 or 9 (9 in case of negative values) */
    JAVA_HASHCODE(null),
    /** CRC32C is a fast hash methods with compact output (length 8), but security is nearly absent */
    CRC32C(null),
    /** SHA1 is considered as one of the better performing hash methods; but not considered secure */
    SHA1("SHA-1"),
    /** MD5 is considered as one of the better performing hash methods; but not considered secure */
    MD5("MD5")
}

