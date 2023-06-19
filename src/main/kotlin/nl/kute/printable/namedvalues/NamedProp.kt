package nl.kute.printable.namedvalues

import nl.kute.core.property.collectPropertyAnnotations
import nl.kute.core.property.getPropValueString
import nl.kute.core.reference.ObjectWeakReference
import nl.kute.core.simplifyClassName
import nl.kute.log.log
import nl.kute.reflection.declaringClass
import nl.kute.util.asString
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

@Suppress("RedundantModalityModifier", "MemberVisibilityCanBePrivate") //
final class NamedProp<T : Any?, V : Any?>(obj: T?, override val property: KProperty<V>) :
    TypedNameValue<T?, V?>, PropertyValue<T?, V?> {

    val coherentProperty: Boolean
    private val objClass = obj?.run { this::class }

    init {
        val propertyClass = property.declaringClass()?.java
        val objClassJava = objClass?.java
        coherentProperty =
            propertyClass == null || objClass == null
                    // Somehow it succeeds when KProperty0 even when incompatible.
                    // It fails however when KProperty1 (ClassCastException on call of valueString)
                    || property is KProperty0 // let KProperty0 go, even if incompatible
                    || objClassJava?.isAssignableFrom(propertyClass) == true

        if (!coherentProperty) {
            with(
                // Instead of downstream ClassCastException, we better signal the issue early
                IllegalStateException(
                    """Property ${property.name} is defined in class ${propertyClass?.kotlin?.simplifyClassName()},
                       |but called on incompatible class ${objClass?.simplifyClassName()} (not in hierarchy).
                       |The property value will not be retrieved; it will return `null` instead!""".trimMargin()
                )
            ) {
                log(this.asString(3))
            }
        }
    }

    override val objectReference: ObjectWeakReference<T?> = ObjectWeakReference(obj)

    override val name: String = if (coherentProperty) property.name else "null"

    override val printModifyingAnnotations: Set<Annotation> by lazy {
        if (!coherentProperty) emptySet() else {
            mutableSetOf<Annotation>().also { annotationSet ->
                objClass?.run {
                    objClass.collectPropertyAnnotations(property, annotationSet)
                }
            }
        }
    }

    // we don't want this to be overridden, it takes defined annotations into account
    // If people want something else, they'd make another implementation of TypedNameValue
    final override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedProp
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = if (!coherentProperty) null
        else objectReference.get()?.getPropValueString(property, printModifyingAnnotations)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamedProp<*, *>

        if (objectReference.get() != other.objectReference.get()) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        var result = objectReference.get()?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        return result
    }
}

@Suppress("unused")
fun <T : Any?, V : Any?> T?.namedVal(prop: KProperty<V?>): TypedNameValue<T?, V?> = NamedProp(this, prop)
