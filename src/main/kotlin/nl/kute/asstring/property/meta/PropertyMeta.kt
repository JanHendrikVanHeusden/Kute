package nl.kute.asstring.property.meta

import kotlin.reflect.KProperty
import kotlin.reflect.KType

/** Metadata about a property */
public sealed interface PropertyMeta: ClassMeta {
    /** The property this [PropertyMeta] is about */
    public val property: KProperty<*>

    /** The [property]'s name */
    public val propertyName: String

    /** The [kotlin.reflect.KProperty.returnType] of the property */
    public val returnType: KType

    /** Is the property's return type considered a base type, according to [nl.kute.asstring.core.isBaseType]? */
    public val isBaseType: Boolean

    /** Is the property's return type a collection-like type, e.g. [Array], [Collection], [Map], [IntArray], [CharArray] and other `*Array`s, etc. */
    public val isCollectionLike: Boolean

    /** Is the property's return type a [CharSequence] type? E.g. [String], [StringBuilder], [java.nio.CharBuffer], etc. */
    public val isCharSequence: Boolean

    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}