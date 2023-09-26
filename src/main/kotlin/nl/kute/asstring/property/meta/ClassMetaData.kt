package nl.kute.asstring.property.meta

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.reflection.util.simplifyClassName
import java.util.Objects
import kotlin.reflect.KClass

/** Metadata about a [KClass] */
@AsStringClassOption(toStringPreference = ToStringPreference.PREFER_TOSTRING) // Not using asString() here, it will cause StackOverflowError
internal open class ClassMetaData(final override val objectClass: KClass<*>) : ClassMeta {

    final override val objectClassName: String by lazy(LazyThreadSafetyMode.NONE) { objectClass.simplifyClassName() }
    final override val packageName: String by lazy(LazyThreadSafetyMode.NONE) { objectClass.java.packageName }

    // Not using asString() here, it will cause StackOverflowError
    override fun toString(): String {
        return buildString {
            append(this@ClassMetaData::class.simplifyClassName())
            append("(objectClassname='$objectClassName',")
            append(" packageName='$packageName')")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this::class != other?.let { it::class }) return false

        other as ClassMetaData
        return objectClass == other.objectClass
    }

    override fun hashCode(): Int =
        Objects.hash(objectClass)
}
