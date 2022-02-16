package nl.kute.printable.annotation

import nl.kute.printable.Printable
import java.lang.annotation.Inherited

/**
 * The [NoPrintMask] annotation can be placed on properties of classes that implement [Printable],
 * to indicate that the property is included in the return value of [Printable.asString], but
 * with its value masked.
 * * Typical usage is to keep sensitive or personally identifiable out of logging etc.
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
annotation class NoPrintMask(
    /** The char to use for masking */
    val mask: Char = '*',
    /** Should nulls be masked too? If `false`, nulls will be shown */
    val maskNulls: Boolean = true,
    /** The minimum length of the mask */
    val maskMinLength: Int = 0
)

