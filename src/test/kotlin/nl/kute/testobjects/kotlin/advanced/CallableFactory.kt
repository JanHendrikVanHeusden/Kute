package nl.kute.testobjects.kotlin.advanced

import java.util.concurrent.Callable

class CallableFactory {

    @Suppress("ObjectLiteralToLambda")
    fun createCallable() = object: Callable<String> {
        override fun call(): String = "this is the Callable result"
    }
}