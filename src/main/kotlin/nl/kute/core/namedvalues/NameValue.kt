package nl.kute.core.namedvalues

import nl.kute.core.weakreference.ObjectWeakReference

internal typealias Supplier<T> = () -> T?

/**
 * Interface for wrapper classes to provide name / value combinations from various sources.
 * For usage, see: [nl.kute.core.AsStringBuilder]
 */
public interface NameValue<V: Any?> {
    //TODO: kdoc
    public val name: String
    //TODO: kdoc
    public val valueString: String?
}

//TODO: kdoc
public interface TypedNameValue<T: Any?, V: Any?>: NameValue<V> {
    //TODO: kdoc
    public val objectReference: ObjectWeakReference<T?>
}