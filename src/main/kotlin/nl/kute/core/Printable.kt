@file:JvmName("Printable")

package nl.kute.core

import nl.kute.hashing.hashString
import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.printable.annotation.modifiy.defaultDigestMethod
import nl.kute.printable.annotation.modifiy.mask
import nl.kute.printable.annotation.modifiy.replacePattern
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.reflection.annotation.annotationOfClass
import nl.kute.reflection.annotation.annotationOfPropertyInHierarchy
import nl.kute.reflection.annotation.annotationOfToString
import nl.kute.reflection.annotation.hasAnnotationInHierarchy
import nl.kute.util.asString
import nl.kute.util.lineEnd
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

// Static stuff (package level) only

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asStringExcluding]
 * @see [asStringExcludingNames]
 */
fun Any.asString(): String {
    return asStringExcluding()
}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 * @param propsToExclude accessible properties that you don't want to be included in the result.
 * E.g. `override fun toString() = `[asStringExcluding]`(::myExcludedProp1, ::myExcludedProp2)`
 * **NB:** Excluding properties will not work from Java classes; use [asStringExcludingNames] instead
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asStringExcludingNames]
 * @see [asString]
 */
fun Any.asStringExcluding(vararg propsToExclude: KProperty<*>): String {
    return asStringExcludingNames(*(propsToExclude.map { it.name }.toTypedArray()))
}

/**
 * Mimics the format of Kotlin data class's [toString] method.
 * * Super-class properties are included
 * * Private properties are included
 * * String value of individual properties is capped at 500; see @[PrintOption] to override the default
 *
 * This method allows you to exclude any properties by name, including inaccessible private ones.
 * @param propNamesToExclude accessible properties that you don't want to be included in the result.
 * E.g. use it when not calling from inside the class:
 * `someObjectWithPrivateProps.`[asStringExcludingNames]`("myExcludedPrivateProp1", "myExcludedProp2")
 * @return A String representation of the receiver object, including class name and property names + values;
 * adhering to related annotations; for these annotations, e.g. @[PrintOption] and others; see package `nl.kute.printable.annotation.modify`
 * @see [asString]
 * @see [asStringExcluding]
 */
fun Any.asStringExcludingNames(vararg propNamesToExclude: String): String {
    try {
        val classPrintOption: PrintOption = this.annotationOfToString() ?: annotationOfClass() ?: PrintOption.defaultPrintOption
        val maxValLengthClass: Int
        with(classPrintOption) {
            maxValLengthClass = if (propMaxStringValueLength in 0 until maxValueLength) {
                propMaxStringValueLength
            } else {
                maxValueLength
            }
        }
        // try to get properties from cache first; not found? retrieve and add to cache
        val properties: Collection<KProperty1<Any, *>> = propertyCache[this::class]
            ?: (propertiesFromHierarchy().also { propertyCache[this::class] = it })
        return properties
            .filterNot { propNamesToExclude.contains(it.name) }
            .filterNot { it.hasAnnotationInHierarchy<PrintOmit>() }
            .joinToString(", ", prefix = "${this::class.simpleName ?: this::class.toString()}(", ")") {
                val propPrintOption: PrintOption = it.annotationOfPropertyInHierarchy() ?: classPrintOption
                val maxValLengthProp: Int
                with(propPrintOption) {
                    maxValLengthProp = if (propMaxStringValueLength >= 0) {
                        propMaxStringValueLength
                    } else {
                        maxValLengthClass
                    }
                }
                "${it.name}=${getPropValueString(it)?.take(maxValLengthProp) ?: propPrintOption.showNullAs}"
            }
    } catch (e: Exception) {
        // We have no logger in the Kute project; we don't want to interfere with the caller project's logger
        // So we can only return a value that describes what happened.
        return "ERROR: Exception ${e.javaClass.simpleName} occurred when building string value for object of class ${this.javaClass};$lineEnd${e.asString()}"
    } catch (t: Throwable) {
        return "FATAL ERROR: Throwable ${t.javaClass.simpleName} occurred when building string value for object of class ${this.javaClass};$lineEnd${t.asString()}"
    }
}

/** @return
 *  * for [Array]s: [Array.contentDeepToString]
 *  * otherwise: the [toString] value of the property, modified if needed by annotations @[PrintOmit],
 *  @[PrintPatternReplace], @[PrintMask], @[PrintHash]
 */
private fun Any.getPropValueString(prop: KProperty1<Any, *>): String? {
    val value: Any? = prop.get(this)
    if (value is Array<*>)
        @Suppress("UNCHECKED_CAST")
        return (prop.get(this) as Array<out Any>).contentDeepToString()
    else {
        var strValue = value?.toString()
        if (prop.hasAnnotationInHierarchy<PrintOmit>()) {
            return ""
        }
        if (prop.hasAnnotationInHierarchy<PrintPatternReplace>()) {
            strValue = replacePattern(this, prop)!!
        }
        if (prop.hasAnnotationInHierarchy<PrintMask>()) {
            strValue = mask(this, prop)
        }
        if (prop.hasAnnotationInHierarchy<PrintHash>()) {
            val digestMethod = prop.annotationOfPropertyInHierarchy<PrintHash>()?.digestMethod ?: defaultDigestMethod
            strValue = hashString(strValue, digestMethod)
        }
        return strValue
    }

}

private fun Any.propertiesFromHierarchy(): Collection<KProperty1<Any, *>> {
    @Suppress("UNCHECKED_CAST")
    val classHierarchy = getClassHierarchy(this::class as KClass<Any>)
    val linkedHashSet: LinkedHashSet<KProperty1<Any, *>> = linkedSetOf()
    return linkedHashSet.also { set ->
        set.addAll(
            classHierarchy
                .map { kClass -> kClass.memberProperties }.flatten()
                .onEach { prop ->
                    try {
                        prop.isAccessible = true // to access private or protected ones
                    } catch (e: Exception) {
                        // ignore if not allowed to make accessible
                    }
                }
                .filter { it.isAccessible }
                .reversed().distinctBy { prop -> prop.name } // with overrides: only get the subclass properties
                .reversed() // reverse again to normal order
        )
    }
}

@Suppress("UNCHECKED_CAST")
private fun Any.getClassHierarchy(theClass: KClass<Any> = this::class as KClass<Any>): Set<KClass<Any>> {
    val kClasses: MutableSet<KClass<Any>> = linkedSetOf()
    theClass.superclasses.filterNot { it == Any::class }.forEach {
        // add super types first
        kClasses.addAll(getClassHierarchy(it as KClass<Any>))
    }
    // ad self; so additional subclass properties are appended at the end
    kClasses.add(theClass)
    return kClasses
}

private val propertyCache: MutableMap<KClass<*>, Collection<KProperty1<Any, *>>> = ConcurrentHashMap()
const val maxValueLength: Int = 500
