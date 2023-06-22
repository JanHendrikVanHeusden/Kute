package nl.kute.reflection.error

/**
 * `UnsupportedOperationException` and/or `KotlinReflectionInternalError` may happen in these cases
 *  (from Kotlin exception message):
 *   > 1. `This class is an internal synthetic class generated by the Kotlin compiler, such as an anonymous class for a lambda, a SAM wrapper, a callable reference, etc.`
 *   > 2. It's a package of file facade: `Packages and file facades are not yet supported in Kotlin reflection.`
 *
 * In thes cases, as Kotlin can't find properties, we don't bother
 *   > The message also suggest this: `Please use Java reflection to inspect this class`
 *
 * But that's out of scope for this more exotic stuff, we just don't care too much, so this exception should be thrown
 * (and should be handled within Kute; must not propagate to outside world!)
 *
 * [SyntheticClassException] should be used to flag these situations to quit early, otherwise
 * it may cause further exceptions downstream
 */

internal class SyntheticClassException(message: String?, cause: Throwable?): Exception(message, cause)