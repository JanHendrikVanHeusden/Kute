package nl.kute.asstring.property.ranking

import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.option.AsStringClassOption

/**
 * Class to explicitly specify that properties need not be ordered
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
@AsStringClassOption(includeCompanion = false)
public class NoOpPropertyRanking private constructor() : PropertyRanking() {

    /** @return A constant value regardless of [propertyValueMetaData] input;
     * so effectively, it will not contribute to ordering
     */
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int = 0

    public companion object {
        /** Singleton instance of [NoOpPropertyRanking] */
        @AsStringOmit
        public val instance: NoOpPropertyRanking = NoOpPropertyRanking()
    }
}
