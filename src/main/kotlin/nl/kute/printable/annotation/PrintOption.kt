package nl.kute.printable.annotation

import nl.kute.printable.Printable
import java.lang.annotation.Inherited
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.findAnnotation

const val printableDefaultNullString: String = "null"
const val printableDefaultMaxLength: Int = 500

/**
 * The [PrintOption] annotation can be placed on classes that implement [Printable],
 * or on properties of these classes.
 * It allows specifying how property values are to be parsed in the [Printable.asString] return value.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@MustBeDocumented
@Inherited
annotation class PrintOption(
    /** How to show nulls? Default is "`"null"`" (by [printableDefaultNullString]), but you may opt for something else */
    val showNullAs: String = printableDefaultNullString,
    /** The maximum length of the output. Default is 500 (by [printableDefaultMaxLength]). 0 means: an empty String; negative values mean: no maximum. */
    val maxLength: Int = printableDefaultMaxLength
)

fun KAnnotatedElement.printOption(): PrintOption? = this.findAnnotation()
