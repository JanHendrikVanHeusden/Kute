package nl.kute.reflection

import kotlin.reflect.KFunction

/** Is the `this` [KFunction] the `toString` method? */
internal fun KFunction<*>.isToString(): Boolean = this.name == "toString" && this.parameters.size == 1 // return value