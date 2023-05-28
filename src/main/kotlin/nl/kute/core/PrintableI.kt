package nl.kute.core

import nl.kute.printable.annotation.option.PrintOption
import nl.kute.util.asString
import kotlin.reflect.KProperty

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
 *  * [Any.asStringExcluding]
 *  * [Any.asStringExcludingNames]
 * ---
 */
interface PrintableI {

    /**
     * Mimics the format of Kotlin data class's [toString] method.
     * * Super-class properties are included
     * * Private properties are included
     * * String value of individual properties is capped at (default) 500; see @[PrintOption] to override the default
     * @return A String representation of the [PrintableI], including class name and property names + values;
     * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify
     * `
     * @see [asStringExcluding]
     * @see [asStringExcludingNames]
     */
    fun asString(): String = (this as Any).asString()

    /**
     * Mimics the format of Kotlin data class's [toString] method.
     * * Super-class properties are included
     * * Private properties are included
     * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
     * @param propsToExclude accessible properties that you don't want to be included in the result.
     * E.g. `override fun toString() = `[asStringExcluding]`(::myExcludedProp1, ::myExcludedProp2)`
     * **NB:** Excluding properties will not work from Java classes; use [asStringExcludingNames] instead
     * @return A String representation of the [PrintableI], including class name and property names + values;
     * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
     * @see [asStringExcluding]
     * @see [asStringExcludingNames]
     */
    fun asStringExcluding(vararg propsToExclude: KProperty<*>): String = (this as Any).asStringExcluding(*propsToExclude)


    /**
     * Mimics the format of Kotlin data class's [toString] method.
     * * Super-class properties are included
     * * Private properties are included
     * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
     *
     * This method allows you to exclude any properties by name, including inaccessible private ones.
     * @param propNamesToExclude property names that you don't want to be included in the result; case-sensitive.
     * E.g. use it when not calling from inside the class:
     * `someObjectWithPrivateProps.`[asStringExcludingNames]`("myExcludedPrivateProp1", "myExcludedProp2")
     * @return A String representation of the [PrintableI], including class name and property names + values;
     * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
     * @see [asString]
     * @see [asStringExcluding]
     */
    fun asStringExcludingNames(vararg propNamesToExclude: String): String = (this as Any).asStringExcludingNames(*propNamesToExclude)

}