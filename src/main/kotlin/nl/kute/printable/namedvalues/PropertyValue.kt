package nl.kute.printable.namedvalues

import nl.kute.core.reference.ObjectWeakReference
import kotlin.reflect.KProperty

interface PropertyValue<T: Any?, V: Any?> {
    val objectReference: ObjectWeakReference<T?>
    val property: KProperty<V>
    val printModifyingAnnotations: Set<Annotation>
    val valueString: String?
}