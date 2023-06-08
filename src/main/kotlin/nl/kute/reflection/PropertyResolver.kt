@file:Suppress("KDocUnresolvedReference")

package nl.kute.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

// TODO: caching of properties

/**
 * Find a member property by name
 * @return The property with the given name if `public`, `inherited` or `internal`;
 *         or `private` when declared in the `this` receiver class.
 *         Returns `null` when no such property is found.
 */
internal fun <T: Any> T.getMemberProperty(name: String): KProperty1<out T, *>? =
    this::class.getMemberProperty(name)

/**
 * Find a member property by name
 * @return The property with the given name if `public`, `inherited` or `internal`;
 *         or `private` when declared in the `this` receiver class.
 *         Returns `null` when no such property is found.
 */
internal fun <T: Any> KClass<T>.getMemberProperty(name: String): KProperty1<out T, *>? =
    this.memberProperties.firstOrNull { it.name == name }

/**
 * Find a property by name
 * @return The property with the given name, regardless of visibility.
 *         In case of name-shadowed private properties, the property of the most
 *         specific subclass is returned.
 *         Returns `null` when no such property is found.
 */
internal fun Any.getPropertyFromSubSuperHierarchy(name: String): KProperty1<out Any, *>? =
    this::class.getPropertyFromSubSuperHierarchy(name)

/**
 * Find a property by name
 * @return The property with the given name, regardless of visibility.
 *         In case of name-shadowed private properties, the property of the most
 *         specific subclass is returned.
 *         Returns `null` when no such property is found.
 */
internal fun KClass<*>.getPropertyFromSubSuperHierarchy(name: String): KProperty1<out Any, *>? =
    this.propertiesFromSubSuperHierarchy().firstOrNull { it.name == name }

/**
 * Get the properties of the [Any] receiver from its class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible (KProperty1.isAccessible])
 */
internal fun Any.propertiesFromSubSuperHierarchy(): List<KProperty1<out Any, *>> =
    this::class.propertiesFromSubSuperHierarchy()

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun KClass<*>.propertiesFromSubSuperHierarchy(): List<KProperty1<out Any, *>> =
    propertiesFromHierarchy(mostSuper = false)

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun KClass<*>.propertiesFromSuperSubHierarchy(): List<KProperty1<out Any, *>> =
    propertiesFromHierarchy(mostSuper = true)

private fun KClass<*>.propertiesFromHierarchy(mostSuper: Boolean): List<KProperty1<out Any, *>> {
    val classHierarchy = if (mostSuper) this.superSubHierarchy() else this.subSuperHierarchy()
    val linkedHashSet: LinkedHashSet<KProperty1<out Any, *>> = linkedSetOf()
    return linkedHashSet.also { theSet ->
        theSet.addAll(
            classHierarchy.asSequence()
                .map { kClass -> kClass.memberProperties }
                .flatten()
                // In case of overloads or name-shadowing, keep the property that is first in the hierarchy
                .distinctBy { prop -> prop.name }
                .toList()
        )
    }.toList()
}

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]),
 * mapped by the property's declaring class.
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun Any.propertyMapBySubSuperHierarchy(): Map<KClass<*>?, List<KProperty1<out Any, *>>> =
    this::class.propertyMapBySubSuperHierarchy()

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]),
 * mapped by the property's declaring class.
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty1.isAccessible])
 */
internal fun KClass<*>.propertyMapBySubSuperHierarchy(): Map<KClass<*>?, List<KProperty1<out Any, *>>> {
    val classHierarchy = this.subSuperHierarchy()
    val linkedHashMap: LinkedHashMap<KClass<*>?, MutableList<KProperty1<out Any, *>>> = LinkedHashMap()
    val pairList: List<Pair<KClass<*>, KProperty1<out Any, *>>> =
        classHierarchy.asSequence()
            .map { kClass -> kClass.memberProperties.map { Pair(kClass, it) } }
            .flatten()
            // In case of overloads or name-shadowing, keep the property of the most specific subclass
            .distinctBy { pair -> pair.second.name }
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
