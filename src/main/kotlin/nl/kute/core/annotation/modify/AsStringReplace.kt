package nl.kute.core.annotation.modify

import nl.kute.core.asString
import nl.kute.log.log
import nl.kute.reflection.simplifyClassName
import nl.kute.util.ifNull
import java.lang.annotation.Inherited
import java.util.concurrent.ConcurrentHashMap
import kotlin.annotation.AnnotationRetention.RUNTIME

/**
 * The [AsStringReplace] annotation can be placed on properties to indicate that the property is included
 * in the return value of [nl.kute.core.asString], but with its value modified. This can be done either by means
 * of regular expression replacement, or by means of literal replacement.
 * * Typical usage is to keep value parts with sensitive or personally identifiable out of logging etc.;
 *   e.g.
 *     * in IBAN European bank numbers you may want to keep the country and bank identifiers, but hide the personal part
 *     * in a URL, you may for instance keep the URL data but leave out query parameters (or the opposite)
 * * This may limit exposure of such data, but on its own it must not be considered as a security feature.
 * * [AsStringReplace] is repeatable. If multiple annotations are present, they are applied in order of occurrence,
 *   with the subsequent replacements working on the result of the previous one.
 *     * If a property with [AsStringReplace] is overridden, and the subclass property is also annotated with
 *       [AsStringReplace], they are applied in order from super class to subclass.
 *
 * As always with regular expressions:
 *  * Design your expressions properly; stay away from so-called catastrophic backtracking!
 *    > [https://regex101.com/r/iXSKTs/1/debugger](https://regex101.com/r/iXSKTs/1/debugger),
 *    [https://www.regular-expressions.info/catastrophic.html](https://www.regular-expressions.info/catastrophic.html)
 *
 * @param pattern The expression to replace, either as regular expression (when [isRegexpPattern] is `true`; default),
 *   or as a literal (when [isRegexpPattern] is `false`). Each occurrence will be replaced by [replacement].
 *    * Invalid regular expression will result in an empty String being returned.
 * @param replacement The replacement; back references are allowed. Default is an empty string `""`
 *  * If [isRegexpPattern] is `true`, regex group capture is supported, Java style (`$1`, `$2` etc.)
 * @param isRegexpPattern
 *  * If `true` (default), the [pattern] will be considered a [Regex] pattern
 *  * If `false`, the [pattern] and [replacement] will be treated as a literals.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
@Repeatable
@Retention(RUNTIME)
@Inherited
@MustBeDocumented
public annotation class AsStringReplace(val pattern: String, val replacement: String = "", val isRegexpPattern: Boolean = true)

@JvmSynthetic // avoid access from external Java code
internal fun AsStringReplace?.replacePattern(strVal: String?): String? =
    if (this == null) strVal else {
        try {
            if (isRegexpPattern) strVal?.replace(cachingRegexFactory[pattern]!!, replacement)
            else strVal?.replace(pattern, replacement)
        } catch (e: Exception) {
            // The property's value is probably sensitive
            // So make sure not to use the value in the error message
            log("${e.javaClass.name.simplifyClassName()} occurred when replacing a value using pattern `$pattern`; and replacement `$replacement` exception: [${e.asString()}]")
            ""
        }
    }

@JvmSynthetic // avoid access from external Java code
internal val cachingRegexFactory: Map<String, Regex> = object: ConcurrentHashMap<String, Regex>() {
    override fun get(key: String): Regex = super.get(key).ifNull {
        Regex(key).also { this[key] = it }
    }
}
