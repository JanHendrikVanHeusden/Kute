@file:Suppress("unused")

package nl.kute.reflection.annotationfinder

import kotlin.reflect.KClass

// TODO: caching of annotations

/**
 * Find any annotations of type [A] on `this` class and its super types.
 * * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
 * * The annotations are returned regardless whether marked as `@Inherited`
 * @param includeInterfaces * if `true`, annotations of super interfaces are included in the result
 *                          * if `false`, annotations of super interfaces are not included in the result
 */
internal inline fun <reified A : Annotation> Any.annotationsOfClassHierarchy(includeInterfaces: Boolean = true): Map<KClass<*>, A> =
    this::class.annotationsOfSubSuperClassHierarchy(includeInterfaces)

/**
 * Find any annotation of type [A] on `this` class and its super types.
 * * The first annotation is returned by order from lowest to highest level, so from subclass to super class / super interface.
 * * The annotations are returned regardless whether marked as `@Inherited`
 * @param includeInterfaces * if `true`, annotations of super interfaces are included in the result
 *                          * if `false`, annotations of super interfaces are not included in the result
 */
internal inline fun <reified A : Annotation> Any.annotationOfClassHierarchy(includeInterfaces: Boolean = true): A? =
    this::class.annotationsOfSubSuperClassHierarchy<A>(includeInterfaces).entries.firstOrNull()?.value

/**
 * Find any annotations of type [A] on `this` class and its super types.
 * * The annotations are ordered from higest to lowest level, so from super class / super interface to subclass.
 * * The annotations are returned regardless whether marked as `@Inherited`
 */
internal inline fun <reified A : Annotation> Any.annotationsOfReverseClassHierarchy(): Map<KClass<*>, A> =
    this::class.annotationsOfSuperSubHierarchy<A>()

/**
 * Find any annotation of type [A] on `this` class and its super types.
 * * The first annotations found is returned, by order from higest to lowest level, so from super class / super interface to subclass.
 * * The annotation is returned regardless whether marked as `@Inherited`
 * * In case multiple interfaces at the topmost level are implemented, the result is stable (because the reflection
 *   api will always return the same interface), but undefined in that no explicit rule is defined on which interface will
 *   be ranked highest in case of same-level interfaces
 */
internal inline fun <reified A : Annotation> Any.annotationOfReverseClassHierarchy(): A? =
    this::class.annotationsOfSuperSubHierarchy<A>().entries.firstOrNull()?.value


/**
 * Find the annotation of type [A] on `this` object or its super types, insofar the annotation is marked as `@Inherited`.
 * The annotation at the lowest level (deepest subclass) where it is defined is returned, if present at all
 * @return The result respects the `@Inherited` meta annotation.
 *         So the requested annotation (if present at all) is returned only if either:
 *          * it is on the `this` class itself
 *          * it is on a superclass / super interface, and the annotation has the meta-annotation `@Inherited`
 */
internal inline fun <reified A : Annotation> Any.annotationOfClassInheritance(): A? =
    this::class.annotationOfClassInheritance()
