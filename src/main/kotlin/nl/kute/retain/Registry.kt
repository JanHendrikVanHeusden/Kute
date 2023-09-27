package nl.kute.retain

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.reflection.util.simplifyClassName
import nl.kute.util.identityHashHex
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Class to support registering entries that may not have a usable [equals] method.
 * * Stateful: each instance of [Registry] keeps its own registry of [T]s.
 *
 * The class's [T] registry is thread safe.
 *  * All mutations are synchronized ([register], [replaceAll], [remove], [clearAll]).
 *  * Retrieval is thread-safe, it won't throw [ConcurrentModificationException]
 */
@AsStringClassOption(toStringPreference = PREFER_TOSTRING)  // to avoid circular dependencies
internal open class Registry<T: Any> {

    private val registry = ConcurrentHashMap<T, Int>()
    private val lockObject = registry
    private val latestAddedId = AtomicInteger(0)

    /** Has anything been registered yet in this registry? */
    fun hasEntry() : Boolean = registry.isNotEmpty()

    /**
     * Add a [T] to this class's [T] registry.
     * @return An [Int]-value ("`ID`", unique per [Registry] instance) that is associated with
     *   the newly added [T], if not present yet;
     *   or the [Int] value associated with the previously added [T]
     *   if that [T]-object was already present.
     */
    open fun register(entry: T): Int {
        synchronized(lockObject) {
            // If already present, returns existing ID
            return registry.entries.firstOrNull { it.key === entry }?.value.ifNull {
                // not present, add the entries and return the id
                val id = latestAddedId.getAndIncrement()
                registry[entry] = id
                id
            }
        }
    }

    /** Removes any existing entries and applies the given [entries] */
    open fun replaceAll(vararg entries: T) {
        synchronized(lockObject) {
            val currentIds: Collection<Int> = getEntryMap().values
            val newIds: List<Int> = entries.map { register(it) }
            (currentIds - newIds.toSet()).forEach { remove(it) }
        }
    }

    /**
     * Removes existing [T] with the given [id], if present
     * @return The [T] that was removed; or `null` if it was not present
     */
    open fun remove(id: Int): T? {
        synchronized(lockObject) {
            return registry.entries.firstOrNull { it.value == id }?.key?.let {
                remove(it)
                it
            }
        }
    }

    /**
     * Removes the given [entry], if present.
     * > The existence-check is **not** done by equality `==`, but whether it is the exact same object (by `===` )
     * @return The ID of the [entry] that was removed; or `null` if it was not present
     */
    open fun remove(entry: T): Int? {
        synchronized(lockObject) {
            return registry.remove(entry)
        }
    }

    /**
     * Removes all entries
     * @return the entries that have been removed
     */
    open fun clearAll(): Collection<T> {
        synchronized(lockObject) {
            val existingEntries = registry.keys.toSet()
            registry.clear()
            return existingEntries
        }
    }

    /** An immutable copy of the registered entries and their ID's */
    fun getEntryMap(): Map<T, Int> = registry.toMap()

    @JvmSynthetic // avoid access from external Java code
    /** @return The [Set] of entries that have been registered */
    internal fun entries(): Set<T> = registry.keys

    // Not using asString(), to avoid circular dependencies
    override fun toString(): String =
        "${this::class.simplifyClassName()}@${this.identityHashHex}(#registered=${registry.size}, latestAddedId=$latestAddedId)"

}

