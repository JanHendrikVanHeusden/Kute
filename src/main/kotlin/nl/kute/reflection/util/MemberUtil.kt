@file:Suppress("unused")
@file:JvmName("MemberUtil")
package nl.kute.reflection.util

import nl.kute.log.logWithCaller
import nl.kute.retain.MapCache
import nl.kute.util.throwableAsString
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.kotlinFunction

private val fqn: String by lazy {
    "$packageName.MemberUtil"
}

/**
 * Determines the [toString]-method, if overridden. [toString] of [Any] or [Object] are ignored.
 * @return The class's [toString] method, if overridden.
 * Otherwise `null`, if not overridden, or if the receiver class is [Any] or [Object]
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.toStringImplementingMethod(): KFunction<String>? {
    try {
        val toStringInfo = classToStringMethodCache[this]
        toStringInfo?.let {
            return if (it.second) it.first as KFunction<String> else null
        }
        val javaToStringMethod = this.java.methods.firstOrNull {
            it.name == "toString" && it.returnType == String::class.java && it.parameters.isEmpty()
        }
        val isOverridden = javaToStringMethod != null
                    && javaToStringMethod.declaringClass != Any::class.java
                    && javaToStringMethod.declaringClass != Object::class.java
        val kotlinToStringMethod: KFunction<String>? =
            javaToStringMethod?.kotlinFunction as KFunction<String>?
        classToStringMethodCache[this] = kotlinToStringMethod to isOverridden

        return if (isOverridden) kotlinToStringMethod else null

    } catch (e: UnsupportedOperationException) {
        // Might happen when called with synthetic class.
        // Functionally, this methods should never be called with such classes though.
        // So if it happens, something is going wrong in the calling code & it should be fixed
        logWithCaller(fqn, e.throwableAsString(50))
        return null
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.hasImplementedToString(): Boolean =
    this.java.isPrimitive
            || classToStringMethodCache[this]?.second ?: (this.toStringImplementingMethod() != null)

/** The 2nd part of the [Pair] indicates whether the [toString] method was overridden for the [KClass] */
@JvmSynthetic // avoid access from external Java code
internal val classToStringMethodCache = MapCache<KClass<*>, Pair<KFunction<*>?, Boolean>>()

@Suppress("UNNECESSARY_SAFE_CALL") // nullability may occur in tests due to mocks that force contrived exceptions
@JvmSynthetic // avoid access from external Java code
internal fun KProperty<*>.declaringClass(): KClass<*>? =
    try {
        this?.javaGetter?.declaringClass?.kotlin ?: this?.javaField?.declaringClass?.kotlin
    } catch (e: InterruptedException) {
        throw e
    } catch (e: Exception) {
        logWithCaller(fqn, "${e.javaClass.name.simplifyClassName()} occurred when retrieving declaring class of property [${this?.name}]; exception: ${e.throwableAsString()}")
        null
    }

@JvmSynthetic // avoid access from external Java code
internal fun KProperty<*>.isPrivate() = this.visibility == PRIVATE
@JvmSynthetic // avoid access from external Java code
internal fun KProperty<*>.isProtected() = this.visibility == PROTECTED
@JvmSynthetic // avoid access from external Java code
internal fun KProperty<*>.isPublic() = this.visibility == PUBLIC

@JvmSynthetic // avoid access from external Java code
internal fun KFunction<*>.isPrivate() = this.visibility == PRIVATE
@JvmSynthetic // avoid access from external Java code
internal fun KFunction<*>.isProtected() = this.visibility == PROTECTED
@JvmSynthetic // avoid access from external Java code
internal fun KFunction<*>.isPublic() = this.visibility == PUBLIC

private val packageName: String by lazy {
    // clumsy, but more maintenance friendly than hard coded
    class Dummy
    Dummy::class.java.packageName
}