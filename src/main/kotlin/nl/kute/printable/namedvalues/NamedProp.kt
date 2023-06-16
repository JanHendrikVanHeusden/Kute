package nl.kute.printable.namedvalues

import nl.kute.core.collectPropertyAnnotations
import nl.kute.core.getPropValueString
import nl.kute.reflection.getPropValue
import kotlin.reflect.KProperty

@Suppress("RedundantModalityModifier")
final class NamedProp<T: Any?, V: Any?>(override val obj: T?, override val property: KProperty<V>):
    TypedNameValue<T?, V?>, PropertyValue<T?, V?> {

    override val name: String = property.name

    // we don't want this to be overridden, it handles all exceptions in a defined manner.
    // If people want something else, they can simply make another implementation of TypedNameValue
    override val valueGetter: Supplier<V?>
        get() = { obj?.getPropValue(property) }

    override val printModifyingAnnotations: Set<Annotation> by lazy {
            mutableSetOf<Annotation>().also {
                if (obj != null) run{
                    obj!!::class.collectPropertyAnnotations(property, it)
                }
            }
    }

    // we don't want this to be overridden, it takes defined annotations into account
    // If people want something else, they can simply make another implementation of TypedNameValue
    override val valueString: String? by lazy {
        obj?.getPropValueString(property, printModifyingAnnotations)
    }
}

@Suppress("unused")
fun <T: Any?, V: Any?>T.namedVal(prop: KProperty<V?>): TypedNameValue<T?, V?> = NamedProp(this, prop)