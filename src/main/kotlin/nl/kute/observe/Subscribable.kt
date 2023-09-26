package nl.kute.observe

import java.util.concurrent.ConcurrentHashMap

/** Convenience type alias for `() -> `[Unit] */
internal typealias Action = () -> Unit

/**
 * Interface with implementation (through default methods) that allows subscription on (some) CRUD events
 * on the implementing class.
 * > The implementing class needs to call the `on...()` methods (e.g. [onAdd], [onRemove]).
 */
internal interface Subscribable {
    /** [Set] of [Action]s to be executed on `add`-operations of the implementing class */
    val additionSubscriptions: MutableSet<Action>
    /** [Set] of [Action]s to be executed on `remove`-operations of the implementing class */
    val removalSubscriptions: MutableSet<Action>

    /**
     * Executes the [Action]s to be executed on `add`-operations
     * @see [additionSubscriptions]
     */
    fun onAdd() = additionSubscriptions.forEach { action -> action() }

    /**
     * Executes the [Action]s to be executed on `remove`-operations
     * @see [removalSubscriptions]
     */
    fun onRemove() = removalSubscriptions.forEach { action -> action() }

    /**
     * Add an [Action] to be executed on `add`-operations of the implementing class
     * @see [additionSubscriptions]
     */
    fun subscribeOnAddition(action: Action) = additionSubscriptions.add(action)

    /**
     * Add an [Action] to be executed on `remove`-operations of the implementing class
     * @see [removalSubscriptions]
     */
    fun subscribeOnRemoval(action: Action) = additionSubscriptions.add(action)
}

internal class Subscribing: Subscribable {
    override val additionSubscriptions: MutableSet<Action> = ConcurrentHashMap.newKeySet()
    override val removalSubscriptions: MutableSet<Action> = ConcurrentHashMap.newKeySet()
}