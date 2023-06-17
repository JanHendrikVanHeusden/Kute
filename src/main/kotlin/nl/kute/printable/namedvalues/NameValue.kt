package nl.kute.printable.namedvalues

typealias Supplier<T> = () -> T?

interface NameValue<V: Any?> {
    val name: String
    val valueString: String?
}

interface TypedNameValue<T: Any?, V: Any?>: NameValue<V> {
    val obj: T?
}