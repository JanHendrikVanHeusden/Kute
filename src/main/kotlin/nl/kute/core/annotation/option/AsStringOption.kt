package nl.kute.core.annotation.option

import nl.kute.config.initialDefaultAsStringOption
import nl.kute.config.initialDefaultIncludeIdentityHash
import nl.kute.config.initialDefaultMaxStringValueLength
import nl.kute.config.initialDefaultNullString
import nl.kute.core.property.resetPropertyAnnotationCache
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringOption] annotation can be placed:
 *  * on classes
 *  * on the [toString] method of these classes
 *  * on properties of these classes
 * It allows specifying how property values are to be parsed in the [nl.kute.core.asString] return value.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
public annotation class AsStringOption(
    /** How to show nulls? Default is "`"null"`" (by [initialDefaultNullString]), but you may opt for something else */
    val showNullAs: String = initialDefaultNullString,
    /** The maximum String value length **per property**.
     * Default is 500 (by [initialDefaultMaxStringValueLength]). 0 means: an empty String; negative values mean: [Int.MAX_VALUE], so effectively no maximum. */
    val propMaxStringValueLength: Int = initialDefaultMaxStringValueLength,
    val includeIdentityHash: Boolean = initialDefaultIncludeIdentityHash
) {
    public companion object DefaultOption {
        /**
         * [AsStringOption] to be used if no explicit [AsStringOption] annotation is specified.
         * > When changed, the property cache will be reset (cleared)
         */
        public var defaultAsStringOption: AsStringOption = initialDefaultAsStringOption
            @JvmSynthetic
            internal set(newDefault) {
                if (newDefault != field) {
                    field = newDefault
                    // Clearing the cache is necessary because the property cache typically references the old and obsolete settings.
                    resetPropertyAnnotationCache()
                }
            }
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun AsStringOption.applyOption(strVal: String?): String =
    strVal?.take(propMaxStringValueLength) ?: showNullAs
