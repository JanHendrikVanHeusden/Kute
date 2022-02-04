package nl.kute.printable.annotation

import nl.kute.printable.Printable

/**
 * The [PrintOptions] annotation can be placed on properties of classes that implement [Printable],
 * to indicate that the property is included in the return value of [Printable.asString], but
 * with some optional preferences.
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class PrintOptions(
    /** How to show nulls? Default is `null`, but you may opt for something else */
    val showNullAs: String = "null",
    /** The maximum length of the output; 0 means: an empty String; negative values mean: no maximum. */
    val maxLength: Int = 500
)

