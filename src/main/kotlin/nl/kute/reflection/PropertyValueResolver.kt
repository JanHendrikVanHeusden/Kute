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
        getPropValue(property)
    } catch (e: Exception) {
        // Might throw IllegalAccessException if a SecurityManager is active
        // TODO: add logging
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
        // Might throw IllegalAccessException if a SecurityManager is active
        // TODO: add logging
        null
    }
}
