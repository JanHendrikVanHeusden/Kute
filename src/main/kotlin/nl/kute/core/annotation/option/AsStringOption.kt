package nl.kute.core.annotation.option

import nl.kute.core.property.clearPropertyAnnotationCache
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/** Default value for how to represent `null` */
const val initialDefaultNullString: String = "null"

/** Current value for how to represent `null` */
var defaultNullString: String = initialDefaultNullString
    private set

/** Default value for the maximum length of the output **per property** */
const val initialDefaultMaxStringValueLength: Int = 500

/** Current value for the maximum length of the output **per property** */
internal var defaultMaxStringValueLength: Int = initialDefaultMaxStringValueLength
    private set

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
annotation class AsStringOption(
    /** How to show nulls? Default is "`"null"`" (by [initialDefaultNullString]), but you may opt for something else */
    val showNullAs: String = initialDefaultNullString,
    /** The maximum String value length **per property**.
     * Default is 500 (by [initialDefaultMaxStringValueLength]). 0 means: an empty String; negative values mean: [Int.MAX_VALUE], so effectively no maximum. */
    val propMaxStringValueLength: Int = initialDefaultMaxStringValueLength
) {
    companion object DefaultOption {
        /** [AsStringOption] to be used if no explicit [AsStringOption] annotation is specified  */
        var defaultAsStringOption = AsStringOption(defaultNullString, defaultMaxStringValueLength)
            internal set
    }
}

internal fun AsStringOption.applyOption(strVal: String?): String =
    strVal?.take(propMaxStringValueLength) ?: showNullAs

class DefaultOption {

    fun restoreInitialDefault() {
        setNullString(initialDefaultNullString)
        setMaxPropertyStringLength(initialDefaultMaxStringValueLength)
    }

    fun setNullString(nullString: String = initialDefaultNullString): DefaultOption {
        if (nullString != defaultNullString) {
            defaultNullString = nullString
            setNewDefaultOption()
        }
        return this
    }
    fun setMaxPropertyStringLength(maxPropStringLength: Int = initialDefaultMaxStringValueLength): DefaultOption {
        if (maxPropStringLength != defaultMaxStringValueLength) {
            defaultMaxStringValueLength = maxPropStringLength
            setNewDefaultOption()
        }
        return this
    }

    private fun setNewDefaultOption() {
        AsStringOption.defaultAsStringOption = AsStringOption(defaultNullString, defaultMaxStringValueLength)
        // Clearing the cahce is necessary because the property cache typically references the old value.
        //
        // Replacing the "old" defaultAsStringOption by the new one is not atomic so would require synchronization
        // on the underlying map, we don't want to do that. So better clear the cache completely
        // (setting the defaults is typically done when the application is initialized, so cache would typically
        // be empty (or almost empty) yet.
        clearPropertyAnnotationCache()
    }
}
