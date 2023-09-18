package nl.kute.performance

import nl.kute.testobjects.performance.PropsToString
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties

typealias ToStringTask = (PropsToString) -> String

val disabledWarning: String =
    """
       |   DISABLED!   DISABLED!   DISABLED!   DISABLED!   DISABLED!   DISABLED!
       |   (set enabled flag to run the test)""".trimIndent()

@Suppress("UNCHECKED_CAST")
fun Collection<KClass<out PropsToString>>.retrieveProperties(): MutableList<KMutableProperty1<out PropsToString, Any>> =
    this.map { it.memberProperties }
        .flatten()
        .map { it as KMutableProperty1<out PropsToString, Any> }
        .toMutableList()

fun <T: Any>Class<T>.javaClassToKotlinClass(): KClass<T> = this.kotlin