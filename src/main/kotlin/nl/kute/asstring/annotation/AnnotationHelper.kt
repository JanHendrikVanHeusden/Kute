package nl.kute.asstring.annotation

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> Set<Annotation>.findAnnotations(): Set<A> =
    this.filterIsInstance<A>().toSet()

@JvmSynthetic // avoid access from external Java code
// Mainly to keep holder class's annotations for use with companion objects
internal val additionalAnnotations: MutableMap<KClass<*>, Set<Annotation>> = ConcurrentHashMap()