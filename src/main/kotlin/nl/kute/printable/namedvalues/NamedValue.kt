package nl.kute.printable.namedvalues

class NamedValue<V: Any?>(override val name: String, private val obj: V?): NameValue<V?> {
    override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedValue
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = obj?.toString()
}

@Suppress("unused")
fun <V: Any?>V?.namedVal(name: String): NameValue<V?> = NamedValue(name, this)
