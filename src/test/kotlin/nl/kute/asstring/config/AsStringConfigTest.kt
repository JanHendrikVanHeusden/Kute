package nl.kute.asstring.config

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.core.ClassMetaFilter
import nl.kute.asstring.core.PropertyMetaFilter
import nl.kute.asstring.core.defaults.initialAsStringClassOption
import nl.kute.asstring.core.defaults.initialAsStringOption
import nl.kute.asstring.core.forceToStringClassRegistry
import nl.kute.asstring.core.propertyOmitFilterRegistry
import nl.kute.asstring.property.meta.ClassMeta
import nl.kute.asstring.property.meta.PropertyMeta
import nl.kute.asstring.property.meta.PropertyValueMeta
import nl.kute.asstring.property.ranking.NoOpPropertyRanking
import nl.kute.asstring.property.ranking.PropertyRankable
import nl.kute.asstring.property.ranking.PropertyRanking
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Predicate
import kotlin.reflect.KClass

class AsStringConfigTest {

// region ~ preparation

    private val preTestShowNullAs = "pre-test"
    private val preTestSurrounder = PropertyValueSurrounder.`§§`
    private val preTestMaxStringValLength = 568568235
    private val preTestElementsLimit = 2346895

    private val preTestAsStringOption = AsStringOption(
        showNullAs = preTestShowNullAs,
        surroundPropValue = preTestSurrounder,
        propMaxStringValueLength = preTestMaxStringValLength,
        elementsLimit = preTestElementsLimit
    )

    private val preTestIncludeIdentityHash = false
    private val preTestToStringPreference = USE_ASSTRING
    private val preTestIncludeCompanion = false
    private val preTestSortNamesAlphabetic = true
    private val preTestPropertySorters: Array<KClass<out PropertyRankable<*>>> =
        (1..17).map { NoOpPropertyRanking::class }.toTypedArray()

    private val preTestAsStringClassOption = AsStringClassOption(
        includeIdentityHash = preTestIncludeIdentityHash,
        toStringPreference = preTestToStringPreference,
        includeCompanion = preTestIncludeCompanion,
        sortNamesAlphabetic = preTestSortNamesAlphabetic,
        propertySorters = preTestPropertySorters
    )

    private val preTestPropertyOmitFilter1: PropertyMetaFilter = let {
        class LocalClassForProp
        val filter: PropertyMetaFilter = { m -> m.objectClass == LocalClassForProp::class }
        filter
    }
    private val preTestPropertyOmitFilter2: PropertyMetaFilter = let {
        val filter: PropertyMetaFilter = { m -> m.packageName == RandomStringUtils.randomAlphabetic(4) }
        filter
    }
    private val preTestPropertyOmitFilters: Collection<PropertyMetaFilter> =
        listOf(preTestPropertyOmitFilter1, preTestPropertyOmitFilter2)

    private val preTestForceToStringFilter1: ClassMetaFilter = let {
        class LocalClass
        val filter: ClassMetaFilter = { m -> m.objectClass == LocalClass::class }
        filter
    }
    private val preTestForceToStringFilter2: ClassMetaFilter = let {
        val filter: ClassMetaFilter = { m -> m.objectClassName == RandomStringUtils.randomAlphabetic(5) }
        filter
    }
    private val preTestForceToStringFilters: Collection<ClassMetaFilter> =
        listOf(preTestForceToStringFilter1, preTestForceToStringFilter2)

    @BeforeEach
    fun setUp() {
        restoreInitialDefaults()
        prepareTest()
    }

    @AfterEach
    fun restoreInitialDefaults() {

        restoreInitialAsStringOption()
        restoreInitialAsStringClassOption()
        forceToStringClassRegistry.clearAll()
        propertyOmitFilterRegistry.clearAll()

        assertThat(AsStringOption.defaultOption).isEqualTo(initialAsStringOption)
        assertThat(AsStringClassOption.defaultOption).isEqualTo(initialAsStringClassOption)
        assertThat(forceToStringClassRegistry.entries()).isEmpty()
        assertThat(propertyOmitFilterRegistry.entries()).isEmpty()
    }

