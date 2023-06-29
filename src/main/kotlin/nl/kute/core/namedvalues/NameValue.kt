package nl.kute.core.namedvalues

import nl.kute.core.weakreference.ObjectWeakReference

typealias Supplier<T> = () -> T?

interface NameValue<V: Any?> {
    val name: String
    val valueString: String?
}

interface TypedNameValue<T: Any?, V: Any?>: NameValue<V> {
    val objectReference: ObjectWeakReference<T?>
}