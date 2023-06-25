package nl.kute.core.annotation.modify

import nl.kute.hashing.DigestMethod
import nl.kute.hashing.hashString
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringHash] annotation can be placed on properties to indicate that the property is included
 * in the return value of [nl.kute.core.asString], but with its String value replaced by the String's hash value.
 * * Usage of a hash method allows tracking of values across multiple entries, without exposing the original value.
 * * Typical usage is to keep sensitive or personally identifiable out of logging etc.
 *     * This may limit exposure of such data, but on its own it must not be considered a security feature.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
annotation class AsStringHash(val digestMethod: DigestMethod = DigestMethod.CRC32C)

fun AsStringHash?.hashString(strVal: String?): String? =
    if (this == null) strVal else hashString(strVal, digestMethod)
