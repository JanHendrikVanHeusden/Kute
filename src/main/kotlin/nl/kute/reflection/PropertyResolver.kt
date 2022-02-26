package nl.kute.reflection

import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * @param prop the property to get the value of
 * @return the property's value as of the `this` receiver
 */
fun Any.getPropValue(prop: KProperty1<Any, *>): Any? = prop.get(this)

fun getPropValue(prop: KProperty0<*>): Any? = prop.get()

/**
 * Get the properties from the hierarchy (see [topDownTypeHierarchy]
 */
fun Any.propertiesFromHierarchy(
    includePrivate: Boolean = false,
    propertyFilter: (KProperty1<out Any, *>) -> Boolean = { true }
): List<KProperty1<out Any, *>> {
    val classHierarchy = this.bottomUpTypeHierarchy()
    val linkedHashSet: LinkedHashSet<KProperty1<out Any, *>> = linkedSetOf()
    return linkedHashSet.also { theSet ->
        theSet.addAll(
            classHierarchy.asSequence()
                .map { kClass -> kClass.memberProperties
                    .filter { propertyFilter(it) }
                    .filter { includePrivate || it.visibility != PRIVATE }
                }
                .flatten()
                // In case of overloads, keep the property of the most specific subclass
                .distinctBy { prop -> prop.name }
                .onEach { prop ->
                    try {
                        prop.isAccessible = true // to access private or protected ones
                    } catch (e: Exception) {
                        // probably due to SecurityManager and/or jig saw boundaries;
                        // we must adhere, so ignore
                    }
                }
                .filter { it.isAccessible }
                .toList()
        )
    }.toList()
}

