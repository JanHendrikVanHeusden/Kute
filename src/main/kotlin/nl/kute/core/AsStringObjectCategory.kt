package nl.kute.core

import nl.kute.config.stringJoinMaxCount
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.getAsStringClassOption
import nl.kute.core.annotation.option.objectIdentity
import nl.kute.core.property.lambdaSignatureString
import nl.kute.reflection.hasImplementedToString
import nl.kute.reflection.simplifyClassName
import nl.kute.util.identityHashHex
import nl.kute.util.throwableAsString
import java.time.temporal.Temporal
import java.util.Calendar
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
internal enum class AsStringObjectCategory(val guardStack: Boolean, val handler: AsStringHandler? = null) {
    /**
     * Base stuff like [Boolean], [Number], [String], [Date], [Temporal], [Char], these have
     * sensible [toString] implementations that we need not (and should not) override
     */
    BASE(false, { it.toString() }),

    /** Override: collections [toString] is vulnerable for stack overflow (in case of recursive data) */
    COLLECTION(true, { (it as Collection<*>).collectionAsString() }),

    /** Override: [Array.contentDeepToString] is vulnerable for stack overflow (in case of recursive data) */
    ARRAY(true, { (it as Array<*>).arrayAsString() }),

    /** Override: arrays of primitives (e.g. [IntArray], [BooleanArray], [CharArray] lack proper [toString] implementation */
    PRIMITIVE_ARRAY(false, { it.primitiveArrayAsString() }),

    /** Override: [Array.contentDeepToString] is vulnerable for stack overflow (in case of recursive data) */
    MAP(true, { (it as Map<*, *>).mapAsString() }),

    /** Override: default [Throwable.toString] is way too verbose */
    THROWABLE(true, { (it as Throwable).throwableAsString() }),

    /** Override: [Annotation.toString] strips package name off */
    ANNOTATION(true, { (it as Annotation).annotationAsString() }),

    /** Override: String representation needs some tidying by [lambdaPropertyAsString] */
    LAMBDA_PROPERTY(false, { it.lambdaPropertyAsString() }),

    /**
     * Override for Java & Kotlin internals: provide sensible alternative for classes
     * without [toString] implementation; add identity if required
     */
    SYSTEM(false, { it.systemClassObjAsString() }),

    /** Custom handling */
    CUSTOM(guardStack = true);

    @JvmSynthetic // avoid access from external Java code
    internal fun hasHandler(): Boolean = handler != null

    companion object CategoryResolver {
        @JvmSynthetic // avoid access from external Java code
        internal fun resolveObjectCategory(obj: Any): AsStringObjectCategory {
            return when {
                obj.isBaseType() -> BASE
                obj is Array<*> -> ARRAY
                obj is Collection<*> -> COLLECTION
                obj.isPrimitiveArray() -> PRIMITIVE_ARRAY
                obj is Map<*, *> -> MAP
                obj is Annotation -> ANNOTATION
                obj is Throwable -> THROWABLE
                obj.isLambdaProperty() -> LAMBDA_PROPERTY
                obj::class.isSystemClass() -> SYSTEM
                else -> CUSTOM
            }
        }
    }
}

private fun Any.isBaseType() =
    this is Boolean
            || this is Number
            || this is CharSequence
            || this is Char
            || this is Temporal
            || this is Date
            || this is Calendar
            || this is UByte
            || this is UShort
            || this is UInt
            || this is ULong

private fun Any.isPrimitiveArray(): Boolean
// It's a pity the kotlin designers didn't create an interface or superclass for these arrays of primitives...
// We must simply list them all... NB:
//  * Java types boolean[], byte[] etc. map to kotlin's BooleanArray, ByteArray, etc.
//  * UByteArray, UShortArray, UIntArray, ULongArray are collections, these need no special handling
        = this is BooleanArray
        || this is ByteArray
        || this is CharArray
        || this is ShortArray
        || this is IntArray
        || this is LongArray
        || this is FloatArray
        || this is DoubleArray

