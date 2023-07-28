package nl.kute.util

internal interface CacheFactory<K: Any, V: Any, C: Any> {
    var cache: C
    val size: Int
    val resetter: () -> Any
}

internal abstract class AbstractCacheFactory<K: Any, V: Any, C: Any>(override var cache: C): CacheFactory<Any, V, C>

internal class SetCacheFactory<K: Any>(override var cache: MutableSet<K>, override val resetter: () -> MutableSet<K>): AbstractCacheFactory<K, Boolean, MutableSet<K>>(cache) {
    fun get(key: K): Boolean = cache.contains(key)
    override val size: Int
        get() = cache.size
}

internal class MapCacheFactory<K: Any, V: Any>(override var cache: MutableMap<K, V>, override val resetter: () -> MutableMap<K, V>): AbstractCacheFactory<K, V, MutableMap<K, V>>(cache) {
    fun get(key: K): V? = cache.get(key)
    override val size: Int
        get() = cache.size
}