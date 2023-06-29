package nl.kute.core.property

import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringMask
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.modify.hashString
import nl.kute.core.annotation.modify.mask
import nl.kute.core.annotation.modify.replacePattern
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.applyOption
import nl.kute.core.asString
import nl.kute.reflection.annotationfinder.annotationOfPropertySubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfPropertySuperSubHierarchy
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfToStringSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationSetOfPropertySuperSubHierarchy
import nl.kute.reflection.getPropValue
import nl.kute.reflection.propertiesFromSubSuperHierarchy
import nl.kute.util.ifNull
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/** @return
 *  * for [Array]s: [Array.contentDeepToString]
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[AsStringOmit],
 *  @[AsStringReplace], @[AsStringMask], @[AsStringHash]
 */
internal fun <T : Any> T?.getPropValueString(prop: KProperty<*>, annotations: Set<Annotation>): String? {
    if (this == null) {
        return null
    }
    val value: Any? = this.getPropValue(prop)
    var strValue = if (value is Array<*> || value is Collection<*>)
        value.asString()
    else {
        value?.toString()
    }
    if (annotations.isEmpty()) {
        return strValue
    }
    if (annotations.any { it is AsStringOmit }) {
        return ""
    }
    val asStringReplaceSet: Set<AsStringReplace> = annotations.findAnnotations()
    val asStringMaskSet: Set<AsStringMask> = annotations.findAnnotations()
    // non-repeating
    val asStringHash: AsStringHash? = annotations.findAnnotation()
    // non-repeating
    val asStringOption: AsStringOption = annotations.findAnnotation()!! // always present

    asStringReplaceSet.forEach {
        strValue = it.replacePattern(strValue)
    }
    asStringMaskSet.forEach {
        strValue = it.mask(strValue)
    }
    strValue = asStringHash.hashString(strValue)
    strValue = asStringOption.applyOption(strValue)
    return strValue
}

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotations(): Set<A> =
    this.filterIsInstance<A>().toSet()

internal fun <T : Any> KClass<T>.propertiesWithPrintModifyingAnnotations(): Map<KProperty<*>, Set<Annotation>> {
    return propsWithAnnotationsCacheByClass[this].ifNull {
        // map each property to an (empty yet) mutable set of annotations
        propertiesFromSubSuperHierarchy().associateWith { mutableSetOf<Annotation>() }
            // populate the set of annotations per property
            .onEach { (prop, annotations) -> collectPropertyAnnotations(prop, annotations) }
            .filter { entry -> entry.value.none { it is AsStringOmit } }
            // add to cache
            .also { propsWithAnnotationsCacheByClass[this] = it }
    }
}

private val propsWithAnnotationsCacheByClass: MutableMap<KClass<*>, Map<KProperty<*>, Set<Annotation>>> = mutableMapOf()

internal fun <T : Any> KClass<T>.collectPropertyAnnotations(prop: KProperty<*>, annotations: MutableSet<Annotation>) {
    (prop.annotationOfPropertySuperSubHierarchy<AsStringOmit>())?.let { annotation ->
        annotations.add(annotation)
        // any further annotations are meaningless because output will be omitted when AsStringOmit is present
        return
    }
    (prop.annotationOfPropertySuperSubHierarchy<AsStringHash>())?.let { annotation ->
        annotations.add(annotation)
    }
    (prop.annotationSetOfPropertySuperSubHierarchy<AsStringMask>()).let { annotationSet ->
        annotations.addAll(annotationSet)
    }
    (prop.annotationSetOfPropertySuperSubHierarchy<AsStringReplace>()).let { annotationSet ->
        annotations.addAll(annotationSet)
    }
    // AsStringOption from lowest subclass in hierarchy with this annotation
    val asStringOptionClassAnnotation =
        annotationOfToStringSubSuperHierarchy() ?: annotationOfSubSuperHierarchy() ?: AsStringOption.defaultAsStringOption
    (prop.annotationOfPropertySubSuperHierarchy() ?: asStringOptionClassAnnotation).let { annotation ->
        annotations.add(annotation)
    }
}