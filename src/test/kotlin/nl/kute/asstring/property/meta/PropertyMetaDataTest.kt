package nl.kute.asstring.property.meta

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

internal class PropertyMetaDataTest {

    private class TestType {
        override fun toString(): String = "I am a TestType"
    }

    private class TestList: MutableList<String> by arrayListOf("this", "is", "a", "TestList")

    private open class TestSuperClass {
        val aList: TestList = TestList()
        val anArray: Array<Int> = arrayOf(1, 2, 4, 8, 16, 32)
        val aChar: Char = RandomStringUtils.randomAlphabetic(1)[0]
        lateinit var aString: String
        val aStringBuilder = StringBuilder("a builder")
    }

    private class TestClass: TestSuperClass() {
        val aTestType: TestType = TestType()
        val anIntArray: IntArray = intArrayOf(1, 10, 100, 1000)
        val aMap: Map<Int, String> = mapOf(0 to "zero", 5 to "five", 15 to "fifteen", 35 to "thirty-five")
        lateinit var aCollection: Collection<*>
        lateinit var aNumber: Number
    }

    val metaDataList = listOf(
        PropertyMetaData(TestClass::aList, TestClass::class),
        PropertyMetaData(TestClass::anArray, TestClass::class),
        PropertyMetaData(TestClass::aChar, TestClass::class),
        PropertyMetaData(TestClass::aString, TestClass::class),
        PropertyMetaData(TestClass::aStringBuilder, TestClass::class),
        PropertyMetaData(TestClass::aTestType, TestClass::class),
        PropertyMetaData(TestClass::anIntArray, TestClass::class),
        PropertyMetaData(TestClass::aMap, TestClass::class),
        PropertyMetaData(TestClass::aCollection, TestClass::class),
        PropertyMetaData(TestClass::aNumber, TestClass::class)
    )

    @Test
    fun `isCollectionLike should yield true for Collection like types`() {
        var count = 0
        metaDataList.filter {
            it.property.name.contains("List")
                    || it.property.name.contains("Array")
                    || it.property.name.contains("Collection")
                    || it.property.name.contains("Map")
        }.forEach {
            count++
            assertThat(it.isCollectionLike)
                .`as`("isCollectionLike should be true for $it")
                .isTrue
        }
        assertThat(count).isEqualTo(5) // just to make sure the test hits anything at all :-)
    }

    @Test
    fun `isCollectionLike should yield false for non-Collection like types`() {
        var count = 0
        metaDataList.filter {
            !it.property.name.contains("List")
                    && !it.property.name.contains("Array")
                    && !it.property.name.contains("Collection")
                    && !it.property.name.contains("Map")
        }.forEach {
            count++
            assertThat(it.isCollectionLike)
                .`as`("isCollectionLike should be false for $it")
                .isFalse
        }
        assertThat(count).isEqualTo(5) // just to make sure the test hits anything at all :-)
    }

    @Test
    fun `property metadata should yield property`() {
        assertThat(metaDataList.map { it.property })
            .containsExactlyInAnyOrderElementsOf(TestClass::class.memberProperties)
    }

    @Test
    fun `property metadata should yield property name`() {
        metaDataList.forEach {
            assertThat(it.propertyName).isEqualTo(it.property.name)
        }
    }

    @Test
    fun `property metadata should yield property return type`() {
        metaDataList.forEach {
            assertThat(it.returnType).isEqualTo(it.property.returnType)
        }
    }

    @Test
    fun `property metadata should yield object class`() {
        metaDataList.forEach {
            assertThat(it.objectClass).isEqualTo(TestClass::class)
        }
    }

    @Test
    fun `property metadata should yield object class name`() {
        metaDataList.forEach {
            assertThat(it.objectClassName).isEqualTo("TestClass")
        }
    }

    @Test
    fun `isBaseType should yield true for base types, false for non-base types`() {
        val baseTypePropNames = listOf("aChar", "aNumber", "aString", "aStringBuilder")
        metaDataList.filter { baseTypePropNames.contains(it.propertyName) }
            .forEach {
                assertThat(it.isBaseType)
                    .`as`("isBaseType should be true for $it")
                    .isTrue
            }
        metaDataList.filter { !baseTypePropNames.contains(it.propertyName) }
            .forEach {
                assertThat(it.isBaseType)
                    .`as`("isBaseType should be false for $it")
                    .isFalse
            }
    }

    @Test
    fun `isCharSequence should yield true for CharSequence types, false for non-CharSequence types`() {
        var count = 0
        metaDataList.filter { it.property.name.startsWith("aString") }
            .forEach {
                count++
                assertThat(it.isCharSequence)
                    .`as`("isCharSequence should be true for $it")
                    .isTrue
            }
        assertThat(count).isEqualTo(2) // just to make sure the test hits anything at all :-)

        count = 0
        metaDataList.filter { !it.property.name.startsWith("aString") }
            .forEach {
                count++
                assertThat(it.isCharSequence)
                    .`as`("isCharSequence should be false for $it")
                    .isFalse
            }
        assertThat(count).isEqualTo(8) // just to make sure the test hits anything at all :-)
    }
}
