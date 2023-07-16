@file:Suppress("unused")

package nl.kute.reflection

import nl.kute.log.logWithCaller
import nl.kute.util.asString
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.kotlinFunction

private const val fqn: String = "nl.kute.reflection.MemberUtil"

@Suppress("UNCHECKED_CAST")
@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.toStringMethod(): KFunction<String>? {
    var isOverridden = false
    val toStringInfo = classToStringMethodCache[this]
    if (toStringInfo != null && toStringInfo.second) {
        return toStringInfo.first as KFunction<String>
    }
    return toStringInfo?.first.ifNull {
        with(this.java) {
            this.methods.firstOrNull {
                it.name == "toString" && it.returnType == String::class.java && it.parameters.isEmpty()
            }.also {
                isOverridden = it != null && it.declaringClass != Object::class.java && it.declaringClass != Any::class.java
            }?.kotlinFunction
        }.also {
            if (it != null) {
                classToStringMethodCache[this] = it to isOverridden
                return if (isOverridden) it as KFunction<String>? else null
            }
        }
    } as KFunction<String>?
}

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.hasImplementedToString(): Boolean =
    classToStringMethodCache[this]?.second ?: (this.toStringMethod() != null)


// The 2nd part of the Pair indicates whether the toString() method was overridden
private val classToStringMethodCache = ConcurrentHashMap<KClass<*>, Pair<KFunction<*>?, Boolean>>()

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
