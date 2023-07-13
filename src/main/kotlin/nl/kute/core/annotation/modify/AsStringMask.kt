package nl.kute.core.annotation.modify

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.math.max
import kotlin.math.min
/**
 * The [AsStringMask] annotation can be placed on properties to indicate that the property is
 * included in the return value of [nl.kute.core.asString], but with its value masked.
 * * Typical usage is to keep sensitive or personally identifiable out of logging etc.
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 * * [AsStringMask] is repeatable. If multiple annotations are present, they are applied in order of occurrence,
 *   with the subsequent mask working on the result of the previous one.
 *    * If a property with [AsStringMask] is overridden, and the subclass property is also annotated with
 *      [AsStringMask], they are applied in order from super class to subclass.
 * ---
 * * If the position of [startMaskAt] is after that of [endMaskAt], the full [String] will be masked.
 * * If [minLength]` >  `[maxLength], [minLength] is used
 */
@Target(AnnotationTarget.PROPERTY)
@Repeatable
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
public annotation class AsStringMask(
    /** At which character index (inclusive) masking should start? */
    val startMaskAt: Int = 0,

    /** At which character index (exclusive) masking should end? */
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

internal fun AsStringMask?.mask(strVal: String?): String? {
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
            // counting backwards from end of String
            max(0, strLength + startMaskAt)
        }
        val maskEnd = if (endMaskAt >= 0) {
            endMaskAt
        } else {
            // counting backwards from end of String
            max(0, strLength + endMaskAt)
        }
        retVal = retVal.take(returnLength)
        if (retVal.length < minLength) {
            retVal = retVal.padEnd(minLength - retVal.length, mask)
        }
        if (maskStart in 0 until returnLength && maskEnd >= maskStart) {
            val endAt = min(maskEnd, returnLength)
            retVal = retVal.replaceRange(maskStart until endAt, mask.toString().repeat(endAt - maskStart))
        }
        return retVal
    }
}
