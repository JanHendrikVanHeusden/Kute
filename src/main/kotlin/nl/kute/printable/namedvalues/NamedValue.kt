package nl.kute.printable.namedvalues

// TODO: weak reference!
class NamedValue<V: Any?>(override val name: String, private val obj: V?): NameValue<V?> {
    override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedValue
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = obj?.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamedValue<*>

        if (name != other.name) return false
        if (obj != other.obj) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (obj?.hashCode() ?: 0)
        return result
    }

}

@Suppress("unused")
fun <V: Any?>V?.namedVal(name: String): NameValue<V?> = NamedValue(name, this)
