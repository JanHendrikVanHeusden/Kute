package nl.kute.reflection

import nl.kute.util.asString
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

private fun <T: Any, V: Any?> T.getPropValueNonSafe(property: KProperty1<T, V>?): V? {
    if (property?.isAccessible == false) {
        // Might throw IllegalAccessException otherwise
        property.isAccessible = true
    }
    return property?.get(this)
}

/** @return the value of the property; may return `null` if the property can not be accessed */
@Suppress("UNNECESSARY_SAFE_CALL") // nullability may occur in tests due to mocks that force contrived exceptions
internal fun <T: Any, V: Any?> T.getPropValue(property: KProperty1<T, V>?): V? {
    return try {
        this.getPropValueNonSafe(property)
    } catch (e1: Exception) {
        // no logging framework present, so we can use standard output only
        try {
            println("${e1?.javaClass?.simpleName} occurred when retrieving value of property [${property?.declaringClass()?.simpleName}.${property?.name}]; exception: ${e1?.asString()}")
        } catch (e2: Exception) {
            println("Exception occurred while evaluating property ${property?.name}; exception: ${e1?.asString()}")
        }
        null
    }
}
