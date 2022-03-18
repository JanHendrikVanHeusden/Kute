package nl.kute.reflection.annotation

import nl.kute.reflection.reverseTypeHierarchy
import nl.kute.reflection.isToString
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

/**
 * Find any annotation of type [A] on the `::toString` methods of `this` class and any of its superclasses.
 * The entries are ordered by key ([KClass]) from lowest to highest level, so from subclass to superclasses / interfaces.
 */
@Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
internal inline fun <reified A : Annotation> Any.annotationsOfToString(): Map<KClass<*>, A> =
    this::class.reverseTypeHierarchy().asSequence()
        .map { kClass ->
            kClass to kClass.memberFunctions.first { it.isToString() }.findAnnotation<A>()
        }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

/**
 * Find the annotation of type [A] on the `::toString` method of `this` class or any of its superclasses.
 * The annotation at the lowest level is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOfToString(): A? =
    annotationsOfToString<A>().values.firstOrNull()
