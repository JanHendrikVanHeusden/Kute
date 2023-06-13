package nl.kute.reflection.annotationfinder

import nl.kute.reflection.declaringClass
import nl.kute.reflection.isPrivate
import nl.kute.reflection.subSuperHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

// TODO: caching of annotations

/**
 * Find any annotation of type [A] on the receiver property of `this` class and its super types.
 * * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
 */
internal inline fun <reified A : Annotation> KProperty<*>.annotationsOfPropertySubSuperHierarchy(): Map<KClass<*>, A> {
    val declaringClass: Class<out Any> = this.javaGetter?.declaringClass ?: this.javaField?.declaringClass ?: return mapOf()
    // The contract of the `associateWith` method explicitly states that the order is preserved
    @Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
    return declaringClass.kotlin.subSuperHierarchy().associateWith { kClass ->
        kClass.memberProperties
            .filter { prop -> prop.name == this.name
                    // include private properties only if in the class itself, not if it's in a superclass
                    // note that Kotlin regards private properties with protected getters as private! (also with Java package level)
                    && (prop.declaringClass()!!.java == declaringClass || !prop.isPrivate()) }
            .firstNotNullOfOrNull { it.findAnnotation<A>() }
    }.filterValues { annotation -> annotation != null } as Map<KClass<*>, A>
}

/**
 * Find annotation of type [A], if any, on the receiver property of `this` class or any of its super types.
 *
 * If found on multiple inheritance levels, the annotation of the lowest subclass level is returned.
 */
internal inline fun <reified A : Annotation> KProperty<*>.annotationOfPropertySubSuperHierarchy(): A? =
    this.annotationsOfPropertySubSuperHierarchy<A>().values
        .firstOrNull()

/**
 * Find annotation of type [A], if any, on the receiver property of `this` class or any of its super types.
 * * If found on multiple inheritance levels, the annotation of the highest superclass or super-interface is returned.
 * * In case multiple interfaces at the topmost level are implemented, the result is stable (because the reflection
 *   api will always return the same interface), but undefined in that no explicit rule is defined on which interface will
 *   be ranked highest in case of same-level interfaces
 */
internal inline fun <reified A : Annotation> KProperty<*>.annotationOfPropertySuperSubHierarchy(): A? =
    this.annotationsOfPropertySubSuperHierarchy<A>().values
        .lastOrNull()
