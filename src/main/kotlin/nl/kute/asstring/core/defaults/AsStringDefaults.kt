@file:JvmName("AsStringDefaults")

package nl.kute.asstring.core.defaults

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.property.ranking.PropertyRankable
import kotlin.reflect.KClass

/** Initial default value for limiting the number of elements of a collection to be parsed */
public const val initialElementsLimit: Int = 50

/** Initial default value for how to represent `null` in the [nl.kute.asstring.core.asString] output */
public const val initialShowNullAs: String = "null"

/** Initial default value for prefix/postfix property value Strings in the [nl.kute.asstring.core.asString] output */
public val initialSurroundPropValue: PropertyValueSurrounder = PropertyValueSurrounder.NONE

/** Initial default value for the maximum length **per property** in the [nl.kute.asstring.core.asString] output */
public const val initialMaxStringValueLength: Int = 500

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.asstring.core.asString] output */
public const val initialIncludeIdentityHash: Boolean = false

/** Initial default value for the choice whether the properties should be pre-sorted alphabetically in  [nl.kute.asstring.core.asString] output */
public const val initialSortNamesAlphabetic: Boolean = false

/** Initial default value for the choice whether to include the object's companion (if any) in the [nl.kute.asstring.core.asString] output */
public const val initialIncludeCompanion: Boolean = false

/** Initial default value for [PropertyRankable] property ranking classes to be used for sorting properties in [nl.kute.asstring.core.asString] output */
public val initialPropertySorters: Array<KClass<PropertyRankable<*>>> = arrayOf()

/** Initial default value for the choice whether the object's identity hash should be included in the [nl.kute.asstring.core.asString] output */
public val initialToStringPreference: ToStringPreference = ToStringPreference.USE_ASSTRING

/** Initial default options for the output of [nl.kute.asstring.core.asString] */
internal val initialAsStringOption: AsStringOption = AsStringOption()

/** Initial default options for the output of [nl.kute.asstring.core.asString] */
internal val initialAsStringClassOption: AsStringClassOption = AsStringClassOption()

/** Convenience method to retrieve [AsStringOption.defaultOption]'s [AsStringOption.showNullAs] */
internal val defaultNullString: String
    get() = AsStringOption.defaultOption.showNullAs

/** Convenience method to retrieve [AsStringOption.defaultOption]'s [AsStringOption.propMaxStringValueLength] */
internal val defaultMaxStringValueLength: Int
    get() = AsStringOption.defaultOption.propMaxStringValueLength