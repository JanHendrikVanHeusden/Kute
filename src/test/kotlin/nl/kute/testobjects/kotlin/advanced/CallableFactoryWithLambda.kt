package nl.kute.testobjects.kotlin.advanced

import java.util.concurrent.Callable

class CallableFactoryWithLambda {

    fun getCallable() = Callable { "this is the Callable result" }
}