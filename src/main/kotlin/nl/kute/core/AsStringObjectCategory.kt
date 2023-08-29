package nl.kute.core

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.core.annotation.option.asStringClassOption
import nl.kute.core.annotation.option.objectIdentity
import nl.kute.core.property.lambdaSignatureString
import nl.kute.reflection.hasImplementedToString
import nl.kute.reflection.simplifyClassName
import nl.kute.util.MapCache
import nl.kute.util.identityHashHex
import nl.kute.util.ifNull
import nl.kute.util.throwableAsString
import java.time.temporal.Temporal
import java.util.Date
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Enum to provide categorization of objects.
 * @param handler The handler is used for basic and system types, like [Collection]s, [Array]s,
 * [Throwable]s, [Annotation]s, [Number], [String], [Date], [Temporal], [Char] etc.
 *  * When a [handler] is provided, it should be invoked by the caller
 *  * When no [handler] is provided, the caller itself should provide the implementation
 *
 * ***Remark about [Iterable]:*** [Iterable]s are categorized as [SYSTEM]
 * > (unlike collections, arrays, maps, etc.; these are handled as specific [AsStringObjectCategory]s).
 *
 * This is because [Iterable] has lots of more exotic implementing types,
 * e.g. `SQLException` and other SQL stuff, `com.sun.source.util.TreePath`, etc.
 * We better rely on the built-in `toString()` method of these types.
 *
 * Same goes for things like Kotlin's ranges (e.g. [IntRange]) are also handled as [SYSTEM],
 * these have a decent [toString] method anyway, no need for custom handling there.
 */
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING)
internal enum class AsStringObjectCategory(
    val guardStack: Boolean,
    val handler: ((Any) -> String)? = null,
    val handlerWithSize: ((Any, Int?) -> String)? = null,
) {
    /**
     * Base stuff like [Boolean], [Number], [String], [Date], [Temporal], [Char], these have
     * sensible [toString] implementations that we need not (and should not) override
     * @see [isBaseType]
     */
    BASE(guardStack = false, handler = { it.toString() }),

    /** Override: [Collection.toString] methods are vulnerable for stack overflow (in case of recursive data) */
    COLLECTION(guardStack = true, handlerWithSize = {it, size -> (it as Collection<*>).collectionAsString(size)}),

    /** Override: [Array.contentDeepToString] is vulnerable for stack overflow (in case of recursive data) */
    ARRAY(guardStack = true, handlerWithSize = {it, size -> (it as Array<*>).arrayAsString(size) }),

    /** Override: arrays of primitives (e.g. [IntArray], [BooleanArray], [CharArray] lack proper [toString] implementation */
    PRIMITIVE_ARRAY(guardStack = false, handlerWithSize = {it, size -> it.primitiveArrayAsString(size) }),

    /** Override: [Map.toString] methods are vulnerable for stack overflow (in case of recursive data) */
    MAP(guardStack = true, handlerWithSize = { it, size -> (it as Map<*, *>).mapAsString(size) }),

    /** Override: default [Throwable.toString] is way too verbose */
    THROWABLE(guardStack = true, handler = { (it as Throwable).throwableAsString() }),

    /** Override: [Annotation.toString] is too verbose (due to inclusion of package name) */
    ANNOTATION(guardStack = true, handler = { (it as Annotation).annotationAsString() }),

    /** Override: String representation needs some tidying by [lambdaPropertyAsString] */
    LAMBDA_PROPERTY(guardStack = false, handler = { it.lambdaPropertyAsString() }),

    /**
     * Override for Java & Kotlin internals: provide sensible alternative for classes
     * without [toString] implementation; also, adds identity if required
     */
    SYSTEM(guardStack = false, handler = { it.systemClassObjAsString() }),

    /** Custom handling */
    CUSTOM(guardStack = true);

    init {
        check(this.handler == null || this.handlerWithSize == null) {
            "Either handler, or handlerWithSize may be specified, but not both. Found: hasHandler=${hasHandler()} and hasHandlerWithSize=${hasHandlerWithSize()}"
        }
    }

    @JvmSynthetic // avoid access from external Java code
    internal fun hasAnyHandler(): Boolean = hasHandler() || hasHandlerWithSize()

    @JvmSynthetic // avoid access from external Java code
    internal fun hasHandler(): Boolean = handler != null

    @JvmSynthetic // avoid access from external Java code
    internal fun hasHandlerWithSize(): Boolean = handlerWithSize != null

    companion object CategoryResolver {
        @JvmSynthetic // avoid access from external Java code
        internal fun resolveObjectCategory(obj: Any): AsStringObjectCategory {
            return objectCategoryCache[obj::class].ifNull {
                when {
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
                }.also { objectCategoryCache[obj::class] = it }
            }
        }
    }
}

@JvmSynthetic // avoid access from external Java code
internal val objectCategoryCache = MapCache<KClass<*>, AsStringObjectCategory>()

