package nl.kute.asstring.property.ranking.impl

import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.property.meta.PropertyValueMeta
import nl.kute.asstring.property.ranking.PropertyRanking

/**
 * Provides ranking for ordering properties in [nl.kute.asstring.core.asString] output, based
 * on [PropertyValueMeta.returnType].
 * Intended mainly to keep known basic types with not too long `toString()`-representations ordered first
 * > E.g. [Number], [java.util.Date], [Char], [Boolean], [java.time.temporal.Temporal] etc.; see [nl.kute.asstring.core.isBaseType];<br>
 * > and collection-like types last
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
@AsStringClassOption(includeCompanion = false)
public open class PropertyRankingByType private constructor(): PropertyRanking() {
    /** @return * A low value if it's a base-type and not a [CharSequence]
     *          * a high value if it's a collection-like type
     *          * a medium value otherwise */
    public override fun getRank(propertyValueMeta: PropertyValueMeta): Int {
        return when {
            propertyValueMeta.isBaseType && !propertyValueMeta.isCharSequence -> 0
            propertyValueMeta.isCollectionLike -> 20
            else -> 10
        }
    }

    public companion object {
        /** Singleton instance of [PropertyRankingByType] */
        @Suppress("unused") // will be called reflectively
        @AsStringOmit
        public val instance: PropertyRankingByType = PropertyRankingByType()
    }
}