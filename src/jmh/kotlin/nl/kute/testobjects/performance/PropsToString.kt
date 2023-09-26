package nl.kute.testobjects.performance

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

/**
 * Class with methods that call the various "`toString()`"-like methods to test
 * > PropsToString would rather be an interface, but JMH does not accept
 * > parameters of abstract/interface type with @State
 */
@State(Scope.Thread)
open class PropsToString {
    open fun withAsString(): String = ""
    open fun withToStringBuilder(): String = ""
    open fun withIdeGeneratedToString(): String = ""
    open fun withGson(): String = ""
}