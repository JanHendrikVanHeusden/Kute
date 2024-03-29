package nl.kute.asstring.annotation.modify

import nl.kute.hashing.DigestMethod
import nl.kute.hashing.hashString
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringHash] annotation can be placed on properties to indicate that the property is included
 * in the return value of [nl.kute.asstring.core.asString], but with its String value replaced by the String's hash value.
 * > To distinguish it from "normal" String content, the hash value is surrounded by `#` characters.
 * * Usage of a hash method allows tracking of values across multiple entries, without exposing the original value.
 * * Typical usage is to keep sensitive or personally identifiable data out of log files etc.
 *     * This may limit exposure of such data, but on its own it must not be considered a security feature.
 * * When applied to property in an interface or a super-class, it will also be applied to the subclass property
 *   in the subclass hierarchy; regardless whether or not the property is overridden, and whether or not
 *   it has its own [AsStringHash] annotation.
 *    * A different [digestMethod] in an overriding property is not honoured. So if an interface property has
 *      a [AsStringHash] annotation with [DigestMethod.CRC32C], and an implementing class specifies [DigestMethod.SHA1]
 *      for that property, the annotation on the overriding property is ignored, so it will still be hashed with
 *      [DigestMethod.CRC32C] (i.e., adhering to the annotation that is highest in the class hierarchy).
 * @param digestMethod The digest algorithm to use; see [DigestMethod]. Default = [DigestMethod.CRC32C]
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
public annotation class AsStringHash(val digestMethod: DigestMethod = DigestMethod.CRC32C)

@JvmSynthetic // avoid access from external Java code
internal fun AsStringHash?.hashString(strVal: String?): String? =
    if (this == null) strVal else "#${hashString(strVal, digestMethod)}#"
