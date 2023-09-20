package nl.kute.asstring.property.meta

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.core.isBaseType
import nl.kute.asstring.core.isCharSequenceType
import nl.kute.asstring.core.isCollectionLikeType
import nl.kute.reflection.util.simplifyClassName
import java.util.Objects
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/** Metadata about a property */
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING) // Not using asString() here, it will cause StackOverflowError
internal open class PropertyMetaData(

    // There is no check if the property actually is a property of objectClass
    // If ever this class were made public, such a check would be recommended
    // (if so, take care of subclasses)

    final override val property: KProperty<*>,
    final override val objectClass: KClass<*>?, ) : PropertyMeta {

    final override val propertyName: String = property.name
    final override val returnType: KType = property.returnType

    override val objectClassName: String? by lazy(LazyThreadSafetyMode.NONE) { objectClass?.simplifyClassName() }
    override val isBaseType: Boolean by lazy(LazyThreadSafetyMode.NONE) { returnType.isBaseType() }
    override val isCollectionLike: Boolean by lazy(LazyThreadSafetyMode.NONE) { returnType.isCollectionLikeType() }
    override val isCharSequence: Boolean by lazy(LazyThreadSafetyMode.NONE) { returnType.isCharSequenceType() }

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
        if (this::class != other?.let { it::class }) return false

        other as PropertyMetaData

        if (propertyName != other.propertyName) return false
        if (returnType != other.returnType) return false
        if (objectClass != other.objectClass) return false

        return true
    }

    override fun hashCode(): Int =
        Objects.hash(objectClass, property)
}
