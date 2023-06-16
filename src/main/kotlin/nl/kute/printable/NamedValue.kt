package nl.kute.printable

class NamedValue<V: Any?>(override val name: String, private val obj: V?): NameValue<V?> {

    override val valueGetter: Supplier<V?>
        get() = { obj }

    override val valueString: String? by lazy { valueGetter()?.toString() }
}

@Suppress("unused")
fun <V: Any?>V?.namedVal(name: String): NameValue<V?> = NamedValue(name, this)