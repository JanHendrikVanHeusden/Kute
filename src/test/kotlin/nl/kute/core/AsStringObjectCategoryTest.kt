package nl.kute.core

import nl.kute.core.AsStringObjectCategory.CategoryResolver.resolveObjectCategory
import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.reflection.hasImplementedToString
import nl.kute.reflection.simplifyClassName
import nl.kute.testobjects.java.JavaClassToTest
import nl.kute.testobjects.java.JavaClassWithComposites
import nl.kute.testobjects.java.JavaClassWithPrimitives
import nl.kute.testobjects.java.advanced.JavaClassWithAnonymousClass
import nl.kute.testobjects.java.advanced.JavaClassWithHigherOrderFunction
import nl.kute.testobjects.java.advanced.JavaClassWithLambda
import nl.kute.testobjects.kotlin.advanced.CallableFactoryWithLambda
import nl.kute.testobjects.kotlin.advanced.KotlinClassWithAnonymousClass
import nl.kute.testobjects.kotlin.advanced.KotlinClassWithAnonymousClassFactory
import nl.kute.util.throwableAsString
import org.apache.commons.lang3.math.Fraction
import org.apache.commons.lang3.mutable.MutableByte
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.CharBuffer
import java.nio.FloatBuffer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.chrono.ChronoZonedDateTime
import java.time.chrono.HijrahDate
import java.time.chrono.JapaneseDate
import java.time.temporal.Temporal
import java.util.Calendar
import java.util.Date
import java.util.EnumSet
import java.util.GregorianCalendar
import java.util.Locale
import java.util.PriorityQueue
import java.util.TreeMap
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.DoubleAdder

internal class AsStringObjectCategoryTest {

