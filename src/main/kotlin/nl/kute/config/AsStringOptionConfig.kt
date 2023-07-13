package nl.kute.config

import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.defaultMaxStringValueLength
import nl.kute.core.annotation.option.defaultNullString
import nl.kute.core.annotation.option.initialDefaultMaxStringValueLength
import nl.kute.core.annotation.option.initialDefaultNullString
import nl.kute.core.property.clearPropertyAnnotationCache

/**
 * Sets the global default for the representation of `null` values.
 * > This operation will reset the property cache if necessary.
 * @see initialDefaultNullString
 * @see defaultNullString
 */
public fun setDefaultNullString(nullString: String = initialDefaultNullString) {
    if (nullString != defaultNullString) {
        defaultNullString = nullString
        applyDefaultOption()
    }
}

/**
 * Sets the global default for the maximum length of the output **per property**
 * > This operation will reset the property cache if necessary.
 * @see initialDefaultMaxStringValueLength
 * @see defaultMaxStringValueLength
 */
public fun setMaxPropertyStringLength(maxPropStringLength: Int = initialDefaultMaxStringValueLength) {
    if (maxPropStringLength != defaultMaxStringValueLength) {
        defaultMaxStringValueLength = maxPropStringLength
        applyDefaultOption()
    }
}

private fun applyDefaultOption() {
    AsStringOption.defaultAsStringOption = AsStringOption(defaultNullString, defaultMaxStringValueLength)
    // Clearing the cache is necessary because the property cache typically references the old and obsolete settings.
    //
    // Replacing the "old" defaultAsStringOption by the new one is non-atomic, so would require additional
    // synchronization on the underlying map, we don't want to do that. So better clear the cache completely
    // (setting the defaults is typically done when the application is initialized, so cache would typically
    // be empty (or almost empty) yet.
    clearPropertyAnnotationCache()
}

/**
 * Reset config options:
 *  * Reset [defaultNullString] to [initialDefaultNullString]
 *  * Reset [defaultMaxStringValueLength] to [initialDefaultMaxStringValueLength]
 *
 *  > This operation will reset the property cache if necessary.
 */
public fun restoreInitialDefaultAsStringOption() {
    setDefaultNullString(initialDefaultNullString)
    setMaxPropertyStringLength(initialDefaultMaxStringValueLength)
}
