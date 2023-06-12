@file:Suppress("unused")

package nl.kute.reflection

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.KVisibility.PUBLIC
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter

/** @return is the `this` [KFunction] the `toString` method? */
internal fun KFunction<*>.isToString(): Boolean = this.name == "toString" && this.parameters.size == 1 // return value

@Suppress("UNNECESSARY_SAFE_CALL") // nullability may occur in tests due to mocks that force contrived exceptions
internal fun KProperty<*>.declaringClass(): KClass<*>? =
    this?.javaGetter?.declaringClass?.kotlin ?: this?.javaField?.declaringClass?.kotlin

internal fun KProperty<*>.isPrivate() = this.visibility == PRIVATE
internal fun KProperty<*>.isProtected() = this.visibility == PROTECTED
internal fun KProperty<*>.isPublic() = this.visibility == PUBLIC

internal fun KFunction<*>.isPrivate() = this.visibility == PRIVATE
internal fun KFunction<*>.isProtected() = this.visibility == PROTECTED
internal fun KFunction<*>.isPublic() = this.visibility == PUBLIC
