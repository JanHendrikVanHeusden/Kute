package nl.kute.asstring.property.meta

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.core.AsStringObjectCategory
import nl.kute.asstring.core.AsStringObjectCategory.ARRAY
import nl.kute.asstring.core.AsStringObjectCategory.BASE
import nl.kute.asstring.core.AsStringObjectCategory.COLLECTION
import nl.kute.asstring.core.AsStringObjectCategory.MAP
import nl.kute.asstring.core.AsStringObjectCategory.PRIMITIVE_ARRAY
import nl.kute.asstring.core.isBaseType
import nl.kute.asstring.core.isCharSequenceType
import nl.kute.asstring.core.isCollectionType
import nl.kute.reflection.util.simplifyClassName
import java.util.Objects
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/** Metadata about a property and the property's value */
public interface PropertyValueMeta: PropertyMeta {

    /** The value of the actual instance of this property */
    public val stringValueLength: Int?
    /** Is the value of the actual instance of this property `null`? */
    public val isNull: Boolean

    override fun toString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

/**
 * Metadata about a property and the property's value
 * @param property The property this [PropertyValueMeta] is about
 * @param objectClass the [KClass] that comprises the property
 * @param propertyValue The value of the actual instance of this property
 * @param stringValueLength The length of the [String]-representation of the actual instance of this property
 */
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING) // Not using asString() here, it will cause StackOverflowError
internal class PropertyValueMetaData(
    property: KProperty<*>,
    objectClass: KClass<*>?,
    propertyValue: Any?,
    override val stringValueLength: Int?,
): PropertyMetaData(property, objectClass), PropertyValueMeta {

    @Suppress("unused")
    constructor(propertyMeta: PropertyMeta, propertyValue: Any?, stringValueLength: Int?):
        this(propertyMeta.property, propertyMeta.objectClass, propertyValue, stringValueLength)

    override val isCharSequence: Boolean =
        propertyValue is CharSequence || propertyValue == null && returnType.isCharSequenceType()

    override val isNull: Boolean = propertyValue == null

    internal val propertyValueCategory: AsStringObjectCategory? =
        if (propertyValue == null) null else AsStringObjectCategory.resolveObjectCategory(propertyValue)

    override val isBaseType: Boolean =
        propertyValueCategory == BASE || propertyValue == null && returnType.isBaseType()

    override val isCollectionLike: Boolean =
        propertyValueCategory in collectionLikeCategories || propertyValue == null && returnType.isCollectionType()

    // Not using asString() here, it will cause StackOverflowError
    override fun toString(): String {
        return buildString {
            append(this@PropertyValueMetaData::class.simplifyClassName())
            append("(")
            append("objectClassname='$objectClassName',")
            append(" stringValueLength=$stringValueLength,")
            append(" propertyName='$propertyName',")
            append(" returnType=$returnType")
            append(" isCharSequence=$isCharSequence,")
            append(" isNull=$isNull,")
            append(" propertyValueCategory=$propertyValueCategory,")
            append(" isBaseType=$isBaseType,")
            append(" isCollectionLike=$isCollectionLike)")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PropertyValueMetaData

        if (propertyName != other.propertyName) return false
        if (returnType != other.returnType) return false
        if (objectClass != other.objectClass) return false
        if (propertyValueCategory != other.propertyValueCategory) return false
        if (stringValueLength != other.stringValueLength) return false
        if (isCharSequence != other.isCharSequence) return false
        if (isNull != other.isNull) return false
        if (isBaseType != other.isBaseType) return false
        if (isCollectionLike != other.isCollectionLike) return false

        return true
    }

    override fun hashCode(): Int =
        Objects.hash(stringValueLength, objectClass, property, isNull)

    private companion object {
        private val collectionLikeCategories: Array<AsStringObjectCategory> =
            arrayOf(COLLECTION, ARRAY, MAP, PRIMITIVE_ARRAY)
    }
}
