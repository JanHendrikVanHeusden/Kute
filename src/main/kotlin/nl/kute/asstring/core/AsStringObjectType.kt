@file:JvmName("AsStringObjectType")

package nl.kute.asstring.core

import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount
import java.util.Date
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.typeOf

private val baseClasses = arrayOf(
    Number::class,
    CharSequence::class,
    Char::class,
    Temporal::class,
    TemporalAmount::class,
    Date::class,
    UByte::class,
    UShort::class,
    UInt::class,
    ULong::class
)

/**
 * Is the object's type considered a base type?
 *
 * Currently, the following types are considered base types:
 * * Primitive wrappers
 * * Elementary Java types like [String], [Char], [CharSequence], [Number],
 * [Date], [Temporal], [TemporalAmount] and their subclasses
 * * The Kotlin unsigned types ([UByte], [UShort], [UInt], [ULong])
 */
public fun Any?.isBaseType(): Boolean =
    this is Boolean
            || this is Number
            || this is CharSequence
            || this is Char
            || this is Temporal
            || this is TemporalAmount
            || this is Date
            || this is UByte
            || this is UShort
            || this is UInt
            || this is ULong

private fun KType.isOfClazz(classes: Array<KClass<*>>): Boolean =
    classes.any {
        isOfClazz(it)
    }

private fun KType.isOfClazz(clazz: KClass<*>): Boolean =
    this.classifier.let {
        it == clazz || (it is KClass<*> && clazz.isSuperclassOf(it))
    }

@JvmSynthetic // avoid access from external Java code
internal fun KType.isBaseType(): Boolean = isOfClazz(baseClasses)

@JvmSynthetic // avoid access from external Java code
internal fun Any?.isPrimitiveArray(): Boolean
// It's a pity the kotlin designers didn't create an interface or superclass for these arrays of primitives...
// We must simply list them all... NB:
//  * Java types boolean[], byte[] etc. map to kotlin's BooleanArray, ByteArray, etc.
//  * UByteArray, UShortArray, UIntArray, ULongArray are collections, these need no special handling
        = this is BooleanArray
        || this is ByteArray
        || this is CharArray
        || this is ShortArray
        || this is IntArray
        || this is LongArray
        || this is FloatArray
        || this is DoubleArray

private val collectionLikeClasses = arrayOf(
    Collection::class,
    Array::class,
    Map::class,
    IntArray::class,
    ByteArray::class,
    CharArray::class,
    LongArray::class,
    FloatArray::class,
    DoubleArray::class,
    ShortArray::class,
    BooleanArray::class
)

@JvmSynthetic // avoid access from external Java code
internal fun KType.isCollectionLikeType(): Boolean = isOfClazz(collectionLikeClasses) || this.isSubtypeOf(typeOf<Array<*>>())

@JvmSynthetic // avoid access from external Java code
internal fun KType.isCharSequenceType(): Boolean = isOfClazz(CharSequence::class)

@JvmSynthetic // avoid access from external Java code
internal fun Any.isLambdaProperty(): Boolean =
    // Check for lambda is not really the best check ever... but seems the best we have...
    // It only finds lambda that is declared as an *instance property*.
    //
    // Lambda declared as a *local variable* (e.g., in a method) can not be detected this way,
    // they cause downstream SyntheticClassException which then needs to be handled
    this::class.java.let {
        it.isSynthetic && it.toString().contains("\$\$Lambda\$")
    }

private val systemClassPackagePrefixes = listOf("java.", "kotlin.")

@JvmSynthetic // avoid access from external Java code
internal fun KClass<*>.isSystemClass() =
    this.java.packageName == "kotlin"
            || systemClassPackagePrefixes.any { this.java.packageName.startsWith(it) }