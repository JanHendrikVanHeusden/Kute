package nl.kute.core

import nl.kute.core.AsStringObjectCategory.CategoryResolver.resolveObjectCategory
import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.reflection.simplifyClassName
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
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.nio.CharBuffer
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
import java.util.PriorityQueue
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.DoubleAdder
import javax.management.Attribute
import javax.management.AttributeList

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
            false
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

    @Test
    fun `AsStringObjectCategory should handle collection as COLLECTION`() {
        listOf<Collection<*>>(
            listOf(12, 15, 28),
            mutableListOf(Any()),
            AttributeList(listOf(Attribute("name1", "value1"), Attribute("name2", Any()))),
            PriorityQueue(listOf("first", "second", "third")),
            EnumSet.allOf(AsStringObjectCategory::class.java),
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
            byteArrayOf(-89, 123, 0),
            charArrayOf('a', 'b', 'c'),
            shortArrayOf(12354, 8314, -24654),
            intArrayOf(3145456, -144564, 3468568, 56545),
            longArrayOf(3456456454245, -3563523, 76708896),
            floatArrayOf(545f, -456456.44f),
            doubleArrayOf(3545454235.24525, -7.897875784156874E8),
            JavaClassWithComposites.ARRAY_OF_CHAR_PRIMITIVES,
            JavaClassWithComposites.ARRAY_OF_BYTE_PRIMITIVES
        ).forEach {
            it.assertPrimitiveArray()
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
    fun `AsStringObjectCategory should handle lambda property as LAMBDA_PROPERTY`() {
        listOf<Any>(
            CallableFactoryWithLambda().getCallable(),
            JavaClassWithHigherOrderFunction().higherOrderFunction,
            JavaClassWithAnonymousClass().propWithLambda,
            JavaClassWithLambda().intsToString,
            JavaClassWithLambda().intSupplier,
            KotlinClassWithAnonymousClassFactory().createLambda(),
            KotlinClassWithAnonymousClass().propWithLambda
        ).forEach {
            it.assertLambdaProperty()
        }
    }

    private fun Any.assertBaseObject() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.BASE)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.toString())
    }

    private fun Array<*>.assertArray() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.ARRAY)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(arrayAsString())
            .isEqualTo(contentDeepToString())
    }

    private fun Collection<*>.assertCollection() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.COLLECTION)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(collectionAsString())
            .isEqualTo(this.asString())
    }

    private fun Map<*, *>.assertMap() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.MAP)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(mapAsString())
            .isEqualTo(this.asString())
    }

    private fun Any.assertPrimitiveArray() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.PRIMITIVE_ARRAY)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.primitiveArrayAsString())
            .isEqualTo(this.primitiveArrayToArray().asString())
    }

    private fun Annotation.assertAnnotation() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.ANNOTATION)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.annotationAsString())
    }

    private fun Throwable.assertThrowable() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.THROWABLE)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.throwableAsString())
    }

    private fun Any.assertLambdaProperty() {
        val category = resolveObjectCategory(this)

        this.assertObjectCategory(AsStringObjectCategory.LAMBDA_PROPERTY)
        assertThat(category.handler!!.invoke(this))
            .isEqualTo(this.lambdaPropertyAsString())
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

    private fun Any.assertObjectCategory(category: AsStringObjectCategory) {
        assertThat(resolveObjectCategory(this))
            .`as`("Object `${this.asString()}` of type ${this::class.simplifyClassName()} should have category ${AsStringObjectCategory.PRIMITIVE_ARRAY}")
            .isSameAs(category)
    }

}