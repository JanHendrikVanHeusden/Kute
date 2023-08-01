package nl.kute.hashing

import nl.kute.hashing.DigestMethod.CRC32C
import nl.kute.hashing.DigestMethod.JAVA_HASHCODE
import nl.kute.log.logWithCaller
import nl.kute.reflection.simplifyClassName
import nl.kute.util.asHexString
import nl.kute.util.byteArrayToHex
import nl.kute.util.throwableAsString
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
