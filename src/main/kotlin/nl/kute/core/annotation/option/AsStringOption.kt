package nl.kute.core.annotation.option

import nl.kute.config.initialAsStringOption
import nl.kute.config.initialElementsLimit
import nl.kute.config.initialMaxStringValueLength
import nl.kute.config.initialNullString
import nl.kute.config.notifyConfigChange
import nl.kute.core.annotation.option.PropertyValueSurrounder.NONE
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringOption] annotation can be placed:
 *  * on classes
 *  * on the [toString] method of classes
 *  * on properties of classes
 *
 * Besides that, the [AsStringOption.defaultOption] is used as a default
 * when no explicit [AsStringOption] annotation is applied.
 *
 * It allows specifying how property values are to be parsed in the [nl.kute.core.asString] return value.
 *
 * @param showNullAs How to show nulls? Default is "`"null"`" (by [initialNullString]), but you may opt for something else
 * @param surroundPropValue Defines prefix and postfix of the property value String. Default = [NONE].
 * @param propMaxStringValueLength The maximum String value length **per property**.
 * * default is 500 (by [initialMaxStringValueLength])
 * * 0 results in an empty String;
 * * negative values mean: [Int.MAX_VALUE], so effectively no maximum.
 * @param elementsLimit limits the number of elements of collection like properties to be
 * represented in the [nl.kute.core.asString] return value. Default = 50 (by [initialElementsLimit])
 * > * Applies to repeating values, e.g. for [Collection]s, [Map]s, [Array]s
 * * **NB**: The String representation is also capped by [propMaxStringValueLength]
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringOption(
    val showNullAs: String = initialNullString,
    val surroundPropValue: PropertyValueSurrounder = NONE,
    val propMaxStringValueLength: Int = initialMaxStringValueLength,
    val elementsLimit: Int = initialElementsLimit
) {

    /** Static holder for [defaultOption] */
    public companion object DefaultOption {
        /**
         * [AsStringOption] to be used as default if no explicit [AsStringOption] annotation is specified.
         * > On change (see [nl.kute.config.AsStringConfig]), the property cache will be reset (cleared).
         */
        public var defaultOption: AsStringOption = initialAsStringOption
            @JvmSynthetic // avoid access from external Java code
            internal set(newDefault) {
                if (newDefault != field) {
                    field = newDefault
                    // not using Observable delegate here, old/new values are not needed, simple notification will do
                    AsStringOption::class.notifyConfigChange()
                }
            }
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun AsStringOption.applyOption(strVal: String?): String =
    strVal?.take(propMaxStringValueLength) ?: showNullAs
