package nl.kute.asstring.annotation.option

import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.core.asString
import nl.kute.asstring.core.defaults.initialIncludeIdentityHash
import nl.kute.asstring.core.defaults.initialPropertySorters
import nl.kute.asstring.core.defaults.initialSortNamesAlphabetic
import nl.kute.asstring.core.defaults.initialToStringPreference
import nl.kute.helper.base.ObjectsStackVerifier
import nl.kute.util.asHexString
import nl.kute.util.identityHashHex
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
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
    fun `when no changes yet, initial defaults should apply`() {
        with(AsStringClassOption.defaultOption) {
            assertThat(this.includeIdentityHash)
                .isEqualTo(initialIncludeIdentityHash)
                .isFalse
            assertThat(this.toStringPreference)
                .isEqualTo(initialToStringPreference)
                .isSameAs(USE_ASSTRING)
            assertThat(this.sortNamesAlphabetic)
                .isEqualTo(initialSortNamesAlphabetic)
                .isFalse
            assertThat(this.propertySorters)
                .isEqualTo(initialPropertySorters)
                .isEmpty()
        }
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
        asStringClassOptionCache.reset()
        assertThat(asStringClassOptionCache.size).isZero
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse

        open class MyTestClass
        val myTestObj = MyTestClass()
        val identityHash = myTestObj.identityHashHex

        assertThat(myTestObj.asString()).isEqualTo("MyTestClass()")
        assertThat(asStringClassOptionCache.size)
            .`as`("applying asString() should add the class to the cache")
            .isEqualTo(1)

        // act
        asStringConfig().withIncludeIdentityHash(true).applyAsDefault()

        // assert
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isTrue
        assertThat(asStringClassOptionCache.size)
            .`as`("Change of defaultAsStringClassOption should clear the cache")
            .isZero
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass@$identityHash()")
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        // act
        restoreInitialAsStringClassOption()
        assertThat(asStringClassOptionCache.size)
            .`as`("Change of defaultAsStringClassOption should clear the cache")
            .isZero
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass()")
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        // arrange
        val myTestObj2 = object : MyTestClass() {
            // empty
        }
        // act
        myTestObj2.asString()
        // assert
        assertThat(asStringClassOptionCache.size).isEqualTo(2)
    }

    @Test
    fun `repeated calls of asString on same class should cache AsStringClassOption only once`() {
        asStringClassOptionCache.reset()
        open class MyTestClass
        repeat(5) {
            MyTestClass().asString()
        }
        assertThat(asStringClassOptionCache.cache)
            .hasSize(1)
            .contains(entry(MyTestClass::class, AsStringClassOption.defaultOption))

        @AsStringClassOption(includeIdentityHash = true)
        class MyTestSubClass: MyTestClass()
        repeat(3) {
            MyTestSubClass().asString()
        }
        assertThat(asStringClassOptionCache.cache)
            .hasSize(2)
            .contains(
                entry(MyTestClass::class, AsStringClassOption.defaultOption),
                entry(MyTestSubClass::class, AsStringClassOption(includeIdentityHash = true))
                )

    }

    @Test
    fun `re-applying same value of AsStringClassOption should not empty the cache`() {
        // arrange
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash).isFalse
        asStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        assertThat(asStringClassOptionCache.size).isZero

        class MyTestClass
        val testStr = MyTestClass().asString()
        assertThat(testStr).isNotNull
        assertThat(asStringClassOptionCache.size)
            .`as`("MyTestClass should be present in cache now")
            .isEqualTo(1)

        // act
        @Suppress("KotlinConstantConditions")
        asStringConfig().withIncludeIdentityHash("aa" == "aa").applyAsDefault()
        // assert
        assertThat(asStringClassOptionCache.size)
            .`as`("MyTestClass should still be present in cache")
            .isEqualTo(1)
    }

}
