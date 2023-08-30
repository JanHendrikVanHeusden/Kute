package nl.kute.core.annotation

@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

@JvmSynthetic // avoid access from external Java code
internal inline fun <reified A : Annotation> Set<Annotation>.findAnnotations(): Set<A> =
    this.filterIsInstance<A>().toSet()