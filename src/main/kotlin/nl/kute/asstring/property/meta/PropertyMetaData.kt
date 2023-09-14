package nl.kute.asstring.property.meta

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.core.isBaseType
import nl.kute.asstring.core.isCharSequenceType
import nl.kute.asstring.core.isCollectionType
import nl.kute.reflection.util.simplifyClassName
import java.util.Objects
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/** Metadata about a property */
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING) // Not using asString() here, it will cause StackOverflowError
public open class PropertyMetaData(
    final override val property: KProperty<*>,
    final override val objectClass: KClass<*>?, ) : PropertyMeta {

    override val objectClassName: String? by lazy { objectClass?.simplifyClassName() }
    override val propertyName: String = property.name
    final override val returnType: KType = property.returnType
    override val isBaseType: Boolean = returnType.isBaseType()
    override val isCollectionLike: Boolean = returnType.isCollectionType()
    override val isCharSequence: Boolean = returnType.isCharSequenceType()

    // Not using asString() here, it will cause StackOverflowError
    override fun toString(): String {
        return buildString {
            append(this@PropertyMetaData::class.simplifyClassName())
            append("(")
            append("objectClassname='$objectClassName',")
            append(" propertyName='$propertyName',")
            append(" returnType=$returnType")
            append(" isCharSequence=$isCharSequence,")
            append(" isBaseType=$isBaseType,")
            append(" isCollectionLike=$isCollectionLike)")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PropertyMetaData

        if (propertyName != other.propertyName) return false
        if (returnType != other.returnType) return false
        if (objectClass != other.objectClass) return false
        if (isCharSequence != other.isCharSequence) return false
        if (isBaseType != other.isBaseType) return false
        if (isCollectionLike != other.isCollectionLike) return false

        return true
    }

    override fun hashCode(): Int =
        Objects.hash(objectClass, property)
}
