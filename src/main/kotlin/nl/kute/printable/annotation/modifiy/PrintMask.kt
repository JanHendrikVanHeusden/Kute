package nl.kute.printable.annotation.modifiy

import nl.kute.reflection.annotationfinder.annotationOfPropertyInHierarchy
import nl.kute.reflection.getPropValue
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty1

/**
 * The [PrintMask] annotation can be placed on properties to indicate that the property is
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
annotation class PrintMask(
    /** At which character index (inclusive) masking should start? */
    val startMaskAt: Int = 0,

    /** At which character index (exclusive) masking should start? */
    val endMaskAt: Int = Int.MAX_VALUE,

    /** The char to use for masking */
    val mask: Char = '*',

    /** Should nulls be masked too? If `false`, nulls will be shown */
    val maskNulls: Boolean = true,

    /** The minimum length of the resulting value; so masking of `"ab"` with [minLength] of `6` will result in `******` */
    val minLength: Int = 0,

    /** The maximum length of the resulting value. If less than the [String]'s length, the [String] will be truncated */
    val maxLength: Int = Int.MAX_VALUE,
)

fun <T : Any, V : Any?> mask(obj: T, prop: KProperty1<T, V>): String? {
    val propVal: V? = obj.getPropValue(prop)
    with(prop.annotationOfPropertyInHierarchy<PrintMask>() ?: return null) {
        var strVal: String
        strVal = if (propVal == null) {
            if (maskNulls) { "null" } else { return null }
        } else {
            propVal.toString()
        }
        val strLength = strVal.length
        var returnLength = strLength
        if (maxLength >= 0 && strLength > maxLength) {
            returnLength = maxLength
        }
        if (minLength >= 0 && returnLength < minLength) {
            returnLength = minLength
        }
        if (returnLength > strLength) {
            strVal = strVal + mask.toString().repeat(returnLength - strLength)
        } else if (returnLength < strLength) {
            strVal = strVal.take(returnLength)
        }

        val maskStart = if (startMaskAt >= 0) { startMaskAt } else { max(0, strLength + startMaskAt) }
        val maskEnd = if (endMaskAt >= 0) { endMaskAt } else { max(0, strLength + endMaskAt) }
        if (maskStart in 0 until returnLength - 1 && maskEnd > maskStart) {
            val startAt = max(maskStart, 0)
            val endAt = min(maskEnd, returnLength)
            return strVal.replaceRange(startAt until endAt, mask.toString().repeat(endAt - startAt))
        } else {
            return mask.toString().repeat(returnLength)
        }
    }
}
