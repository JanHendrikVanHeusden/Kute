package nl.kute.core.annotation

import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.config.AsStringConfig
import nl.kute.config.restoreInitialAsStringClassOption
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.asStringClassOptionCacheSize
import nl.kute.core.annotation.option.resetAsStringClassOptionCache
import nl.kute.core.asString
import nl.kute.util.asHexString
import nl.kute.util.identityHashHex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AsStringClassOptionTest: ObjectsStackVerifier {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
    }

    @Test
    fun `class annotation with AsStringClassOption should be honoured`() {
        // arrange
        @AsStringClassOption(includeIdentityHash = true)
        open class MyTestClass

        @AsStringClassOption(includeIdentityHash = false)
        class MySubClass: MyTestClass()

        val myTestObj = MyTestClass()
        val identityHash = System.identityHashCode(myTestObj).asHexString
        val mySubObj = MySubClass()

        // act, assert
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass@$identityHash()")
        assertThat(mySubObj.asString()).isEqualTo("MySubClass()")
    }

    @Test
    fun `class annotation with default AsStringClassOption should apply default`() {
        // arrange
        @AsStringClassOption(includeIdentityHash = true)
        open class MyTestClass

        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        @AsStringClassOption // applies default (false)
        class MySubClass: MyTestClass()

        val myTestObj = MyTestClass()
        val mySubObj = MySubClass()
        val identityHash = System.identityHashCode(myTestObj).asHexString

        // act, assert
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass@$identityHash()")
        assertThat(mySubObj.asString()).isEqualTo("MySubClass()")
    }

    @Test
    fun `subclass should inherit AsStringClassOption`() {
        // arrange
        @AsStringClassOption(includeIdentityHash = true)
        open class MyTestClass

        class MySubClass: MyTestClass()

        val myTestObj = MyTestClass()
        val identityHash = System.identityHashCode(myTestObj).asHexString
        // act, assert
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass@$identityHash()")

        // arrange
        val mySubObj = MySubClass()
        val identityHashSub = System.identityHashCode(mySubObj).asHexString
        // act, assert
        assertThat(mySubObj.asString()).isEqualTo("MySubClass@$identityHashSub()")
    }

    @Test
    fun `change of default AsStringClassOption should be applied, and clear the cache when needed`() {
        // arrange
        resetAsStringClassOptionCache()
        assertThat(asStringClassOptionCacheSize).isZero
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse

        open class MyTestClass
        val myTestObj = MyTestClass()
        val identityHash = myTestObj.identityHashHex

        assertThat(myTestObj.asString()).isEqualTo("MyTestClass()")
        assertThat(asStringClassOptionCacheSize)
            .`as`("applying asString() should add the class to the cache")
            .isEqualTo(1)

        // act
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()

        // assert
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isTrue
        assertThat(asStringClassOptionCacheSize)
            .`as`("Change of defaultAsStringClassOption should clear the cache")
            .isEqualTo(0)
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass@$identityHash()")
        assertThat(asStringClassOptionCacheSize).isEqualTo(1)

        // act
        restoreInitialAsStringClassOption()
        assertThat(asStringClassOptionCacheSize)
            .`as`("Change of defaultAsStringClassOption should clear the cache")
            .isEqualTo(0)
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass()")
        assertThat(asStringClassOptionCacheSize).isEqualTo(1)

        // arrange
        val myTestObj2 = object : MyTestClass() {
            // empty
        }
        // act
        myTestObj2.asString()
        // assert
        assertThat(asStringClassOptionCacheSize).isEqualTo(2)
    }

    @Test
    fun `repeated calls of asString on same class should be cached only once`() {
        resetAsStringClassOptionCache()
        class MyTestClass
        repeat(5) {
            MyTestClass().asString()
            assertThat(asStringClassOptionCacheSize).isEqualTo(1)
        }
    }

    @Test
    fun `re-applying same value of AsStringClassOption should not empty the cache`() {
        // arrange
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        assertThat(asStringClassOptionCacheSize).isZero

        class MyTestClass
        val testStr = MyTestClass().asString()
        assertThat(testStr).isNotNull
        assertThat(asStringClassOptionCacheSize)
            .`as`("MyTestClass should be present in cache now")
            .isEqualTo(1)

        // act
        @Suppress("KotlinConstantConditions")
        AsStringConfig().withIncludeIdentityHash("aa" == "aa").applyAsDefault()
        // assert
        assertThat(asStringClassOptionCacheSize)
            .`as`("MyTestClass should still be present in cache")
            .isEqualTo(1)
    }

}
