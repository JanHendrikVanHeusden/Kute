package nl.kute.asstring.namedvalues

import nl.kute.asstring.weakreference.ObjectWeakReference

/**
 * A [NameValue] implementation where its value is provided directly, as parameter [value].
 * * Goal: To provide an additional value to include in the [nl.kute.asstring.core.asString] output
 * * Usage: See  [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed]
 *
 * [NamedValue] is intended for situations where the value is provided once, and reassignment to
 * the source value need not be reflected. State changes are reflected though.
 * E.g.:
 * ```
 * var aDate = Date()
 * val namedValue: NamedValue<Date> = NamedValue("aDate", aDate)
 *
 * // this state change *will* be reflected in the `namedValue`:
 * aDate.time = aDate.time + 3_600_000
 * println(namedValue.value) // yields date with new time
 * // this reassignment *will not* be reflected in the `namedValue`:
 * aDate = Date()
 * println(namedValue.value) // yields date with new time, but not the latest value of aDate
 * ```
 * * Usage of [NamedValue] in a pre-built [nl.kute.asstring.core.AsStringBuilder] is not recommended,
 *   as reassignment is not reflected
 *   * If reassignment should be reflected, consider using [NamedSupplier] or [NamedProp]
 * * Annotations that affect output of [nl.kute.asstring.core.asString] are taken in account only if these are part of
 *   the [value] object. Annotations in the outer context (e.g. in the above example: annotations within the class
 *   that holds the `aDate` variable) are not taken in account.
 *   * If annotations in the outer context are to be honoured, consider using [NamedProp]
 * * The [value] is weakly referenced only, so the [NamedValue] does not prevent garbage collection of the [value]
 * @param name The name to identify this [NamedValue]'s value
 * @param value The value of this [NamedValue]
 * @see [nl.kute.asstring.core.AsStringBuilder.withAlsoNamed]
 */
public class NamedValue<V: Any?>(override val name: String, value: V?): AbstractNameValue<V?>() {
    private val valueReference: ObjectWeakReference<V?> = ObjectWeakReference(value)
    override val value: V?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedValue
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() = valueReference.get()
}
