package nl.kute.core.property

import nl.kute.config.subscribeConfigChange
import nl.kute.core.AsStringObjectCategory
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
import nl.kute.core.lambdaToStringRegex
import nl.kute.reflection.annotationfinder.annotationOfPropertySubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfPropertySuperSubHierarchy
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfToStringSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationSetOfPropertySuperSubHierarchy
import nl.kute.reflection.getPropValue
import nl.kute.reflection.hasImplementedToString
import nl.kute.reflection.propertiesFromSubSuperHierarchy
import nl.kute.util.MapCache
import nl.kute.util.ifNull
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaType

/** @return
 *  * for [Array]s: [Array.contentDeepToString]
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[AsStringOmit],
 *  @[AsStringReplace], @[AsStringMask], @[AsStringHash]
 */
@JvmSynthetic // avoid access from external Java code
internal fun <T : Any> T?.getPropValueString(prop: KProperty<*>, annotations: Set<Annotation>): String? {
    if (this == null) {
        return null
    }
    val value: Any? = this.getPropValue(prop)

    val hasHandler: Boolean = value?.let { AsStringObjectCategory.resolveObjectCategory(value) }?.hasHandler() == true
    var strValue: String? = if (hasHandler)
        value.asString()
    else if (value == null) {
        null
    }
    else if (value::class.hasImplementedToString()) {
        value.toString()
    } else {
        value.asString()
    }
    if (prop.isLambdaProperty(strValue)) {
        strValue = strValue!!.lambdaSignatureString()
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

internal fun KProperty<*>.isLambdaProperty(stringValue: String?): Boolean {
    return stringValue != null
            && returnType.javaType.toString().startsWith("kotlin.jvm.functions.Function")
            && stringValue.matches(lambdaToStringRegex)
}

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotation(): A? =
    this.firstOrNull { it is A } as A?

private inline fun <reified A : Annotation> Set<Annotation>.findAnnotations(): Set<A> =
    this.filterIsInstance<A>().toSet()

@JvmSynthetic // avoid access from external Java code
internal fun <T : Any> KClass<T>.propertiesWithAsStringAffectingAnnotations(): Map<KProperty<*>, Set<Annotation>> {
    // referring to inner cache (so propsWithAnnotationsCacheByClass.cache)
    // because of possible race conditions when resetting the cache
    propsWithAnnotationsCacheByClass.cache.let { theCache ->
        return theCache[this].ifNull {
            // map each property to an (empty yet) mutable set of annotations
            propertiesFromSubSuperHierarchy().associateWith { mutableSetOf<Annotation>() }
                // populate the set of annotations per property
                .onEach { (prop, annotations) -> collectPropertyAnnotations(prop, annotations) }
                .filter { entry -> entry.value.none { it is AsStringOmit } }
                // add to cache
                .also { theCache[this] = it }
        }
    }
}

/**
 * Cache for classes with their properties, and their annotations that may modify the properties' String representation by [asString].
 * > This cache will be reset (cleared) when [AsStringOption.defaultOption] is changed.
 */
@JvmSynthetic // avoid access from external Java code
internal var propsWithAnnotationsCacheByClass = MapCache<KClass<*>, Map<KProperty<*>, Set<Annotation>>>()
    .also {
        @Suppress("UNCHECKED_CAST")
        (AsStringOption::class as KClass<Annotation>).subscribeConfigChange { it.reset() }
    }

@JvmSynthetic // avoid access from external Java code
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
    // AsStringOption from the lowest subclass in hierarchy with this annotation; or defaultOption if not annotated
    val asStringOptionClassAnnotation: AsStringOption =
        annotationOfToStringSubSuperHierarchy() ?: annotationOfSubSuperHierarchy() ?: AsStringOption.defaultOption
    (prop.annotationOfPropertySubSuperHierarchy() ?: asStringOptionClassAnnotation).let { annotation ->
        annotations.add(annotation)
    }
}

private val lambdaParamPackageNameRegex = Regex("""([ (<])\b[a-zA-Z0-9_$]+?\.+?(.+?\b([,)>]|$))""")

/**
 * Strips off the package names of a String that is supposed to be a lambda signature,
 * e.g.
 *  * `(kotlin.Int, kotlin.String) -> kotlin.String`
 *      => `(Int, String) -> String`
 *  * `(kotlin.Pair<kotlin.Int, kotlin.Int>) -> kotlin.Pair<kotlin.Int, kotlin.Int>`
 *      => `(Pair<Int, Int>) -> Pair<Int, Int>`
 */
@JvmSynthetic // avoid access from external Java code
internal fun String.lambdaSignatureString(): String =
    this.replace(lambdaParamPackageNameRegex, "$1$2").let { stripped ->
        if (stripped == this) stripped else stripped.lambdaSignatureString()
    }
