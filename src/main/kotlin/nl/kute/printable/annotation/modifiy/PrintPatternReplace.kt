package nl.kute.printable.annotation.modifiy

import nl.kute.reflection.annotation.annotationOfPropertyInHierarchy
import nl.kute.reflection.getPropValue
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KProperty1

/**
 * The [PrintPatternReplace] annotation can be placed on properties to indicate that the property is included
 * in the return value of [nl.kute.core.asString], but with its value replaced.
 * * Typical usage is to keep value parts with sensitive or personally identifiable out of logging etc.;
 *   e.g.
 *      * you may want to hide parts of phone numbers, but such that the leading and trailing digits are kept
 *      * in IBAN European bank numbers you may want to keep the country and bank identifiers, but hide the personal part
 *      * in a URL, you may for instance keep the URL data but leave out query parameters (or the opposite)
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 *
 * As always with regular expressions:
 *  * Usage with lengthy data may have a significant performance penalty!
 *     * Design your expressions properly; stay away from so-called catastrophic backtracking!
 */
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
@Inherited
@Retention(RUNTIME)
annotation class PrintPatternReplace(
    /**
     * The regular expression pattern; groups are allowed.
     * * Invalid regular expression will result in an empty String
     */
    val pattern: String,
    /** The replacement expression; regex group replacements in Java style (`$1`, `$2` etc. are allowed) */
    val replacement: String

)

fun <T: Any, V: Any?>replacePattern(obj: T, prop: KProperty1<T, V>): String? {
    val strVal = (obj.getPropValue(prop) ?: return null).toString()
    with (prop.annotationOfPropertyInHierarchy<PrintPatternReplace>() ?: return strVal) {
        return try {
            strVal.replace(Regex(pattern), replacement)
        } catch (e: Exception) {
            // no logging framework present, so we only can use standard output
            // NB: the property value is not included in the log message, to avoid exposing a value that the caller intended to replace
            println("${e.javaClass.simpleName} occurred when replacing a value using pattern $pattern; exception message = [${e.message}]")
            ""
        }
    }
}