package nl.kute.asstring.filter

import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.core.asString
import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.asstring.property.filter.PropertyMetaFilter
import nl.kute.asstring.property.filter.propertyOmitFilter
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
        propertyOmitFilter.clearAllFilters()
    }

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
        val filter: PropertyMetaFilter = { meta -> meta.propertyName == "filterMeOut" }
        propertyOmitFilter.addFilter(filter)

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
        val filter: PropertyMetaFilter = { m -> m.isCollectionLike }
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
        val filter1: PropertyMetaFilter = { meta ->
            meta.objectClass == MyClassForPropertyFiltering::class
                    && meta.propertyName == "filterMeOut"
        }
        val filter2: PropertyMetaFilter = { meta ->
            meta.returnType.classifier == List::class
        }
        val filter3: PropertyMetaFilter = { meta ->
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
    fun `filter registry should not contain duplicates when applying the same filter more than once`() {
        val filter1: PropertyMetaFilter = { _ -> true }
        val filter2: PropertyMetaFilter = { _ -> true } // identical, but not same object
        propertyOmitFilter.setFilters(filter1, filter2)
        assertThat(propertyOmitFilter.getFilters())
            .containsExactlyInAnyOrder(filter1, filter2)
            .hasSize(2)

        propertyOmitFilter.setFilters(filter1, filter1)
        assertThat(propertyOmitFilter.getFilters())
            .containsExactly(filter1)
            .hasSize(1)

        propertyOmitFilter.addFilter(filter1)
        assertThat(propertyOmitFilter.getFilters())
            .containsExactly(filter1)
            .hasSize(1)

        propertyOmitFilter.addFilter(filter2)
        assertThat(propertyOmitFilter.getFilters())
            .containsExactlyInAnyOrder(filter1, filter2)
            .hasSize(2)
    }

}