    private fun prepareTest() {
        AsStringOption.defaultOption = preTestAsStringOption
        AsStringClassOption.defaultOption = preTestAsStringClassOption
        preTestPropertyOmitFilters.forEach {
            propertyOmitFilterRegistry.register(it)
        }
        preTestForceToStringFilters.forEach {
            forceToStringClassRegistry.register(it)
        }
    }

// endregion

// region ~ Tests

    @Test
    fun withShowNullAs() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = "nothing here"
        val config = asStringConfig().withShowNullAs(newVal)
        // assert
        assertThat(AsStringOption.defaultOption.showNullAs)
            .`as`("Config not applied yet")
            .isEqualTo(preTestShowNullAs)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringOption.defaultOption.showNullAs)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption(showNullAs = newVal)
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withSurroundPropValue() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = PropertyValueSurrounder.`""`
        val config = asStringConfig().withSurroundPropValue(newVal)
        // assert
        assertThat(AsStringOption.defaultOption.surroundPropValue)
            .`as`("Config not applied yet")
            .isEqualTo(preTestSurrounder)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringOption.defaultOption.surroundPropValue)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption(surroundPropValue = newVal)
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withMaxPropertyStringLength() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = 1555
        val config = asStringConfig().withMaxPropertyStringLength(newVal)
        // assert
        assertThat(AsStringOption.defaultOption.propMaxStringValueLength)
            .`as`("Config not applied yet")
            .isEqualTo(preTestMaxStringValLength)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringOption.defaultOption.propMaxStringValueLength)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption(propMaxStringValueLength = newVal)
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withElementsLimit() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = 1462
        val config = asStringConfig().withElementsLimit(newVal)
        // assert
        assertThat(AsStringOption.defaultOption.elementsLimit)
            .`as`("Config not applied yet")
            .isEqualTo(preTestElementsLimit)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringOption.defaultOption.elementsLimit)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption(elementsLimit = newVal)
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withIncludeIdentityHash() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = true
        val config = asStringConfig().withIncludeIdentityHash(newVal)
        // assert
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash)
            .`as`("Config not applied yet")
            .isEqualTo(preTestIncludeIdentityHash)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringClassOption.defaultOption.includeIdentityHash)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption(includeIdentityHash = newVal)
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withToStringPreference() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = PREFER_TOSTRING
        val config = asStringConfig().withToStringPreference(newVal)
        // assert
        assertThat(AsStringClassOption.defaultOption.toStringPreference)
            .`as`("Config not applied yet")
            .isEqualTo(preTestToStringPreference)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringClassOption.defaultOption.toStringPreference)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption(toStringPreference = newVal)
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withIncludeCompanion() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = true
        val config = asStringConfig().withIncludeCompanion(newVal)
        // assert
        assertThat(AsStringClassOption.defaultOption.includeCompanion)
            .`as`("Config not applied yet")
            .isEqualTo(preTestIncludeCompanion)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringClassOption.defaultOption.includeCompanion)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption(includeCompanion = newVal)
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withPropertiesAlphabetic() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val newVal = true
        val config = asStringConfig().withPropertiesAlphabetic(newVal)
        // assert
        assertThat(AsStringClassOption.defaultOption.sortNamesAlphabetic)
            .`as`("Config not applied yet")
            .isEqualTo(preTestSortNamesAlphabetic)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringClassOption.defaultOption.sortNamesAlphabetic)
            .isEqualTo(newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption(sortNamesAlphabetic = newVal)
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withPropertySorters() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        class PropRankable1: PropertyRanking() {
            override fun getRank(propertyValueMeta: PropertyValueMeta): Int = 14
        }
        class PropRankable2: PropertyRanking() {
            override fun getRank(propertyValueMeta: PropertyValueMeta): Int = 38
        }
        val newVal: Array<KClass<out PropertyRankable<*>>> = arrayOf(PropRankable1::class, PropRankable2::class)
        val config = asStringConfig().withPropertySorters(*newVal)
        // assert
        assertThat(AsStringClassOption.defaultOption.propertySorters)
            .`as`("Config not applied yet")
            .isEqualTo(preTestPropertySorters)

        // act
        config.applyAsDefault()
        // assert
        assertThat(AsStringClassOption.defaultOption.propertySorters)
            .containsExactlyInAnyOrder(*newVal)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption(propertySorters = newVal)
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withPropertyOmitFilters() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val propOmitFilter1: PropertyMetaFilter = { m -> m.propertyName == "bogus" }
        val propOmitFilter2: PropertyMetaFilter = { m -> m.propertyName == RandomStringUtils.randomAlphabetic(7) }
        val propOmitFilter3: PropertyMetaFilter = { m -> m.objectClass == this::class }

        val newVal = arrayOf(propOmitFilter1, propOmitFilter2, propOmitFilter3)
        val config = asStringConfig().withPropertyOmitFilters(*newVal)
        // assert
        assertThat(propertyOmitFilterRegistry.entries())
            .containsExactlyInAnyOrderElementsOf(preTestPropertyOmitFilters)

        // act
        config.applyAsDefault()
        // assert
        assertThat(propertyOmitFilterRegistry.entries())
            .containsExactlyInAnyOrder(propOmitFilter1, propOmitFilter2, propOmitFilter3)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry(propertyOmitFilters = newVal.toList())
        assertForceToStringClassRegistry()
    }

    @Test
    fun withPropertyOmitFilterPredicates() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val filter1: PropertyMetaFilter = { m -> m.packageName == "hi" }
        val filter2: PropertyMetaFilter = { m -> m.propertyName == "really?" }
        val filter3: PropertyMetaFilter = { m -> m.objectClass == Predicate::class }
        val pred1: Predicate<PropertyMeta> = Predicate(filter1)
        val pred2: Predicate<PropertyMeta> = Predicate(filter2)
        val pred3: Predicate<PropertyMeta> = Predicate(filter3)

        val newVal = arrayOf(pred1, pred2, pred3)
        val config = asStringConfig().withPropertyOmitFilterPredicates(*newVal)
        // assert
        assertThat(propertyOmitFilterRegistry.entries())
            .containsExactlyInAnyOrderElementsOf(preTestPropertyOmitFilters)

        // act
        config.applyAsDefault()
        // assert
        assertThat(propertyOmitFilterRegistry.entries()).hasSize(3)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertForceToStringClassRegistry()
    }

    @Test
    fun withForceToStringFilters() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val forceToStringFilter1: ClassMetaFilter = { m -> m.objectClassName == "weird" }
        val forceToStringFilter2: ClassMetaFilter = { m -> m.packageName == RandomStringUtils.randomAlphabetic(7) }
        val forceToStringFilter3: ClassMetaFilter = { m -> m.objectClass == this::class }

        val newVal = arrayOf(forceToStringFilter1, forceToStringFilter2, forceToStringFilter3)
        val config = asStringConfig().withForceToStringFilters(*newVal)
        // assert
        assertThat(forceToStringClassRegistry.entries())
            .containsExactlyInAnyOrderElementsOf(preTestForceToStringFilters)

        // act
        config.applyAsDefault()
        // assert
        assertThat(forceToStringClassRegistry.entries())
            .containsExactlyInAnyOrder(forceToStringFilter1, forceToStringFilter2, forceToStringFilter3)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry(forceToStringFilters = newVal.toList())
    }

    @Test
    fun withForceToStringFilterPredicates() {
        // assert initial situation
        assertPreTest()

        // arrange - set changes in config, but not applied yet
        val filter1: ClassMetaFilter = { m -> m.packageName == "hi" }
        val filter2: ClassMetaFilter = { m -> m.objectClassName == "really?" }
        val filter3: ClassMetaFilter = { m -> m.objectClass == Predicate::class }
        val pred1: Predicate<ClassMeta> = Predicate(filter1)
        val pred2: Predicate<ClassMeta> = Predicate(filter2)
        val pred3: Predicate<ClassMeta> = Predicate(filter3)

        val newVal = arrayOf(pred1, pred2, pred3)
        val config = asStringConfig().withForceToStringFilterPredicates(*newVal)
        // assert
        assertThat(forceToStringClassRegistry.entries())
            .containsExactlyInAnyOrderElementsOf(preTestForceToStringFilters)

        // act
        config.applyAsDefault()
        // assert
        assertThat(forceToStringClassRegistry.entries()).hasSize(3)

        // assert that all other values are unchanged
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
    }

    @Test
    fun getPropertyOmitFilters() {
        val filters = mapOf(preTestPropertyOmitFilter1 to 1, preTestPropertyOmitFilter2 to 2)
        assertThat(asStringConfig().getPropertyOmitFilters())
            .containsExactlyInAnyOrderEntriesOf(filters)
    }

    @Test
    fun getForceToStringFilters() {
        val filters = mapOf(preTestForceToStringFilter1 to 1, preTestForceToStringFilter2 to 2)
        assertThat(asStringConfig().getForceToStringFilters())
            .containsExactlyInAnyOrderEntriesOf(filters)
    }

    @Test
    fun getAsStringOptionDefault() {
        assertThat(asStringConfig().getAsStringOptionDefault())
            .isEqualTo(preTestAsStringOption)
    }

    @Test
    fun getAsStringClassOptionDefault() {
        assertThat(asStringConfig().getAsStringClassOptionDefault())
            .isEqualTo(preTestAsStringClassOption)
    }

