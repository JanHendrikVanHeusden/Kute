package nl.kute.hashing

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
    CRC32C({ java.util.zip.CRC32C() }),

    /**
     * SHA1 is considered as one of the better performing hash methods; but not considered secure.
     * Length is 40 when represented as a hex String.
     */
    SHA1({ java.security.MessageDigest.getInstance("SHA-1") }),

    /**
     * MD5 is considered as one of the better performing hash methods; but not considered secure
     * Length is 32 when represented as a hex String.
     */
    MD5({ java.security.MessageDigest.getInstance("MD5") })
}