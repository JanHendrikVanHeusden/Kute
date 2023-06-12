package nl.kute.reflection.annotationfinder

import nl.kute.reflection.subSuperHierarchy
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
internal inline fun <reified A : Annotation> KClass<*>.annotationsOfSubSuperHierarchy(includeInterfaces: Boolean = true): Map<KClass<*>, A> =
    this.subSuperHierarchy().asSequence()
        .map { kClass -> kClass to kClass.findAnnotation<A>() }
        .filter { includeInterfaces || !it.first.java.isInterface }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

internal inline fun <reified A : Annotation> KClass<*>.annotationOfSubSuperHierarchy(): A? =
    this.annotationsOfSubSuperHierarchy<A>().firstNotNullOfOrNull { it.value }
