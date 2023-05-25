package nl.kute.printable.annotation

import nl.kute.printable.Printable
import nl.kute.reflection.annotation.annotationOfPropertyInHierarchy
import nl.kute.reflection.getPropValue
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KProperty1

/**
 * The [PrintMask] annotation can be placed on properties of classes that implement [Printable],
 * to indicate that the property is included in the return value of [Printable.asString], but
 * with its value masked.
 * * Typical usage is to keep sensitive or personally identifiable out of logging etc.
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
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

    /** The minimum length of the masked value */
    val minLength: Int = -1,

    /** The maximum length of the masked value */
    val maxLength: Int = Int.MAX_VALUE,
)

fun <T: Any, V: Any?> mask(obj: T, prop: KProperty1<T, V>): String? {
    val propVal: V? = obj.getPropValue(prop)
    with(prop.annotationOfPropertyInHierarchy<PrintMask>() ?: return null) {
        var strVal: String
        strVal = if (propVal == null) {
            if (maskNulls) { "null" } else { return null }
        } else {
            propVal.toString()
        }
        var maskedLength = strVal.length
        if (maxLength >= 0 && strVal.length > maxLength) {
            maskedLength = maxLength
        }
        if (minLength >= 0 && maskedLength < minLength) {
            maskedLength = minLength
        }
        if (maskedLength > strVal.length) {
            strVal = strVal + mask.toString().repeat(maskedLength - strVal.length)
        } else if (maskedLength < strVal.length) {
            strVal = strVal.take(maskedLength)
        }

        if ((startMaskAt in 0 until maskedLength-1 || endMaskAt in 0 until maskedLength) && endMaskAt > startMaskAt ) {
            val startAt = maxOf(startMaskAt, 0)
            val endAt = minOf(endMaskAt, maskedLength)
            return strVal.replaceRange(startAt until endAt, mask.toString().repeat(endAt - startAt))
        } else {
            return mask.toString().repeat(maskedLength)
        }
    }
}
