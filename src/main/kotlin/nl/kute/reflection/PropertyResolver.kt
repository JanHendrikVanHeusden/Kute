package nl.kute.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Get the properties of the [Any] receiver from its class hierarchy (see [topDownTypeHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun Any.propertiesFromHierarchy(): List<KProperty1<out Any, *>> =
    this::class.propertiesFromHierarchy()

/**
 * Get the properties from the class hierarchy (see [bottomUpTypeHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun KClass<*>.propertiesFromHierarchy(): List<KProperty1<out Any, *>> {
    val classHierarchy = this.bottomUpTypeHierarchy()
    val linkedHashSet: LinkedHashSet<KProperty1<out Any, *>> = linkedSetOf()
    return linkedHashSet.also { theSet ->
        theSet.addAll(
            classHierarchy.asSequence()
                .map { kClass -> kClass.memberProperties }
                .flatten()
                // In case of overloads or name-shadowing, keep the property of the most specific subclass
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

/**
 * Get the properties from the class hierarchy (see [bottomUpTypeHierarchy]),
 * mapped by the property's declaring class.
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
fun Any.propertyMapByHierarchy(): Map<KClass<*>?, List<KProperty1<out Any, *>>> =
    this::class.propertyMapByHierarchy()

/**
 * Get the properties from the class hierarchy (see [bottomUpTypeHierarchy]),
 * mapped by the property's declaring class.
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun KClass<*>.propertyMapByHierarchy(): Map<KClass<*>?, List<KProperty1<out Any, *>>> {
    val classHierarchy = this.bottomUpTypeHierarchy()
    val linkedHashMap: LinkedHashMap<KClass<*>?, MutableList<KProperty1<out Any, *>>> = LinkedHashMap()
    val pairList: List<Pair<KClass<*>, KProperty1<out Any, *>>> =
        classHierarchy.asSequence()
            .map { kClass -> kClass.memberProperties.map { Pair(kClass, it) } }
            .flatten()
            // In case of overloads or name-shadowing, keep the property of the most specific subclass
            .distinctBy { pair -> pair.second.name }
            .onEach { pair ->
                try {
                    pair.second.isAccessible = true // to access private or protected ones
                } catch (e: Exception) {
                    // probably due to SecurityManager and/or jig saw boundaries;
                    // we must adhere, so ignore
                }
            }
            .filter { it.second.isAccessible }
            .map { Pair(it.second.declaringClass() ?: it.first, it.second) }
            .toList()
    pairList.forEach {
        if (!linkedHashMap.containsKey(it.first)) {
            linkedHashMap[it.first] = mutableListOf(it.second)
        } else {
            linkedHashMap[it.first]!!.add(it.second)
        }
    }
    return linkedHashMap
}
