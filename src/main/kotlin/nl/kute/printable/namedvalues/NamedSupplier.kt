package nl.kute.printable.namedvalues

class NamedSupplier<V: Any>(override val name: String, private val supplier: Supplier<V?>): NameValue<V?> {

    override val valueGetter: Supplier<V?>
        get() = supplier

    override val valueString: String? by lazy { valueGetter()?.toString() }
}

@Suppress("unused")
fun <V: Any> Supplier<V>.namedVal(name: String): NameValue<V?> = NamedSupplier(name,this)
