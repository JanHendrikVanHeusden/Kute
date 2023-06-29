package nl.kute.core.weakreference

import nl.kute.core.annotation.option.defaultNullString
import java.lang.ref.WeakReference

class ObjectWeakReference<T: Any?>(obj: T?): WeakReference<T?>(obj) {
    override fun toString(): String = this.get()?.toString() ?: defaultNullString

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjectWeakReference<*>

        return this.get() == other.get()
    }

    override fun hashCode(): Int {
        return this.get()?.hashCode() ?: 0
    }

}