package nl.kute.reflection.annotation

import nl.kute.reflection.reverseTypeHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Find any annotation of type [A] on `this` class and any of its superclasses.
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
@Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
internal inline fun <reified A : Annotation> Any.annotationsOfClass(): Map<KClass<*>, A> =
    this::class.reverseTypeHierarchy().asSequence()
        .map { kClass -> kClass to kClass.findAnnotation<A>() }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

/**
 * Find the annotation of type [A] on `this` object or any of its supertypes;
 * the annotation at the lowest level (deepest subclass) where it is defined is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOfClass(): A? = this::class.annotationOfClass()
