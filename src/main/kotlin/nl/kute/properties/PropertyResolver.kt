package nl.kute.properties

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class PropertyResolver {

    /**
     * @param prop the property to get the value of
     * @return the property's value as of the `this` receiver
     */
    fun Any.getPropValue(prop: KProperty1<Any, *>): Any? = prop.get(this)

    /**
     * Get the properties from the hierarchy (see [classHierarchy]
     */
    fun Any.propertiesFromHierarchy(
        includeNonPublic: Boolean = false,
        propertyFilter: (KProperty1<out Any, *>) -> Boolean = { true }
    ): Collection<KProperty1<out Any, *>> {
        val classHierarchy = this.classHierarchy()
        val linkedHashSet: LinkedHashSet<KProperty1<out Any, *>> = linkedSetOf()
        return linkedHashSet.also { theSet ->
            theSet.addAll(
                classHierarchy
                    .map { kClass -> kClass.memberProperties }.flatten()
                    // Property overrides would cause duplicate names. We want to keep the subclass property only.
                    // But distinctBy keeps the first value (would be the topmost superclass property).
                    // To keep the last ones (deepest subclass), reverse it, then call distinctBy
                    .reversed().distinctBy { prop -> prop.name }
                    .reversed() // reverse again to normal order
                    .onEach { prop ->
                        if (includeNonPublic) {
                            try {
                                prop.isAccessible = true // to access private or protected ones
                            } catch (e: Exception) {
                                // probably due to SecurityManager and/or jig saw boundaries;
                                // we must adhere, so ignore
                            }
                        }
                    }
                    .filter { it.isAccessible }
                    .filter { propertyFilter(it) }
            )
        }
    }

}
