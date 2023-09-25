package nl.kute.asstring.filter

import nl.kute.asstring.config.PropertyOmitFilter
import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.core.asString
import nl.kute.asstring.core.propertyOmitFiltering
import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import nl.kute.reflection.util.simplifyClassName
import nl.kute.util.throwableAsString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

class PropertyFilteringTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        propertyOmitFiltering.clearAll()
        resetStdOutLogger()
    }

// region ~ Tests

    @Test
    fun `properties should be filtered out according to single filter`() {
        // arrange
        @Suppress("unused")
        class MyClassForPropertyFiltering {
            val shouldBeThere = "I should be there"
            val filterMeOut = "I should be filtered out"
        }
        // without filter
        assertThat(asStringConfig().getPropertyOmitFilters()).isEmpty()
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should be filtered out"
            )

        // act: apply filter
        val filter: PropertyOmitFilter = { meta -> meta.propertyName == "filterMeOut" }
        propertyOmitFiltering.register(filter)

        // assert
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there"
            )
        assertThat(asStringConfig().getPropertyOmitFilters())
            .containsExactly(filter)
            .hasSize(1)
    }

    @Test
    fun `properties should be filtered by collection-like when specified`() {
        // arrange
        assertThat(asStringConfig().getPropertyOmitFilters()).isEmpty()

        @Suppress("unused")
        class TestPropFilter {
            val listToExclude = listOf("I will be excluded")
            val arrayToExclude = arrayOf("I will be excluded too")
            val mapToExclude = mapOf("I" to "will also be excluded")
            lateinit var lateInitToExclude: Collection<*>
            val included = "I will be there"

            override fun toString() = this.asString()
        }

        // act, assert
        assertThat(TestPropFilter().toString())
            .isObjectAsString("TestPropFilter",
                "listToExclude=[I will be excluded]",
                "arrayToExclude=[I will be excluded too]",
                "mapToExclude={I=will also be excluded}",
                "included=I will be there",
                "lateInitToExclude=null"
            )

        // arrange
        val filter: PropertyOmitFilter = { m -> m.isCollectionLike }
        asStringConfig().withPropertyOmitFilters(filter).applyAsDefault()

        // act, assert
        assertThat(TestPropFilter().toString())
            .isObjectAsString("TestPropFilter",
                "included=I will be there"
            )
    }

    @Test
    fun `properties should be filtered out according to multiple filters`() {
        // arrange
        @Suppress("unused")
        open class MyClassForPropertyFiltering {
            val shouldBeThere = "I should be there"
            open val filterMeOut = "I should be filtered out"
            private val listValToFilterOut: List<Any> = emptyList()
        }
        @Suppress("unused")
        open class MySubClassForPropertyFiltering: MyClassForPropertyFiltering() {
            override val filterMeOut = "I should not be filtered out"
            private val anotherListValToFilterOut: Collection<Any> = emptyList()
        }

        // without filters
        assertThat(asStringConfig().getPropertyOmitFilters()).isEmpty()

        // act, assert: no filters
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should be filtered out",
                "listValToFilterOut=[]"
            )
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
                "anotherListValToFilterOut=[]"
            )

        // arrange: filters
        val filter1: PropertyOmitFilter = { meta ->
            meta.objectClass == MyClassForPropertyFiltering::class
                    && meta.propertyName == "filterMeOut"
        }
        val filter2: PropertyOmitFilter = { meta ->
            meta.returnType.classifier == List::class
        }
        val filter3: PropertyOmitFilter = { meta ->
            meta.returnType.isSubtypeOf(typeOf<Collection<*>>())
        }
        // applying filters 1 & 2
        asStringConfig().withPropertyOmitFilters(filter1, filter2).applyAsDefault()

        assertThat(asStringConfig().getPropertyOmitFilters())
            .containsExactlyInAnyOrder(filter1, filter2)
            .hasSize(2)

        // act, assert
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
            )
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
                "anotherListValToFilterOut=[]"
            )

        // applying filters 1, 2, 3
        asStringConfig().withPropertyOmitFilters(filter1, filter2, filter3).applyAsDefault()

        assertThat(asStringConfig().getPropertyOmitFilters())
            .containsExactlyInAnyOrder(filter1, filter2, filter3)
            .hasSize(3)

        // act, assert
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
            )

        // arrange: reset filters
        asStringConfig().withPropertyOmitFilters().applyAsDefault()

        // act, assert: no filters
        assertThat(MyClassForPropertyFiltering().asString())
            .isObjectAsString("MyClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should be filtered out",
                "listValToFilterOut=[]"
            )
        assertThat(MySubClassForPropertyFiltering().asString())
            .isObjectAsString("MySubClassForPropertyFiltering",
                "shouldBeThere=I should be there",
                "filterMeOut=I should not be filtered out",
                "anotherListValToFilterOut=[]"
            )
    }

    @Test
    fun `private nested  classes should yield a decent result`() {
        // arrange
        val filter: PropertyOmitFilter =
            // this won't compile, as propA is private:
            // { meta -> meta.propertyName == PrivateLocalTestClass::propA.name }

            // same filter as comment above, but now from companion object - does it work?
            PrivateLocalTestClass.filter

        asStringConfig().withPropertyOmitFilters(filter).applyAsDefault()

        // act, assert: it works :-)
        assertThat(PrivateLocalTestClass().asString())
            .`as`("propA should be filtered out, the filter should handle it properly even if the property is private")
            .isObjectAsString(
            "PrivateLocalTestClass",
            // "propA=prop A",
            "propB=prop B"
        )
    }

    @Test
    fun `filters that throw an Exception should be removed and have their Exception logged`() {
        // arrange
        val errorMsg = "I crashed!"
        val exception = IllegalStateException(errorMsg)
        val throwingFilter: PropertyOmitFilter = { _ -> throw exception }
        val dummyFilter: PropertyOmitFilter = { _ -> false }
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        @Suppress("unused")
        class TestClass {
            val prop1 = "prop 1"
            val prop2 = "prop 2"
        }

        asStringConfig().withPropertyOmitFilters(throwingFilter, dummyFilter).applyAsDefault()

        // act, assert
        assertThat(propertyOmitFiltering.entries()).containsExactlyInAnyOrder(throwingFilter, dummyFilter)
        assertThat(TestClass().asString()).isObjectAsString(
            "TestClass",
            "prop1=prop 1",
            "prop2=prop 2"
        )
        assertThat(logMsg).contains(
            IllegalStateException::class.simplifyClassName(),
            errorMsg,
            exception.throwableAsString(),
            "the exception will be ignored, and the filter will be removed from the registry (not used anymore)"
        )
        assertThat(propertyOmitFiltering.entries())
            // throwingFilter should be removed when exception encountered
            .containsExactly(dummyFilter)

        logMsg = ""
        assertThat(TestClass().asString()).isObjectAsString(
            "TestClass",
            "prop1=prop 1",
            "prop2=prop 2"
        )
        assertThat(logMsg)
            .`as`("No exception should be logged anymore because throwing filter has been removed")
            .isEmpty()
    }

// endregion

// region ~ Classes, objects, helpers  etc. to be used for testing

    @Suppress("unused")
    private open class PrivateLocalTestClass {
        private val propA = "prop A"
        protected val propB = "prop B"

        companion object {
            val filter: PropertyOmitFilter = { meta ->
                meta.propertyName == PrivateLocalTestClass::propA.name
            }
        }
    }

// endregion

}