    @Test
    fun `AsStringObjectCategory should handle numeric object as BASE`() {
        // arbitrary choice of Number subclasses
        listOf<Number>(
            1,
            2.34,
            AtomicLong(14L),
            DoubleAdder(),
            BigDecimal("123.456"),
            Fraction.TWO_THIRDS,
            MutableByte(238),
            Byte.MAX_VALUE,
            JavaClassWithPrimitives.BYTE,
            JavaClassWithPrimitives.SHORT,
            JavaClassWithPrimitives.INT,
            JavaClassWithPrimitives.LONG,
            JavaClassWithPrimitives.FLOAT,
            JavaClassWithPrimitives.DOUBLE
        ).forEach {
            it.assertBaseObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle textual object as BASE`() {
        val charBufContent = "My chars are added to the CharBuffer"
        val charBuffer = CharBuffer.allocate(40).append(charBufContent)

        // arbitrary choice of CharSequence subclasses
        listOf<CharSequence>(
            "a string",
            StringBuffer("I am buffered"),
            charBuffer
        ).forEach {
            it.assertBaseObject()
        }
        // toString() of CharBuffer is a bit weird, but anyway we adhere to it
        // NB: the content should NOT be read, that would move the internal pointer
        assertThat(charBuffer.asString()).doesNotContain(charBufContent)
    }

    @Test
    fun `AsStringObjectCategory should handle chars as BASE`() {
        ("\u2764\u261dabc" + JavaClassWithPrimitives.CHAR).forEach {
            it.assertBaseObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle date as BASE`() {
        class DateSubClass: Date() {
            override fun toString(): String = "this is a weird toString() of " + super.toString()
        }
        listOf<Date>(
            java.sql.Date.from(Date().toInstant()),
            Date(),
            DateSubClass()
        ).forEach {
            it.assertBaseObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle Temporal as BASE`() {
        // arbitrary choice of Temporal subclasses
        listOf<Temporal>(
            Date().toInstant(),
            LocalDate.now(),
            HijrahDate.now(),
            OffsetDateTime.now(),
            ZonedDateTime.now(),
            ChronoZonedDateTime.from(ZonedDateTime.now()),
            JapaneseDate.now()
        ).forEach {
            it.assertBaseObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle Boolean as BASE`() {
        listOf(
            JavaClassWithPrimitives.BOOL,
            true,
            false,
            UByte.MIN_VALUE,
        ).forEach {
            it.assertBaseObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle Calendar as BASE`() {
        listOf<Calendar>(
            Calendar.getInstance(),
            GregorianCalendar()
        ).forEach {
            it.assertBaseObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle array as ARRAY`() {
        listOf<Array<*>>(
            arrayOf(12, 15, 28),
            arrayOfNulls<Any>(20),
            Array(3) { i -> (i * 1).toBigDecimal() },
            JavaClassWithComposites.ARRAY_OF_BYTE_OBJECTS,
            JavaClassWithComposites.ARRAY_OF_CHARACTER
        ).forEach {
            it.assertArray()
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `AsStringObjectCategory should handle collection as COLLECTION`() {
        listOf<Collection<*>>(
            listOf(12, 15, 28),
            mutableListOf(Any()),
            PriorityQueue(listOf("first", "second", "third")),
            EnumSet.allOf(AsStringObjectCategory::class.java),
            ubyteArrayOf(89u, 123u, 254u),
            ushortArrayOf(12354u, 8314u, 24654u),
            uintArrayOf(3145456u, 144564u, 3468568u, 56545u),
            ulongArrayOf(3456456454245u, 3563523u, 76708896u),
        ).forEach {
            it.assertCollection()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle map as MAP`() {
        listOf<Map<*, *>>(
            mapOf(1 to Date(), 2 to "tomorrow"),
            ConcurrentHashMap<String, Any>().also { it.putAll(listOf("first" to 1, "second" to 2)) },
            ConcurrentSkipListMap<Int, Any>(mapOf(1 to Date(), 2 to "tomorrow")),
            TreeMap(mapOf(LocalDateTime.now() to 0, LocalDateTime.now().minusDays(1) to -1))
        ).forEach {
            it.assertMap()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle array of primitives as PRIMITIVE_ARRAY`() {
        listOf(
            booleanArrayOf(true, false),
            charArrayOf('a', 'b', 'c'),
            byteArrayOf(-89, 123, 0),
            shortArrayOf(12354, 8314, -24654),
            intArrayOf(3145456, -144564, 3468568, 56545),
            longArrayOf(3456456454245, -3563523, 76708896),
            floatArrayOf(545f, -456456.44f),
            doubleArrayOf(3545454235.24525, -7.897875784156874E8),
            JavaClassWithComposites.ARRAY_OF_CHAR_PRIMITIVES,
            JavaClassWithComposites.ARRAY_OF_BYTE_PRIMITIVES,
        ).forEach {
            it.assertPrimitiveArray()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle throwable as THROWABLE`() {
        listOf(
            Throwable(),
            IllegalStateException(),
            AssertionError(),
            AccessDeniedException(File(""))
        ).forEach {
            it.assertThrowable()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle annotation as ANNOTATION`() {
        listOf(
            JvmSynthetic(),
            JvmName("aName"),
            AsStringOption(),
            AsStringHash(),
            Override()
        ).forEach {
            it.assertAnnotation()
        }
    }

    @Test
    fun `AsStringObjectCategory should handle lambda property as LAMBDA_PROPERTY`() {
        listOf<Any>(
            CallableFactoryWithLambda().getCallable(),
            JavaClassWithHigherOrderFunction().higherOrderFunction,
            JavaClassWithAnonymousClass().propWithLambda,
            JavaClassWithLambda().intsToString,
            JavaClassWithLambda().intSupplier,
            KotlinClassWithAnonymousClassFactory().createLambda(),
            KotlinClassWithAnonymousClass().propWithLambda,
            Comparator<String> { _, _ -> 0 }
        ).forEach {
            it.assertLambdaProperty()
        }
    }

    @Test
    fun `java and kotlin stuff without overridden toString should be handled as SYSTEM and yield their identity`() {
        // arbitrary choice of java and kotlin classes without toString() implementation
        listOf(
            Any(),
            Object(),
            FloatBuffer.wrap(floatArrayOf(2.3f, 5.8f)),
            repeat(1) {},
            lazy {  },
            AsStringObjectCategoryTest::class,
            AsStringObjectCategoryTest::class.java
        ).forEach {
            it.assertSystemObject()
        }
    }

    @Test
    fun `java and kotlin stuff having toString implemented should be handled as SYSTEM and yield their toString`() {
        // arbitrary choice of java and kotlin classes that have a toString() implementation
        listOf<Any>(
            UUID.randomUUID(),
            RoundingMode.CEILING,
            Locale.getDefault(),
        ).forEach {
            it.assertSystemObject()
        }
    }

    @Suppress("unused")
    @Test
    fun `AsStringObjectCategory should handle custom classes as CUSTOM`() {
        open class MyNestedClass
        class MyNestedSubClass: MyNestedClass()
        val myNestedClassObject = object : MyNestedClass() {
            fun someExtraFun() = 1
        }
        listOf(
            MyClass(),
            MySubClass(),
            myClassObject,
            MyNestedClass(),
            MyNestedSubClass(),
            myNestedClassObject,
            JavaClassToTest("a", 1)
        ).forEach {
            it.assertCustomObject()
        }
    }

    @Test
    fun `AsStringObjectCategory should be cached`() {
        // arrange
        objectCategoryCache.reset()
        assertThat(objectCategoryCache.cache).hasSize(0)

        // act
        AsStringObjectCategory.resolveObjectCategory(MyClass())
        MyClass().asString()
        // assert
        assertThat(objectCategoryCache.cache)
            .containsExactly(entry(MyClass::class, AsStringObjectCategory.CUSTOM))

        // act
        2.asString()
        // assert
        assertThat(objectCategoryCache.cache)
            .hasSize(2)
            .contains(
                entry(MyClass::class, AsStringObjectCategory.CUSTOM),
                entry(Int::class, AsStringObjectCategory.BASE)
            )
    }

// region ~ Test classes etc.

    private open class MyClass
    private class MySubClass: MyClass()
    @Suppress("unused")
    private val myClassObject = object : MyClass() {
        fun someExtraFun() = 1
    }

// endregion

// region ~ Test helper methods

    private fun Any.assertBaseObject() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isFalse

        this.assertObjectCategory(AsStringObjectCategory.BASE)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.toString())
    }

    private fun Array<*>.assertArray() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isTrue

        this.assertObjectCategory(AsStringObjectCategory.ARRAY)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(arrayAsString())
            .isEqualTo(contentDeepToString())
    }

    private fun Collection<*>.assertCollection() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isTrue

        this.assertObjectCategory(AsStringObjectCategory.COLLECTION)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(collectionAsString())
            .isEqualTo(this.asString())
    }

    private fun Map<*, *>.assertMap() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isTrue

        this.assertObjectCategory(AsStringObjectCategory.MAP)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(mapAsString())
            .isEqualTo(this.asString())
    }

    private fun Any.assertPrimitiveArray() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isFalse

        this.assertObjectCategory(AsStringObjectCategory.PRIMITIVE_ARRAY)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.primitiveArrayAsString())
            .isEqualTo(this.primitiveArrayToArray().asString())
    }

    private fun Annotation.assertAnnotation() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isTrue

        this.assertObjectCategory(AsStringObjectCategory.ANNOTATION)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.annotationAsString())
    }

    private fun Throwable.assertThrowable() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isTrue

        this.assertObjectCategory(AsStringObjectCategory.THROWABLE)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.throwableAsString())
    }

    private fun Any.assertLambdaProperty() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isFalse

        this.assertObjectCategory(AsStringObjectCategory.LAMBDA_PROPERTY)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.lambdaPropertyAsString())
    }

    private fun Any.assertSystemObject() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isFalse

        this.assertObjectCategory(AsStringObjectCategory.SYSTEM)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.systemClassObjAsString())

        if (this::class.hasImplementedToString()) {
            assertThat(this.asString()).isEqualTo(this.toString())
        } else {
            assertThat(this.asString()).isEqualTo(this.systemClassIdentity())
        }
    }

    private fun Any.assertCustomObject() {
        val category = resolveObjectCategory(this)
        assertThat(category.guardStack).isTrue

        this.assertObjectCategory(AsStringObjectCategory.CUSTOM)
        assertThat(category.handler).isNull()
    }

    private fun Any.primitiveArrayToArray(): Array<*> =
        when (this) {
            is BooleanArray -> this.iterator().asSequence().toList().toTypedArray()
            is ByteArray -> this.iterator().asSequence().toList().toTypedArray()
            is CharArray -> this.iterator().asSequence().toList().toTypedArray()
            is ShortArray -> this.iterator().asSequence().toList().toTypedArray()
            is IntArray -> this.iterator().asSequence().toList().toTypedArray()
            is LongArray -> this.iterator().asSequence().toList().toTypedArray()
            is FloatArray -> this.iterator().asSequence().toList().toTypedArray()
            is DoubleArray -> this.iterator().asSequence().toList().toTypedArray()
            else -> throw IllegalArgumentException("Should be called with arrays of primitives only")
        }

    private fun Any.assertObjectCategory(expected: AsStringObjectCategory) {
        val actual = resolveObjectCategory(this)
        assertThat(actual)
            .`as`("Object `${this.asString()}` of type ${this::class.simplifyClassName()} should have category $expected but is $actual")
            .isSameAs(expected)
    }

// endregion

}