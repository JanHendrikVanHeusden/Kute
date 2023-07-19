package nl.kute.testobjects.kotlin.advanced

import java.util.concurrent.Callable

class KotlinClassWithCallable {

    @Suppress("ObjectLiteralToLambda", "unused")
    val myCallable = object: Callable<String> {
        override fun call(): String = "this is the Callable result"
    }
}