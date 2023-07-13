@file:Suppress("unused")

package nl.kute.reflection

import nl.kute.log.logWithCaller
import nl.kute.util.asString
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

private const val fqn: String = "nl.kute.reflection.MemberUtil"

/** @return is the `this` [KFunction] the `toString` method? */
@JvmSynthetic // avoid access from external Java code
internal fun KFunction<*>.isToString(): Boolean = this.name == "toString" && this.parameters.size == 1 // return value

@Suppress("UNNECESSARY_SAFE_CALL") // nullability may occur in tests due to mocks that force contrived exceptions
@JvmSynthetic // avoid access from external Java code
internal fun KProperty<*>.declaringClass(): KClass<*>? =
    try {
        this?.javaGetter?.declaringClass?.kotlin ?: this?.javaField?.declaringClass?.kotlin
    } catch (e: Exception) {
        logWithCaller(fqn, "${e.javaClass.simpleName} occurred when retrieving declaring class of property [${this?.name}]; exception: ${e.asString()}")
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
