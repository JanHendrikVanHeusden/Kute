package nl.kute.printable.annotation

import nl.kute.printable.Printable
import nl.kute.printable.annotation.PrintOption.Defaults.defaultMaxLength
import nl.kute.printable.annotation.PrintOption.Defaults.defaultNullString
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [PrintOption] annotation can be placed on classes that implement [Printable]:
 *  * at class level
 *  * or on properties of these classes.
 * It allows specifying how property values are to be parsed in the [Printable.asString] return value.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class PrintOption(
    /** How to show nulls? Default is "`"null"`" (by [defaultNullString]), but you may opt for something else */
    val showNullAs: String = defaultNullString,
    /** The maximum length of the output **per property**. Default is 500 (by [defaultMaxLength]). 0 means: an empty String; negative values mean: [Int.MAX_VALUE], so effectively no maximum. */
    val maxLength: Int = defaultMaxLength
) {
    companion object Defaults {
        /** Default value for how to represent `null` */
        const val defaultNullString: String = "null"

        /** Default value for the maximum length of the output **per property** */
        const val defaultMaxLength: Int = 500
    }
}