// endregion

// region ~ test helper stuff

    private fun assertPreTest() {
        AsStringOption.defaultOption.assertAsStringOption()
        AsStringClassOption.defaultOption.assertAsStringClassOption()
        assertPropertyOmitFilterRegistry()
        assertForceToStringClassRegistry()
    }

    private fun AsStringOption.assertAsStringOption(
        showNullAs: String = preTestShowNullAs,
        surroundPropValue: PropertyValueSurrounder = preTestSurrounder,
        propMaxStringValueLength: Int = preTestMaxStringValLength,
        elementsLimit: Int = preTestElementsLimit) {

        assertThat(this).isEqualTo(
            AsStringOption(
                showNullAs = showNullAs,
                surroundPropValue = surroundPropValue,
                propMaxStringValueLength = propMaxStringValueLength,
                elementsLimit = elementsLimit
            )
        )
    }

    private fun AsStringClassOption.assertAsStringClassOption(
        includeIdentityHash: Boolean = preTestIncludeIdentityHash,
        toStringPreference: ToStringPreference = preTestToStringPreference,
        includeCompanion: Boolean = preTestIncludeCompanion,
        sortNamesAlphabetic: Boolean = preTestSortNamesAlphabetic,
        propertySorters: Array<KClass<out PropertyRankable<*>>> = preTestPropertySorters) {

        assertThat(this).isEqualTo(
            AsStringClassOption(
                includeIdentityHash = includeIdentityHash,
                toStringPreference = toStringPreference,
                includeCompanion = includeCompanion,
                sortNamesAlphabetic = sortNamesAlphabetic,
                propertySorters = propertySorters
            )
        )
    }

    private fun assertPropertyOmitFilterRegistry(propertyOmitFilters: Collection<PropertyMetaFilter> = preTestPropertyOmitFilters.toList()) {
        assertThat(propertyOmitFilterRegistry.entries())
            .containsExactlyInAnyOrderElementsOf(propertyOmitFilters)
    }

    private fun assertForceToStringClassRegistry(forceToStringFilters: Collection<ClassMetaFilter> = preTestForceToStringFilters.toList()) {
        assertThat(forceToStringClassRegistry.entries())
            .containsExactlyInAnyOrderElementsOf(forceToStringFilters)
    }

// endregion
}