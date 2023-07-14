package nl.kute.config

import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.asString
import nl.kute.core.property.resetPropertyAnnotationCache
import nl.kute.core.property.propertyAnnotationCacheSize
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AsStringOptionConfigTest {

    @BeforeEach
    @AfterEach
    fun setUp() {
        restoreInitialDefaultAsStringOption()
    }

    @Test
    fun `change of AsStringOption defaults should be applied & also empty the cache`() {
        // arrange
        open class TestClass {
            val str600 = "x".repeat(600)
            open val aNullValue: Any? = null
        }
        val initialMaxPropStrLength = AsStringOption.defaultAsStringOption.propMaxStringValueLength
        val initialNullStr = AsStringOption.defaultAsStringOption.showNullAs

        assertThat(initialMaxPropStrLength)
            .isEqualTo(500)
            .isEqualTo(initialMaxPropStrLength)
        assertThat(initialNullStr)
            .isEqualTo("null")
            .isEqualTo(initialNullStr)

        val testObj = TestClass()
        assertThat(testObj.str600).hasSize(600)

        resetPropertyAnnotationCache()
        assertThat(propertyAnnotationCacheSize).isZero

        // act, assert - defaults
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(500)}\b.+$""")
            .contains("aNullValue=null")
        assertThat(propertyAnnotationCacheSize).isEqualTo(1)

        // act, assert - max length
        AsStringOptionBuilder().maxPropertyStringLength(20).applyAsDefault()
        assertThat(propertyAnnotationCacheSize).isEqualTo(0)
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(20)}\b.+$""")
            .contains("aNullValue=null")
        assertThat(propertyAnnotationCacheSize).isEqualTo(1)

        // act, assert - null str
        AsStringOptionBuilder().showNullAs("--!").applyAsDefault()
        assertThat(propertyAnnotationCacheSize).isEqualTo(0)
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(20)}\b.+$""")
            .contains("aNullValue=--!")
        assertThat(propertyAnnotationCacheSize).isEqualTo(1)

        // act, assert - reset defaults
        restoreInitialDefaultAsStringOption()
        assertThat(propertyAnnotationCacheSize).isEqualTo(0)
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(500)}\b.+$""")
            .contains("aNullValue=null")
        assertThat(propertyAnnotationCacheSize).isEqualTo(1)

        val testClassSub: TestClass = object: TestClass() {
            override val aNullValue: String? = null
        }
        @Suppress("UNUSED_VARIABLE")
        val theStringValue = testClassSub.asString()
        assertThat(propertyAnnotationCacheSize).isEqualTo(2)
    }

    @Test
    fun `change of AsStringOption defaults should not influence local AsStringOption settings`() {
        // arrange
        @AsStringOption(propMaxStringValueLength = 20, showNullAs = "(-)")
        @Suppress("unused")
        class MyTestClass {
            val aDefaultNullValue: Any? = null
            @AsStringOption(showNullAs = "!-!")
            val aNonDefaultNullValue: Any? = null
            val str50: String = "a".repeat(50)
        }
        val testObj = MyTestClass()
        fun assertIt(description: String) {
            assertThat(testObj.asString())
                .`as`(description)
                .contains("aDefaultNullValue=(-)")
                .contains("aNonDefaultNullValue=!-!")
                .matches("""^.+\bstr50=${"a".repeat(20)}\b.+$""")
        }
        assertIt("Verifying `Before` situation")

        // act
        AsStringOptionBuilder()
            .showNullAs("*")
            .maxPropertyStringLength(2)
            .applyAsDefault()

        // assert
        assertIt("`After` situation should be same as `Before`")
    }

    @Test
    fun `re-applying same values should not empty the cache`() {
        // arrange
        AsStringOptionBuilder()
            .showNullAs("---")
            .maxPropertyStringLength(12)
            .applyAsDefault()
        assertThat(propertyAnnotationCacheSize).isZero

        class MyTestClass
        val testStr = MyTestClass().asString()
        assertThat(testStr).isNotNull
        assertThat(propertyAnnotationCacheSize)
            .`as`("MyTestClass should be present in cache now")
            .isEqualTo(1)

        // act
        AsStringOptionBuilder()
            .showNullAs("-".repeat(3))
            .maxPropertyStringLength(9+3)
            .applyAsDefault()
        // assert
        assertThat(propertyAnnotationCacheSize)
            .`as`("MyTestClass should still be present in cache")
            .isEqualTo(1)
    }
}