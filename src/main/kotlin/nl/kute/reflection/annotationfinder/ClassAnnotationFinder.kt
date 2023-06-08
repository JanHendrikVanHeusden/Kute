package nl.kute.reflection.annotationfinder

import nl.kute.reflection.reverseTypeHierarchy
import nl.kute.reflection.topDownTypeHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

// TODO: caching of annotations

/**
 * Find any annotation of type [A] on `this` class and its super types.
 * * The annotations are returned regardless whether marked as `@Inherited`
 * * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
 * @param includeInterfaces * if `true`, annotations of super interfaces are included in the result
 *                          * if `false`, annotations of super interfaces are not included in the result
 */
internal inline fun <reified A : Annotation> KClass<*>.annotationsOfClassHierarchy(includeInterfaces: Boolean = true): Map<KClass<*>, A> =
    this.reverseTypeHierarchy().asSequence()
        .map { kClass -> kClass to kClass.findAnnotation<A>() }
        .filter { includeInterfaces || !it.first.java.isInterface }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

internal inline fun <reified A : Annotation> KClass<*>.annotationOfClassHierarchy(): A? =
    this.annotationsOfClassHierarchy<A>().firstNotNullOfOrNull { it.value }

/**
 * Find any annotation of type [A] on `this` class and its super types.
 * * The annotations are returned regardless whether marked as `@Inherited`
 * * The annotations are ordered from highest to lowest level, so from super class / super interface to subclass.
* * In case multiple interfaces at the topmost level are implemented, the result is stable (because the reflection
*   api will always return the same interface), but undefined in that no explicit rule is defined on which interface will
*   be ranked hightest in case of same-level interfaces
*/
internal inline fun <reified A : Annotation> KClass<*>.annotationsOfReverseClassHierarchy(): Map<KClass<*>, A> =
    this.topDownTypeHierarchy().asSequence()
        .map { kClass -> kClass to kClass.findAnnotation<A>() }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

internal inline fun <reified A : Annotation> KClass<*>.annotationOfReverseClassHierarchy(): A? =
    this.annotationsOfReverseClassHierarchy<A>().firstNotNullOfOrNull { it.value }

/**
 * Find the annotation of type [A] on the `this` class or its super types;
 * the annotation at the lowest level is returned, if present at all.
 * @return The result respects the `@Inherited` meta annotation.
 *         So the requested annotation (if present at all) is returned only if either:
 *          * it is on the `this` class itself
 *          * it is on a superclass / super interface, and the annotation has the meta-annotation `@Inherited`
 */

internal inline fun <reified A : Annotation> KClass<*>.annotationOfClassInheritance(): A? =
    /* (Kotlin's findAnnotation() method does not find annotations up the class hierarchy, even as marked @Inherited :-()
        So we are going via java class, which does adhere to the `@Inherited` annotation */
    this.java.getAnnotation(A::class.java)
