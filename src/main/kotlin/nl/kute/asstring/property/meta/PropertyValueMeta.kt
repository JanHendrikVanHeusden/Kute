package nl.kute.asstring.property.meta

/** Metadata about a property and the property's value */
public sealed interface PropertyValueMeta: PropertyMeta {

    /** The value of the actual instance of this property */
    public val stringValueLength: Int?
    /** Is the value of the actual instance of this property `null`? */
    public val isNull: Boolean

    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}