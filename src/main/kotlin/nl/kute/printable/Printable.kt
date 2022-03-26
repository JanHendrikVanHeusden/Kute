package nl.kute.printable

import nl.kute.printable.annotation.PrintOption
import nl.kute.printable.annotation.PrintOption.Defaults.defaultMaxLength
import nl.kute.printable.annotation.PrintOption.Defaults.defaultNullString
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.isAccessible

// TODO Refactor for use with annotations to specify which properties (not) to include in asString
/**
 * Interface with easy-to-use methods for [String] representation; typically for data-centric classes.
 *
 * Intended for Kotlin classes only; not for Java classes (you may try; no guarantees).
 * It should work however for Kotlin classes that extend Java classes.
 *
 * Implementing classes are required to override the [toString] method.
 * The typical implementation will call [asString], simply like this:
 *
 * ```override fun toString() = asString()```
 */
interface Printable {

    /**
     * @param propsToExclude properties that you don't want to be included in the result.
     *
     *        E.g. override fun toString() = asString(::myExcludedProp1, ::myExcludedProp2)
     * @see [asStringExcludingNames]
     */
    fun asString(vararg propsToExclude: KProperty<*>): String {
        return asStringExcludingNames(*(propsToExclude.map { it.name }.toTypedArray()))
    }

    /**
     * Mimics the format of Kotlin data class's [toString] method.
     * * Super-class properties are included
     * * Private properties are included
     * * String value of individual properties is capped at 500
     *
     * This method allows you to exclude private properties by name.
     * E.g. use it when not calling from inside the class:
     *
     *        someObjectWithPrivateProps.asStringExcludingNames("myExcludedPrivateProp1", "myExcludedProp2")
     *
     * @param propNamesToExclude property names that you don't want to be included in the result; case-sensitive.
     * @return the requested string
     */
    fun asStringExcludingNames(vararg propNamesToExclude: String): String {
        // try to get properties from cache first; not found? retrieve and add to cache
        @Suppress("UNCHECKED_CAST")
        val properties: Collection<KProperty1<Any, *>> = propertyCache[this::class as KClass<Any>]
            ?: (propertiesFromHierarchy().also { propertyCache[this::class as KClass<Any>] = it })
        return properties
            .filterNot { propNamesToExclude.contains(it.name) }
            .joinToString(", ", prefix = "${this::class.simpleName ?: this::class.toString()}(", ")") {
                "${it.name}=${getPropValue(it).take(maxValueLength)}"
            }
    }

    // To force override of toString()
    @PrintOption(maxLength = defaultMaxLength, showNullAs = defaultNullString)
    override fun toString(): String

    /** @return for [Array]s: [Array.contentDeepToString]; otherwise: [toString] of the property */
    private fun getPropValue(prop: KProperty1<Any, *>): String {
        val value = prop.get(this) ?: return "null"
        return if (value is Array<*>)
            @Suppress("UNCHECKED_CAST")
            (prop.get(this) as Array<out Any>).contentDeepToString()
        else
            value.toString()
    }

    private fun propertiesFromHierarchy(): Collection<KProperty1<Any, *>> {
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
    private fun getClassHierarchy(theClass: KClass<Any> = this::class as KClass<Any>): Set<KClass<Any>> {
        val kClasses: MutableSet<KClass<Any>> = linkedSetOf()
        theClass.superclasses.filterNot { it == Any::class }.forEach {
            // add super types first
            kClasses.addAll(getClassHierarchy(it as KClass<Any>))
        }
        // ad self; so additional subclass properties are appended at the end
        kClasses.add(theClass)
        return kClasses
    }

    private companion object {
        private val propertyCache: MutableMap<KClass<Any>, Collection<KProperty1<Any, *>>> = ConcurrentHashMap()
        const val maxValueLength: Int = 500
    }
}
