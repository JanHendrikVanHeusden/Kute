package nl.kute.core.weakreference

import nl.kute.reflection.simplifyClassName
import java.lang.ref.WeakReference

/**
 * [WeakReference] implementation with [toString] overridden
 * @param obj The referenced object. `null`-safe.
 */
public class ObjectWeakReference<T: Any?>(obj: T?): WeakReference<T?>(obj) {
    /** @return String representation containing the referenced object's [toString] */
    override fun toString(): String {
        val className = this.get()?.let { it::class.simplifyClassName() }
        return "$objectWeakRefClassName of $className(${this.get().toString()})"
    }
}

private val objectWeakRefClassName = ObjectWeakReference::class.simplifyClassName()