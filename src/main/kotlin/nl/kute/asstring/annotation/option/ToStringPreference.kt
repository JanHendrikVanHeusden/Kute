package nl.kute.asstring.annotation.option

/**
 * Preference for whether to use [toString] (insofar implemented) or [nl.kute.asstring.core.asString] (with dynamic property &
 * value resolution) for custom classes.
 * [toString] implementation
 */
public enum class ToStringPreference {

    /**
     * [USE_ASSTRING] is **recommended** unless:
     *  * specific requirements apply to [toString] methods
     *  * [toString] methods have been created with additional value, e.g. additional values
     *    for tracing objects in logs, etc.
     *    * NB: Such values can also be added to
     *  [nl.kute.asstring.core.asString]; see [nl.kute.asstring.core.AsStringBuilder]
     *
     * When [USE_ASSTRING] applies, [nl.kute.asstring.core.asString] will dynamically resolve properties and values for custom
     * classes, even if the class or any of its superclasses has [toString] overridden.
     * Characteristics:
     * * Maintenance-friendly: new or renamed properties of your custom classes are automatically reflected in
     *   the [nl.kute.asstring.core.asString] output
     * * Full protection against errors due to recursion / self reference / mutual references ([StackOverflowError]s)
     * * Bypasses existing [toString] implementations of your custom classes
     * * Fully honours annotations like [nl.kute.asstring.annotation.modify.AsStringMask] etc.
     * * Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable]s etc.
     * @see [PREFER_TOSTRING]
     */
    USE_ASSTRING,

    /**
     * When [PREFER_TOSTRING] applies, [nl.kute.asstring.core.asString] will call the object's [toString] method on
     * custom classes, provided that the class or any of its superclasses has overridden [toString].
     * * Less maintenance-friendly: new or renamed properties of your custom classes will not be automatically reflected
     *   for classes with a home-made [toString] implementation
     *     * If no
     *   [toString] implemented, [nl.kute.asstring.core.asString] will be used
     * * Limited protection against errors due to recursion / self reference / mutual references (only when no home-made
     *   [toString] implementation)
     * * Honours existing [toString] implementations of your custom classes
     * * Does not honour annotations like [nl.kute.asstring.annotation.modify.AsStringMask] etc. when a home made [toString]
     *   implementation is present
     * * Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable]s etc. only
     *   when no home-made [toString] implementation
     * @see [USE_ASSTRING]
     */
    PREFER_TOSTRING,
}