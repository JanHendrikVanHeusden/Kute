package nl.kute.core.namedvalues

import nl.kute.core.weakreference.ObjectWeakReference

internal typealias Supplier<T> = () -> T?

public interface NameValue<V: Any?> {
    public val name: String
    public val valueString: String?
}

public interface TypedNameValue<T: Any?, V: Any?>: NameValue<V> {
    public val objectReference: ObjectWeakReference<T?>
}