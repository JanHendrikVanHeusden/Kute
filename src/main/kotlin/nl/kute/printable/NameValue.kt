package nl.kute.printable

typealias Supplier<T> = () -> T?

interface NameValue<V: Any?> {
    val name: String
    val valueGetter: Supplier<V?>
    val valueString: String?
}

interface TypedNameValue<T: Any?, V: Any?>: NameValue<V> {
    val obj: T?
}