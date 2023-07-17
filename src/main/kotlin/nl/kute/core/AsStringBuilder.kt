@file:JvmName("AsStringBuilder")

package nl.kute.core

import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.namedVal
import nl.kute.core.property.propertiesWithPrintModifyingAnnotations
import nl.kute.core.weakreference.ObjectWeakReference
import nl.kute.reflection.declaringClass
import nl.kute.reflection.simplifyClassName
import kotlin.reflect.KProperty

//TODO: kdoc
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
            obj!!::class.propertiesWithPrintModifyingAnnotations().keys
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

    /** Allows adding properties of related objects, e.g. member objects, delegates etc. */
    public fun withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(props.map { objectReference.get().namedVal(it) })
        }
        return this
    }
    //TODO: kdoc
    public fun withAlsoNamed(vararg nameValues: NameValue<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(nameValues)
        }
        return this
    }
    //TODO: kdoc
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

    override fun toString(): String {
        return "${this::class.simplifyClassName()} -> ${objJavaClass?.name?.simplifyClassName() ?: "null"}" +
                " (built = $isBuilt; excluded = $propertyNamesToExclude; properties = ${classPropertyNames - propertyNamesToExclude};" +
                " alsoNamed = ${alsoNamed.map { it.name }})"
    }

    //TODO: kdoc
    public companion object {
        @JvmStatic
        //TODO: kdoc
        public fun Any?.asStringBuilder(): AsStringBuilder = AsStringBuilder(this)
    }

}
