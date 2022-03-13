package nl.kute.reflection.annotation

import nl.kute.reflection.bottomUpTypeHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/**
 * Find any annotation of type [A] on the receiver property of `this` class and any of its superclasses.
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
internal inline fun <reified A : Annotation> KProperty<*>.annotationsOfProperty(): Map<KClass<*>, A> {
    val declaringClass = this.javaGetter?.declaringClass ?: this.javaField?.declaringClass ?: return mapOf()
    @Suppress("UNCHECKED_CAST") // For cast of Map<KClass<*>, A?> to Map<KClass<*>, A>
    return declaringClass.kotlin.bottomUpTypeHierarchy().associateWith { kClass ->
        kClass.memberProperties
            .filter { prop -> prop.name == this.name && prop.visibility != PRIVATE }
            .map { it.findAnnotation<A>() }.firstOrNull()
    }.filterValues { annotation -> annotation != null } as Map<KClass<*>, A>
}

/**
 * Find annotation of type [A], if any, on the receiver property of `this` class and any of its superclasses.
 * The annotations are ordered from lowest to highest level, so from subclass to superclasses / interfaces.
 */
internal inline fun <reified A : Annotation> KProperty<*>.annotationOfProperty(): A? =
    this.annotationsOfProperty<A>().values.firstOrNull()

