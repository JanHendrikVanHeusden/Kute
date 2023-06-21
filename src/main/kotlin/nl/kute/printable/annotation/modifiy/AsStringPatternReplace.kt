package nl.kute.printable.annotation.modifiy

import nl.kute.core.asString
import nl.kute.log.log
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringPatternReplace] annotation can be placed on properties to indicate that the property is included
 * in the return value of [nl.kute.core.asString], but with its value replaced.
 * * Typical usage is to keep value parts with sensitive or personally identifiable out of logging etc.;
 *   e.g.
 *      * you may want to hide parts of phone numbers, but such that the leading and trailing digits are kept;
 *      * in IBAN European bank numbers you may want to keep the country and bank identifiers, but hide the personal part
 *      * in a URL, you may for instance keep the URL data but leave out query parameters (or the opposite)
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 *
 * As always with regular expressions:
 *  * Usage with lengthy data may have a significant performance penalty!
 *     * Design your expressions properly; stay away from so-called catastrophic backtracking!
 *       > [https://regex101.com/r/iXSKTs/1/debugger](https://regex101.com/r/iXSKTs/1/debugger),
 *       > [https://www.regular-expressions.info/catastrophic.html](https://www.regular-expressions.info/catastrophic.html)
 * @param pattern The regular expression to replace
 * @param replacement The replacement; back references are allowed.
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class AsStringPatternReplace(
    /**
     * The regular expression pattern; groups are allowed.
     * * Invalid regular expression will result in an empty String
     */
    val pattern: String,
    /** The replacement expression; supports regex group capture in Java style (`$1`, `$2` etc.) */
    val replacement: String
)

fun AsStringPatternReplace?.replacePattern(strVal: String?): String? =
    if (this == null) strVal else {
        try {
            // caching of Regex by pattern would be nice here, to avoid compiling same patterns over and over.
            strVal?.replace(Regex(pattern), replacement)
        } catch (e: Exception) {
            // The property's value is probably sensitive, so make sure not to use the value in the error message
            log("${e.javaClass.simpleName} occurred when replacing a value using pattern $pattern; exception: [${e.asString()}]")
            ""
        }
    }
