package nl.kute.testobjects.kotlin.advanced

class KotlinClassWithAnonymousClass {
    @Suppress("ObjectLiteralToLambda", "unused")
    val propWithAnonymousInnerClass = object: AFunctionalInterface {
        override fun doSomeThing(): String = "doing something"
    }
    val propWithLambda = AFunctionalInterface { "doing some thing" }
}