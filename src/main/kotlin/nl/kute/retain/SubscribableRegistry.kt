package nl.kute.retain

import nl.kute.observe.Subscribable
import nl.kute.observe.Subscribing

/**
 * [Registry] that allows subscription on `change`-events on the registry, by means of [Subscribable]
 * @see [Registry]
 * @see [Subscribable]
 */
internal open class SubscribableRegistry<T: Any> : Registry<T>(), Subscribable by Subscribing() {

    override fun register(entry: T): Int = super.register(entry)
        .also { onChange() }

    override fun remove(entry: T): Int? = super.remove(entry)
        .also { onChange() }

    /**
     * Removes all entries
     * @return the entries that have been removed
     */
    override fun clearAll(): Collection<T> = super.clearAll()
        .also { onChange() }

}