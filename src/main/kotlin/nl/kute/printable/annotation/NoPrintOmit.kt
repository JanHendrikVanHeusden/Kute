package nl.kute.printable.annotation

import nl.kute.printable.Printable
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [NoPrintOmit] annotation can be placed on properties of classes that implement [Printable],
 * to indicate that the property is excluded from the return value of [Printable.asString].
 * * Typical usage is to leave insignificant data out of [String] representations
 *     * It may also be used keep sensitive or personally identifiable out of logging etc.
 *     * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class NoPrintOmit