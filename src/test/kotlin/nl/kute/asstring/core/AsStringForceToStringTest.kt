package nl.kute.asstring.core

import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.reflection.util.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AsStringForceToStringTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        forceToStringClassRegistry.clearAll()
    }

    @Test
    fun `when a class matches a single force toString filter, asString() should return toString result`() {
        // arrange
        val toStringResult = "the toString() result"

        class TestClass {
            val aProp = "I am a prop"
            override fun toString() = toStringResult
        }
        val testObj = TestClass()

        assertThat(forceToStringClassRegistry.entries()).isEmpty()

        assertThat(testObj.asString())
            .`as`("No filter set, asString() should dynamically resolve properties")
            .isObjectAsString(
            TestClass::class.simplifyClassName(),
            "aProp=${testObj.aProp}"
        )

        // act
        val classMetaFilter: ClassMetaFilter = { meta -> meta.objectClass == TestClass::class }
        val filterId: Int = forceToStringClassRegistry.register(classMetaFilter)

        // assert
        assertThat(testObj.asString())
            .`as`("Matching filter is set to force toString(), so asString() should return toString() result")
            .isEqualTo(toStringResult)

        // act
        forceToStringClassRegistry.remove(filterId)

        // assert
        assertThat(forceToStringClassRegistry.entries()).isEmpty()
        assertThat(testObj.asString())
            .`as`("No filter present anymore, asString() should dynamically resolve properties again")
            .isObjectAsString(
            TestClass::class.simplifyClassName(),
            "aProp=${testObj.aProp}"
        )
    }

    @Test
    fun `when a class matches at least one force toString filter, asString() should return toString result`() {
        // arrange
        val toStringResult = "the toString() result"

        class TestClass {
            val aProp = "I am a prop"
            override fun toString() = toStringResult
        }
        val testObj = TestClass()

        // assert initial situation
        assertThat(forceToStringClassRegistry.entries()).isEmpty()
        assertThat(testObj.asString())
            .`as`("No filter set, asString() should dynamically resolve properties")
            .isObjectAsString(
                TestClass::class.simplifyClassName(),
                "aProp=${testObj.aProp}"
            )

        // create some filters
        val nonMatching1: ClassMetaFilter = { meta -> meta.objectClass != TestClass::class }
        val nonMatching2: ClassMetaFilter = { meta -> meta.packageName == "" }
        val matching1: ClassMetaFilter = { meta -> meta.packageName == this::class.java.packageName }
        val nonMatching4: ClassMetaFilter = { meta -> meta.objectClassName == "just some class" }
        val matching2: ClassMetaFilter = { meta -> meta.objectClassName!!.contains("TestClass") }

        // act: register filters
        forceToStringClassRegistry.register(nonMatching1)
        forceToStringClassRegistry.register(nonMatching2)
        forceToStringClassRegistry.register(matching1)
        forceToStringClassRegistry.register(nonMatching4)
        forceToStringClassRegistry.register(matching2)

        // assert - matching filter should cause toString() to be called
        assertThat(forceToStringClassRegistry.entries()).hasSize(5)
        assertThat(testObj.asString())
            .`as`("Matching filter is set to force toString(), so asString() should return toString() result")
            .isEqualTo(toStringResult)

        // act
        forceToStringClassRegistry.remove(matching1)
        assertThat(forceToStringClassRegistry.entries()).hasSize(4)

        // assert - matching filter should cause toString() to be called
        assertThat(forceToStringClassRegistry.entries()).hasSize(4)
        assertThat(testObj.asString())
            .`as`("Still a matching filter, so asString() should return toString() result")
            .isEqualTo(toStringResult)

        forceToStringClassRegistry.remove(matching2)
        assertThat(forceToStringClassRegistry.entries()).hasSize(3)

        // assert - no matching filter anymore, should use asString() now
        assertThat(testObj.asString())
            .`as`("No matching filter present anymore, asString() should dynamically resolve properties again")
            .isObjectAsString(
                TestClass::class.simplifyClassName(),
                "aProp=${testObj.aProp}"
            )
    }

}