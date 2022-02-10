package nl.kute.reflection

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. any interfaces, in order of hierarchy; super interfaces first
 * 2. any super classes, in order of hierarchy, up to but excluding [Any]; super classes first
 * 3. the class itself
 * @return class hierarchy as an unmodifiable ordered set
 */
fun Any.classHierarchy(): Set<KClass<*>> = Collections.unmodifiableSet(getClassHierarchy(this::class))

private fun getClassHierarchy(theClass: KClass<*>): Set<KClass<*>> {
    val kClasses: MutableSet<KClass<*>> = linkedSetOf()
    theClass.superclasses.filterNot { it == Any::class }.forEach {
        // add superclasses first by recursive call
        kClasses.addAll(getClassHierarchy(it))
    }
    // ad self; so additional subclass properties are appended at the end
    kClasses.add(theClass)
    // move all interfaces to top of collection
    return kClasses.sortedBy { !it.javaObjectType.isInterface }.toSet()
}
