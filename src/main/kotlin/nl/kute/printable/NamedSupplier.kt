package nl.kute.printable

class NamedSupplier<V: Any>(override val name: String, private val supplier: Supplier<V?>): NameValue<V?> {

    override val valueGetter: Supplier<V?>
        get() = supplier
}

fun <V: Any> Supplier<V>.namedVal(name: String): NameValue<V?> = NamedSupplier(name,this)

private class myClass() {

    fun counter() = ++counter

    companion object {
        var counter: Int = 0
    }
}
fun main() {
    val supplier: Supplier<Int> = { myClass().counter() }
    supplier.namedVal("countering")
}