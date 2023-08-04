package nl.kute.core.property

import nl.kute.core.AsStringObjectCategory
import nl.kute.core.AsStringObjectCategory.ARRAY
import nl.kute.core.AsStringObjectCategory.BASE
import nl.kute.core.AsStringObjectCategory.COLLECTION
import nl.kute.core.AsStringObjectCategory.MAP
import nl.kute.core.AsStringObjectCategory.PRIMITIVE_ARRAY
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.reflection.simplifyClassName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName

// TODO: kdoc
public interface PropertyInformation {
    public val objectClassName: String?
    public val jvmClassName: String?
    public val propertyName: String
    public val returnType: KType
    public val isNull: Boolean
    public val isBaseType: Boolean
    public val isCollectionLike: Boolean
    public val isCharSequence: Boolean
    public val stringValueLength: Int?

    public override fun toString(): String
}

private val collectionLikeCategories: Array<AsStringObjectCategory> =
    arrayOf(COLLECTION, ARRAY, MAP, PRIMITIVE_ARRAY)

// TODO: kdoc
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING) // Not using asString() here, it will cause StackOverflowError
public class PropertyValueInfo(
    @JvmSynthetic // avoid access from external Java code
    internal val objectClass: KClass<*>?,
    @JvmSynthetic // avoid access from external Java code
    internal val property: KProperty<*>,
    propertyValue: Any?,
    override val stringValueLength: Int?): PropertyInformation {

    override val objectClassName: String? by lazy { objectClass?.simplifyClassName() }
    override val jvmClassName: String? = objectClass?.jvmName
    override val propertyName: String = property.name
    override val returnType: KType = property.returnType
    override val isCharSequence: Boolean = propertyValue is CharSequence
    override val isNull: Boolean = propertyValue == null
    private val propertyValueCategory: AsStringObjectCategory? =
        if (propertyValue == null) null else AsStringObjectCategory.resolveObjectCategory(propertyValue)
    override val isBaseType: Boolean = propertyValueCategory == BASE
    override val isCollectionLike: Boolean = propertyValueCategory in collectionLikeCategories

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

}