package nl.kute.retain

import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

// TODO: Tests

/**
 * Class to support registering entries that may not have a usable [equals] method.
 * * Stateful: each instance of [Registry] keeps its own registry of [T]s.
 *
 * The class's [T] registry is thread safe.
 *  * All mutations are synchronized ([register], [replaceAll], [remove], [clearAll]).
 *  * Retrieval is thread-safe, it won't throw [ConcurrentModificationException]
 */
internal class Registry<T: Any> {

    private val registry = ConcurrentHashMap<T, Int>()
    private val lockObject = registry
    private val latestAddedId = AtomicInteger(0)

    /** Is any [T] present in this registry? */
    fun hasEntry() : Boolean = registry.isNotEmpty()

    /**
     * Add a [T] to this class's [T] registry.
     * @return An [Int]-value ("`ID`", unique per [Registry] instance) that is associated with
     *   the newly added [T], if not present yet;
     *   or the [Int] value associated with the previously added [T]
     *   if that [T]-object was already present.
     */
    fun register(entry: T): Int {
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
    fun replaceAll(vararg entries: T) {
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
    @Suppress("MemberVisibilityCanBePrivate")
    fun remove(id: Int): T? {
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
    fun remove(entry: T): Int? {
        synchronized(lockObject) {
            return registry.remove(entry)
        }
    }

    /**
     * Removes all entries
     * @return the entries that have been removed
     */
    fun clearAll(): Collection<T> {
        synchronized(lockObject) {
            val existingEntries = registry.keys.toSet()
            registry.clear()
            return existingEntries
        }
    }

    /** A new immutable copy of the registered entries and their ID's */
    fun getEntryMap(): Map<T, Int> = registry.toMap()

    @JvmSynthetic // avoid access from external Java code
    /**
     * @return The [Set] of entries that have been registered
     * > **NB:** This is the **mutable** internal representation, *not* a defensive copy.
     *   **Do not** modify this [Set] from the outside!
     */
    internal fun entries(): Set<T> = registry.keys

}

