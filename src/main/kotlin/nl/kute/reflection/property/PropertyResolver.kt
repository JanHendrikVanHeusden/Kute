@file:Suppress("KDocUnresolvedReference", "SameParameterValue")

package nl.kute.reflection.property

import nl.kute.reflection.error.SyntheticClassException
import nl.kute.reflection.util.declaringClass
import nl.kute.reflection.util.isPrivate
import nl.kute.reflection.util.subSuperHierarchy
import nl.kute.reflection.util.superSubHierarchy
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

internal typealias PropertyFilter = (KProperty<*>) -> Boolean

/**
 * Properties with return types that are (sub)types of the classes in [propertyReturnTypesToOmit]
 * are omitted by [propertiesFromSubSuperHierarchy]
 */
@JvmSynthetic // avoid access from external Java code
internal val propertyReturnTypesToOmit: MutableSet<KClass<out Any>> = ConcurrentHashMap.newKeySet()

/** Property filter based on [propertyReturnTypesToOmit] (should be negated when applied) */
private val propertyClassFilter: PropertyFilter =
    { property: KProperty<*> -> propertyReturnTypesToOmit
        .any { it.java.isAssignableFrom(property.returnType.jvmErasure.java) }
    }

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result
 *     * This implies that the properties returned are unique by name
 * * The resulting properties are returned regardless of visibility
 *   So not only `public` or `protected`, but also `internal` and `private` properties
 * * The properties may or may not be accessible ([KProperty.isAccessible])
 * * Properties that return (sub)types of any classes in [propertyReturnTypesToOmit] are omitted
 */
@JvmSynthetic // avoid access from external Java code
internal fun <T : Any> KClass<T>.propertiesFromSubSuperHierarchy(): List<KProperty<*>> =
    propertiesFromHierarchy(mostSuper = false)

/**
 * Get the properties from the class hierarchy (see [subSuperHierarchy]).
 * * In case of property overrides or name-shadowing, only the property from the most specific subclass
 *   is present in the result.
 *     * This implies that the properties are unique by name.
 * * The resulting properties are returned regardless of visibility.
 *   So not only `public` or `protected`, but also `internal` and `private` properties.
 * * The properties may or may not be accessible ([KProperty.isAccessible])
 */
@Suppress("SameParameterValue")
@JvmSynthetic // avoid access from external Java code
internal fun <T : Any> KClass<T>.propertiesFromHierarchy(mostSuper: Boolean): List<KProperty<*>> {
    val classHierarchy: List<KClass<in T>> = if (mostSuper) this.superSubHierarchy() else this.subSuperHierarchy()
    val linkedHashSet: LinkedHashSet<KProperty1<T, *>> = linkedSetOf()
    return linkedHashSet.also { theSet ->
        @Suppress("UNCHECKED_CAST")
        theSet.addAll(
            classHierarchy.asSequence()
                .map { kClass ->
                    try {
                        kClass.memberProperties
                    } catch (e: UnsupportedOperationException) {
                        // UnsupportedOperationException may happen in these cases (from Kotlin exception message):
                        //   > 1. This class is an internal synthetic class generated by the Kotlin compiler,
                        //   >    such as an anonymous class for a lambda, a SAM wrapper, a callable reference, etc.
                        //   > 2. Packages and file facades are not yet supported in Kotlin reflection.
                        // In these somewhat exotic cases, as Kotlin can't find properties, we don't bother
                        //   > The message also suggest this: `Please use Java reflection to inspect this class`
                        //   > But that's out of scope for this more exotic stuff, we just don't care that much

                        // to be handled within Kute. Must not propagate to outside-world!
                        throw SyntheticClassException(e.message, e)
                    } catch (error: kotlin.reflect.jvm.internal.KotlinReflectionInternalError) {
                        // Kotlin's reflection can't handle some classes, typically Java classes with
                        // anonymous inner classes, etc.
                        // Normally one should *never* handle `Error`s, but neither in Java's nor in Kotlin's reflection
                        // there seems any way to reliably and viably determine these classes otherwise.
                        // Many operations (e.g. `memberProperties` or `superTypes`) on these classes will throw
                        // kotlin.reflect.jvm.internal.KotlinReflectionInternalError
                        throw SyntheticClassException(error.message, error)
                    }
                }
                .flatten()
                // include private properties only if in the class itself, not if it's in a superclass
                // note that Kotlin regards protected stuff as private! (also with Java package level)
                .filter { !it.isPrivate() || it.declaringClass() == this }
                // In case of overloads or name-shadowing, keep the property that is first in the hierarchy
                .distinctBy { prop -> prop.name }
                .filterNot { propertyClassFilter(it) }
                .toList() as List<KProperty1<T, *>>
        )
    }.toList()
}
