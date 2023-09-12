package nl.kute.asstring.property.meta

import kotlin.reflect.KClass
import kotlin.reflect.KType

/** Interface for metadata about a property and the property's value */
public interface PropertyValueMetaData {
    /** [KClass] that comprises the property */
    public val objectClass: KClass<*>?
    /** Class name of the [KClass] that comprises the property */
    public val objectClassName: String?
    /** The property's name */
    public val propertyName: String

    /** The [kotlin.reflect.KProperty.returnType] of the property */
    public val returnType: KType
    /** Is the property's value `null`? */
    public val isNull: Boolean
    /** Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType]? */
    public val isBaseType: Boolean
    /** Is the property's return type a collection-like type, e.g. [Array], [Collection], [Map], [IntArray], [CharArray] and other `*Array`s, etc. */
    public val isCollectionLike: Boolean
    /** Is the property's return type a [CharSequence] type? E.g. [String], [StringBuilder], [java.nio.CharBuffer], etc. */
    public val isCharSequence: Boolean
    /** The property's value length (as retrieved with the associated object) */
    public val stringValueLength: Int?

    public override fun toString(): String
    public override fun hashCode(): Int
    public override fun equals(other: Any?): Boolean
}