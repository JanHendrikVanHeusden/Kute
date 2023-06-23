package nl.kute.core.property

import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringMask
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.modify.AsStringPatternReplace
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
import nl.kute.reflection.getPropValue
import nl.kute.reflection.propertiesFromSubSuperHierarchy
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/** @return
 *  * for [Array]s: [Array.contentDeepToString]
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[AsStringOmit],
 *  @[AsStringPatternReplace], @[AsStringMask], @[AsStringHash]
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
    val asStringPatternReplace: AsStringPatternReplace? = annotations.findAnnotation()
    val asStringMask: AsStringMask? = annotations.findAnnotation()
    val asStringHash: AsStringHash? = annotations.findAnnotation()
    val asStringOption: AsStringOption = annotations.findAnnotation()!! // always present
    strValue = asStringPatternReplace.replacePattern(strValue)
    strValue = asStringMask.mask(strValue)
    strValue = asStringHash.hashString(strValue)
    strValue = asStringOption.applyOption(strValue)
    return strValue
}

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

internal fun <T : Any> KClass<T>.propertiesWithPrintModifyingAnnotations(): Map<KProperty<*>, Set<Annotation>> {
    // map each property to an (empty yet) mutable set of annotations
    val resultMap: Map<KProperty<*>, MutableSet<Annotation>> =
        propertiesFromSubSuperHierarchy().associateWith { mutableSetOf() }

    resultMap.forEach { (prop, annotations) -> collectPropertyAnnotations(prop, annotations) }
    return resultMap.filterNot { it.value.any { it is AsStringOmit } }
}

internal fun <T : Any> KClass<T>.collectPropertyAnnotations(prop: KProperty<*>, annotations: MutableSet<Annotation>) {
    (prop.annotationOfPropertySuperSubHierarchy<AsStringOmit>())?.let { annotation ->
        annotations.add(annotation)
        // any further annotations are meaningless because output will be omitted when AsStringOmit is present
        return
    }
    (prop.annotationOfPropertySuperSubHierarchy<AsStringHash>())?.let { annotation ->
        annotations.add(annotation)
    }
    (prop.annotationOfPropertySuperSubHierarchy<AsStringMask>())?.let { annotation ->
        annotations.add(annotation)
    }
    (prop.annotationOfPropertySuperSubHierarchy<AsStringPatternReplace>())?.let { annotation ->
        annotations.add(annotation)
    }
    // AsStringOption from lowest subclass in hierarchy with this annotation
    val asStringOptionClassAnnotation =
        annotationOfToStringSubSuperHierarchy() ?: annotationOfSubSuperHierarchy() ?: AsStringOption.defaultAsStringOption
    (prop.annotationOfPropertySubSuperHierarchy() ?: asStringOptionClassAnnotation).let { annotation ->
        annotations.add(annotation)
    }
}