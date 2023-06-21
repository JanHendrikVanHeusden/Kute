package nl.kute.printable.annotation.modifiy

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.math.max
import kotlin.math.min

/**
 * The [AsStringMask] annotation can be placed on properties to indicate that the property is
 * included in the return value of [nl.kute.core.asString], but with its value masked.
 * * Typical usage is to keep sensitive or personally identifiable out of logging etc.
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 *
 * * If the position of [startMaskAt] is after that of [endMaskAt], the full [String] will be masked.
 * * If [minLength]` >  `[maxLength], [minLength] is used
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class AsStringMask(
    /** At which character index (inclusive) masking should start? */
    val startMaskAt: Int = 0,

    /** At which character index (exclusive) masking should start? */
    val endMaskAt: Int = Int.MAX_VALUE,

    /** The char to use for masking */
    val mask: Char = '*',

    /**
     * Should nulls be masked too?
     *  * when `true`, nulls will be replaced by `"null"` and then be masked
     *  * when `false`, nulls will be left as `null`
     */
    val maskNulls: Boolean = true,

    /** The minimum length of the resulting value; so masking of `"ab"` with [minLength] of `6` will result in `******` */
    val minLength: Int = 0,

    /** The maximum length of the resulting value. If less than the [String]'s length, the [String] will be truncated */
    val maxLength: Int = Int.MAX_VALUE,
)

fun AsStringMask?.mask(strVal: String?): String? {
    if (this == null) {
        return strVal
    } else {
        var retVal = strVal ?: if (maskNulls) {
            "null"
        } else {
            return null
        }
        val strLength = retVal.length
        var returnLength = strLength
        if (maxLength in 0 until strLength) {
            returnLength = maxLength
        }
        if (minLength >= 0 && returnLength < minLength) {
            returnLength = minLength
        }
        if (returnLength > strLength) {
            retVal += mask.toString().repeat(returnLength - strLength)
        } else if (returnLength < strLength) {
            retVal = retVal.take(returnLength)
        }

        val maskStart = if (startMaskAt >= 0) {
            startMaskAt
        } else {
            max(0, strLength + startMaskAt)
        }
        val maskEnd = if (endMaskAt >= 0) {
            endMaskAt
        } else {
            max(0, strLength + endMaskAt)
        }
        return if (maskStart in 0 until returnLength && maskEnd > maskStart) {
            val startAt = max(maskStart, 0)
            val endAt = min(maskEnd, returnLength)
            retVal.replaceRange(startAt until endAt, mask.toString().repeat(endAt - startAt))
        } else {
            mask.toString().repeat(returnLength)
        }
    }
}
