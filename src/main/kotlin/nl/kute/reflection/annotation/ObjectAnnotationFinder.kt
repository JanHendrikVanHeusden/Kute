package nl.kute.reflection.annotation

import kotlin.reflect.KClass

/**
 * Find any annotation of type [A] on `this` class and its super types.
 * * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
 * * The annotations are returned regardless whether marked as `@Inherited`
 * @param includeInterfaces * if `true`, annotations of super interfaces are included in the result
 *                          * if `false`, annotations of super interfaces are not included in the result
 */
internal inline fun <reified A : Annotation> Any.annotationsOfClass(includeInterfaces: Boolean = true): Map<KClass<*>, A> =
    this::class.annotationsOfClass(includeInterfaces)

/**
 * Find the annotation of type [A] on `this` object or its super types.
 * the annotation at the lowest level (deepest subclass) where it is defined is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOfClass(): A? =
    this::class.annotationOfClass()
