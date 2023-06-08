@file:Suppress("unused")

package nl.kute.reflection.annotationfinder

import nl.kute.reflection.isToString
import nl.kute.reflection.subSuperHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

// TODO: caching of annotations

/**
 * Find any annotation of type [A] on the `::toString` methods of `this` class and its super types.
 * The entries are ordered by key ([KClass]) from lowest to highest level, so from subclass to super class / super interface.
 */
internal inline fun <reified A : Annotation> Any.annotationsOfToStringSubSuperHierarchy(): Map<KClass<*>, A> =
    this::class.subSuperHierarchy().asSequence()
        .map { kClass ->
            kClass to kClass.memberFunctions.first { it.isToString() }.findAnnotation<A>()
        }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

/**
 * Find the annotation of type [A] on the `::toString` method of `this` class or its super types.
 * The annotation at the lowest level subclass level is returned, if present at all
 * * In case multiple interfaces at the topmost level are implemented, the result is stable (because the reflection
 *   api will always return the same interface), but undefined in that no explicit rule is defined on which interface will
 *   be ranked highest in case of same-level interfaces
 */
internal inline fun <reified A : Annotation> Any.annotationOfToStringSubSuperHierarchy(): A? =
    annotationsOfToStringSubSuperHierarchy<A>().values.firstOrNull()

/**
 * Find the annotation of type [A] on the `::toString` method of `this` class or its super types.
 * The annotation at the highest level supertype level is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOfToStringSuperSubHierarchy(): A? =
    annotationsOfToStringSubSuperHierarchy<A>().values.lastOrNull()
