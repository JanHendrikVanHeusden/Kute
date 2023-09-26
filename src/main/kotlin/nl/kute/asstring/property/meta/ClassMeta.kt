package nl.kute.asstring.property.meta

import kotlin.reflect.KClass

/** Metadata about a [KClass] */
public sealed interface ClassMeta {

    /** [KClass] that comprises the property */
    public val objectClass: KClass<*>

    /**
     * Simplified name of the [objectClass]
     *  * without package name
     *  * without reference to the outer class, in case of inner / nested class
     */
    public val objectClassName: String?

    public val packageName: String?

    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}