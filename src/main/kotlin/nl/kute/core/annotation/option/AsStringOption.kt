package nl.kute.core.annotation.option

import nl.kute.config.initialAsStringOption
import nl.kute.config.initialMaxStringValueLength
import nl.kute.config.initialNullString
import nl.kute.config.notifyConfigChange
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
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringOption(
    /** How to show nulls? Default is "`"null"`" (by [initialNullString]), but you may opt for something else */
    val showNullAs: String = initialNullString,
    /** The maximum String value length **per property**.
     * Default is 500 (by [initialMaxStringValueLength]). 0 means: an empty String; negative values mean: [Int.MAX_VALUE], so effectively no maximum. */
    val propMaxStringValueLength: Int = initialMaxStringValueLength
) {

    /** Static holder for [defaultOption] */
    public companion object DefaultOption {
        /**
         * [AsStringOption] to be used if no explicit [AsStringOption] annotation is specified.
         * > When changed, the property cache will be reset (cleared).
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
