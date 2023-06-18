@file:JvmName("AsStringBuilder")

package nl.kute.core

import nl.kute.printable.namedvalues.NameValue
import kotlin.reflect.KProperty

// TODO: weak reference!
class AsStringBuilder private constructor(val obj: Any?) {

    private val alsoProperties: MutableSet<KProperty<*>> = mutableSetOf()
    private val alsoNamed: MutableSet<NameValue<*>> = mutableSetOf()

    private val onlyProperties: MutableSet<KProperty<*>> = mutableSetOf()
    private var isOnlyPropertiesSet: Boolean = false
    private val onlyNames: MutableSet<String> = mutableSetOf()
    private var isOnlyNamesSet: Boolean = false
    private val onlyNamed: MutableSet<NameValue<*>> = mutableSetOf()
    private var isOnlyNamedSet: Boolean = false

    private val exceptPropertes: MutableSet<KProperty<*>> = mutableSetOf()
    private val exceptNames: MutableSet<String> = mutableSetOf()

    private var isBuilt: Boolean = false

    fun withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && props != null) {
            this.alsoProperties.addAll(props)
        }
        return this
    }
    fun withAlsoNamed(vararg nameValues: NameValue<*>): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && nameValues != null) {
            this.alsoNamed.addAll(nameValues)
        }
        return this
    }
    fun onlyProperties(vararg props: KProperty<*>): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && props != null) {
            this.onlyProperties.addAll(props)
            isOnlyPropertiesSet = true
        }
        return this
    }
    fun onlyNamed(vararg nameValues: NameValue<*>): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && nameValues != null) {
            this.onlyNamed.addAll(nameValues)
            isOnlyNamedSet = true
        }
        return this
    }
    fun withOnlyNames(vararg names: String): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && names != null) {
            this.onlyNames.addAll(names)
            isOnlyNamesSet = true
        }
        return this
    }
    fun exceptProperties(vararg props: KProperty<*>): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && props != null) {
            this.exceptPropertes.addAll(props)
        }
        return this
    }
    fun exceptNames(vararg names: String): AsStringBuilder {
        @Suppress("SENSELESS_COMPARISON") // can be null when called from Java with explicit null
        if (!isBuilt && names != null) {
            this.exceptNames.addAll(names)
        }
        return this
    }

    fun asString(): String {
        isBuilt = true
        return ""
    }

    companion object {
        @JvmStatic
        fun Any.asStringBuilder() = AsStringBuilder(this)
    }

}

