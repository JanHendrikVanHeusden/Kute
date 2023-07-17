package nl.kute.core

import nl.kute.core.annotation.option.AsStringOption

/**
 * Interface with easy-to-use methods for [String] representation; typically for data-centric classes.
 *  * Intended for **Kotlin** classes in first place.
 *  * Methods should work in **Java**-classes as well, insofar no Kotlin specific types are involved
 *     * Specifically, [asStringExcluding] uses [KProperty] arguments that do not exist in Java;
 *      one can use [asStringExcludingNames] instead
 * ---
 * Implementing this interface with its default methods is a functionally equal alternative
 * for directly calling the static methods:
 *  * [Any.asString]
 *  * [AsStringBuilder]
 * ---
 */
//TODO: kdoc
public interface Printable {

    /**
     * Mimics the format of Kotlin data class's [toString] method.
     * * Super-class properties are included
     * * Private properties are included (but not in subclasses)
     * * String value of individual properties is capped at (default) 500; see @[AsStringOption] to override the default
     * @return A String representation of the [Printable], including class name and property names + values;
     * adhering to related annotations; for these annotations, e.g. @[AsStringOption] and others:
     * see package `nl.kute.core.annotation.modify
     * * For finer control of [asString] output, see [AsStringBuilder].
     *
     * @see [AsStringBuilder]
     */
    //TODO: kdoc
    public fun asString(): String = (this as Any).asString()

    /**
     * Abstract override of [toString], so implementing classes are forced to implement it
     * > It is not allowed to provide a default implementation of a member of [Any] in an interface
     *
     * Suggested and simplest implementation is:
     * > `override fun toString(): String = asString()`
     * * adheres to related annotations; for these annotations, e.g. @[AsStringOption] and others:
     *   see package `nl.kute.core.annotation.modify
     * * For finer control of [asString] output, see [AsStringBuilder].
     */
    override fun toString(): String

}