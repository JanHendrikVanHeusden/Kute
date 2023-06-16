package nl.kute.printable.namedvalues

import kotlin.reflect.KProperty

interface PropertyValue<T: Any?, V: Any?> {
    val obj: T?
    val property: KProperty<V>
    val printModifyingAnnotations: Set<Annotation>
    val valueString: String?
}