/**
 * Is the object's type considered a base type?
 *
 * Currently, the following types are considered base types:
 * * Primitives / primitive wrappers
 * * Elementary Java types like [String], [Char], [CharSequence], [Number],
 * [java.util.Date], [java.time.temporal.Temporal] and their subclasses
 * * The Kotlin unsigned types ([UByte], [UShort], [UInt], [ULong])
 */
public fun Any.isBaseType(): Boolean =
    this is Boolean
            || this is Number
            || this is CharSequence
            || this is Char
            || this is Temporal
            || this is Date
            || this is UByte
            || this is UShort
            || this is UInt
            || this is ULong

private val baseTypes = arrayOf(
    Number::class.java,
    CharSequence::class.java,
    Char::class.java,
    Temporal::class.java,
    Date::class.java,
    UByte::class.java,
    UShort::class.java,
    UInt::class.java,
    ULong::class.java
)

@JvmSynthetic // avoid access from external Java code
internal fun KType.isBaseType(): Boolean {
    return this is KClass<*> && baseTypes.any {it.isAssignableFrom(this.java)}
}

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

private val collectionTypes = arrayOf(
    Collection::class.java,
    Array::class.java,
    Map::class.java,
    IntArray::class.java,
    ByteArray::class.java,
    CharArray::class.java,
    LongArray::class.java,
    FloatArray::class.java,
    DoubleArray::class.java,
    ShortArray::class.java,
    BooleanArray::class.java
)

@JvmSynthetic // avoid access from external Java code
internal fun KType.isCollectionType(): Boolean {
    return this is KClass<*> && collectionTypes.any {it.isAssignableFrom(this.java)}
}

@JvmSynthetic // avoid access from external Java code
internal fun KType.isCharSequenceType(): Boolean =
    this is KClass<*> && CharSequence::class.java.isAssignableFrom(this.java)

@JvmSynthetic // avoid access from external Java code
internal fun Any.objectIdentity() =
    this.objectIdentity(this::class.asStringClassOption())

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.toStringPreference() = this.asStringClassOption().toStringPreference

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
internal fun Collection<*>.collectionAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(
        prefix = "${collectionIdentity(includeIdentity)}[",
        separator = ", ",
        postfix = "]",
        limit = limit ?: AsStringOption.defaultOption.elementsLimit
    ) { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal fun Any.primitiveArrayAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return this.primitiveArrayIterator().asSequence()
        .joinToString(
            prefix = "${collectionIdentity(includeIdentity)}[",
            separator = ", ",
            postfix = "]",
            limit = limit ?: AsStringOption.defaultOption.elementsLimit
        )
}

@JvmSynthetic // avoid access from external Java code
internal fun Map<*, *>.mapAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return this.entries.joinToString(
        prefix = "${collectionIdentity(includeIdentity)}{",
        separator = ", ",
        postfix = "}",
        limit = limit ?: AsStringOption.defaultOption.elementsLimit
    ) { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal fun Array<*>.arrayAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(
        prefix = "${collectionIdentity(includeIdentity)}[",
        separator = ", ",
        postfix = "]",
        limit = limit ?: AsStringOption.defaultOption.elementsLimit
    ) { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal val lambdaToStringRegex: Regex = Regex("""^\(.*?\) ->.+$""")

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

// aimed to remove a not very useful lengthy number preceded by `/0x`
private val javaLambdaNumberRegex = Regex("/0x[0-9a-f]+@.+$")

/** Removes modifiers from `toGenericString()` output; so `public abstract interface Callable<V>()` => `Callable<V>()` */
private fun String.removeModifiers(typeName: String): String =
    this.indexOf(typeName).let {
        if (it <= 0) this else this.drop(this.length - typeName.length)
}

@JvmSynthetic // avoid access from external Java code
internal fun Any.lambdaPropertyAsString(): String {
    // Lambda properties implicitly (or sometimes explicitly) implement a single functional interface.
    // This provides us some more useful information, so let's add it to the output.
    // NB: Using java reflection; we can't use Kotlin reflection here, that will cause KotlinReflectionInternalError
    val typeSuffix = this::class.java.interfaces.firstOrNull()?.let {
        "=${it.toGenericString().removeModifiers(it.typeName).simplifyClassName()}()" } ?: ""

    // the replacement removes a non-informative lengthy octal number, so
    //    `JavaClassWithLambda$$Lambda$366/0x0000000800291440@27a0a5a2`
    // -> `JavaClassWithLambda$$Lambda$366
    // (also removed identityHash, this is added again at the end)
    return this.toString().simplifyClassName()
        .replace(javaLambdaNumberRegex, "") + "$typeSuffix @${this.identityHashHex}"
}

private val systemClassPackagePrefixes = listOf("java.", "kotlin.")

private fun KClass<*>.isSystemClass() =
    this.java.packageName == "kotlin"
            || systemClassPackagePrefixes.any { this.java.packageName.startsWith(it) }
