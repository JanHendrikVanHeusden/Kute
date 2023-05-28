package nl.kute.printable.annotation.modifiy

import nl.kute.hashing.DigestMethod
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

val defaultDigestMethod = DigestMethod.CRC32C

/**
 * The [PrintHash] annotation can be placed on properties to indicate that the property is included
 * in the return value of [nl.kute.core.asString], but with its value replaced by its hash value.
 * * Typical usage is to keep sensitive or personally identifiable out of logging etc.
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 * * Usage of a hash method allows tracking of values across multiple entries, without exposing the original value.
 *
 * Usage notes:
 * * When using one of these methods, the full String resulting from the hash method is used, regardless
 *   of any max / min length settings
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class PrintHash(val digestMethod: DigestMethod = DigestMethod.CRC32C)
