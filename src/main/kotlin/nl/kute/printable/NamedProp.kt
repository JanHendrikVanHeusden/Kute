package nl.kute.printable

import nl.kute.reflection.getPropValue
import kotlin.reflect.KProperty

class NamedProp<T: Any?, V: Any?>(val obj: T?, val property: KProperty<V>?): TypedNameValue<T?, V?> {

    override val name: String = property?.name ?: "null"

    // we don't want this to be overridden, it handles all exceptions in a defined manner.
    // If people want something else, they can simply make another implementation of TypedNameValue
    @Suppress("RedundantModalityModifier")
    override final val valueGetter: Supplier<V?>
        get() = { obj?.getPropValue(property) }
}

fun <T: Any?, V: Any?>T.namedVal(prop: KProperty<V?>): TypedNameValue<T?, V?> = NamedProp(this, prop)