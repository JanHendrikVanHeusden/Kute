package nl.kute.testobjects.kotlin.advanced

class KotlinClassWithAnonymousClassFactory {
    @Suppress("ObjectLiteralToLambda")
    fun createAnonymousInnerClass() = object: AFunctionalInterface {
        override fun doSomeThing(): String = "doing something"
    }
    fun createLambda() = AFunctionalInterface { "doing something" }
}