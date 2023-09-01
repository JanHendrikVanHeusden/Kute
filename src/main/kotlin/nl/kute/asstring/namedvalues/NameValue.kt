package nl.kute.asstring.namedvalues

/**
 * Interface for classes to provide name / value combinations from various sources.
 * > For usage, see: [nl.kute.asstring.core.AsStringBuilder]
 */
public interface NameValue<V: Any?> {
    /** The name to identify this [NameValue] */
    public val name: String
    /** The value of this [NameValue] */
    public val value: Any?
}

