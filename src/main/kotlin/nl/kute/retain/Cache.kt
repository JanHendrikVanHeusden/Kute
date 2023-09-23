package nl.kute.retain

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.core.asString
import java.util.concurrent.ConcurrentHashMap

/** Initial capacity for caches */
@JvmSynthetic // avoid access from external Java code
internal const val defaultInitialCapacity: Int = 200

/** General cache */
@AsStringClassOption(toStringPreference = USE_ASSTRING)
internal interface Cache<K : Any, V : Any, C : Any> {

    /**
     * The caching memory structure; typically a [MutableMap] or [MutableSet].
     * > For *non-atomic* operations (e.g. check if present, then set) refer directly to the [cache]
     * > rather than using convenience methods [get], [contains] etc. (to avoid bugs due to race-conditions)!
     */
    val cache: C

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
internal abstract class AbstractCache<K : Any, V : Any, C : Any>(private val initialCapacity: Int = defaultInitialCapacity) :
    Cache<K, V, C> {

    /**
     * When resetting (replacing) the cache, use this as the new initial capacity,
     * taking into account the actual [size] of the current [cache]
     */
    protected open fun newCapacity(): Int =
        maxOf(initialCapacity, size + defaultInitialCapacity)

    override fun toString(): String = asString()
}

internal open class SetCache<T : Any>(initialCapacity: Int = defaultInitialCapacity) :
    AbstractCache<T, Boolean, MutableSet<T>>(initialCapacity) {

    /**
     * The thread safe [MutableSet] cache (a [ConcurrentHashMap.newKeySet])
     * > For *non-atomic* operations (e.g. check if present, then set) refer directly to the [cache]
     * > rather than using convenience methods [SetCache.get], [SetCache.contains], [SetCache.add]
     * > (to avoid bugs due to race-conditions)!
     */
    @Volatile // see reset()
    override var cache: ConcurrentHashMap.KeySetView<T, Boolean> =
        ConcurrentHashMap.newKeySet(initialCapacity)
        protected set

    /** @return 'true' if the [key] is present in the [cache]; `false` otherwise */
    override operator fun get(key: T): Boolean = cache.contains(key)

    override val size: Int
        get() = cache.size

    /** Resets the [cache] by fully replacing it by a new [ConcurrentHashMap.newKeySet] */
    override fun reset() {
        // create a new set instead of clearing the old one, to avoid intermediate situations
        // while concurrently reading from / writing to the set, as these operations may not be atomic
        cache = ConcurrentHashMap.newKeySet(newCapacity())
    }

    override operator fun contains(key: T): Boolean = cache.contains(key)

    /** Adds a value to the [cache] */
    fun add(key: T) {
        cache.add(key)
    }
}

internal open class MapCache<K : Any, V : Any>(initialCapacity: Int = defaultInitialCapacity) :
    AbstractCache<K, V, MutableMap<K, V>>(initialCapacity) {

    /**
     * The thread safe [MutableMap] cache (a [ConcurrentHashMap]).
     * > For *non-atomic* operations (e.g. check if present, then set) refer directly to the [cache]
     * > rather than using convenience methods [MapCache.get], [MapCache.contains], [MapCache.set]
     * > (to avoid bugs due to race-conditions)!
     */
    @Volatile // see reset()
    override var cache: ConcurrentHashMap<K, V> = ConcurrentHashMap(initialCapacity)
        protected set

    /** @return The value corresponding to the given [key]; or `null` if such a key is not present in the [cache]. */
    override operator fun get(key: K): V? = cache[key]

    override val size: Int
        get() = cache.size

    /** Resets the [cache] by fully replacing it by a new [ConcurrentHashMap] */
    override fun reset() {
        // create a new map instead of clearing the old one, to avoid intermediate situations
        // while concurrently reading from / writing to the map, as these operations are not atomic typically
        cache = ConcurrentHashMap(newCapacity())
    }

    override operator fun contains(key: K): Boolean = cache.keys.contains(key)

    /** Adds a key-value pair to the [cache] */
    operator fun set(key: K, value: V) {
        cache[key] = value
    }

}