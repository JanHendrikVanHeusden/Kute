package nl.kute.printable.annotation.modifiy

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringOmit] annotation can be placed on properties to indicate that the property is excluded
 * (both name and value) from the return value of [nl.kute.core.asString].
 * * Typical usage is to leave insignificant data out of [String] representations
 *     * It may also be used keep sensitive or personally identifiable out of logging etc.
 *     * This may limit exposure of such data; but on its own it must **not** be considered a security feature.
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class AsStringOmit