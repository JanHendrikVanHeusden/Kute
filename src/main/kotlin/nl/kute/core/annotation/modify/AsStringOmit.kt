package nl.kute.core.annotation.modify

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringOmit] annotation can be placed on properties to indicate that the property is excluded
 * (both name and value) from the return value of [nl.kute.core.asString].
 * * Typical usage is to leave insignificant or sensitive data out of [String] representations
 *     * It may be used keep sensitive or personally identifiable out of logging etc.
 *       This may limit exposure of such data; but on its own it must **not** be considered a security feature.
 * * When applied to a property in an interface of a super-class, it will be applied to that property
 *   in the subclass hierarchy; regardless whether or not the property is overridden, and whether or not
 *   it has its own [AsStringOmit] annotation.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
public annotation class AsStringOmit