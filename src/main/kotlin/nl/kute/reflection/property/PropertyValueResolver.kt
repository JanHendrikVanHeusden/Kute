package nl.kute.reflection.property

import nl.kute.exception.handleWithReturn
import nl.kute.logging.log
import nl.kute.reflection.error.handlePropValException
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

private fun <T : Any?, V : Any?> T?.getPropValueNonSafe(property: KProperty1<T, V>?): V? {
    if (property?.isAccessible == false) {
        // Might throw IllegalAccessException or InaccessibleObjectException, e.g.:
        //  * when a security manager is in place that blocks access
        //  * when using named modules (Java Jigsaw) that do not allow deep reflective access
        property.isAccessible = true
    }
    return if (this == null) null else property?.get(this)
}

@Suppress("UnusedReceiverParameter")
private fun <T : Any?, V : Any?> T?.getPropValueNonSafe(property: KProperty0<V>?): V? {
    if (property?.isAccessible == false) {
        // Might throw IllegalAccessException or InaccessibleObjectException, e.g.:
        //  * when a security manager is in place that blocks access
        //  * when using named modules (Java Jigsaw) that do not allow deep reflective access
        property.isAccessible = true
    }
    return property?.get()
}

/** @return the value of the property; may return `null` if the property can not be accessed */
@JvmSynthetic // avoid access from external Java code
internal fun <T : Any?, V : Any?> T.getPropValue(property: KProperty<V>?): V? {
    return try {
        when (property) {
            is KProperty0<V> -> {
                this?.getPropValueNonSafe(property)
            }

            is KProperty1<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                this?.getPropValueNonSafe(property as KProperty1<T, V>)
            }

            else -> {
                "Unsupported property type ${property!!::class}".let {
                    // should not happen
                    log(it)
                    throw UnsupportedOperationException(it)
                }
            }
        }
    } catch (e: Exception) {
        handleWithReturn(e, null) {
            property?.handlePropValException(e)
        }
    }
}
