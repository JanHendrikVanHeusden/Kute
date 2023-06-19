package nl.kute.printable.namedvalues

import nl.kute.core.property.collectPropertyAnnotations
import nl.kute.core.property.getPropValueString
import nl.kute.core.reference.ObjectWeakReference
import kotlin.reflect.KProperty

@Suppress("RedundantModalityModifier") //
final class NamedProp<T: Any?, V: Any?>(obj: T?, override val property: KProperty<V>):
    TypedNameValue<T?, V?>, PropertyValue<T?, V?> {
    override val objectReference: ObjectWeakReference<T?> = ObjectWeakReference(obj)
    private val objClass = obj?.run { this::class }

    override val name: String = property.name

    override val printModifyingAnnotations: Set<Annotation> by lazy {
        mutableSetOf<Annotation>().also { annotationSet ->
            objClass?.run {
                objClass.collectPropertyAnnotations(property, annotationSet)
            }
        }
    }

    // we don't want this to be overridden, it takes defined annotations into account
    // If people want something else, they'd make another implementation of TypedNameValue
    final override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedProp
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = objectReference.get()?.getPropValueString(property, printModifyingAnnotations)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamedProp<*, *>

        if (objectReference.get() != other.objectReference.get()) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = objectReference.get()?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }
}

@Suppress("unused")
fun <T: Any?, V: Any?>T?.namedVal(prop: KProperty<V?>): TypedNameValue<T?, V?> = NamedProp(this, prop)
