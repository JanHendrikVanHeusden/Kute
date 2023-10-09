package nl.kute.asstring.filter

import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.core.asString
import nl.kute.asstring.property.meta.ClassMeta
import nl.kute.reflection.util.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Predicate

class ForceToStringFilteringTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        asStringConfig()
            .withForceToStringFilters() // empty, so clears filters
            .applyAsDefault()
    }

    private val class1ToString = "toString() result of TestClass1"
    private val class2ToString = "toString() result of TestClass2"

    private inner class TestClass1 {
        @Suppress("unused")
        private val prop1: String? = null
        override fun toString(): String {
            return class1ToString
        }
    }

    private inner class TestClass2 {
        @Suppress("unused")
        private val prop2: String? = null
        override fun toString(): String {
            return class2ToString
        }
    }

    @Test
    fun `forceToString predicate should yield toString result when applied`() {
        // arrange
        val testObj1 = TestClass1()
        val testObj2 = TestClass2()
        assertThat(testObj1.asString())
            .isEqualTo(testObj1.javaClass.getSimpleName() + "(prop1=null)")
        assertThat(testObj2.asString())
            .isEqualTo(testObj2.javaClass.getSimpleName() + "(prop2=null)")
        val filter1 = Predicate { meta: ClassMeta -> meta.objectClassName == TestClass1::class.simplifyClassName() }
        // act
        asStringConfig()
            .withForceToStringFilterPredicates(filter1)
            .applyAsDefault()
        // assert
        assertThat(testObj1.asString())
            .`as`("Matches filter, so asString() should return toString() result")
            .isEqualTo(class1ToString)
        assertThat(testObj2.asString())
            .`as`("Does not match filter, so asString() should yield dynamic property resolution")
            .isEqualTo(testObj2.javaClass.getSimpleName() + "(prop2=null)")

        // arrange
        val filter2 = Predicate { meta: ClassMeta -> meta.objectClassName!!.contains("Class2") }
        // act
        asStringConfig().withForceToStringFilterPredicates(filter1, filter2).applyAsDefault()

        // assert
        assertThat(testObj1.asString())
            .`as`("Matches filter, so asString() should return toString() result")
            .isEqualTo(class1ToString)
        assertThat(testObj2.asString())
            .`as`("Matches filter, so asString() should return toString() result")
            .isEqualTo(class2ToString)

        // act
        asStringConfig().withForceToStringFilterPredicates(filter2).applyAsDefault()

        // assert
        assertThat(testObj1.asString())
            .`as`("Does not match filter, so asString() should yield dynamic property resolution")
            .isEqualTo(testObj1.javaClass.getSimpleName() + "(prop1=null)")
        assertThat(testObj2.asString())
            .`as`("Matches filter, so asString() should return toString() result")
            .isEqualTo(class2ToString)
    }

    @Test
    fun `forceToString with lambda should yield toString result when applied`() {
        // arrange
        val toStringResult = "toString() result"

        class TestClass {
            override fun toString(): String {
                return toStringResult
            }
        }

        val testObj = TestClass()
        assertThat(testObj.asString()).isEqualTo("TestClass()")

        // normally, one should use Predicate instead of Function1 (which is a *Kotlin reflection type*)
        // but, it can be done, and should work
        val toStringFilter = { meta: ClassMeta -> meta.objectClassName == "TestClass" }
        asStringConfig().withForceToStringFilters(toStringFilter).applyAsDefault()

        // act, assert
        assertThat(testObj.asString()).isEqualTo(toStringResult)
    }
}
