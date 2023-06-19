@file:JvmName("AsStringBuilder")

package nl.kute.core

import nl.kute.core.property.propertiesWithPrintModifyingAnnotations
import nl.kute.core.reference.ObjectWeakReference
import nl.kute.printable.namedvalues.NameValue
import nl.kute.printable.namedvalues.namedVal
import kotlin.reflect.KProperty

class AsStringBuilder private constructor(obj: Any?) {

    val objectReference: ObjectWeakReference<*> = ObjectWeakReference(obj)

    private val classProperties: Set<KProperty<*>> by lazy {
        if (obj != null) {
            obj::class.propertiesWithPrintModifyingAnnotations().keys
        } else {
            emptySet()
        }
    }
    private val classPropertyNames: Set<String> by lazy {
        classProperties.map { it.name }.toSet()
    }

    private val alsoNamed: MutableSet<NameValue<*>> by lazy { mutableSetOf() }
    private val alsoNamedAsTypedArray by lazy { alsoNamed.toTypedArray() }

    private val onlyProperties: MutableSet<KProperty<*>> by lazy { mutableSetOf() }
    private var isOnlyPropertiesSet: Boolean = false
    private val onlyNames: MutableSet<String> by lazy { mutableSetOf() }
    private var isOnlyNamesSet: Boolean = false

    private val exceptProperties: MutableSet<KProperty<*>> by lazy { mutableSetOf() }
    private val exceptPropertyNames: MutableSet<String> by lazy { mutableSetOf() }

    private val propertyNamesToExclude: MutableSet<String> by lazy { mutableSetOf() }

    private var isBuilt: Boolean = false

    /** Allows adding properties of related objects, e.g. member objects, delegates etc. */
    fun withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(props.map { objectReference.get().namedVal(it) })
        }
        return this
    }
    fun withAlsoNamed(vararg nameValues: NameValue<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(nameValues)
        }
        return this
    }
    fun onlyProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.onlyProperties.addAll(props)
            isOnlyPropertiesSet = true
        }
        return this
    }
    fun withOnlyNames(vararg names: String): AsStringBuilder {
        if (!isBuilt) {
            this.onlyNames.addAll(names)
            isOnlyNamesSet = true
        }
        return this
    }
    fun exceptProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.exceptProperties.addAll(props)
        }
        return this
    }
    fun exceptPropertyNames(vararg names: String): AsStringBuilder {
        if (!isBuilt) {
            this.exceptPropertyNames.addAll(names)
        }
        return this
    }

    private fun build() {
        if (isBuilt) {
            return
        }
        val propNamesToInclude: Set<String> =
            (if (isOnlyPropertiesSet) (onlyProperties.map { it.name }) else classPropertyNames)
                .filter { !isOnlyNamesSet || onlyNames.contains(it) }
                .filterNot { exceptProperties.map { it.name }.contains(it) }
                .filterNot { exceptPropertyNames.contains(it) }
                .toSet()
        propertyNamesToExclude.addAll(classPropertyNames - propNamesToInclude)
        isBuilt = true
    }

    fun asString(): String {
        build()
        return objectReference.get().objectAsString(propertyNamesToExclude, *alsoNamedAsTypedArray)
    }

    companion object {
        @JvmStatic
        fun Any?.asStringBuilder() = AsStringBuilder(this)
    }

}

