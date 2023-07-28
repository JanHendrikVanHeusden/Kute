package nl.kute.core.namedvalues

import kotlin.reflect.KProperty

/**
 * Interface for classes that aim to resolve a [property]'s value
 * with respect to it's associated [asStringAffectingAnnotations]
 * @param V The [property]'s return type
 */
internal interface PropertyValue<V: Any?> {
    /** The property to resolve the value of */
    val property: KProperty<V>
    /** The annotations that may affect the [property]'s String representation */
    val asStringAffectingAnnotations: Set<Annotation>
}