package nl.kute.printable.namedvalues

import nl.kute.core.reference.ObjectWeakReference

// TODO: weak reference!
class NamedValue<V: Any?>(override val name: String, value: V?): NameValue<V?> {
    val valueReference: ObjectWeakReference<V?> = ObjectWeakReference(value)

    override val valueString: String
        // Using `get` so it's evaluated when required only, not at construction time of the NamedValue
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = valueReference.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamedValue<*>

        if (name != other.name) return false
        if (valueReference != other.valueReference) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (valueReference.get()?.hashCode() ?: 0)
        return result
    }

}

@Suppress("unused")
fun <V: Any?>V?.namedVal(name: String): NameValue<V?> = NamedValue(name, this)
