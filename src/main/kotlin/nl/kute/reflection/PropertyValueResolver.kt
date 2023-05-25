package nl.kute.reflection

import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

private fun <V: Any?> getPropValueNonSafe(property: KProperty0<V>): V? {
    if (!property.isAccessible) {
        // Might throw IllegalAccessException otherwise
        property.isAccessible = true
    }
    return property.get()
}

/** @return the value of the property; may return `null` if the property can not be accessed */
internal fun <V: Any?> getPropValue(property: KProperty0<V>): V? {
    return try {
        // Might throw IllegalAccessException if a SecurityManager is active
        getPropValueNonSafe(property)
    } catch (e: Exception) {
        // no logging framework present, so we only can use standard output
        println("${e.javaClass.simpleName} occurred when retrieving value of property [${property.declaringClass()?.simpleName}.${property.name}]; exception message = [${e.message}]")
        null
    }
}

private fun <T: Any, V: Any?> T.getPropValueNonSafe(property: KProperty1<T, V>): V? {
    if (!property.isAccessible) {
        // Might throw IllegalAccessException otherwise
        property.isAccessible = true
    }
    return property.get(this)
}

/** @return the value of the property; may return `null` if the property can not be accessed */
internal fun <T: Any, V: Any?> T.getPropValue(property: KProperty1<T, V>): V? {
    return try {
        this.getPropValueNonSafe(property)
    } catch (e: Exception) {
        // no logging framework present, so we only can use standard output
        println("${e.javaClass.simpleName} occurred when retrieving value of property [${this.javaClass.simpleName}.${property.name}]; exception message = [${e.message}]")
        null
    }
}
