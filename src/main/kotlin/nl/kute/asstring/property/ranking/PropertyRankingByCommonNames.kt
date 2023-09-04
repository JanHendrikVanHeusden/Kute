package nl.kute.asstring.property.ranking

import nl.kute.asstring.core.isBaseType
import java.util.UUID

/**
 * Provides ranking for ordering properties in [nl.kute.asstring.core.asString] output, based on common property names and suffixes,
 * combined with value lengths.
 * > *Common names / suffixes include, for instance, `id`, `...Id`, `code`, `type`, `uuid`, `json`, `xml`, `text`, `desc`, etc.*
 *
 * It is intended to demonstrate usage of naming conventions (together with lengths and types) as a means of ranking
 * properties and thus their ordering.
 * * Names and suffixes usually case-insensitive
 * * For sure it won't fit **your** naming conventions. So the implementation is given "as is"!
 *    * You are encouraged to roll your own ranking, if you feel like it!
 * * Of course it can be combined it with other provided implementations, e.g. [PropertyRankingByLength] or [PropertyRankingByLength].
 * @see [nl.kute.asstring.annotation.option.AsStringClassOption.propertySorters]
 */
public open class PropertyRankingByCommonNames private constructor(): PropertyRanking() {

    /** @return A numeric rank based on [PropertyValueMetaData.stringValueLength] and T-shirt sizes as of [ValueLengthRanking] */
    public override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int {
        val propName = propertyValueMetaData.propertyName
        val propNameLower = propertyValueMetaData.propertyName.lowercase()
        val propSizeRank = ValueLengthRanking.getRank(propertyValueMetaData.stringValueLength)
        val returnTypeClass = propertyValueMetaData.returnType.classifier
        return when {
            propNameLower == "id" -> 0
            propName.endsWith("Id") -> 5
            propNameLower.endsWith("_id") -> 5
            returnTypeClass?.isBaseType() == true && returnTypeClass != String::class  -> 15
            returnTypeClass == UUID::class || propNameLower.endsWith("uuid") -> 15
            propSizeRank <= ValueLengthRanking.M && propNameLower.contains("ident") -> 20
            propSizeRank <= ValueLengthRanking.M && propName.endsWith("Ref") -> 20
            propSizeRank <= ValueLengthRanking.M && propNameLower.endsWith("_ref") -> 20
            propSizeRank <= ValueLengthRanking.L && propNamesCategorizing.any { propNameLower.contains(it) } -> 30
            propSizeRank <= ValueLengthRanking.L && propNamesNumberSuffix.any { propNameLower.endsWith(it) } -> 30
            propSizeRank <= ValueLengthRanking.XL && propNamesLink.any { propNameLower.endsWith(it) } -> 40
            propSizeRank <= ValueLengthRanking.L && propNameLower.contains("sql") -> 50
            propSizeRank <= ValueLengthRanking.M -> 40
            propNameLower.contains("json") -> 60
            propNamesDocument.any { propNameLower.contains(it) } -> 80
            propNamesContentFormat.any { propNameLower.contains(it) } -> 80
            propNameLower.contains("xml") -> 80
            propNamesInformative.any { propNameLower.contains(it) } -> if (propSizeRank <= ValueLengthRanking.L) 50 else 100
            propSizeRank >= ValueLengthRanking.XL -> 150
            else -> 40
        }
    }

    public companion object {
        /** Singleton instance of [PropertyRankingByCommonNames] */
        @Suppress("unused") // will be called reflectively
        public val instance: PropertyRankingByCommonNames = PropertyRankingByCommonNames()

        // region ~ Extensions and keywords to distinguish categories of properties
        
        /** @suppress - no KDoc generated */
        public val propNamesCategorizing: Array<String> = arrayOf("code", "type", "category", "order", "kind")
        /** @suppress - no KDoc generated */
        public val propNamesNumberSuffix: Array<String> = arrayOf("num", "no", "nr", "number", "numer", "numero")
        /** @suppress - no KDoc generated */
        public val propNamesLink: Array<String> = arrayOf("url", "uri", "link")
        /** @suppress - no KDoc generated */
        public val propNamesInformative: Array<String> = arrayOf("text", "txt", "tekst", "desc", "info", "expl")
        /** @suppress - no KDoc generated */
        public val propNamesDocument: Array<String> = arrayOf("file", "report", "doc", "content")
        /** @suppress - no KDoc generated */
        public val propNamesContentFormat: Array<String> = arrayOf("xml", "csv", "yaml", "yml", "axon", "html", "htm", "protobuf")

        // endregion
    }
}

