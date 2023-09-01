package nl.kute.config

import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.core.annotation.option.asStringClassOptionCache
import nl.kute.core.asString
import nl.kute.core.property.propsWithAnnotationsCacheByClass
import nl.kute.core.useToStringByClass
import nl.kute.util.hexHashCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class AsStringClassOptionConfigTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringOption()
        restoreInitialAsStringClassOption()
    }

    @Test
    fun `change of AsStringClassOption defaults should be applied & also empty the caches`() {
        // arrange
        open class TestClass {
            @AsStringOmit
            val toStringPrefix = "(IntelliJ generated toString)"

            val str600 = "x".repeat(600)
            open val aNullValue: Any? = null

            override fun toString(): String {
                return "$toStringPrefix TestClass(str600='$str600', aNullValue=$aNullValue)"
            }
        }

        val initialIncludeIdentHash = AsStringClassOption.defaultOption.includeIdentityHash
        assertThat(initialIncludeIdentHash)
            .isFalse
            .isEqualTo(initialIncludeIdentityHash)

        val initialToStringPref = AsStringClassOption.defaultOption.toStringPreference
        assertThat(initialToStringPref)
            .isSameAs(USE_ASSTRING)
            .isEqualTo(initialToStringPreference)

        val testObj = TestClass()
        val testObjHash = testObj.hexHashCode()

        propsWithAnnotationsCacheByClass.reset()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        useToStringByClass.reset()
        assertThat(useToStringByClass.size).isZero

        // act, assert - defaults
        assertThat(testObj.asString())
            .doesNotContain(testObj.toStringPrefix) // asString() not calling toString()
            .doesNotContain(testObjHash)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(useToStringByClass.size).isEqualTo(1)

        // act, assert - include hash
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        assertThat(asStringClassOptionCache.size).isZero
        assertThat(useToStringByClass.size).isZero
        assertThat(testObj.asString())
            .doesNotContain(testObj.toStringPrefix) // asString() not calling toString()
            .contains(testObjHash)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)
        assertThat(useToStringByClass.size).isEqualTo(1)

        // act, assert - use toString()
        AsStringConfig().withToStringPreference(PREFER_TOSTRING).applyAsDefault()
        assertThat(asStringClassOptionCache.size).isZero
        assertThat(useToStringByClass.size).isZero
        assertThat(testObj.asString())
            .contains(testObj.toStringPrefix) // asString() calling toString()
            .doesNotContain(testObjHash) // not part of toString()
        assertThat(asStringClassOptionCache.size).isEqualTo(1)
        assertThat(useToStringByClass.size).isEqualTo(1)

        // act, assert - reset defaults
        restoreInitialAsStringClassOption()

        assertThat(asStringClassOptionCache.size).isZero
        assertThat(testObj.asString())
            .doesNotContain(testObjHash)
            .doesNotContain(testObj.toStringPrefix)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)
        assertThat(useToStringByClass.size).isEqualTo(1)

        val testClassSub: TestClass = object: TestClass() {
            override val aNullValue: String? = null
        }
        @Suppress("UNUSED_VARIABLE")
        val theStringValue = testClassSub.asString()
        assertThat(asStringClassOptionCache.size).isEqualTo(2)
        assertThat(useToStringByClass.size).isEqualTo(2)
    }

    @Test
    fun `change of AsStringClassOption defaults should not influence local AsStringClassOption settings`() {
        // arrange
        @AsStringClassOption(includeIdentityHash = true, toStringPreference = USE_ASSTRING)
        class MyTestClass {
            @AsStringOmit
            val toStringValue = "This is an object of class MyTestClass"
            @Suppress("unused")
            val str50: String = "a".repeat(10)
            override fun toString() = toStringValue
        }

        val testObj = MyTestClass()
        val testObjHash = testObj.hexHashCode()
        fun assertIt(description: String) {
            assertThat(testObj.asString())
                .`as`(description)
                .doesNotContain(testObj.toStringValue)
                .contains(testObjHash)
        }
        assertIt("Verifying `Before` situation")

        // act
        AsStringConfig()
            .withIncludeIdentityHash(false)
            .withToStringPreference(PREFER_TOSTRING)
            .applyAsDefault()

        // assert
        assertIt("`After` situation should be same as `Before`")
    }

    @Test
    fun `re-applying same values should not empty the useToStringByClass registry`() {
        // arrange
        AsStringConfig()
            .withToStringPreference(PREFER_TOSTRING)
            .withIncludeIdentityHash(true)
            .applyAsDefault()
        assertThat(useToStringByClass.size).isZero

        class MyTestClass
        val testStr = MyTestClass().asString()
        assertThat(testStr).isNotNull
        assertThat(useToStringByClass.size)
            .`as`("MyTestClass should be present in cache now")
            .isEqualTo(1)

        // act
        AsStringConfig()
            .withToStringPreference(PREFER_TOSTRING)
            .withIncludeIdentityHash(true)
            .applyAsDefault()
        // assert
        assertThat(useToStringByClass.size)
            .`as`("MyTestClass should still be present in cache")
            .isEqualTo(1)
    }

    @Test
    fun `change of AsStringClassOption default should notify subscriber`() {
        // arrange
        @Suppress("UNCHECKED_CAST")
        val asStringClassOptionClass: KClass<Annotation> = AsStringClassOption::class as KClass<Annotation>
        var notified = false
        val callback = {
            notified = true
        }
        asStringClassOptionClass.subscribeConfigChange(callback)
        // act
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        // assert
        assertThat(notified).isTrue
    }
}