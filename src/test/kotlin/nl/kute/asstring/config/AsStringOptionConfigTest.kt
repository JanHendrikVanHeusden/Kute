package nl.kute.asstring.config

import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.core.asString
import nl.kute.asstring.core.defaults.initialMaxStringValueLength
import nl.kute.asstring.core.defaults.initialShowNullAs
import nl.kute.asstring.property.propsWithAnnotationsCacheByClass
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class AsStringOptionConfigTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringOption()
        restoreInitialAsStringClassOption()
    }

    @Test
    fun `when no changes yet, initial defaults should apply`() {
        with(AsStringOption.defaultOption) {
            assertThat(this.showNullAs).isEqualTo(initialShowNullAs)
            assertThat(this.propMaxStringValueLength).isEqualTo(initialMaxStringValueLength)
        }
    }

    @Test
    fun `change of AsStringOption defaults should be applied & also empty the properties & annotations cache`() {
        // arrange
        open class TestClass {
            val str600 = "x".repeat(600)
            open val aNullValue: Any? = null
        }

        val initialMaxPropStrLength = AsStringOption.defaultOption.propMaxStringValueLength
        assertThat(initialMaxPropStrLength)
            .isEqualTo(500)
            .isEqualTo(initialMaxStringValueLength)

        val initialNullStr = AsStringOption.defaultOption.showNullAs
        assertThat(initialNullStr)
            .isEqualTo("null")
            .isEqualTo(initialShowNullAs)

        val testObj = TestClass()
        assertThat(testObj.str600).hasSize(600)

        propsWithAnnotationsCacheByClass.reset()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero

        // act, assert - defaults
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(500)}\b.+$""")
            .contains("aNullValue=null")
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)

        // act, assert - max length
        AsStringConfig().withMaxPropertyStringLength(20).applyAsDefault()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(20)}\b.+$""")
            .contains("aNullValue=null")
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)

        // act, assert - null str
        AsStringConfig().withShowNullAs("--!").applyAsDefault()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(20)}\b.+$""")
            .contains("aNullValue=--!")
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)

        // act, assert - reset defaults
        restoreInitialAsStringOption()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(testObj.asString())
            .matches("""^.+\bstr600=${testObj.str600.take(500)}\b.+$""")
            .contains("aNullValue=null")
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)

        val testClassSub: TestClass = object: TestClass() {
            override val aNullValue: String? = null
        }
        @Suppress("UNUSED_VARIABLE")
        val theStringValue = testClassSub.asString()
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(2)
    }

    @Test
    fun `change of AsStringOption defaults should not influence local AsStringOption settings`() {
        // arrange
        @AsStringOption(showNullAs = "(-)", propMaxStringValueLength = 20)
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
        AsStringConfig()
            .withShowNullAs("*")
            .withMaxPropertyStringLength(2)
            .applyAsDefault()

        // assert
        assertIt("`After` situation should be same as `Before`")
    }

    @Test
    fun `re-applying same values should not empty the properties & annotations cache`() {
        // arrange
        AsStringConfig()
            .withShowNullAs("---")
            .withMaxPropertyStringLength(12)
            .applyAsDefault()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero

        class MyTestClass
        val testStr = MyTestClass().asString()
        assertThat(testStr).isNotNull
        assertThat(propsWithAnnotationsCacheByClass.size)
            .`as`("MyTestClass should be present in cache now")
            .isEqualTo(1)

        // act
        AsStringConfig()
            .withShowNullAs("-".repeat(3))
            .withMaxPropertyStringLength(9+3)
            .applyAsDefault()
        // assert
        assertThat(propsWithAnnotationsCacheByClass.size)
            .`as`("MyTestClass should still be present in cache")
            .isEqualTo(1)
    }

    @Test
    fun `change of AsStringOption default should call subscribed callback`() {
        // arrange
        @Suppress("UNCHECKED_CAST")
        val asStringOptionClass: KClass<Annotation> = AsStringOption::class as KClass<Annotation>
        var notified = false
        val callback = {
            notified = true
        }
        asStringOptionClass.subscribeConfigChange(callback)
        // act
        AsStringConfig().withShowNullAs("<empty>").applyAsDefault()
        // assert
        assertThat(notified).isTrue
    }
}