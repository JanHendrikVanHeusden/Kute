package nl.kute.core.namedvalues

import nl.kute.core.weakreference.ObjectWeakReference
import kotlin.reflect.KProperty

internal interface PropertyValue<T: Any?, V: Any?> {
    val objectReference: ObjectWeakReference<T?>
    val property: KProperty<V>
    val printModifyingAnnotations: Set<Annotation>
    val valueString: String?
}