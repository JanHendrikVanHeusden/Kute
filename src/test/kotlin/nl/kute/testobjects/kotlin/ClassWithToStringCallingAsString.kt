package nl.kute.testobjects.kotlin

import nl.kute.core.asString

@Suppress("unused")
class ClassWithToStringCallingAsString {
    val prop1 = "I am prop1"
    override fun toString(): String = asString()
}