package nl.kute.core

import kotlin.reflect.KProperty

class AsStringBuilder internal constructor(val obj: Any?) {

    fun withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder {
        return this
    }
    fun withOnlyProperties(vararg props: KProperty<*>): AsStringBuilder {
        return this
    }
    fun exceptProperties(vararg props: KProperty<*>): AsStringBuilder {
        return this
    }
    fun exceptNames(vararg propNames: String): AsStringBuilder {
        return this
    }

    fun asString(): String = ""

}

fun Any.asStringBuilder() = AsStringBuilder(this)
