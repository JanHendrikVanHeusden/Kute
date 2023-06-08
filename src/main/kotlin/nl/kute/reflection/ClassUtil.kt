package nl.kute.reflection

import kotlin.reflect.KClass

// TODO: caching of class hierarchies

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. any interfaces, in order of hierarchy; super interfaces first
 * 2. any super types, in order of hierarchy, up to but excluding [Any]; super types first
 * 3. the class itself
 * @return class hierarchy as an unmodifiable list
 */
internal fun KClass<*>.superSubHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this.java.superSubHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the [Class]
 * @see KClass.subSuperHierarchy
 */
internal fun Class<*>.superSubHierarchy(includeInterfaces: Boolean = true): List<Class<*>> =
    getSuperSubHierarchy(this, includeInterfaces).sortedBy { !it.isInterface }

///**
// * Gets the class hierarchy of the object's [KClass]
// * @see KClass.reverseHierarchy
// */
//internal fun Any.topDownTypeHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
//    this::class.java.reverseTypeHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. the class itself
 * 2. any super classes, in order of hierarchy, up to but excluding [Any]; super classes first
 * 3. any interfaces, in order of hierarchy; super interfaces first
 * @return class hierarchy as an unmodifiable list
 */
internal fun KClass<*>.subSuperHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this.java.subSuperHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the [Class]
 * @see KClass.subSuperHierarchy
 */
internal fun Class<*>.subSuperHierarchy(includeInterfaces: Boolean = true): List<Class<*>> =
    this.superSubHierarchy(includeInterfaces).reversed()

/**
 * Gets the class hierarchy of the object's [KClass]
 * @see KClass.subSuperHierarchy
 */
internal fun Any.subSuperHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this::class.java.subSuperHierarchy(includeInterfaces).map { it.kotlin }

// Kotlin's method `KClass.supertypes` returns classes as well as interfaces.
// That's quite inconvenient for us: we want to keep these separate.
// Going  via the Java Class makes it easier to keep this separate
private fun getSuperSubHierarchy(theClass: Class<*>, includeInterfaces: Boolean = true): Set<Class<*>> {
    if (theClass == Any::class.java || theClass == Object::class.java) {
        return emptySet()
    }
    if (theClass.isInterface) {
        return if (includeInterfaces) getSuperSubInterfaceHierarchy(theClass) else emptySet()
    }

    val classHierarchy: MutableSet<Class<*>> = linkedSetOf()
    classHierarchy.addAll(getSuperSubHierarchy(theClass.superclass, includeInterfaces))
    classHierarchy.add(theClass)
    if (!includeInterfaces) {
        return classHierarchy
    }
    val interfaceHierarchy: MutableSet<Class<*>> = linkedSetOf()
    theClass.interfaces.forEach { interfaceHierarchy.addAll(getSuperSubInterfaceHierarchy(it)) }
    return classHierarchy + interfaceHierarchy
}

private fun getSuperSubInterfaceHierarchy(theInterface: Class<*>): Set<Class<*>> {
    if (!theInterface.isInterface) {
        return emptySet()
    }
    val hierarchy: MutableSet<Class<*>> = linkedSetOf()
    theInterface.interfaces.forEach { hierarchy.addAll(getSuperSubInterfaceHierarchy(it)) }
    hierarchy.add(theInterface)
    return hierarchy
}
