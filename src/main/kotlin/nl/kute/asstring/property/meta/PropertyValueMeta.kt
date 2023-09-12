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
import kotlin.reflect.KType

private val collectionLikeCategories: Array<AsStringObjectCategory> =
    arrayOf(COLLECTION, ARRAY, MAP, PRIMITIVE_ARRAY)

/**
 * Metadata about a property and the property's value
 * >
 */
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING) // Not using asString() here, it will cause StackOverflowError
public class PropertyValueMeta(
    propertyValue: Any?,
    @JvmSynthetic // avoid access from external Java code
    internal val objectClass: KClass<*>?,
    @JvmSynthetic // avoid access from external Java code
    internal val property: KProperty<*>,
    override val stringValueLength: Int?): PropertyValueMetaData {

    override val objectClassName: String? by lazy { objectClass?.simplifyClassName() }
    override val propertyName: String = property.name
    override val returnType: KType = property.returnType

    override val isCharSequence: Boolean =
        propertyValue is CharSequence || propertyValue == null && returnType.isCharSequenceType()

    override val isNull: Boolean = propertyValue == null

    private val propertyValueCategory: AsStringObjectCategory? =
        if (propertyValue == null) null else AsStringObjectCategory.resolveObjectCategory(propertyValue)

    override val isBaseType: Boolean =
        propertyValueCategory == BASE || propertyValue == null && returnType.isBaseType()

    override val isCollectionLike: Boolean =
        propertyValueCategory in collectionLikeCategories || propertyValue == null && returnType.isCollectionType()

    // Not using asString() here, it will cause StackOverflowError
    override fun toString(): String {
        return buildString {
            append(this::class.simplifyClassName())
            append("(stringValueLength=$stringValueLength,")
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

        other as PropertyValueMeta

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

}

