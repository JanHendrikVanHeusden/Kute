package nl.kute.core.namedvalues

import nl.kute.core.property.collectPropertyAnnotations
import nl.kute.core.property.getPropValueString
import nl.kute.core.weakreference.ObjectWeakReference
import nl.kute.log.log
import nl.kute.reflection.declaringClass
import nl.kute.reflection.simplifyClassName
import nl.kute.util.asString
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

@Suppress("RedundantModalityModifier")
//TODO: kdoc
public final class NamedProp<T : Any?, V : Any?>(obj: T?, override val property: KProperty<V>) :
    TypedNameValue<T?, V?>, PropertyValue<T?, V?> {

    @Suppress("MemberVisibilityCanBePrivate")
    //TODO: kdoc
    public val coherentProperty: Boolean
    private val objClass = obj?.let { it::class }

    init {
        val propertyClass = property.declaringClass()?.java
        val objClassJava = objClass?.java
        coherentProperty = isPropertyCoherent(propertyClass, objClassJava)
    }

    override val objectReference: ObjectWeakReference<T?> = ObjectWeakReference(obj)

    override val name: String = if (coherentProperty) property.name else "null"

    override val printModifyingAnnotations: Set<Annotation> by lazy {
        if (!coherentProperty) emptySet() else {
            mutableSetOf<Annotation>().also { annotationSet ->
                objClass?.run {
                    collectPropertyAnnotations(property, annotationSet)
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

    private fun isPropertyCoherent(propertyClass: Class<out Any>?, objClassJava: Class<out T & Any>?): Boolean {
        val isCoherent = (propertyClass == null || objClass == null
                // Somehow retrieving the property value will succeed when property is of type KProperty0, even when
                // incompatible (non-coherent) classes (feels weird; maybe necessary to support delegation?)
                // This appears necessary for retrieval of values from super hierarchy, so seems "by design"
                // of Kotlin designers. So we allow the inconsistency in case of KProperty0.
                //   > It fails however when it is KProperty1: KProperty1<T, V>.get(obj) with
                //   > ClassCastException on call of valueString.
                //     > KProperty1<T,V> also defines the object type T
                //     > (whereas KProperty0<V> only defines the value type)
                || property is KProperty0 // let KProperty0 go, even if incompatible
                //   > So for KProperty1, we want to detect incompatibility / inconsistency early,
                //   > instead of difficult to track downstream ClassCastException
                //   So mark it as not coherent in that case.
                || objClassJava?.isAssignableFrom(propertyClass) == true)

        if (!isCoherent) {
            // Instead of downstream ClassCastException, we better signal the issue early
            with(
                IllegalStateException(
                    """Property ${property.name} is defined in class ${propertyClass?.kotlin?.simplifyClassName()},
                           |but called on incompatible class ${objClass?.simplifyClassName()} (not in hierarchy).
                           |The property value will not be retrieved; it will return `null` instead!""".trimMargin()
                )
            ) {
                // just log it; not throwing it
                log(this.asString(3))
            }
        }
        return isCoherent
    }
}

//TODO: kdoc
public fun <T : Any?, V : Any?> T?.namedProp(prop: KProperty<V?>): TypedNameValue<T?, V?> = NamedProp(this, prop)
