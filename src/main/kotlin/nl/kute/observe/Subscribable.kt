package nl.kute.observe

import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import java.util.concurrent.ConcurrentHashMap

/** Convenience type alias for `() -> `[Unit] */
internal typealias Action = () -> Unit

/**
 * Interface with implementation (through default methods) that allows subscription
 * to `change`-events on the implementing class.
 * > The implementing class needs to call the [onChange] method when a `change`-event occurs
 */
internal interface Subscribable {
    /** Executes the [Action]s to be executed on `change`-operations */
    fun onChange()

    /** Add an [Action] to be executed on `change`-operations of the implementing class */
    fun subscribeToChange(action: Action)
}

internal class Subscribing: Subscribable {

    /** [Set] of [Action]s to be executed on `change`-events of the implementing class */
    private val changeSubscriptions: MutableSet<Action> = ConcurrentHashMap.newKeySet()

    /**
     * Executes the [Action]s to be executed on `remove`-operations
     * @see [changeSubscriptions]
     */
    override fun onChange() = changeSubscriptions.forEach { action -> action() }

    /**
     * Add an [Action] to be executed on `remove`-operations of the implementing class
     * @see [changeSubscriptions]
     */
    override fun subscribeToChange(action: Action) {
        changeSubscriptions.add(action)
    }

    private val asStringBuilder = asStringBuilder()
        .withOnlyProperties(this::changeSubscriptions)
        .build()

    override fun toString(): String = asStringBuilder.asString()

}