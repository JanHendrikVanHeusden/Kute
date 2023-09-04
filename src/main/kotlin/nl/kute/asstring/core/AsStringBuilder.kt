@file:JvmName("AsStringBuilder")

package nl.kute.asstring.core

import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.namedvalues.NameValue
import nl.kute.asstring.namedvalues.namedProp
import nl.kute.asstring.property.propertiesWithAsStringAffectingAnnotations
import nl.kute.asstring.weakreference.ObjectWeakReference
import nl.kute.reflection.util.declaringClass
import nl.kute.reflection.util.simplifyClassName
import kotlin.reflect.KProperty

/**
 * Builder for more fine-grained specification of what is included/excluded in the output of [nl.kute.asstring.core.asString],
 * like additional values, filtering out properties (by reference or by name), etc.
 *
 * An [AsStringBuilder] object is constructed by calling static method [asStringBuilder].
 *
 * Building it can be done explicitly by calling [AsStringBuilder.build], or implicitly by the first call to
 * [AsStringBuilder.asString].
 * * After being built, the [AsStringBuilder] is effectively immutable.
 * * Without any further settings (e.g. [exceptProperties], [withAlsoProperties], [alsoNamed] etc.),
 *   a call to `MyClass.`[AsStringBuilder.asString] produces the same result as `MyClass.`[nl.kute.asstring.core.asString]
 *
 * *Usage:*
 * * Recommended is to declare a property of type [AsStringBuilder] in your class.
 *  * Alternatively, an [AsStringBuilder] object can be constructed on the fly (typically in a [toString] method);
 *    this is less efficient though.
 * * On the [AsStringBuilder] object, call the `with*`- and `except*`-methods to specify what to include / exclude.
 *    * Exclusion can also be accomplished (in a more static way & different semantics) with
 *   the [nl.kute.asstring.annotation.modify.AsStringOmit] annotation
 * > E.g.
 * ```
 * class MyClass(val myProp1: Prop1Type, val myProp2: Prop2Type, val unimportantProp: Unimportant, myParam: ParamClass) {
 *   // using static method to construct it
 *   val asStringBuilder = asStringBuilder()
 *       .exceptProperties(::unimportantProp)
 *       .withAlsoNamed(NamedValue("myParam", myParam))
 *   // ... more code
 *  }
 * ```
 *
 * * An [AsStringBuilder] does not prevent garbage collection of the object it applies to (it uses weak reference).
 * * For best performance, the [AsStringBuilder] object (or the [AsStringProducer] object produced by the [build]
 *   method) may be stored as an instance variable within the class it applies to.
 *   This avoids the cost of building it over and over (like when it is part of a log-statement or of a
 *   `toString()` method).
 * * Properties of this class (and any subclasses) are excluded from rendering
 *   by [nl.kute.asstring.core.asString]
 * @param [obj] The object to create the [AsStringBuilder] for (and to ultimately produce the [obj].[asString]`()` for)
 */
public class AsStringBuilder private constructor(private var obj: Any?) : AsStringProducer() {

    private val objectReference: ObjectWeakReference<*> = ObjectWeakReference(obj)
    private val objJavaClass: Class<*>? = obj?.javaClass
    private val isMatchingProperty: (KProperty<*>) -> Boolean =
        { objJavaClass == null
                || it.declaringClass()?.java?.isAssignableFrom(objJavaClass) == true
        }

    private val classProperties: Set<KProperty<*>> by lazy {
        if (obj == null) {
            emptySet()
        } else {
            obj!!::class.propertiesWithAsStringAffectingAnnotations().keys
        }
    }

    private val classPropertyNames: Set<String> by lazy {
        classProperties.map { it.name }.toSet()
    }

    private val alsoNamed: MutableSet<NameValue<*>> by lazy { mutableSetOf() }
    private val alsoNamedAsTypedArray by lazy { alsoNamed.toTypedArray() }

    private val onlyProperties: MutableSet<KProperty<*>> by lazy { mutableSetOf() }
    private var isOnlyPropertiesSet: Boolean = false
    private val onlyPropertyNames: MutableSet<String> by lazy { mutableSetOf() }
    private var isOnlyPropertyNamesSet: Boolean = false

    private val exceptProperties: MutableSet<KProperty<*>> by lazy { mutableSetOf() }
    private val exceptPropertyNames: MutableSet<String> by lazy { mutableSetOf() }

    private val propertyNamesToExclude: MutableSet<String> by lazy { mutableSetOf() }

    private var isBuilt: Boolean = false

