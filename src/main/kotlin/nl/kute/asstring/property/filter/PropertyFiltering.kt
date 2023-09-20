package nl.kute.asstring.property.filter

import nl.kute.asstring.property.meta.PropertyMeta
import nl.kute.asstring.property.meta.PropertyMetaData
import nl.kute.util.ifNull
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/** Alias for type `(`[PropertyMeta]`)` -> [Boolean] */
public typealias PropertyMetaFilter = (PropertyMeta) -> Boolean

/**
 * Class to support filtering of properties by [PropertyMetaData].
 * * Stateful: each instance of [PropertyFiltering] keeps its own registry of [PropertyMetaFilter]s.
 *
 * Typical usage is to filter properties that should not be rendered by [nl.kute.asstring.core.asString],
 * e.g. to avoid performance issues.
 *  * Filtering out properties based on general criteria may be an alternative (or addition)
 *   to applying [nl.kute.asstring.annotation.modify.AsStringOmit]
 *  > Think of JPA (`Hibernate`, Spring Data, etc.) or `Exposed` entities with properties that are [Collection]s of child records.
 *    **Retrieving such properties** by means of reflection **might cause child records to be fetched from the database**,
 *    which is probably not your intent when retrieving a String representation of your entity.
 *
 * The class's [PropertyMetaFilter] registry is thread safe.
 *  * All mutations are synchronized ([addFilter], [removeFilter], [clearAllFilters]).
 *  * Retrieval is thread-safe, it won't throw [ConcurrentModificationException]
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
internal class PropertyFiltering {

    private val propertyFilterRegistry = ConcurrentHashMap<PropertyMetaFilter, Int>()
    private val lockObject = propertyFilterRegistry
    private val latestAddedId = AtomicInteger(0)

    /** Is any [PropertyMetaFilter] present in this registry? */
    fun hasFilter() : Boolean = propertyFilterRegistry.isNotEmpty()

    /**
     * Add a [PropertyMetaFilter] to this class's [PropertyMetaFilter] registry.
     * @return An [Int]-value ("`ID`", unique per [PropertyFiltering] instance) that is associated with
     *   the newly added [PropertyMetaFilter], if not present yet;
     *   or the [Int] value associated with the previously added [PropertyMetaFilter]
     *   if that [PropertyMetaFilter]-object was already present.
     */
    fun addFilter(filter: PropertyMetaFilter): Int {
        synchronized(lockObject) {
            // If already present, returns existing ID
            return propertyFilterRegistry.entries.firstOrNull { it.key === filter }?.value.ifNull {
                // not present, add the filter and return the id
                val id = latestAddedId.getAndIncrement()
                propertyFilterRegistry[filter] = id
                id
            }
        }
    }

    /** Removes any existing [PropertyMetaFilter] and applies the given [filters] */
    fun setFilters(vararg filters: PropertyMetaFilter) {
        synchronized(lockObject) {
            val currentFilterIds: Collection<Int> = getRegisteredFilters().values
            val newFilterIds: List<Int> = filters.map { addFilter(it) }
            (currentFilterIds - newFilterIds.toSet()).forEach { removeFilter(it) }
        }
    }

    /**
     * Removes existing [PropertyMetaFilter] with the given [filterId], if present
     * @return The [PropertyMetaFilter] that was removed; or `null` if it was not present
     */
    fun removeFilter(filterId: Int): PropertyMetaFilter? {
        synchronized(lockObject) {
            return propertyFilterRegistry.entries.firstOrNull { it.value == filterId }?.key?.let {
                removeFilter(it)
                it
            }
        }
    }

    /**
     * Removes the given [filter], if present.
     * > The existence-check is **not** done by equality `==`, but whether it is the exact same object (by `===` )
     * @return The ID of the [filter] that was removed; or `null` if it was not present
     */
    fun removeFilter(filter: PropertyMetaFilter): Int? {
        synchronized(lockObject) {
            return propertyFilterRegistry.remove(filter)
        }
    }

    /**
     * Removes all filters
     * @return the filters that have been removed
     */
    fun clearAllFilters(): Collection<PropertyMetaFilter> {
        synchronized(lockObject) {
            val existingFilters = propertyFilterRegistry.keys.toSet()
            propertyFilterRegistry.clear()
            return existingFilters
        }
    }

    /** A new read-only map of the registered filters and their ID's */
    fun getRegisteredFilters(): Map<PropertyMetaFilter, Int> = propertyFilterRegistry.toMap()

    @JvmSynthetic // avoid access from external Java code
    /** @return The [Set] of filters that have been registered */
    internal fun getFilters(): Set<PropertyMetaFilter> = propertyFilterRegistry.keys

}

/**
 * [PropertyFiltering] instance to omit matching properties from the output
 * of [nl.kute.asstring.core.asString]
 */
@JvmSynthetic // avoid access from external Java code
internal val propertyOmitFilter: PropertyFiltering = PropertyFiltering()
