package nl.kute.asstring.namedvalues

import nl.kute.asstring.weakreference.ObjectWeakReference
import nl.kute.log.log
import nl.kute.reflection.util.simplifyClassName
import nl.kute.util.throwableAsString
import java.util.concurrent.Callable

private typealias Supplier<T> = () -> T?

/**
 * A [NameValue] implementation where the value to be resolved is provided by means of a [Supplier] lambda.
 *
 * [NamedSupplier] is intended for situations where the supplied value needs to be evaluated on each access,
 * both state changes and reassignment.
 * E.g.:
 * ```
 * var aDate = Date()
 * val namedSupplier: NamedSupplier<Date> = NamedSupplier("aDate", { aDate })
 *
 * // this state change *will* be reflected in the `namedSupplier`:
 * aDate.time = aDate.time + 3_600_000
 * println(namedSupplier.value) // yields date with new time
 * // this reassignment *will also* be reflected in the `namedSupplier`:
 * aDate = Date()
 * println(namedSupplier.value) // yields date after reassignment
 * ```
 * * Usage of [NamedSupplier] in a pre-built [nl.kute.asstring.core.AsStringBuilder] is good practice, as reassignment is reflected
 * * Annotations that affect output of [nl.kute.asstring.core.asString] are taken in account only if these are part of
 *   the supplied object. Annotations in the outer context (e.g. in the above example: annotations within the class
 *   that holds the `aDate` variable) are not taken in account.
 *   * If annotations in the outer context are to be honoured, consider using [NamedProp]
 * * The supplier is weakly referenced only, so the [NamedSupplier] does not prevent garbage collection of the supplier,
 *   or it's supplied value.
 * @param name The name to identify this [NamedSupplier]'s value
 * @param supplier The lambda to supply the [value] on request
 */
public class NamedSupplier<V: Any?>(override val name: String, supplier: Supplier<V?>): AbstractNameValue<V?>() {

    /**
     * Secondary constructor, mainly for usage from Java.
     * > In Java, `() -> myValue` is translated to a [Callable] when introduced as a local variable
     * @param name The name to identify this [NamedSupplier]'s value
     * @param callable The [Callable] to supply the [value] on request
     */
    public constructor(name: String, callable: Callable<V?>) : this(name, {callable.call()})

    private val supplierReference: ObjectWeakReference<Supplier<V>> = ObjectWeakReference(supplier)

    override val value: V?
        // Using `get()` so it's evaluated when required only, not at construction time of the NamedSupplier
        // NB: not using `lazy`, it should honour changes in the underlying object
        get() {
            return try {
                (supplierReference.get())?.invoke()
            } catch (e: Exception) {
                log("Exception ${e::class.simplifyClassName()} while evaluating supplier of ${this::class.simplifyClassName()}: ${e.throwableAsString()} ")
                return null
            }
        }

    // Deliberately no equals() and hashCode()
    // The only relevant way to test equality would be to use the combination of `name` and supplied value.
    // The supplied value may be different by subsequent invocations however, so unsuitable for,
    // say, equality check, or for finding it in a HashSet.
    // So this leaves `name` only; but that alone is not sufficient to decide on equality.
    //
    // So we rely on the default (super): each NamedSupplier object is different from any other.
}

/**
 * Convenience method to construct a [NamedSupplier]
 * @receiver The [V] producing supplier the [NamedSupplier] is about
 * @param name The name to be associated with the value to be supplied
 */
public fun <V: Any?> Supplier<V?>.namedSupplier(name: String): NameValue<V?> =
    NamedSupplier(name,this)
