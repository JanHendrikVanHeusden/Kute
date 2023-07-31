package nl.kute.util

import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.asString
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

/** Initial capacity for caches */
private const val defaultInitialCapacity: Int = 200

/** General cache */
internal interface Cache<K : Any, V : Any, C : Any> {

    /** The size of the cache */
    val size: Int

    /** @return Get the cached content if present */
    operator fun get(key: K): V?

    /** Resets the cache by completely clearing or replacing it */
    fun reset()

    /** @return `true` if the [key] is present in the cache; `false` if not */
    operator fun contains(key: K): Boolean
}

/** General cache */
internal abstract class AbstractCache<K : Any, V : Any, C : Any>(private val initialCapacity: Int? = null) :
    Cache<K, V, C> {

    /** The caching memory structure; typically a [MutableMap] or [MutableSet] */
    protected abstract val cache: C

    @AsStringOmit
    protected val newCapacity: Int
        get() = maxOf(
            initialCapacity ?: defaultInitialCapacity, minOf((size * 1.5).roundToInt(), size + defaultInitialCapacity)
        )

    override fun toString(): String = asString()
}

internal open class SetCache<T : Any>(initialCapacity: Int? = null) :
    AbstractCache<T, Boolean, MutableSet<T>>(initialCapacity) {

    /** The thread safe [MutableSet] cache (a [ConcurrentHashMap.newKeySet])  */
    override var cache: MutableSet<T> = ConcurrentHashMap.newKeySet(initialCapacity ?: defaultInitialCapacity)

    /** @return 'true' if the [key] is present in the [cache]; `false` otherwise */
    override operator fun get(key: T): Boolean = cache.contains(key)

    override val size: Int
        get() = cache.size

    /** Resets the [cache] by fully replacing it by a new [ConcurrentHashMap.newKeySet] */
    override fun reset() {
        // create a new set instead of clearing the old one, to avoid intermediate situations
        // while concurrently reading from / writing to the set, as these operations may not be atomic
        cache = ConcurrentHashMap.newKeySet(newCapacity)
    }

    override operator fun contains(key: T): Boolean = cache.contains(key)

    /** Adds a value to the [cache] */
    fun add(key: T) {
        cache.add(key)
    }
}

internal open class MapCache<K : Any, V : Any>(initialCapacity: Int? = null) :
    AbstractCache<K, V, MutableMap<K, V>>(initialCapacity) {

    /** The thread safe [MutableMap] cache (a [ConcurrentHashMap])  */
    override var cache: MutableMap<K, V> = ConcurrentHashMap(initialCapacity ?: defaultInitialCapacity)

    /** @return The value corresponding to the given [key]; or `null` if such a key is not present in the [cache]. */
    override operator fun get(key: K): V? = cache[key]

    override val size: Int
        get() = cache.size

    /** Resets the [cache] by fully replacing it by a new [ConcurrentHashMap] */
    override fun reset() {
        // create a new map instead of clearing the old one, to avoid intermediate situations
        // while concurrently reading from / writing to the map, as these operations are not atomic typically
        cache = ConcurrentHashMap(newCapacity)
    }

    override operator fun contains(key: K): Boolean = cache.keys.contains(key)

    /** Adds a key-value pair to the [cache] */
    operator fun set(key: K, value: V) {
        cache[key] = value
    }

}