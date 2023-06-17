package nl.kute.printable.namedvalues

class NamedSupplier<V: Any?>(override val name: String, private val supplier: Supplier<V?>): NameValue<V?> {
    override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedSupplier
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = supplier()?.toString()
}

@Suppress("unused")
fun <V: Any> Supplier<V>.namedVal(name: String): NameValue<V?> = NamedSupplier(name,this)
