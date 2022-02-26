package nl.kute.reflection

import kotlin.reflect.KClass

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. any interfaces, in order of hierarchy; super interfaces first
 * 2. any super classes, in order of hierarchy, up to but excluding [Any]; super classes first
 * 3. the class itself
 * @return class hierarchy as an unmodifiable list
 */
fun KClass<*>.topDownTypeHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this.java.topDownTypeHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the [Class]
 * @see KClass.topDownTypeHierarchy
 */
fun Class<*>.topDownTypeHierarchy(includeInterfaces: Boolean = true): List<Class<*>> =
    getTopDownTypeHierarchy(this, includeInterfaces).sortedBy { !it.isInterface }

/**
 * Gets the class hierarchy of the object's [KClass]
 * @see KClass.topDownTypeHierarchy
 */
fun Any.topDownTypeHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this::class.java.topDownTypeHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. the class itself
 * 2. any super classes, in order of hierarchy, up to but excluding [Any]; super classes first
 * 3. any interfaces, in order of hierarchy; super interfaces first
 * @return class hierarchy as an unmodifiable list
 */
fun KClass<*>.bottomUpTypeHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this.java.bottomUpTypeHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the [Class]
 * @see KClass.bottomUpTypeHierarchy
 */
fun Class<*>.bottomUpTypeHierarchy(includeInterfaces: Boolean = true): List<Class<*>> =
    this.topDownTypeHierarchy(includeInterfaces).reversed()

/**
 * Gets the class hierarchy of the object's [KClass]
 * @see KClass.bottomUpTypeHierarchy
 */
fun Any.bottomUpTypeHierarchy(includeInterfaces: Boolean = true): List<KClass<*>> =
    this::class.java.bottomUpTypeHierarchy(includeInterfaces).map { it.kotlin }

// Kotlin's method `KClass.supertypes` returns classes as well as interfaces.
// That's quite inconvenient for us: we want to keep these separate.
// Going  via the Java Class makes it easier to keep this separate
private fun getTopDownTypeHierarchy(theClass: Class<*>, includeInterfaces: Boolean = true): Set<Class<*>> {
    if (theClass == Any::class.java || theClass == Object::class.java) {
        return emptySet()
    }
    if (theClass.isInterface) {
        return if (includeInterfaces) getTopDownInterfaceHierarchy(theClass) else emptySet()
    }

    val classHierarchy: MutableSet<Class<*>> = linkedSetOf()
    classHierarchy.addAll(getTopDownTypeHierarchy(theClass.superclass, includeInterfaces))
    classHierarchy.add(theClass)
    if (!includeInterfaces) {
        return classHierarchy
    }
    val interfaceHierarchy: MutableSet<Class<*>> = linkedSetOf()
    theClass.interfaces.forEach { interfaceHierarchy.addAll(getTopDownInterfaceHierarchy(it)) }
    return classHierarchy + interfaceHierarchy
}

private fun getTopDownInterfaceHierarchy(theInterface: Class<*>): Set<Class<*>> {
    if (!theInterface.isInterface) {
        return emptySet()
    }
    val hierarchy: MutableSet<Class<*>> = linkedSetOf()
    theInterface.interfaces.forEach { hierarchy.addAll(getTopDownInterfaceHierarchy(it)) }
    hierarchy.add(theInterface)
    return hierarchy
}
