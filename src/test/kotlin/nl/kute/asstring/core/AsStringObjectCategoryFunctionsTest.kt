@file:Suppress("unused")

package nl.kute.asstring.core

import com.google.common.util.concurrent.AtomicDouble
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.reflection.util.hasImplementedToString
import nl.kute.util.identityHashHex
import org.apache.commons.math3.fraction.BigFraction
import org.apache.commons.math3.util.Decimal64
import org.apache.commons.text.TextStringBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.math.BigInteger
import java.math.MathContext
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.Date
import java.util.Stack
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.DoubleAccumulator
import java.util.function.DoubleBinaryOperator
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

class AsStringObjectCategoryFunctionsTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
    }

    @Test
    fun `asStringFallBack should include the same identity as non-overridden toString`() {
        val identityRegex = Regex("""^.+(@[0-9a-f]+)$""")
        @Suppress("LocalVariableName")
        val `@hexHash` = "\$1"
        // Non-overridden toString() removes leading zeroes from the hex hashCode
        // So the test is repeated a number of times, so we are reasonably sure that it would hit a leading zero
        // (which asStringFallBack also needs to remove)
        (1..100).forEach { _ ->
            Any().let {
                assertThat(it.asStringFallBack().replace(identityRegex, `@hexHash`))
                    .isNotEmpty
                    .doesNotStartWith("0")
                    .isEqualTo(it.toString().replace(identityRegex, `@hexHash`))
            }
        }
    }

    @Test
    fun `asStringFallBack should handle nulls correctly`() {
        assertThat(null.asStringFallBack()).isEqualTo("null")
    }

    @Test
    fun `objectIdentity should honour AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        @AsStringClassOption(includeIdentityHash = true)
        class WithAnnotation
        var testObj: Any = WithAnnotation()
        var idHash = testObj.identityHashHex
        assertThat(testObj.objectIdentity()).isEqualTo("WithAnnotation@$idHash")

        class WithoutAnnotation
        testObj = WithoutAnnotation()
        assertThat(testObj.objectIdentity()).isEqualTo("WithoutAnnotation")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        idHash = testObj.identityHashHex
        assertThat(testObj.objectIdentity()).isEqualTo("WithoutAnnotation@$idHash")
    }

    @Test
    fun `collectionIdentity should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val collections = listOf(arrayOf(12.0), listOf<Any>(), mapOf(1 to "one", 2 to "two"), Stack<String>())
        collections.forEach {
            restoreInitialAsStringClassOption()
            val collection = it
            val className = collection::class.simpleName
            val idHash = collection.identityHashHex

            assertThat(collection.collectionIdentity())
                .`as`("When no identity required, it follows the default toString() of the Collection, so no className")
                .isEqualTo("")

            assertThat(collection.collectionIdentity(includeIdentity = true))
                .isEqualTo("$className@$idHash")

            AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
            assertThat(collection.collectionIdentity())
                .isEqualTo("$className@$idHash")
        }
    }

    @Test
    fun `collectionAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val collection: Collection<String> = Stack<String>().also { it.addAll(listOf("one", "two")) }
        val className = collection::class.simpleName
        val idHash = collection.identityHashHex

        assertThat(collection.collectionAsString())
            .isEqualTo("[one, two]")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        assertThat(collection.collectionAsString())
            .isEqualTo("$className@$idHash[one, two]")
    }

    @Test
    fun `arrayAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val array: Array<String> = arrayOf("one", "two")
        val className = array::class.simpleName
        val idHash = array.identityHashHex

        assertThat(array.arrayAsString())
            .isEqualTo("[one, two]")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        assertThat(array.arrayAsString())
            .isEqualTo("$className@$idHash[one, two]")
    }

    @Test
    fun `mapAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        val map = mapOf(1 to "one", 2 to "two")
        val className = map::class.simpleName
        val idHash = map.identityHashHex

        assertThat(map.mapAsString())
            .isEqualTo("{1=one, 2=two}")

        AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
        assertThat(map.mapAsString())
            .isEqualTo("$className@$idHash{1=one, 2=two}")
    }

    @Test
    fun `systemClassIdentity should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        // arbitrary list of some Java and Kotlin built-in stuff
        val systemStuff = listOf(System.out, UUID.randomUUID(), '0', arrayListOf('1'), StringBuffer("x"))
        systemStuff.forEach {
            restoreInitialAsStringClassOption()
            val systemObj = it
            val className = systemObj::class.simpleName
            val idHash = systemObj.identityHashHex

            assertThat(systemObj.systemClassIdentity())
                .isEqualTo(className)

            assertThat(systemObj.systemClassIdentity(includeIdentity = true))
                .isEqualTo("$className@$idHash")

            AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
            assertThat(systemObj.systemClassIdentity())
                .isEqualTo("$className@$idHash")
        }
    }

    @Test
    fun `systemClassObjAsString should honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        // arbitrary list of some Java and Kotlin built-in stuff
        val systemStuff = listOf(System.out, UUID.randomUUID(), arrayListOf('1'), StringBuffer("x"))
        systemStuff.forEach {
            restoreInitialAsStringClassOption()
            val systemObj = it
            val className = systemObj::class.simpleName
            val idHash = systemObj.identityHashHex

            if (systemObj::class.hasImplementedToString()) {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo(systemObj.toString())
            } else {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo("$className")
            }

            AsStringConfig().withIncludeIdentityHash(includeHash = true).applyAsDefault()
            if (systemObj::class.hasImplementedToString()) {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo(systemObj.toString())
            } else {
                assertThat(systemObj.systemClassObjAsString())
                    .isEqualTo("$className@$idHash")
            }
        }
    }

    @Test
    fun `annotationAsString should remove the package name`() {
        val defaultOption = AsStringOption.defaultOption
        assertThat(defaultOption.annotationAsString())
            .isObjectAsString("AsStringOption",
                "showNullAs=${defaultOption.showNullAs}",
                "surroundPropValue=NONE",
                "propMaxStringValueLength=${defaultOption.propMaxStringValueLength}",
                "elementsLimit=${defaultOption.elementsLimit}"
            )
    }

    @Test
    fun `syntheticClassObjectAsString should return the lambda type and honour default AsStringClassOption`() {
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse

        val stringSupplier = { "this is the lambda return value" }
        val intFunction: (Int, Int) -> Int = { i, j -> i + j }
        val swap: (Pair<Int, Int>) -> Pair<Int, Int> = { p -> Pair(p.second, p.first) }

        assertThat(stringSupplier.syntheticClassObjectAsString())
            .isEqualTo("() -> String")
        assertThat(intFunction.syntheticClassObjectAsString())
            .isEqualTo("(Int, Int) -> Int")
        assertThat(swap.syntheticClassObjectAsString())
            .isEqualTo("(Pair<Int, Int>) -> Pair<Int, Int>")

        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()

        assertThat(stringSupplier.syntheticClassObjectAsString())
            .`as`("no identity hash should be included in lambda representation")
            .isEqualTo("() -> String")
    }

    @Test
    fun `syntheticClassObjectAsString should return asStringFallBack for non-lambdas`() {
        assertThat(System.out.syntheticClassObjectAsString() )
            .isEqualTo(System.out.asStringFallBack())

        class Dummy {
            var dummy: Int = 0
            fun setTheDummy(x: Int) {
                this.dummy = x
            }
        }
        val unit: Unit = Dummy().setTheDummy(1)
        assertThat(unit.syntheticClassObjectAsString())
            .isEqualTo(unit.asStringFallBack())
    }

    internal class TestBaseTypes {
        val b00 = "a string"
        val b01 = 5
        var b02: AtomicInteger? = AtomicInteger(2)
        val b03 = AtomicDouble(50.4566) // Guava
        val b04 = BigInteger("85")
        val b05 = BigFraction(3, 7) // Apache commons
        val b06 = Decimal64(1.23456789) // Apache commons
        val b07 = DoubleAccumulator(mock<DoubleBinaryOperator>(), 12.5)
        val b08 = 3.45
        var b09: Date? = Date()
        val b10 = Date().time // Long
        val b11 = 'x'
        val b12 = StringBuffer()
        val b13 = StringBuilder("builder")
        lateinit var b14: String
        val b15 = LocalDate.now()
        val b16 = ZonedDateTime.now()
        val b17 = ZonedDateTime.now() - Duration.ofMinutes(3)
        val b18 = Duration.ofMillis(5)
        val b19 = 5u.toUByte()
        val b20 = 88u.toUShort()
        val b21 = 44u
        val b22 = Long.MAX_VALUE.toULong()
        lateinit var b23: LocalDate
        var b24: String? = null
    }

    @Test
    fun `isBaseType should return true for objects of types defined as base types`() {
        val testObj = TestBaseTypes()
        // assign lateinit var
        testObj.b23 = LocalDate.now()
        testObj.b14 = "late"

        val baseValues: List<Any> =
            TestBaseTypes::class.memberProperties.map { it.get(testObj) }.filterNotNull()

        baseValues.forEach {
            assertThat(it.isBaseType())
                .`as`("expected $it of class ${it::class} to be a base type")
                .isTrue()
        }
    }

    @Test
    fun `isBaseType should return true for types defined as base types`() {
        TestBaseTypes::class.memberProperties
            .forEach {
                assertThat(it.returnType.isBaseType())
                    .`as`("expected type ${it.returnType} of property ${it.name} to be a base type")
                    .isTrue()
            }
    }

    internal class NonBaseTypes {
        var n0: Any? = Any()
        lateinit var n1: Collection<String>
        var n2: UUID? = null
        val n3 = TestBaseTypes::class
        lateinit var n4: KType
        var n5: KType? = null
        var n6: MathContext = MathContext.DECIMAL128
    }

    @Test
    fun `isBaseType should return false for null`() {
        assertThat(null.isBaseType()).isFalse
    }

    @Test
    fun `isBaseType should return false for objects of types not defined as base types`() {
        val testObj = NonBaseTypes()
        // assign lateinit vars
        testObj.n1 = listOf()
        testObj.n4 = NonBaseTypes::class.memberProperties.first().returnType
        val nonBaseValues: List<Any> =
            NonBaseTypes::class.memberProperties.map { it.get(testObj) }.filterNotNull()

        nonBaseValues.forEach {
            assertThat(it.isBaseType())
                .`as`("expected $it of class ${it::class} not to be a base type")
                .isFalse()
        }
    }

    @Test
    fun `isBaseType should return false for types not defined as base types`() {
        NonBaseTypes::class.memberProperties
            .forEach {
                assertThat(it.returnType.isBaseType())
                    .`as`("expected type ${it.returnType} of property ${it.name} not to be a base type")
                    .isFalse()
            }
    }

    internal class CollectionLikeTypes {
        val aList = listOf("a", "list")
        lateinit var aColl: Collection<*>
        val anArray: Array<Int> = arrayOf()
        val aSetMock: Set<Double> = mock<HashSet<Double>>()
        val aMap = mapOf(Date() to UUID.randomUUID())
        lateinit var anIntArray: IntArray
        val aByteArray = byteArrayOf(4, 8)
        val aCharArray = charArrayOf('a', 'c', 'a')
        lateinit var longArray: LongArray
        val aFloatArray = floatArrayOf()
        var aDoubleArray: DoubleArray? = null
        val aShortArray = shortArrayOf(12, 25, Short.MAX_VALUE)
        val aBooleanArray = booleanArrayOf(true, false, true)
        lateinit var anAbstractCollection: AbstractCollection<*>
        @OptIn(ExperimentalUnsignedTypes::class)
        var uIntArray: UIntArray? = null
    }

    @Test
    fun `isCollectionType should return true for types defined as collection-like types`() {
        CollectionLikeTypes::class.memberProperties
            .forEach {
                assertThat(it.returnType.isCollectionLikeType())
                    .`as`("expected type ${it.returnType} of property ${it.name} to be a collection-like type")
                    .isTrue()
            }
    }

    internal class NonCollectionLikeTypes {
        var n0: Any? = Any()
        lateinit var n1: LongRange
        var n2: UUID? = null
        val n3 = TestBaseTypes::class
        lateinit var n4: KType
        var n5: KType? = null
        var n6: MathContext = MathContext.DECIMAL128
        val n7 = 1..10
    }

    @Test
    fun `isCollectionType should return false for types not defined as collection-like types`() {
        NonCollectionLikeTypes::class.memberProperties
            .forEach {
                assertThat(it.returnType.isCollectionLikeType())
                    .`as`("expected type ${it.returnType} of property ${it.name} not to be a collection-like type")
                    .isFalse
            }
    }

    internal class CharSequenceTypes {
        val s = "a string"
        val sbf = StringBuffer()
        val sbl = StringBuilder("builder")
        var sOpt: String? = null
        val tsb: TextStringBuilder? = null // Apache commons text
    }

    @Test
    fun `isCharSequenceType should return true for CharSequence types`() {
        CharSequenceTypes::class.memberProperties
            .forEach {
                assertThat(it.returnType.isCharSequenceType())
                    .`as`("expected type ${it.returnType} of property ${it.name} to be a CharSequence type")
                    .isTrue()
            }
    }

    internal class NonCharSequence {
        val i = 5
        var ai: AtomicInteger? = AtomicInteger(2)
        val ad = AtomicDouble(50.4566) // Guava
        val bi = BigInteger("85")
        val d = 3.45
        var dt: Date? = Date()
        val l = Date().time // Long
        val c = 'x'
        var cMock: Char? = mock()
        var bb = ByteBuffer.allocate(20)
        val ld = LocalDate.now()
        val du = Duration.ofMillis(5)
        val ub = 5u.toUByte()
        val us = 88u.toUShort()
        val ui = 44u
        val ul = Long.MAX_VALUE.toULong()
        lateinit var ldl: LocalDate
        val u = UUID.randomUUID()
    }

    @Test
    fun `isCharSequenceType should return false for non-CharSequence types`() {
        NonCharSequence::class.memberProperties
            .forEach {
                assertThat(it.returnType.isCharSequenceType())
                    .`as`("expected type ${it.returnType} of property ${it.name} not to be a CharSequence type")
                    .isFalse
            }
    }

}