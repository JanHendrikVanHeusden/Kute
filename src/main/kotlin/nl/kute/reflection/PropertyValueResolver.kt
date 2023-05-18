package nl.kute.reflection

import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

internal fun <V: Any?> getPropValue(property: KProperty0<V>): V? {
    if (!property.isAccessible) {
        // Might throw IllegalAccessException otherwise
        property.isAccessible = true
    }
    return property.get()
}

internal fun <V: Any?> getPropValueSafe(property: KProperty0<V>): V? {
    return try {
        // Might throw IllegalAccessException if a SecurityManager is active
        getPropValue(property)
    } catch (e: Exception) {
        null
    }
}

internal fun <T: Any, V: Any?> T.getPropValue(property: KProperty1<T, V>): V? {
    if (!property.isAccessible) {
        // Might throw IllegalAccessException otherwise
        property.isAccessible = true
    }
    return property.get(this)
}

internal fun <T: Any, V: Any?> T.getPropValueSafe(property: KProperty1<T, V>): V? {
    return try {
        this.getPropValue(property)
    } catch (e: Exception) {
        // no logging framework present, so we only can use standard output
        println("${e.javaClass.simpleName} occurred when retrieving value of property [${this.javaClass.canonicalName}.${property.name}]; exception message = [${e.message}]")
        null
    }
}
