package nl.kute.core.namedvalues

import nl.kute.core.weakreference.ObjectWeakReference

/** Interface for classes that want to keep a weak reference to an object */
internal interface WeakReferencing<T: Any?> {
    /**
     * The [ObjectWeakReference] referencing the object that is to provide the value
     * (i.e., that will eventually produce the [NamedValue.value])
     */
    val objectReference: ObjectWeakReference<T?>
}