package nl.kute.reflection.annotationfinder

import nl.kute.reflection.isToString
import nl.kute.reflection.reverseTypeHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

// TODO: caching of annotations

/**
 * Find any annotation of type [A] on the `::toString` methods of `this` class and its super types.
 * The entries are ordered by key ([KClass]) from lowest to highest level, so from subclass to super class / super interface.
 */
internal inline fun <reified A : Annotation> Any.annotationsOfToString(): Map<KClass<*>, A> =
    this::class.reverseTypeHierarchy().asSequence()
        .map { kClass ->
            kClass to kClass.memberFunctions.first { it.isToString() }.findAnnotation<A>()
        }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

/**
 * Find the annotation of type [A] on the `::toString` method of `this` class or its super types.
 * The annotation at the lowest level is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOfToString(): A? =
    annotationsOfToString<A>().values.firstOrNull()
