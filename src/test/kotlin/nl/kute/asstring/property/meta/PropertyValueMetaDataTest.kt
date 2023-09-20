package nl.kute.asstring.property.meta

import nl.kute.asstring.core.AsStringObjectCategory
import nl.kute.asstring.core.asString
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties

internal class PropertyValueMetaDataTest {

    private class TestType {
        override fun toString(): String = "I am a TestType"
    }

    private class TestList : MutableList<String> by arrayListOf("this", "is", "a", "TestList")

    private open class TestSuperClass {
        var aList: TestList? = TestList()
        val anArray: Array<Int> = arrayOf(1, 2, 4, 8, 16, 32)
        var aChar: Char? = RandomStringUtils.randomAlphabetic(1)[0]
        lateinit var aString: String
        val aStringBuilder = StringBuilder("a builder")
    }

    private class TestClass : TestSuperClass() {
        val aTestType: TestType = TestType()
        val anIntArray: IntArray = intArrayOf(1, 10, 100, 1000)
        val aMap: Map<Int, String> = mapOf(0 to "zero", 5 to "five", 15 to "fifteen", 35 to "thirty-five")
        lateinit var aCollection: Collection<*>
        lateinit var aNumber: Number
    }

    private val testObj = TestClass().also {
        it.aList = null
        it.aNumber = 15.12
        it.aCollection = listOf<Any>()
    }

    val metaDataList = listOf(
        PropertyValueMetaData(TestClass::aList, TestClass::class, testObj.aList, testObj.aList.asString().length),
        PropertyValueMetaData(TestClass::anArray, TestClass::class, testObj.anArray, testObj.anArray.asString().length),
        PropertyValueMetaData(TestClass::aChar, TestClass::class, testObj.aChar, testObj.aChar.asString().length),
        PropertyValueMetaData(TestClass::aString, TestClass::class, "a String!", "a String!".length),
        PropertyValueMetaData(
            TestClass::aStringBuilder,
            TestClass::class,
            testObj.aStringBuilder,
            testObj.aStringBuilder.asString().length
        ),
        PropertyValueMetaData(
            TestClass::aTestType,
            TestClass::class,
            testObj.aTestType,
            testObj.aTestType.asString().length
        ),
        PropertyValueMetaData(
            TestClass::anIntArray,
            TestClass::class,
            testObj.anIntArray,
            testObj.anIntArray.asString().length
        ),
        PropertyValueMetaData(TestClass::aMap, TestClass::class, testObj.aMap, testObj.aMap.asString().length),
        PropertyValueMetaData(
            TestClass::aCollection,
            TestClass::class,
            testObj.aCollection,
            testObj.aCollection.asString().length
        ),
        PropertyValueMetaData(TestClass::aNumber, TestClass::class, testObj.aNumber, testObj.aNumber.asString().length),
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

    @Test
    fun `isNull should yield null for null property values, false otherwise`() {
        val nullProp = TestClass::aList
        var count = 0
        metaDataList.filter { it.property.name == nullProp.name }
            .forEach {
                count++
                assertThat(it.isNull)
                    .`as`("isNull should be true for $it")
                    .isTrue
            }
        assertThat(count).isEqualTo(1) // just to make sure the test hits anything at all :-)

        count = 0
        metaDataList.filter { it.property.name != nullProp.name }
            .forEach {
                count++
                assertThat(it.isNull)
                    .`as`("isNull should be false for $it")
                    .isFalse
            }
        assertThat(count).isEqualTo(9) // just to make sure the test hits anything at all :-)
    }

    @Test
    fun `propertyValueCategory should yield corresponding value`() {

        assertThat(
            PropertyValueMetaData(TestClass::aList, TestClass::class, testObj.aList, 0)
                .propertyValueCategory).isNull()
        assertThat(
            PropertyValueMetaData(TestClass::anArray, TestClass::class, testObj.anArray, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.ARRAY)
        assertThat(
            PropertyValueMetaData(TestClass::aChar, TestClass::class, testObj.aChar, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.BASE)
        assertThat(
            PropertyValueMetaData(TestClass::aString, TestClass::class, "a String!", 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.BASE)
        assertThat(
            PropertyValueMetaData(TestClass::aStringBuilder, TestClass::class, testObj.aStringBuilder, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.BASE)
        assertThat(
            PropertyValueMetaData(TestClass::aTestType, TestClass::class, testObj.aTestType, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.CUSTOM)
        assertThat(
            PropertyValueMetaData(TestClass::anIntArray, TestClass::class, testObj.anIntArray, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.PRIMITIVE_ARRAY)
        assertThat(
            PropertyValueMetaData(TestClass::aMap, TestClass::class, testObj.aMap, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.MAP)
        assertThat(
            PropertyValueMetaData(TestClass::aCollection, TestClass::class, testObj.aCollection, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.COLLECTION)
        assertThat(
            PropertyValueMetaData(TestClass::aNumber, TestClass::class, testObj.aNumber, 0)
                .propertyValueCategory).isEqualTo(AsStringObjectCategory.BASE)
    }
}
    