    /**
     * Allows adding additional related properties, e.g. member objects, delegates etc.
     * @param props the properties to be added to the output of [asString]
     */
    public fun withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(props.map { it.namedProp(objectReference.get()) })
        }
        return this
    }

    /**
     * Allows adding additional related [NameValue]s, that provide property-like named values.
     *
     * This allows to add arbitrary values to the [asString] output, e.g. unrelated properties,
     * calculated values, etc.
     * > For usage of these, see the subclasses of [NameValue]
     * @param nameValues The [NameValue]s to add to the output of [asString]
     * @see [nl.kute.asstring.namedvalues.NameValue]
     * @see [nl.kute.asstring.namedvalues.NamedSupplier]
     * @see [nl.kute.asstring.namedvalues.NamedProp]
     * @see [nl.kute.asstring.namedvalues.NamedValue]
     */
    public fun withAlsoNamed(vararg nameValues: NameValue<*>): AsStringBuilder {
        if (!isBuilt) {
            this.alsoNamed.addAll(nameValues)
        }
        return this
    }

    /**
     * Restricts the output to only the object's properties listed in [props].
     * > *NB:* [NameValue]s added by [withAlsoNamed] are not filtered out.
     * @param props The restrictive list of properties to be included in the output of [asString].
     */
    public fun withOnlyProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.onlyProperties.addAll(props.filter(isMatchingProperty))
            isOnlyPropertiesSet = true
        }
        return this
    }

    /**
     * Restricts the output to only the object's property names listed in [names].
     * > *NB:* [NameValue]s added by [withAlsoNamed] are not filtered out.
     * @param names The restrictive list of property names to be included in the output of [asString].
     */
    public fun withOnlyPropertyNames(vararg names: String): AsStringBuilder {
        if (!isBuilt) {
            this.onlyPropertyNames.addAll(names)
            isOnlyPropertyNamesSet = true
        }
        return this
    }

    /**
     * Restricts the output by excluding the object's properties listed in [props].
     * > *NB:* [NameValue]s added by [withAlsoNamed] are not excluded.
     * @param props The list of properties to be excluded in the output of [asString].
     */
    public fun exceptProperties(vararg props: KProperty<*>): AsStringBuilder {
        if (!isBuilt) {
            this.exceptProperties.addAll(props)
        }
        return this
    }

    /**
     * Restricts the output by excluding the object's property names listed in [names].
     * > *NB:* [NameValue]s added by [withAlsoNamed] are not excluded.
     * @param names The list of property names to be excluded in the output of [asString].
     */
    public fun exceptPropertyNames(vararg names: String): AsStringBuilder {
        if (!isBuilt) {
            this.exceptPropertyNames.addAll(names)
        }
        return this
    }

    private fun buildIt(): AsStringProducer {
        val propNamesToInclude: Set<String> =
            (if (isOnlyPropertiesSet) (onlyProperties.map { it.name }) else classPropertyNames)
                .asSequence()
                .filter { !isOnlyPropertyNamesSet || onlyPropertyNames.contains(it) }
                .filterNot { exceptProperties.map { prop -> prop.name }.contains(it) }
                .filterNot { exceptPropertyNames.contains(it) }
                .toSet()
        propertyNamesToExclude.addAll(classPropertyNames - propNamesToInclude)
        isBuilt = true
        // Nullify obj to make it eligible for garbage collection
        // NB: It must not be nullified prematurely (i.e., before being built), that might cause unexpected
        //     behaviour (could be garbage collected before or during the lazy initialization of collections)
        obj = null
        return this
    }

    /**
     * Build this [AsStringBuilder].
     *
     * When built:
     *  * the internal state of the [AsStringBuilder] is initialized
     *  * the [AsStringBuilder] is effectively immutable
     *  * the [obj] will be eligible for garbage collection
     */
    public fun build(): AsStringProducer {
        return if (isBuilt) this else buildIt()
    }

    /**
     * * Builds the [AsStringBuilder], insofar not already built
     * * Produces the [asString]-result.
     * @return The resulting [String] according to the specified options
     * @see [build]
     */
    override fun asString(): String {
        build()
        return objectReference.get().objectAsString(propertyNamesToExclude, *alsoNamedAsTypedArray)
    }

    override fun toString(): String =
        "${this::class.simplifyClassName()} -> ${objJavaClass?.name?.simplifyClassName() ?: "null"}" +
                " (built = $isBuilt; excluded = $propertyNamesToExclude; properties = ${classPropertyNames - propertyNamesToExclude};" +
                " alsoNamed = ${alsoNamed.map { it.name }})"

    public companion object {
        /**
         * Static method to construct an [AsStringBuilder] for the receiver object
         * @receiver The object to construct the [AsStringBuilder] for
         */
        @JvmStatic
        public fun Any?.asStringBuilder(): AsStringBuilder = AsStringBuilder(this)
    }

}
