@file:JvmName("AsStringBuilder")

package nl.kute.core

import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.namedProp
import nl.kute.core.property.propertiesWithAsStringAffectingAnnotations
import nl.kute.core.weakreference.ObjectWeakReference
import nl.kute.reflection.declaringClass
import nl.kute.reflection.simplifyClassName
import kotlin.reflect.KProperty

/**
 * Builder to allow more options than [nl.kute.core.asString], like additional properties, filtering out properties
 * (by property or by name), specify ordering, etc.
 *
 * Building it can be done explicitly by calling [AsStringBuilder.build], or implicitly by the first call to
 * [AsStringBuilder.asString].
 * * After being built, the [AsStringBuilder] is effectively immutable.
 * * Without any further settings (e.g. [exceptProperties], [withAlsoProperties], [alsoNamed] etc.),
 *   the call to [AsStringBuilder.asString] produces the same result as [nl.kute.core.asString]
 *
 * *Usage:*
 * * For best performance, the [AsStringBuilder] object (or the [AsStringProducer] object produced by the [build]
 *   method) may be stored as an instance variable within the class it applies to.
 *   This avoids the cost of building it over and over (like when it is part of a log-statement or of a
 *   `toString()` method).
 * * An [AsStringBuilder] does not prevent garbage collection of the object it applies to (it uses weak reference).
 */
public class AsStringBuilder private constructor(private var obj: Any?) : AsStringProducer() {

    private val objectReference: ObjectWeakReference<*> = ObjectWeakReference(obj)
    private val objJavaClass: Class<*>? = obj?.javaClass
    private val isMatchingProperty: (KProperty<*>) -> Boolean =
        { objJavaClass == null
                || it.declaringClass()?.java?.isAssignableFrom(objJavaClass) == true
        }

    private val classProperties: Set<KProperty<*>> by lazy {
        if (obj == null) {
            emptySet()
        } else {
            obj!!::class.propertiesWithAsStringAffectingAnnotations().keys
        }
    }

    private val classPropertyNames: Set<String> by lazy {
        classProperties.map { it.name }.toSet()
    }

    private val alsoNamed: MutableSet<NameValue<*>> by lazy { mutableSetOf() }
    private val alsoNamedAsTypedArray by lazy { alsoNamed.toTypedArray() }

    private val onlyProperties: MutableSet<KProperty<*>> by lazy { mutableSetOf() }
    private var isOnlyPropertiesSet: Boolean = false
    private val onlyPropertyNames: MutableSet<String> by lazy { mutableSetOf() }
    private var isOnlyPropertyNamesSet: Boolean = false

    private val exceptProperties: MutableSet<KProperty<*>> by lazy { mutableSetOf() }
    private val exceptPropertyNames: MutableSet<String> by lazy { mutableSetOf() }

    private val propertyNamesToExclude: MutableSet<String> by lazy { mutableSetOf() }

    private var isBuilt: Boolean = false

    /**
     * Allows adding additional related properties, e.g. member objects, delegates etc.
     * @param props the properties to be added to the output of [asString]
     */
    public fun withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(props.map { objectReference.get().namedProp(it) })
        }
        return this
    }

    /**
     * Allows adding additional related [NameValue]s, that provide property-like named values.
     *
     * This allows to add arbitrary values to the [asString] output, e.g. unrelated properties,
     * calculated values, etc.
     * > For usage of these, see the subclasses of [NameValue]
     * @param nameValues The [NameValue]s to add to the output of [asString]
     */
    public fun withAlsoNamed(vararg nameValues: NameValue<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(nameValues)
        }
        return this
    }

    /**
     * Restricts the output to only the object's properties listed in [props].
     * > *NB:* [NameValue]s added by [withAlsoNamed] and [withAlsoProperties] are not filtered out.
     * @param props The restrictive list of properties to be included in the output of [toString].
     */
    public fun withOnlyProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.onlyProperties.addAll(props.filter(isMatchingProperty))
            isOnlyPropertiesSet = true
        }
        return this
    }
    //TODO: kdoc
    public fun withOnlyPropertyNames(vararg names: String): AsStringBuilder {
        if (!isBuilt) {
            this.onlyPropertyNames.addAll(names)
            isOnlyPropertyNamesSet = true
        }
        return this
    }
    //TODO: kdoc
    public fun exceptProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.exceptProperties.addAll(props)
        }
        return this
    }
    //TODO: kdoc
    public fun exceptPropertyNames(vararg names: String): AsStringBuilder {
        if (!isBuilt) {
            this.exceptPropertyNames.addAll(names)
        }
        return this
    }

    //TODO: kdoc
    public fun build(): AsStringProducer {
        if (isBuilt) {
            return this
        }
        val propNamesToInclude: Set<String> =
            (if (isOnlyPropertiesSet) (onlyProperties.map { it.name }) else classPropertyNames)
                .filter { !isOnlyPropertyNamesSet || onlyPropertyNames.contains(it) }
                .filterNot { exceptProperties.map { it.name }.contains(it) }
                .filterNot { exceptPropertyNames.contains(it) }
                .toSet()
        propertyNamesToExclude.addAll(classPropertyNames - propNamesToInclude)
        isBuilt = true
        // Nullify to make it eligible for garbage collection
        // NB: It must not be nullified before build() is called, that might cause unexpected behaviour
        //     (could be garbage collected before or during the lazy initialization of collections)
        obj = null
        return this
    }

    override fun asString(): String {
        build()
        return objectReference.get().objectAsString(propertyNamesToExclude, *alsoNamedAsTypedArray)
    }

    override fun toString(): String =
        "${this::class.simplifyClassName()} -> ${objJavaClass?.name?.simplifyClassName() ?: "null"}" +
                " (built = $isBuilt; excluded = $propertyNamesToExclude; properties = ${classPropertyNames - propertyNamesToExclude};" +
                " alsoNamed = ${alsoNamed.map { it.name }})"

    /** Static method [asStringBuilder] to create an [AsStringBuilder] object */
    public companion object {
        @JvmStatic
        //TODO: kdoc
        public fun Any?.asStringBuilder(): AsStringBuilder = AsStringBuilder(this)
    }

}
