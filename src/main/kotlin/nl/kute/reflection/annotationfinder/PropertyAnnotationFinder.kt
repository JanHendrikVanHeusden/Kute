package nl.kute.reflection.annotationfinder

import nl.kute.reflection.util.declaringClass
import nl.kute.reflection.util.isPrivate
import nl.kute.reflection.util.subSuperHierarchy
import nl.kute.reflection.util.superSubHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Find any annotation of type [A] on the receiver property of `this` class and its super types.
 * * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
 */
@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KProperty<*>.annotationByPropertySubSuperHierarchy(): Map<KClass<*>, A> {
    val declaringClass: Class<out Any> =
        this.javaGetter?.declaringClass ?: this.javaField?.declaringClass ?: return mapOf()
    // The contract of the `associateWith` method explicitly states that the order is preserved
    @Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
    return declaringClass.kotlin.subSuperHierarchy().associateWith { kClass ->
        kClass.memberProperties
            .filter { prop ->
                prop.name == this.name
                        // include private properties only if in the class itself, not if it's in a superclass
                        // note that Kotlin regards private properties with protected getters as private! (also with Java package level)
                        && (prop.declaringClass()!!.java == declaringClass || !prop.isPrivate())
            }
            .firstNotNullOfOrNull { it.findAnnotation<A>() }
    }.filterValues { annotation -> annotation != null } as Map<KClass<*>, A>
}

@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KProperty<*>.annotationSetByPropertySuperSubHierarchy(): Map<KClass<*>, List<A>?> {
    val declaringClass: Class<out Any> =
        this.javaGetter?.declaringClass ?: this.javaField?.declaringClass ?: return mapOf()
    // The contract of the `associateWith` method explicitly states that the order is preserved
    return declaringClass.kotlin.superSubHierarchy().associateWith { kClass ->
        kClass.memberProperties.asSequence()
            .filter { prop ->
                prop.name == this.name
                        // include private properties only if in the class itself, not if it's in a superclass
                        // note that Kotlin regards private properties with protected getters as private! (also with Java package level)
                        && (prop.declaringClass()!!.java == declaringClass || !prop.isPrivate())
            }.map { it.findAnnotations<A>() }.firstOrNull { it.isNotEmpty()}
    }.filterValues { it != null }
}

/**
 * Find annotation of type [A], if any, on the receiver property of `this` class or any of its super types.
 *
 * If found on multiple inheritance levels, the annotation of the lowest subclass level is returned.
 */
@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KProperty<*>.annotationOfPropertySubSuperHierarchy(): A? =
    this.annotationByPropertySubSuperHierarchy<A>().values
        .firstOrNull()

/**
 * Find annotation of type [A], if any, on the receiver property of `this` class or any of its super types.
 * * If found on multiple inheritance levels, the annotation of the highest superclass or super-interface is returned.
 * * In case multiple interfaces at the topmost level are implemented, the result is stable (because the reflection
 *   api will always return the same interface), but undefined in that no explicit rule is defined on which interface will
 *   be ranked highest in case of same-level interfaces
 */
@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KProperty<*>.annotationOfPropertySuperSubHierarchy(): A? =
    this.annotationByPropertySubSuperHierarchy<A>().values
        .lastOrNull()

@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> KProperty<*>.annotationSetOfPropertySuperSubHierarchy(): Set<A> =
    this.annotationSetByPropertySuperSubHierarchy<A>().values
        .filterNotNull()
        .flatten()
        .toSet()
