@file:JvmName("AsStringTestHelper")

package nl.kute.asstring.core.test.helper

import nl.kute.test.helper.containsExactCharCount
import nl.kute.test.helper.containsExhaustiveInAnyOrder
import nl.kute.test.helper.trimFirstIf
import nl.kute.test.helper.trimLastIf
import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Condition

/**
 * @return AssertJ [Condition] to assert that the exact number of `=` characters in a String
 * is equal to [expectedCount].
 * > Typically usage is to assert the number of properties captured in a [nl.kute.asstring.core.asString] return value,
 * > by counting the `=` characters.
 */
internal fun equalSignCount(expectedCount: Int): Condition<String> =
    containsExactCharCount('=', expectedCount)

/**
 * Convenience assertion for [nl.kute.asstring.core.asString], which asserts that:
 * * the given input String starts with the given [className] (where trailing `(` is ignored) followed by `(`
 * * that the remainder of the input String consists exactly of the given [propertyStrings], in any order
 * * with as last String [withLastPropertyString] (where trailing `)` is ignored)
 * * followed by `)`
 * * with the intermittent `, ` separators and trailing `)` ignored
 * @return if the assertion succeeds, the resulting [AbstractStringAssert];
 * otherwise an [AssertionError] is thrown
 */
fun AbstractStringAssert<*>.isObjectAsString(className: String, vararg propertyStrings: String, withLastPropertyString: String = "") =
    this.containsExhaustiveInAnyOrder(
        *propertyStrings,
        withPrefix = "${className.trimLastIf('(')}(",
        withSuffix = "${withLastPropertyString.trimFirstIf(')')})",
        ignoreChars = ", "
    )
