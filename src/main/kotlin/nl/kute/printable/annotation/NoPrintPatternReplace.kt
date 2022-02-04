package nl.kute.printable.annotation

import nl.kute.printable.Printable

/**
 * The [NoPrintPatternReplace] annotation can be placed on properties of classes that implement [Printable],
 * to indicate that the property is included in the return value of [Printable.asString], but
 * with its value replaced.
 * * Typical usage is to keep value parts with sensitive or personally identifiable out of logging etc.;
 *   e.g.
 *      * you may want to hide parts of phone numbers, but such that the leading and trailing digits are kept
 *      * in IBAN European bank numbers you may want to keep the country and bank identifiers, but hide the personal part
 *      * in a URL, you may want to keep the URL data but leave out query parameters
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 *
 * As always with regular expressions:
 *  * Usage with lengthy data may have a significant performance penalty;
 *  * Design your expressions properly; stay away from so-called catastrophic backtracking
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class NoPrintPatternReplace(
    /**
     * The regular expression pattern; groups are allowed.
     * * Invalid regular expression will result in an empty String
     */
    val pattern: String,
    /**
     * The replacement expression; regex group replacements in Java style (`$1`, `$2` etc. are allowed)
     */
    val replacement: String
)