package nl.kute.printable.annotation.option

import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/** Default value for how to represent `null` */
const val defaultNullString: String = "null"

/** Default value for the maximum length of the output **per property** */
const val defaultMaxStringValueLength: Int = 500

/**
 * The [PrintOption] annotation can be placed:
 *  * on classes
 *  * on the [toString] method of these classes
 *  * on properties of these classes
 * It allows specifying how property values are to be parsed in the [nl.kute.core.asString] return value.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class PrintOption(
    /** How to show nulls? Default is "`"null"`" (by [defaultNullString]), but you may opt for something else */
    val showNullAs: String = defaultNullString,
    /** The maximum String value length **per property**.
     * Default is 500 (by [defaultMaxStringValueLength]). 0 means: an empty String; negative values mean: [Int.MAX_VALUE], so effectively no maximum. */
    val propMaxStringValueLength: Int = defaultMaxStringValueLength
) {
    companion object DefaultOption {
        /** [PrintOption] to be used if no explicit [PrintOption] annotation is specified  */
        val defaultPrintOption =
            PrintOption(showNullAs = defaultNullString, propMaxStringValueLength = defaultMaxStringValueLength)
    }
}

fun PrintOption.applyOption(strVal: String?): String =
    strVal?.take(propMaxStringValueLength) ?: showNullAs
