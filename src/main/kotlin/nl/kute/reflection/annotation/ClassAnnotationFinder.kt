package nl.kute.reflection.annotation

import nl.kute.reflection.reverseTypeHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Find the annotation of type [A] on the `this` class or any of its superclasses;
 * the annotation at the lowest level is returned, if present at all.
 * @return The result respects the `@Inherited` meta annotation.
 *         So the requested annotation (if present at all) is returned only if either:
 *          * it is on the `this` class itself
 *          * it is on a superclass / super interface, and the annotation has the meta-annotation `@Inherited`
 */
internal inline fun <reified A : Annotation> KClass<*>.annotationOfClass(): A? =
/* (Kotlin's findAnnotation() method does not find annotations up the class hierarchy, even as marked @Inherited :-()
    So we are going via java class, which does adhere to the `@Inherited` annotation */
    this.java.getAnnotation(A::class.java)

/**
 * Find any annotation of type [A] on `this` class and any of its superclasses.
 * The annotations are returned regardless whether marked as `@Inherited`
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
internal inline fun <reified A : Annotation> KClass<*>.annotationsOfClass(includeInterfaces: Boolean = false): Map<KClass<*>, A> =
    this.reverseTypeHierarchy().asSequence()
        .map { kClass -> kClass to kClass.findAnnotation<A>() }
        .filter { includeInterfaces || !it.first.java.isInterface }
        .filter { it.second != null }
        .associate { it.first to it.second!! }
