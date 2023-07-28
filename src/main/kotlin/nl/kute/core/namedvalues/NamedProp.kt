package nl.kute.core.namedvalues

import nl.kute.config.defaultNullString
import nl.kute.core.property.collectPropertyAnnotations
import nl.kute.core.property.getPropValueString
import nl.kute.core.weakreference.ObjectWeakReference
import nl.kute.log.log
import nl.kute.reflection.declaringClass
import nl.kute.reflection.simplifyClassName
import nl.kute.util.throwableAsString
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * A [NameValue] implementation where the value to be resolved is provided through the given [property]
 *
 * [NamedProp] is a wrapper for a [property], with a weak reference [objectReference] to the object
 * the [property] is to be associated with.
 *
 * [NamedProp] is intended for situations where the [property]'s value needs to be evaluated on each access, where:
 * * All annotations that affect output of [nl.kute.core.asString] are taken into account
 *   (this sets it apart it from [NamedValue] and [NamedProp])
 * * Both state change and reassignment are reflected
 *
 * E.g.:
 * ```
 * class MyClass(var aDate: Date = Date())
 * val myObject1 = MyClass(Date().also { it.time = it.time - 86_400_000 }) // yesterday
 * val myObject2 = MyClass(myObject1.aDate) // also yesterday
 *
 * val myClass1NamedProp = NamedProp(myObject1, MyClass::aDate).also { println(it.value) /* yesterday */ }
 * val myObject2NamedProp = NamedProp(myObject2, myObject2::aDate).also { println(it.value) /* yesterday */ }
 *
 * myObject1.aDate = Date() // today
 * myObject2.aDate = myObject1.aDate // today
 * println(myClass1NamedProp.value) // today
 * println(myObject2NamedProp.value) // today
 * ```
 * * Usage of [NamedProp] in a pre-built [nl.kute.core.AsStringBuilder] is good practice,
 *   as reassignment is reflected
 * * All annotations that affect output of [nl.kute.core.asString] are taken in account:
 *   these that are part of the supplied object as well as annotations in the outer context (e.g.
 *   in the above example: annotations within the class that holds the `aDate` variable).
 * * The object the property is associated with is weakly referenced only, so the [NamedProp] does not prevent
 *   garbage collection of the [property] or it's supplied value.
 *
 * > The property is of the more general type [KProperty] (specifically, not [kotlin.reflect.KProperty0]
 * > or [kotlin.reflect.KProperty1]). [KProperty] allows more flexibility in values to be handled, but it also
 * > allows incoherent calls, e.g.:
 * `NamedProp(objOfaClass, AnotherClass::someProperty)`
 *
 * > [NamedProp] is resilient for such incoherent calls, but the resulting [value] may be `null`, and a log
 * > message may be issued on construction of the [NamedProp].
 *
 * @param obj The object on which the [property] is to be resolved
 * @param property The property to retrieve the value with.
 */
@Suppress("RedundantModalityModifier")
public final class NamedProp<T : Any?, V : Any?>(obj: T?, public override val property: KProperty<V>) :
    NameValue<V>, WeakReferencing<T>, PropertyValue<V?> {

    /**
     * Indicates whether the [property]'s value can be resolved on the given object
     * * `true` if no breaking incoherence was found between object and [property]
     * * `false` if a wrong (incoherent) combination of object and [property] was passed to the constructor
     * that would break property value resolution; in that case, the result will always be `null`
     * > When `false`, a log message is issued on construction of the [NamedProp], and the [value]
     * > will always be `null`, regardless on actual values of the object and the [property].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public val propertyCoherentWithObject: Boolean
    private val objClass = obj?.let { it::class }

    init {
        val propertyClass = property.declaringClass()?.java
        val objClassJava = objClass?.java
        propertyCoherentWithObject = isPropertyCoherent(propertyClass, objClassJava)
    }

    override val objectReference: ObjectWeakReference<T?> = ObjectWeakReference(obj)

    public override val name: String = if (propertyCoherentWithObject) property.name else defaultNullString

    override val asStringAffectingAnnotations: Set<Annotation> by lazy {
        if (!propertyCoherentWithObject) emptySet() else {
            mutableSetOf<Annotation>().also { annotationSet ->
                objClass?.run {
                    collectPropertyAnnotations(property, annotationSet)
                }
            }
        }
    }

    // final: we don't want this to be overridden, it takes defined annotations into account
    // If people want something else, they'd make another NameValue implementation
    /** The [String] representation of the [property] value, with annotations taken into account */
    final override val value: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedProp
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = if (!propertyCoherentWithObject) null
        else objectReference.get()?.getPropValueString(property, asStringAffectingAnnotations)

    private fun isPropertyCoherent(propertyClass: Class<out Any>?, objClassJava: Class<out T & Any>?): Boolean {
        val isCoherent = ((propertyClass == null || objClass == null
                // Somehow retrieving the property value will succeed when property is of type KProperty0,
                // even with incompatible (non-coherent) classes. A bit weird.
                // This seems to be necessary for retrieval of values from super hierarchy, so probably
                // "by design" of Kotlin authors.
                // So we also allow/ignore the inconsistency in case of **KProperty0**.
                //
                // It fails however when it is **KProperty1**: on inconsistent call with KProperty1<T, V>.get(obj)
                // it would throw ClassCastException on call of valueString.
                //     > KProperty1<T,V> also defines the object type T
                //     > (whereas KProperty0<V> only defines the value type)
                // So we detect it early to prevent ClassCastException downstream:
                || property is KProperty0) || propertyClass.isAssignableFrom(objClassJava as Class<*>))

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
                log(this.throwableAsString(3))
            }
        }
        return isCoherent
    }
}

/**
 * Convenience method to construct a [NamedProp]
 * @receiver The [T] object the [NamedProp] is about
 * @param prop The [V] producing property associated with the receiver object
 */
public fun <T : Any?, V : Any?> T?.namedProp(prop: KProperty<V?>): NamedProp<T?, V?> = NamedProp(this, prop)
