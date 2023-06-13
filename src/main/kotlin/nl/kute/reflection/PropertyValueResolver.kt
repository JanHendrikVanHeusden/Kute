package nl.kute.reflection

import nl.kute.util.asString
import java.lang.reflect.InvocationTargetException
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
internal fun <T: Any, V: Any?> T.getPropValue(property: KProperty1<T, V>?): V? {
    return try {
        this.getPropValueNonSafe(property)
    } catch (e: InvocationTargetException) {
        if (property?.isLateinit == true && e.cause is UninitializedPropertyAccessException) {
            // lateinit property not yet initialized, no need to print error message. Just consider it null
            return null
        } else printErrMessage(property, e)
        null
    }
    catch (e1: Exception) {
        // no logging framework present, so we can use standard output only
        printErrMessage(property, e1)
        null
    }
}

private fun <T : Any, V : Any?> printErrMessage(property: KProperty1<T, V>?, e: Exception) {
    try {
        // The property's value may be sensitive, so make sure not to use the value in the error message
        println("${e.javaClass.simpleName} occurred when retrieving value of property [${property?.declaringClass()?.simpleName}.${property?.name}]; exception: ${e.asString()}")
    } catch (e2: Exception) {
        println("Exception occurred while evaluating property ${property?.name}; exception: ${e.asString()}")
    }
}
