package nl.kute.core.annotation.modify

import nl.kute.core.asString
import nl.kute.log.log
import nl.kute.util.ifNull
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
 * * [AsStringPatternReplace] is repeatable. If multiple annotations are present, they are applied in order of occurrence,
 *   with the subsequent replacements working on the result of the previous one.
 *    * If a property with [AsStringPatternReplace] is overridden, and the subclass property is also annotated with
 *      [AsStringPatternReplace], they are applied in order from super class to subclass.
 *
 * As always with regular expressions:
 *  * Usage with lengthy data may have a significant performance penalty!
 *     * Design your expressions properly; stay away from so-called catastrophic backtracking!
 *       > [https://regex101.com/r/iXSKTs/1/debugger](https://regex101.com/r/iXSKTs/1/debugger),
 *       > [https://www.regular-expressions.info/catastrophic.html](https://www.regular-expressions.info/catastrophic.html)
 *
 * @param pattern The regular expression to replace
 * @param replacement The replacement; back references are allowed.
 */
@Target(AnnotationTarget.PROPERTY)
@Repeatable
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
annotation class AsStringPatternReplace(
    /**
     * The expression to replace, either as literal (default) or as regular expression.
     *  * If [isRegexpPattern]:
     *    * Capturing groups are allowed
     *    * Invalid regular expression will result in an empty String
     */
    val pattern: String,
    /**
     * The replacement expression.
     *  * If [isRegexpPattern]:
     *    * Regex group capture is supported, Java style (`$1`, `$2` etc.)
     */
    val replacement: String,
    /**
     * * If `true`, [pattern] will be considered as a regular-expression pattern
     * * If `false`, [pattern] and [replacement] will be considered as literals using [String.replace];
     */
    val isRegexpPattern: Boolean,
)

internal fun AsStringPatternReplace?.replacePattern(strVal: String?): String? =
    if (this == null) strVal else {
        try {
            strVal?.replace(cachingRegexFactory[pattern]!!, replacement)
        } catch (e: Exception) {
            // The property's value is probably sensitive
            // So make sure not to use the value in the error message
            log("${e.javaClass.simpleName} occurred when replacing a value using pattern $pattern; exception: [${e.asString()}]")
            ""
        }
    }

private val cachingRegexFactory: Map<String, Regex> = object: HashMap<String, Regex>() {
    override fun get(key: String): Regex = super.get(key).ifNull {
        Regex(key).also { this[key] = it }
    }
}
