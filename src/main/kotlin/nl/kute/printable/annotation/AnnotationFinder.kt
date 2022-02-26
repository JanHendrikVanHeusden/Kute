package nl.kute.printable.annotation

import nl.kute.reflection.isToString
import nl.kute.reflection.topDownTypeHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Find any annotation of type [A] on the receiver property of `this` class and any of its superclasses.
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
internal inline fun <reified A : Annotation> KProperty<*>.annotationsOfProperty(): List<A> {
    val annotationList = listOfNotNull(this.findAnnotation<A>())
    val declaringClass = this.javaGetter?.declaringClass ?: this.javaField?.declaringClass ?: return annotationList
    val map = declaringClass.kotlin.topDownTypeHierarchy().associateWith { kClass ->
        kClass.memberProperties
            .filter { prop -> prop.name == this.name && prop.visibility != PRIVATE }
            .map { it.findAnnotation<A>() }.firstOrNull()
    }.filterValues { annotation -> annotation != null }
    return listOfNotNull(this.findAnnotation())
}



/**
 * Find any annotation of type [A] on the `::toString` methods of `this` class and any of its superclasses.
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
internal inline fun <reified A : Annotation> Any.annotationsOfToString(): List<A> =
    this::class.topDownTypeHierarchy().reversed().asSequence()
        .map { kClass -> kClass.memberFunctions.filter { kFunction -> kFunction.isToString() } }.flatten()
        .map { it.findAnnotation<A>() }
        .filterNotNull()
        .toList()

/**
 * Find any annotation of type [A] on `this` class and any of its superclasses.
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
@Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
internal inline fun <reified A : Annotation> Any.annotationsOfClassByClass(): Map<KClass<*>, A> =
    this::class.topDownTypeHierarchy().reversed().asSequence()
        .map { kClass -> kClass to kClass.findAnnotation<A>() }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

/**
 * Find any annotation of type [A] on the `::toString` methods of `this` class and any of its superclasses.
 * The entries are ordered by key ([KClass]) from lowest to highest level, so from subclass to superclasses / interfaces.
 */
@Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
internal inline fun <reified A : Annotation> Any.annotationsOfToStringByClass(): Map<KClass<*>, A> =
    this::class.topDownTypeHierarchy().reversed().asSequence()
        .map { kClass ->
            kClass to kClass.memberFunctions.first { it.isToString() }.findAnnotation<A>()
        }
        .filter { it.second != null }
        .associate { it.first to it.second!! }

/**
 * Find the annotation of type [A] on `this` object or any of its supertypes;
 * the annotation at the lowest level (deepest subclass) where it is defined is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOf(): A? = this::class.annotationOfClass()

/**
 * Find the annotation of type [A] on the `this` class or any of its superclasses;
 * the annotation at the lowest level is returned, if present at all
 */
internal inline fun <reified A : Annotation> KClass<*>.annotationOfClass(): A? =
// (Kotlin's findAnnotation() method does not
    // find annotations up the class hierarchy, even as marked @Inherited :-()
    // So going via java class, which adheres to the @Inherited annotation
    this.java.getAnnotation(A::class.java)

/**
 * Find the annotation of type [A] on the `::toString` method of `this` class or any of its superclasses.
 * The annotation at the lowest level is returned, if present at all
 */
internal inline fun <reified A : Annotation> Any.annotationOfToString(): A? =
    annotationsOfToString<A>().firstOrNull()
