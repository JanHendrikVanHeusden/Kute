package nl.kute.core.annotation.option

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/** Initial default value for how to represent `null` */
public const val initialDefaultNullString: String = "null"

/**
 * Current default value for how to represent `null`
 * @see nl.kute.config.setDefaultNullString
 */
public var defaultNullString: String = initialDefaultNullString
    @JvmSynthetic // avoid access from external Java code
    internal set

/**
 * Default value for the maximum length of the output **per property**
 * @see nl.kute.config.setMaxPropertyStringLength
 */
public const val initialDefaultMaxStringValueLength: Int = 500

/** Current default value for the maximum length of the output **per property** */
public var defaultMaxStringValueLength: Int = initialDefaultMaxStringValueLength
    @JvmSynthetic // avoid access from external Java code
    internal set

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
    val propMaxStringValueLength: Int = initialDefaultMaxStringValueLength
) {
    public companion object DefaultOption {
        /** [AsStringOption] to be used if no explicit [AsStringOption] annotation is specified  */
        public var defaultAsStringOption: AsStringOption = AsStringOption(defaultNullString, defaultMaxStringValueLength)
            @JvmSynthetic // avoid access from external Java code
            internal set
    }
}

@JvmSynthetic // avoid access from external Java code
internal fun AsStringOption.applyOption(strVal: String?): String =
    strVal?.take(propMaxStringValueLength) ?: showNullAs

