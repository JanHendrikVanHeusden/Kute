@file:Suppress("ClassName")

package nl.kute.testobjects.kotlin.advanced

import java.util.concurrent.Callable

interface `An interface`

class `Callable Factory With Lambda`: `An interface` {

    fun getCallable() = Callable { "this is the Callable result" }
}