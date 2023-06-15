@file:Suppress("KDocUnresolvedReference", "SameParameterValue")

package nl.kute.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

// TODO: caching of properties

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty.isAccessible])
 */
internal fun <T : Any> KClass<T>.propertiesFromSubSuperHierarchy(): List<KProperty<*>> =
    propertiesFromHierarchy(mostSuper = false)

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty.isAccessible])
 */
@Suppress("SameParameterValue")
private fun <T : Any> KClass<T>.propertiesFromHierarchy(mostSuper: Boolean): List<KProperty<*>> {
    val classHierarchy: List<KClass<in T>> = if (mostSuper) this.superSubHierarchy() else this.subSuperHierarchy()
    val linkedHashSet: LinkedHashSet<KProperty1<T, *>> = linkedSetOf()
    return linkedHashSet.also { theSet ->
        @Suppress("UNCHECKED_CAST")
        theSet.addAll(
            classHierarchy.asSequence()
                .map { kClass -> kClass.memberProperties }
                .flatten()
                // include private properties only if in the class itself, not if it's in a superclass
                // note that Kotlin regards protected and properties as private! (also with Java package level)
                .filter { !it.isPrivate() || it.declaringClass() == this }
                // In case of overloads or name-shadowing, keep the property that is first in the hierarchy
                .distinctBy { prop -> prop.name }
                .toList() as List<KProperty1<T, *>>
        )
    }.toList()
}
