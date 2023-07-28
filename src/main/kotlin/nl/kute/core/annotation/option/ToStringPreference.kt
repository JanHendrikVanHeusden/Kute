package nl.kute.core.annotation.option

public enum class ToStringPreference {
    /**
     * Always use [nl.kute.core.asString], even if the class or any of its superclasses has [toString] overridden.
     * Characteristics:
     * * Maintenance-friendly: new or renamed properties are automatically reflected in the [nl.kute.core.asString] output
     * * Full protection against errors due to recursion
     *   (e.g. [StackOverflowError]s in case of self-referencing or mutually referencing object graphs)
     * * Bypasses existing [toString] implementations of your classes
     * * Fully honours annotations like [nl.kute.core.annotation.modify.AsStringMask] etc.
     * * Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable]s etc.
     * @see [PREFER_TOSTRING]
     */
    USE_ASSTRING,

    /**
     * If the class is a [Collection], [Map], [Array], etc. or if it does not have [toString] overridden,
     * use [nl.kute.core.asString]; otherwise (so [toString] implemented and not a collection etc.), use [toString]
     * * Less maintenance-friendly: new or renamed properties in your classes will not be automatically reflected
     *   for classes with a home-made [toString] implementation
     * * Protection against errors due to recursion, on objects without overridden [toString], and on collections etc.
     *   (e.g. [StackOverflowError]s in case of self-referencing or mutually referencing object graphs);
     *   but no protection against recursion errors in your custom class's [toString] methods
     * * Honours existing [toString] implementations of your classes
     * * Honours annotations like [nl.kute.core.annotation.modify.AsStringMask] etc. only when no home made [toString]
     *   implementation is present
     * * Improved String representation of lambda's, primitive arrays (like Java's `int[]`), [Throwable]s etc.
     * @see [USE_ASSTRING]
     */
    PREFER_TOSTRING,
}