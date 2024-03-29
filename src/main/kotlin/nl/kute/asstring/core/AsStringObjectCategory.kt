package nl.kute.asstring.core

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.core.AsStringObjectCategory.SYSTEM
import nl.kute.asstring.core.defaults.initialElementsLimit
import nl.kute.exception.throwableAsString
import nl.kute.reflection.util.hasImplementedToString
import nl.kute.reflection.util.simplifyClassName
import nl.kute.retain.MapCache
import nl.kute.util.identityHashHex
import nl.kute.util.ifNull
import java.time.temporal.Temporal
import java.util.Date
import kotlin.reflect.KClass

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
    internal val guardStack: Boolean,
    internal val handler: ((Any) -> String)? = null,
    internal val handlerWithSize: ((Any, Int?) -> String)? = null,
) {
    /**
     * Base stuff like [Boolean], [Number], [String], [Date], [Temporal], [Char], these have
     * sensible [toString] implementations that we need not (and should not) override
     * @see [isBaseType]
     */
    BASE(guardStack = false, handler = { it.toString() }),

    /** Override: [Collection.toString] methods are vulnerable for stack overflow (in case of recursive data) */
    COLLECTION(guardStack = true, handlerWithSize = { it, size -> (it as Collection<*>).collectionAsString(size)}),

    /** Override: [Array.contentDeepToString] is vulnerable for stack overflow (in case of recursive data) */
    ARRAY(guardStack = true, handlerWithSize = { it, size -> (it as Array<*>).arrayAsString(size) }),

    /** Override: arrays of primitives (e.g. [IntArray], [BooleanArray], [CharArray] lack proper [toString] implementation */
    PRIMITIVE_ARRAY(guardStack = false, handlerWithSize = { it, size -> it.primitiveArrayAsString(size) }),

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

private fun elementsLimit(limit: Int?) =
    (limit ?: AsStringOption.defaultOption.elementsLimit).let { if (it >= 0) it else initialElementsLimit }

@JvmSynthetic // avoid access from external Java code
internal fun Collection<*>.collectionAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(
        prefix = "${collectionIdentity(includeIdentity)}[",
        separator = ", ",
        postfix = "]",
        limit = elementsLimit(limit)
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
            limit = elementsLimit(limit)
        )
}

@JvmSynthetic // avoid access from external Java code
internal fun Map<*, *>.mapAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return this.entries.joinToString(
        prefix = "${collectionIdentity(includeIdentity)}{",
        separator = ", ",
        postfix = "}",
        limit = elementsLimit(limit)
    ) { it.asString() }
}

@JvmSynthetic // avoid access from external Java code
internal fun Array<*>.arrayAsString(limit: Int? = null): String {
    val includeIdentity = AsStringClassOption.defaultOption.includeIdentityHash
    return joinToString(
        prefix = "${collectionIdentity(includeIdentity)}[",
        separator = ", ",
        postfix = "]",
        limit = elementsLimit(limit)
    ) { it.asString() }
}

/** Removes modifiers etc. from `toGenericString()` output; so `public abstract interface Callable<V>()` => `Callable<V>()` */
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
    return this.toString().simplifyClassName()
        .replace(javaLambdaNumberRegex, "") + typeSuffix
}

// aimed to remove a not very useful lengthy number preceded by `/0x`
private val javaLambdaNumberRegex = Regex("/0x[0-9a-f]+@.+$")
