package nl.kute.util

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt

/** Initial capacity for caches */
private const val defaultInitialCapacity: Int = 200

/** General cache */
internal interface Cache<K : Any, V : Any, C : Any> {

    /** The size of the cache */
    val size: Int

    /** @return The cached content if present; `null` if not present */
    fun get(key: K): V?

    /** Resets the cache by completely clearing or replacing it */
    fun reset()

    /** @return `true` if the [key] is present in the cache; `false` if not */
    fun contains(key: K): Boolean = get(key) != null
}

/** General cache */
internal abstract class AbstractCache<K : Any, V : Any, C : Any>(private val initialCapacity: Int? = null) : Cache<K, V, C> {
    /** The caching memory structure; typically a [MutableMap] or [MutableSet] */
    protected abstract val cache: C
    protected val newCapacity: Int
        get() = maxOf(initialCapacity ?: defaultInitialCapacity, minOf((size * 1.5).roundToInt()), size + defaultInitialCapacity)
}

internal class SetCache<T : Any>(initialCapacity: Int? = null) :
    AbstractCache<T, Boolean, MutableSet<T>>(initialCapacity) {

    /** The thread safe [MutableSet] cache (a [ConcurrentHashMap.newKeySet])  */
    override var cache: MutableSet<T> = ConcurrentHashMap.newKeySet(initialCapacity ?: defaultInitialCapacity)

    override fun get(key: T): Boolean = cache.contains(key)

    override val size: Int
        get() = cache.size

    /** Resets the [cache] by fully replacing it by a new [ConcurrentHashMap.newKeySet] */
    override fun reset() {
        cache = ConcurrentHashMap.newKeySet(newCapacity)
    }

    /** Adds a value to the [cache] */
    fun add(key: T) {
        cache.add(key)
    }
}

internal open class MapCache<K : Any, V : Any>(initialCapacity: Int? = null) :
    AbstractCache<K, V, MutableMap<K, V>>(initialCapacity) {

    /** The thread safe [MutableMap] cache (a [ConcurrentHashMap])  */
    override var cache: MutableMap<K, V> = ConcurrentHashMap(initialCapacity ?: defaultInitialCapacity)

    override fun get(key: K): V? = cache.get(key)

    override val size: Int
        get() = cache.size

    /** Resets the [cache] by fully replacing it by a new [ConcurrentHashMap] */
    override fun reset() {
        cache = ConcurrentHashMap(newCapacity)
    }

    /** Adds a key-value pair to the [cache] */
    fun add(key: K, value: V) {
        cache[key] = value
    }

    /** Adds a key-value pair to the [cache] */
    fun add(entry: Map.Entry<K, V>) {
        cache[entry.key] = entry.value
    }
}