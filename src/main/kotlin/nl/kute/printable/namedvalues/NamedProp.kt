package nl.kute.printable.namedvalues

import nl.kute.core.property.collectPropertyAnnotations
import nl.kute.core.property.getPropValueString
import kotlin.reflect.KProperty

@Suppress("RedundantModalityModifier")
final class NamedProp<T: Any?, V: Any?>(override val obj: T?, override val property: KProperty<V>):
    TypedNameValue<T?, V?>, PropertyValue<T?, V?> {

    override val name: String = property.name

    override val printModifyingAnnotations: Set<Annotation> by lazy {
            mutableSetOf<Annotation>().also {
                if (obj != null) run{
                    obj!!::class.collectPropertyAnnotations(property, it)
                }
            }
    }

    // we don't want this to be overridden, it takes defined annotations into account
    // If people want something else, they can simply make another implementation of TypedNameValue
    override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedProp
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = obj?.getPropValueString(property, printModifyingAnnotations)
}

@Suppress("unused")
fun <T: Any?, V: Any?>T.namedVal(prop: KProperty<V?>): TypedNameValue<T?, V?> = NamedProp(this, prop)