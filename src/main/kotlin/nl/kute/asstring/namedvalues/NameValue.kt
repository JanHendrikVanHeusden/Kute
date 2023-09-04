package nl.kute.asstring.namedvalues

import nl.kute.reflection.property.propertyReturnTypesToOmit

/**
 * Interface for classes to provide name / value combinations from various sources.
 * * This interface is **sealed**, so it can not be implemented directly by external classes.
 *  Please extend [AbstractNameValue] instead.
 * * Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString] output
 * * Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed]
 * @see [AbstractNameValue]
 * @see [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed]
 */
public sealed interface NameValue<V: Any?> {
    /** The name to identify this [NameValue] */
    public val name: String
    /** The value of this [NameValue] */
    public val value: Any?
}

/**
 * Abstract base class for classes to provide name / value combinations from various sources.
 * * Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString] output
 * * Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed]
 */
public abstract class AbstractNameValue<V: Any?>: NameValue<V> {
    private companion object {
        init {
            // prevent properties with return type NameValue from rendering by asString()
            propertyReturnTypesToOmit.add(NameValue::class)
        }
    }
}

