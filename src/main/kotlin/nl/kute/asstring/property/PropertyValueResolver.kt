package nl.kute.asstring.property

import nl.kute.asstring.annotation.additionalAnnotations
import nl.kute.asstring.annotation.findAnnotation
import nl.kute.asstring.annotation.findAnnotations
import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringMask
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.modify.hashString
import nl.kute.asstring.annotation.modify.mask
import nl.kute.asstring.annotation.modify.replacePattern
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.applyOption
import nl.kute.asstring.annotation.option.asStringClassOption
import nl.kute.asstring.config.subscribeConfigChange
import nl.kute.asstring.core.AsStringObjectCategory
import nl.kute.asstring.core.asString
import nl.kute.asstring.core.hasEffectiveRankProvider
import nl.kute.asstring.core.lambdaToStringRegex
import nl.kute.asstring.property.ranking.PropertyValueMeta
import nl.kute.asstring.property.ranking.PropertyValueMetaData
import nl.kute.reflection.annotationfinder.annotationOfPropertySubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfPropertySuperSubHierarchy
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfToStringSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationSetOfPropertySuperSubHierarchy
import nl.kute.reflection.property.getPropValue
import nl.kute.reflection.property.propertiesFromSubSuperHierarchy
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
internal fun <T : Any> T?.getPropValueString(prop: KProperty<*>, annotations: Set<Annotation>): Pair<PropertyValueMetaData?, String?> {
    var value: Any? = null
    val stringVal: String? = let {
        if (this == null) {
            return@let null
        }
        value = this.getPropValue(prop)

        val asStringObjectCategory = value?.let { AsStringObjectCategory.resolveObjectCategory(value!!) }
        val hasHandlerWithSize: Boolean = asStringObjectCategory?.hasHandlerWithSize() == true
        val hasHandler: Boolean = asStringObjectCategory?.hasHandler() == true
        val asStringOption: AsStringOption? = annotations.findAnnotation()

        var strValue: String? = if (hasHandler)
            value.asString()
        else if (hasHandlerWithSize)
            value.asString(asStringOption?.elementsLimit)
        else if (value == null) {
            null
        } else {
            value.asString()
        }
        if (prop.isLambdaProperty(strValue)) {
            strValue = strValue!!.lambdaSignatureString()
        }
        if (annotations.any { it is AsStringOmit }) {
            return@let ""
        }
        val asStringReplaceSet: Set<AsStringReplace> = annotations.findAnnotations()
        val asStringMaskSet: Set<AsStringMask> = annotations.findAnnotations()
        // non-repeating
        val asStringHash: AsStringHash? = annotations.findAnnotation()

        asStringReplaceSet.forEach {
            strValue = it.replacePattern(strValue)
        }
        asStringMaskSet.forEach {
            strValue = it.mask(strValue)
        }
        strValue = asStringHash.hashString(strValue)
        strValue = asStringOption?.applyOption(strValue)
        return@let strValue
    }
    val objClass = if (this == null) null else this@getPropValueString::class
    val hasEffectiveRankProvider = objClass?.asStringClassOption()?.propertySorters.hasEffectiveRankProvider()
    val propertyValueMeta =
        if (objClass != null && hasEffectiveRankProvider) PropertyValueMeta(value, objClass, prop, stringVal?.length)
        // no property sorting required
        else null
    return (propertyValueMeta to stringVal)
}

internal fun KProperty<*>.isLambdaProperty(stringValue: String?): Boolean {
    return stringValue != null
            && returnType.javaType.toString().startsWith("kotlin.jvm.functions.Function")
            && stringValue.matches(lambdaToStringRegex)
}

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
    val asStringOptionAnnotationClassLevel: AsStringOption =
        annotationOfToStringSubSuperHierarchy() ?:
        annotationOfSubSuperHierarchy() ?:
        additionalAnnotations[this]?.findAnnotation() ?:
        AsStringOption.defaultOption

    (prop.annotationOfPropertySubSuperHierarchy() ?: asStringOptionAnnotationClassLevel)
        .let { annotation ->
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
