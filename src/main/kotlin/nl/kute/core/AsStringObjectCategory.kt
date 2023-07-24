package nl.kute.core

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.getAsStringClassOption
import nl.kute.core.annotation.option.objectIdentity
import nl.kute.core.property.lambdaSignatureString
import nl.kute.reflection.hasImplementedToString
import nl.kute.reflection.simplifyClassName
import nl.kute.util.asString
import nl.kute.util.identityHashHex
import java.time.temporal.Temporal
import java.util.Date
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaType

/**
 * Enum to provide categorization of objects.
 * @param handler The handler is used for basic and system types, like [Collection]s, [Array]s,
 * [Throwable]s, [Annotation]s, [Number], [String], [Date], [Temporal], [Char] etc.
 *  * When a [handler] is provided, it should be invoked by the caller
 *  * When no [handler] is provided, the caller itself should provide the implementation
 */
internal enum class AsStringObjectCategory(val handler: AsStringHandler? = null, val guardStack: Boolean = false) {
// TODO: tests
    /**
     * Internal stuff like [Number], [String], [Date], [Temporal], [Char],
     * these have sensible [toString] implementations that we shouldn't override
     */
    BASE( { it.toString() }),

    /** Override: collections [toString] is vulnerable for stack overflow (in case of recursive data) */
    COLLECTION( { (it as Collection<*>).collectionAsString() }, true),

    /** Override: [Array.contentDeepToString] is vulnerable for stack overflow (in case of recursive data) */
    ARRAY( { (it as Array<*>).arrayAsString() }, true),

    /** Override: [Array.contentDeepToString] is vulnerable for stack overflow (in case of recursive data) */
    MAP( { (it as Map<*, *>).mapAsString() }, true),

    /** Override: [Throwable.toString] is way too verbose */
    THROWABLE( { (it as Throwable).asString() }),

    /** Override: [Annotation.toString] strip package name */
    ANNOTATION( { (it as Annotation).annotationAsString() }),

    /**
     * Override: *Java* lambda's cause `kotlin.reflect.jvm.internal.KotlinReflectionInternalError`
     * if not handled specifically
     * > *Kotlin* lambdas are not viably recognisable by reflection or other means...
     * > so this category is for Java lambdas only.
     */
    JAVA_LAMBDA( { it.javaSyntheticClassObjectAsString() }),

    /**
     * Override for Java & Kotlin internals: provide sensible alternative for classes
     * without [toString] implementation; add identity if required
     */
    SYSTEM( { it.systemClassObjAsString() }),

    /** Custom handling */
    CUSTOM(guardStack = true);

    @JvmSynthetic // avoid access from external Java code
    internal fun hasHandler(): Boolean = handler != null

    companion object CategoryResolver {
        @JvmSynthetic // avoid access from external Java code
        internal fun resolveObjectCategory(obj: Any): AsStringObjectCategory {
            return when {
                (obj is Number || obj is CharSequence || obj is Char || obj is Temporal) -> BASE
                obj is Array<*> -> ARRAY
                obj is Collection<*> -> COLLECTION
                obj is Map<*, *> -> MAP
                obj.isJavaDate() -> BASE
                obj is Annotation -> ANNOTATION
                obj is Throwable -> THROWABLE
                // not really best check ever... but seems the best we have...
                // NB: Kotlin lambdas are not viably recognisable by reflection or other means...
                //     so this category is for Java lambdas only.
                obj::class.java.let {
                    it.isSynthetic && it.toString().contains("\$\$Lambda\$")
                } -> JAVA_LAMBDA
                obj::class.isSystemClass() -> SYSTEM
                else -> CUSTOM
            }
        }
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun Any.objectIdentity() = this.objectIdentity(getAsStringClassOption())

@JvmSynthetic // avoid access from external Java code
internal fun Any.collectionIdentity(includeIdentity: Boolean = AsStringClassOption.defaultOption.includeIdentityHash) =
    if (includeIdentity) "${this::class.simplifyClassName()}@${this.identityHashHex}"
    else ""

@JvmSynthetic // avoid access from external Java code
internal fun Any.systemClassIdentity(includeIdentity: Boolean = AsStringClassOption.defaultOption.includeIdentityHash) =
    if (includeIdentity) "${this::class.simplifyClassName()}@${this.identityHashHex}"
    else this::class.simplifyClassName()

/**
 * Construct a meaningful String representation for system class objects where [toString] is not overridden
 * (so Strings like `java.lang.Object@5340477f`).
 * If [AsStringClassOption.defaultOption] has [AsStringClassOption.includeIdentityHash], the hash
 * value is included as well, e.g. `Any@5340477f()`
 * @return
 *  * When [toString] is like `java.lang.Object@1234acef`, a meaningful String, e.g. `Any()`;
 *  * Otherwise, the default [toString] of the object.
 */
@JvmSynthetic // avoid access from external Java code
internal fun Any.systemClassObjAsString(): String =
    if (this::class.java.isPrimitive) this.toString()
    else if (this::class.hasImplementedToString()) this.toString() else "${systemClassIdentity()}()"

@JvmSynthetic // avoid access from external Java code
internal fun Annotation.annotationAsString(): String = toString().simplifyClassName()

@JvmSynthetic // avoid access from external Java code
internal fun Collection<*>.collectionAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(prefix = "${collectionIdentity(includeIdentity)}[", separator = ", ", postfix = "]") { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal fun Map<*, *>.mapAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return this.entries.joinToString(prefix = "${collectionIdentity(includeIdentity)}{", separator = ", ", postfix = "}") { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal fun Array<*>.arrayAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(prefix = "${collectionIdentity(includeIdentity)}[", separator = ", ", postfix = "]") { it.asString() }
}

private val lambdaToStringRegex: Regex = Regex("""^\(.*?\) ->.+$""")

@JvmSynthetic // avoid access from external Java code
internal fun Any.syntheticClassObjectAsString(): String {
    return this.toString().let {
        if (it.matches(lambdaToStringRegex))
            // Lambda's have a nice toString(), e.g. `(kotlin.Int) -> kotlin.String`,
            // let's use that, but strip package names
            it.lambdaSignatureString()
        else this.asStringFallBack()
    }
}

private val javaLambdaRegex = Regex("/[0-pa-fx]+@")

@JvmSynthetic // avoid access from external Java code
internal fun Any?.javaSyntheticClassObjectAsString(): String =
// the replacement removes a not very useful lengthy octal number, so
//    `JavaClassWithLambda$$Lambda$366/0x0000000800291440@27a0a5a2`
    // -> `JavaClassWithLambda$$Lambda$366@27a0a5a2`
    this.toString().simplifyClassName().replace(javaLambdaRegex, "@")

private val systemClassPackageNames = listOf("kotlin", "java.")
private fun KClass<*>.isSystemClass() =
    systemClassPackageNames.any { this.java.packageName.startsWith(it) }

private val datePackageNames = listOf("java.util", "java.sql")
private fun Any.isJavaDate() =
    (this is Date) && datePackageNames.any { this::class.java.packageName.startsWith(it) }

internal fun KProperty<*>.isLambdaProperty(stringValue: String?): Boolean {
    return stringValue != null
            && returnType.javaType.toString().startsWith("kotlin.jvm.functions.Function")
            && stringValue.matches(lambdaToStringRegex)
}
