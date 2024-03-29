@file:Suppress("RedundantNotNullExtensionReceiverOfInline")

package nl.kute.reflection.annotationfinder

import nl.kute.reflection.util.subSuperHierarchy
import nl.kute.reflection.util.toStringImplementingMethod
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Find any annotation of type [A] on the `::toString` methods of `this` class and its super types.
 * The entries are ordered by key ([KClass]) from lowest to highest level, so from subclass to super class / super interface.
 */
@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KClass<*>.annotationsOfToStringSubSuperHierarchy(): Map<KClass<*>, A> =
    subSuperHierarchy().asSequence()
        .map { kClass ->
            kClass to kClass.toStringImplementingMethod()?.findAnnotation<A>()
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
@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KClass<*>.annotationOfToStringSubSuperHierarchy(): A? =
    annotationsOfToStringSubSuperHierarchy<A>().values.firstOrNull()