private fun Any.primitiveArrayIterator(): Iterator<*> =
    when (this) {
        is BooleanArray -> this.iterator()
        is CharArray -> this.iterator()
        is ByteArray -> this.iterator()
        is ShortArray -> this.iterator()
        is IntArray -> this.iterator()
        is LongArray -> this.iterator()
        is FloatArray -> this.iterator()
        is DoubleArray -> this.iterator()
        else -> throw IllegalArgumentException("Should be called with arrays of primitives only")
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
    if (this::class.java.isPrimitive)
        this.toString()
    else if (this::class.hasImplementedToString())
        this.toString()
    else
        systemClassIdentity()

@JvmSynthetic // avoid access from external Java code
internal fun Annotation.annotationAsString(): String {
    // default toString() of annotations start with @ + package name; let's strip that off
    return toString().let {
        // We can't check if the annotation's toString() is actually overridden
        // (calling toStringImplementingMethod() here might throw UnsupportedOperationException)
        if (it.startsWith("@") && it.contains('.')) it.drop(1).simplifyClassName()
        else it
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun Collection<*>.collectionAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(
        prefix = "${collectionIdentity(includeIdentity)}[",
        separator = ", ",
        postfix = "]",
        limit = stringJoinMaxCount
    ) { it.asString() }
}

internal fun Any.primitiveArrayAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return this.primitiveArrayIterator().asSequence()
        .joinToString(
            prefix = "${collectionIdentity(includeIdentity)}[",
            separator = ", ",
            postfix = "]",
            limit = stringJoinMaxCount
        )
}

@JvmSynthetic // avoid access from external Java code
internal fun Map<*, *>.mapAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return this.entries.joinToString(
        prefix = "${collectionIdentity(includeIdentity)}{",
        separator = ", ",
        postfix = "}",
        limit = stringJoinMaxCount
    ) { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal fun Array<*>.arrayAsString(): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(
        prefix = "${collectionIdentity(includeIdentity)}[",
        separator = ", ",
        postfix = "]",
        limit = stringJoinMaxCount
    ) { it.asString() }
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

private fun Any.isLambdaProperty(): Boolean =
    // Check for lambda is not really the best check ever... but seems the best we have...
    // It only finds lambda that is declared as an *instance property*.
    //
    // Lambda declared as a *local variable* (e.g., in a method) can not be detected this way,
    // they cause downstream SyntheticClassException which then needs to be handled
    this::class.java.let {
        it.isSynthetic && it.toString().contains("\$\$Lambda\$")
    }

// the replacement removes a not very useful lengthy octal number, so
private val javaLambdaOctalRegex = Regex("/0x[0-9a-f]+@.+$")

@JvmSynthetic // avoid access from external Java code
internal fun Any.lambdaPropertyAsString(): String {
    // Lambda properties implicitly (or sometimes explicitly) implement a single functional interface.
    // This provides us some more useful information, so let's add it to the output.
    // NB: Using java reflection; we can't use Kotlin reflection here, that will cause KotlinReflectionInternalError
    val typeSuffix = this::class.java.interfaces.firstOrNull()?.let {
        "=${it.toGenericString().simplifyClassName()}()" } ?: ""

    // the replacement removes a non-informative lengthy octal number, so
    //    `JavaClassWithLambda$$Lambda$366/0x0000000800291440@27a0a5a2`
    // -> `JavaClassWithLambda$$Lambda$366
    // (also removed identityHash, this is added again at the end)
    return this.toString().simplifyClassName()
        .replace(javaLambdaOctalRegex, "") + "$typeSuffix @${this.identityHashHex}"
}

private val systemClassPackagePrefixes = listOf("java.", "kotlin.")

private fun KClass<*>.isSystemClass() =
    this.java.packageName == "kotlin"
            || systemClassPackagePrefixes.any { this.java.packageName.startsWith(it) }

internal fun KProperty<*>.isLambdaProperty(stringValue: String?): Boolean {
    return stringValue != null
            && returnType.javaType.toString().startsWith("kotlin.jvm.functions.Function")
            && stringValue.matches(lambdaToStringRegex)
}
