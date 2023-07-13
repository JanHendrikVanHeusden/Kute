package nl.kute.reflection

import kotlin.reflect.KClass

private val packageNameRegex = Regex(""".+\.(.*)$""")
private fun String.simplifyClassName() = this.replace(packageNameRegex, "$1")

internal fun KClass<*>.simplifyClassName() =
    try {
        simpleName ?: toString().simplifyClassName()
    } catch (e: Exception) {
        toString()
    }

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. any interfaces, in order of hierarchy; super interfaces first
 * 2. any super types, in order of hierarchy, up to but excluding [Any]; super types first
 * 3. the class itself
 * @return class hierarchy as an unmodifiable list
 */
internal fun <T : Any> KClass<T>.superSubHierarchy(includeInterfaces: Boolean = true): List<KClass<in T>> =
    this.java.superSubHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the [Class]
 * @see KClass.subSuperHierarchy
 */
internal fun <T : Any> Class<T>.superSubHierarchy(includeInterfaces: Boolean = true): List<Class<in T>> =
    getSuperSubHierarchy(this, includeInterfaces).sortedBy { !it.isInterface }

/**
 * Gets the class hierarchy of the `this` receiver, ordered like this:
 * 1. the class itself
 * 2. any super classes, in order of hierarchy, up to but excluding [Any]; super classes first
 * 3. any interfaces, in order of hierarchy; super interfaces first
 * @return class hierarchy as an unmodifiable list
 */
internal fun <T : Any> KClass<T>.subSuperHierarchy(includeInterfaces: Boolean = true): List<KClass<in T>> =
    this.java.subSuperHierarchy(includeInterfaces).map { it.kotlin }

/**
 * Gets the class hierarchy of the [Class]
 * @see KClass.subSuperHierarchy
 */
internal fun <T : Any> Class<T>.subSuperHierarchy(includeInterfaces: Boolean = true): List<Class<in T>> =
    this.superSubHierarchy(includeInterfaces).reversed()

/**
 * Gets the class hierarchy of the object's [KClass]
 * @see KClass.subSuperHierarchy
 */
@Suppress("UNCHECKED_CAST")
internal fun <T : Any> T.subSuperHierarchy(includeInterfaces: Boolean = true): List<KClass<in T>> =
    (this::class.java.subSuperHierarchy(includeInterfaces) as List<Class<in T>>).map { it.kotlin }

// Kotlin's method `KClass.supertypes` returns classes as well as interfaces.
// That's quite inconvenient for us: we want to keep these separate.
// Going  via the Java Class makes it easier to keep this separate
private fun <T : Any> getSuperSubHierarchy(theClass: Class<T>, includeInterfaces: Boolean = true): Set<Class<in T>> {
    if (theClass == Any::class.java || theClass == Object::class.java) {
        return emptySet()
    }
    if (theClass.isInterface) {
        return if (includeInterfaces) getSuperSubInterfaceHierarchy(theClass) else emptySet()
    }

    val classHierarchy: MutableSet<Class<in T>> = linkedSetOf()
    classHierarchy.addAll(getSuperSubHierarchy(theClass.superclass, includeInterfaces))
    classHierarchy.add(theClass)
    if (!includeInterfaces) {
        return classHierarchy
    }
    val interfaceHierarchy: MutableSet<Class<in T>> = linkedSetOf()
    theClass.interfaces.forEach {
        @Suppress("UNCHECKED_CAST")
        interfaceHierarchy.addAll(getSuperSubInterfaceHierarchy(it) as Set<Class<in T>>)
    }
    return classHierarchy + interfaceHierarchy
}

private fun <T : Any> getSuperSubInterfaceHierarchy(theInterface: Class<T>): Set<Class<in T>> {
    if (!theInterface.isInterface) {
        return emptySet()
    }
    val hierarchy: MutableSet<Class<in T>> = linkedSetOf()
    theInterface.interfaces.forEach {
        @Suppress("UNCHECKED_CAST")
        hierarchy.addAll(getSuperSubInterfaceHierarchy(it) as Set<Class<in T>>)
    }
    hierarchy.add(theInterface)
    return hierarchy
}
