package nl.kute.asstring.property.ranking.impl

import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.property.meta.PropertyValueMeta
import nl.kute.asstring.property.ranking.PropertyRanking

/**
 * Class to explicitly specify that properties need not be ordered
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
@AsStringClassOption(includeCompanion = false)
public class NoOpPropertyRanking private constructor() : PropertyRanking() {

    /** @return A constant value regardless of [propertyValueMeta] input;
     * so effectively, it will not contribute to ordering
     */
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int = 0

    public companion object {
        /** Singleton instance of [NoOpPropertyRanking] */
        @AsStringOmit
        public val instance: NoOpPropertyRanking = NoOpPropertyRanking()
    }
}
