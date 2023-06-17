package nl.kute.core.property

import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.printable.annotation.modifiy.hashString
import nl.kute.printable.annotation.modifiy.mask
import nl.kute.printable.annotation.modifiy.replacePattern
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.printable.annotation.option.applyOption
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
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[PrintOmit],
 *  @[PrintPatternReplace], @[PrintMask], @[PrintHash]
 */
internal fun <T : Any> T.getPropValueString(prop: KProperty<*>, annotations: Set<Annotation>): String? {
    val value: Any? = this.getPropValue(prop)
    var strValue = if (value is Array<*>)
        value.contentDeepToString()
    else {
        value?.toString()
    }
    if (annotations.isEmpty()) {
        return strValue
    }
    if (annotations.any { it is PrintOmit }) {
        return ""
    }
    val printPatternReplace: PrintPatternReplace? = annotations.findAnnotation()
    val printMask: PrintMask? = annotations.findAnnotation()
    val printHash: PrintHash? = annotations.findAnnotation()
    val printOption: PrintOption = annotations.findAnnotation()!! // always present
    strValue = printPatternReplace.replacePattern(strValue)
    strValue = printMask.mask(strValue)
    strValue = printHash.hashString(strValue)
    strValue = printOption.applyOption(strValue)
    return strValue
}

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

internal fun <T : Any> KClass<T>.propertiesWithPrintModifyingAnnotations(): Map<KProperty<*>, Set<Annotation>> {
    // map each property to an (empty yet) mutable set of annotations
    val resultMap: Map<KProperty<*>, MutableSet<Annotation>> =
        propertiesFromSubSuperHierarchy().associateWith { mutableSetOf() }

    resultMap.forEach { (prop, annotations) -> collectPropertyAnnotations(prop, annotations) }
    return resultMap
}

internal fun <T : Any> KClass<T>.collectPropertyAnnotations(prop: KProperty<*>, annotations: MutableSet<Annotation>) {
    (prop.annotationOfPropertySuperSubHierarchy<PrintOmit>())?.let { annotation ->
        annotations.add(annotation)
        // any further annotations are meaningless because output will be omitted when PrintOmit is present
        return
    }
    (prop.annotationOfPropertySuperSubHierarchy<PrintHash>())?.let { annotation ->
        annotations.add(annotation)
    }
    (prop.annotationOfPropertySuperSubHierarchy<PrintMask>())?.let { annotation ->
        annotations.add(annotation)
    }
    (prop.annotationOfPropertySuperSubHierarchy<PrintPatternReplace>())?.let { annotation ->
        annotations.add(annotation)
    }
    // PrintOption from lowest subclass in hierarchy with this annotation
    val printOptionClassAnnotation =
        annotationOfToStringSubSuperHierarchy() ?: annotationOfSubSuperHierarchy() ?: PrintOption.defaultPrintOption
    (prop.annotationOfPropertySubSuperHierarchy() ?: printOptionClassAnnotation).let { annotation ->
        annotations.add(annotation)
    }
}