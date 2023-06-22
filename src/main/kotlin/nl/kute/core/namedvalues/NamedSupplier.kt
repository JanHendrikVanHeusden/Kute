package nl.kute.core.namedvalues

import nl.kute.core.reference.ObjectWeakReference
import nl.kute.core.simplifyClassName
import nl.kute.log.log
import nl.kute.util.asString

class NamedSupplier<V: Any?>(override val name: String, supplier: Supplier<V?>): NameValue<V?> {
    @Suppress("MemberVisibilityCanBePrivate")
    val supplierReference: ObjectWeakReference<Supplier<V>> = ObjectWeakReference(supplier)
    override val valueString: String?
        // Using `get` so it's evaluated when required only, not at construction time of the NamedSupplier
        // NB: don't use `lazy`, it should honour changes in the underlying object
        get() {
            return try {
                (supplierReference.get())?.invoke()?.toString()
            } catch (e: Exception) {
                log("Exception ${e::class.simplifyClassName()} while evaluation supplier of ${this::class.simplifyClassName()}: ${e.asString()} ")
                return null
            }
        }

    // Deliberately no equals() and hashCode()
    // The only relevant way to test equality would be to use the combination of `name` and supplied value.
    // The supplied value may be different by subsequent invocations however, so unsuitable for,
    // say, equality check, or for finding it in a HashSet.
    // So this leaves `name` only; but that alone is not sufficient to decide on equality.
    //
    // So we rely on the default (super) implementation, where each object is different from any other.
}

@Suppress("unused")
fun <V: Any?> Supplier<V?>.namedVal(name: String): NameValue<V?> = NamedSupplier(name,this)
