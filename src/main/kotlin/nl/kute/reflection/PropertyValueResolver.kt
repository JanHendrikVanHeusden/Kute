package nl.kute.reflection

import nl.kute.log.log
import nl.kute.reflection.errormessage.illegalAccessInfo
import nl.kute.reflection.errormessage.inaccessibleObjectInfo
import nl.kute.util.asString
import java.lang.reflect.InaccessibleObjectException
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

var illegalAccessReported: Boolean = false
var inaccessibleObjectReported: Boolean = false

private fun <T : Any, V : Any?> T.getPropValueNonSafe(property: KProperty1<T, V>?): V? {
    if (property?.isAccessible == false) {
        // Might throw IllegalAccessException or InaccessibleObjectException, e.g.:
        //  * when a security manager is in place that blocks access
        //  * when using named modules (Java Jigsaw) that do not allow deep reflective access
        property.isAccessible = true
    }
    return property?.get(this)
}

/** @return the value of the property; may return `null` if the property can not be accessed */
internal fun <T : Any, V : Any?> T.getPropValue(property: KProperty1<T, V>?): V? {
    return try {
        this.getPropValueNonSafe(property)
    } catch (e: IllegalAccessException) {
        // This may happen when a security manager blocks property access
        if (!illegalAccessReported) {
            log(
                """${e.javaClass.simpleName} occurred when retrieving value of property [${property?.declaringClass()?.simpleName}.${property?.name}]; exception: ${e.asString()}
                | ${illegalAccessInfo}
                | Objects that are not accessible will be represented as `null`.
                | This warning is shown only once.""".trimMargin()
            )
            illegalAccessReported = true
        }
        null
    } catch (e: InaccessibleObjectException) {
        // This may happen when using named modules (Java Jigsaw) that do not allow deep reflective access
        if (!inaccessibleObjectReported) {
            log(
                """${e.javaClass.simpleName} occurred when retrieving value of property [${property?.declaringClass()?.simpleName}.${property?.name}]; exception: ${e.asString()}
                | ${inaccessibleObjectInfo}
                | Objects that are not accessible will be represented as `null`.
                | This warning is shown only once.""".trimMargin()
            )
            inaccessibleObjectReported = true
        }
        null
    } catch (e: InvocationTargetException) {
        if (property?.isLateinit == true && e.cause is UninitializedPropertyAccessException) {
            // lateinit property not yet initialized, no need to print error message. Just consider it null
            return null
        } else {
            logErrMessage(property, e.cause ?: e)
        }
        null
    } catch (e1: Exception) {
        logErrMessage(property, e1)
        null
    }
}

private fun <T : Any, V : Any?> Any.logErrMessage(property: KProperty1<T, V>?, t: Throwable) {
    try {
        // The property's value may be sensitive, so make sure not to use the value in the error message
        log("${t.javaClass.simpleName} occurred when retrieving value of property [${property?.declaringClass()?.simpleName}.${property?.name}]; exception: ${t.asString()}")
    } catch (e2: Exception) {
        // ignore
    }
}
