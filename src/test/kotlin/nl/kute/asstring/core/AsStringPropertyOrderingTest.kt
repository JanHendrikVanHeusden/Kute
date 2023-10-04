package nl.kute.asstring.core

import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.asStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.namedvalues.NamedValue
import nl.kute.asstring.namedvalues.namedProp
import nl.kute.asstring.property.meta.PropertyValueMeta
import nl.kute.asstring.property.ranking.PropertyRanking
import nl.kute.asstring.property.ranking.impl.NoOpPropertyRanking
import nl.kute.asstring.property.ranking.impl.PropertyRankingByCommonNames
import nl.kute.asstring.property.ranking.impl.PropertyRankingByStringValueLength
import nl.kute.asstring.property.ranking.impl.ValueLengthRanking
import nl.kute.asstring.property.ranking.impl.ValueLengthRanking.Companion.getRank
import nl.kute.asstring.property.ranking.impl.ValueLengthRanking.L
import nl.kute.asstring.property.ranking.impl.ValueLengthRanking.M
import nl.kute.asstring.property.ranking.impl.ValueLengthRanking.XL
import nl.kute.asstring.property.ranking.propertyRankingRegistryByClass
import nl.kute.exception.throwableAsString
import nl.kute.logging.logger
import nl.kute.logging.resetStdOutLogger
import nl.kute.reflection.util.simplifyClassName
import nl.kute.test.helper.isObjectAsString
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

class AsStringPropertyOrderingTest {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
        resetStdOutLogger()
    }

    @Test
    fun `properties should be in expected order with single custom ranking`() {
        assertThat(TestClassWithSingleLetterProperties().asString())
            .isEqualTo("TestClassWithSingleLetterProperties(z=z, x=x, w=w, u=u, p=p, l=l, f=f, e=e, d=d, b=b)")
    }

    @Test
    fun `properties should be in expected order with multiple custom rankings - 1`() {
        val testObj = TestClassWithSingleLetterPropertiesAndDifferentValueLengths()
        assertThat(testObj.a.getRank()).isSameAs(L)
        assertThat(testObj.d.getRank()).isSameAs(M)
        assertThat(testObj.l.getRank()).isSameAs(ValueLengthRanking.S)
        assertThat(testObj.p.getRank()).isSameAs(XL)
        assertThat(testObj.asString())
            .isEqualTo("TestClassWithSingleLetterPropertiesAndDifferentValueLengths(" +
                    "z=z, x=x, w=w, u=u, l=, f=f, e=e, b=b, d=${"d".repeat(30)}, a=${"a".repeat(80)}, p=${"p".repeat(120)})")
    }

    @Test
    fun `properties should be in expected order with multiple custom rankings - 2`() {
        val testObj = CommonNames()
        with(testObj) {
            assertThat(testObj.asString())
                .isEqualTo("CommonNames(" +
                        "id=$id" +
                        ", myUuid=$myUuid" +
                        ", someVal=$someVal" +
                        ", myRef=$myRef" +
                        ", aNumber=$aNumber" +
                        ", a40String=$a40String" +
                        ", a70String=$a70String" +
                        ", anObject=${anObject.asString()}" +
                        ", aJsonVal=$aJsonVal" +
                        ", a250String=$a250String" +
                        ")"
                )
        }
    }

    @Test
    fun `properties with Ascii names should be ordered alphabetically as specified`() {
        val testObj = AsciiNames()
        val sortedProps = AsciiNames::class.memberProperties
            .sortedBy { it.name }
            .joinToString { "${it.name}=${it.get(testObj)}" }
        assertThat(testObj.asString())
            .isEqualTo("${testObj::class.simpleName}($sortedProps)")
            .isEqualTo("${testObj::class.simpleName}(aap=monkey, ezel=donkey, kat=cat, paard=horse, zebra=zebra)")
    }

    @Test
    fun `properties should be ordered alphabetically case insensitive as specified`() {
        // act, assert
        // This test will fail if property names are sorted case-sensitive
        assertThat(AClass().asString())
            .isEqualTo("AClass(anInterfaceProp=an interface property, aProp=a property)")
    }

    @Test
    fun `properties that differ in case only should be rendered correctly when ordered alphabetically`() {
        // act, assert
        // This test will fail if property names are sorted case-sensitive
        println(AClassWithPropsThatDifferInCaseOnly().asString())
        assertThat(AClassWithPropsThatDifferInCaseOnly().asString())
            .isObjectAsString("AClassWithPropsThatDifferInCaseOnly",
                "aProp=My name has an uppercase letter",
                "aprop=My name is lower case only",
                "APROP=My name is UPPER case only"
            )
    }

    @Test
    fun `properties with non-Ascii characters should be ordered alphabetically as specified`() {
        val testObj = WeirdNames()

        val sortedProps = WeirdNames::class.memberProperties
            .sortedBy { it.name }
            .joinToString { "${it.name}=${it.get(testObj)}" }
        assertThat(testObj.asString())
            .isEqualTo("${testObj::class.simpleName}($sortedProps)")
            .isEqualTo("${testObj::class.simpleName}(&=ampersand, ®=registered trademark, ๚=it's a Thai character, ⏰=alarm clock, ㆠ=Bopomofo letter bu, 𐔰=it's Caucasian, 👼=baby angel, 👾=alien monster)")
    }

    @Test
    fun `instance properties and companion properties should be sorted by same sorters if not specified at companion`() {
        // arrange
        val testObj = WithPropsAndCompanionProps()
        // check annotations
        val asStringClassOption = testObj::class.annotations.first { it is AsStringClassOption } as AsStringClassOption
        assertThat(asStringClassOption.sortNamesAlphabetic).isTrue
        assertThat(asStringClassOption.propertySorters).isEqualTo(arrayOf(PropertyRankingByStringValueLength::class))
        assertThat(testObj::class.companionObject!!.annotations.isEmpty())
        // act, assert
        assertThat(testObj.asString())
            .contains(testObj::class.companionObjectInstance.asString())
            .isEqualTo("WithPropsAndCompanionProps(" +
                    "a=a, p=p, x=x, u=502192e9-638c-41cb-ab67-e2082f07705f," +
                    " aLongString=${testObj.aLongString}," +
                    " companion: Companion(" +
                    "a=ca, p=p, x=cx, u=abc71969-adc6-4538-bb3b-d5aad893185f," +
                    " aLongString=${WithPropsAndCompanionProps.aLongString}" +
                    "))")
    }

    @Test
    fun `subclasses should not have the super-class's companion object rendered`() {
        // arrange
        val testObj = SubClassOfWithPropsAndCompanionPropsToBeReverseSorted()
        // check annotations
        val asStringClassOption = testObj::class.annotations.first { it is AsStringClassOption } as AsStringClassOption
        assertThat(asStringClassOption.sortNamesAlphabetic).isTrue
        assertThat(asStringClassOption.propertySorters)
            .isEqualTo(arrayOf(ReverseAlphabeticPropertyRanking::class, ReverseAlphabeticPropertyRanking2nd::class))
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("SubClassOfWithPropsAndCompanionPropsToBeReverseSorted" +
                "(x=x, u=502192e9-638c-41cb-ab67-e2082f07705f, p=p, aLongString=${testObj.aLongString}, a=a)"
            )
    }

    @Test
    fun `property rendering should follow default sorting if class not annotated`() {
        // arrange
        val testObj = NoAsStringClassOption()
        // check annotations and default
        val asStringClassOption = testObj::class.annotations.firstOrNull { it is AsStringClassOption } as AsStringClassOption?
        assertThat(asStringClassOption).isNull()

        // Apply some weird sorting
        AsStringConfig()
            .withPropertySorters(PropertyRankingByOddEvenHashCodeOfPropName::class)
            .applyAsDefault()
        // apply the weird sorting to the props to get the expected props ordering
        var expectedPropValueString = NoAsStringClassOption::class.memberProperties
            .sortedBy { prop -> prop.name.hashCode().let { if (it % 2 == 0) -it else it } }
            .joinToString(separator = ", ") { "${it.name}=${it.get(testObj)}" }

        // act, assert
        assertThat(testObj.asString()).contains(expectedPropValueString)

        // arrange: alphabetic sorting
        AsStringConfig()
            .withPropertySorters(ReverseAlphabeticPropertyRanking::class, ReverseAlphabeticPropertyRanking2nd::class)
            .applyAsDefault()
        // apply alphabetic sorting to the props
        expectedPropValueString = NoAsStringClassOption::class.memberProperties
            .sortedByDescending { prop -> prop.name }
            .joinToString(separator = ", ") { "${it.name}=${it.get(testObj)}" }

        // act, assert
        assertThat(testObj.asString()).contains(expectedPropValueString)
    }

    @Test
    fun `named values should be rendered at the end of the asString result, in order of input`() {
        // arrange
        val testObj = WithPropsAndCompanionProps()
        val asStringClassOption = testObj::class.annotations.first { it is AsStringClassOption } as AsStringClassOption
        assertThat(asStringClassOption.sortNamesAlphabetic).isTrue
        assertThat(asStringClassOption.propertySorters).isEqualTo(arrayOf(PropertyRankingByStringValueLength::class))
        assertThat(testObj::class.companionObject!!.annotations.isEmpty())
        val asStringBuilder = testObj
            .asStringBuilder()
            .withAlsoNamed(
                testObj::aLongString.namedProp(testObj),
                testObj::p.namedProp(testObj),
                NamedValue("aNamedValue", "just something")
            )
        // act, assert
        assertThat(asStringBuilder.asString())
            .contains(testObj::class.companionObjectInstance.asString())
            .`as`("Should neither be ordered alphabetically nor sorted otherwise; should just follow input order of named values")
            .endsWith(", ${testObj::aLongString.name}=${testObj.aLongString}" +
                    ", ${testObj::p.name}=${testObj.p}" +
                    ", aNamedValue=just something)"
            )
    }

    @Test
    fun `Exceptions thrown by getRank() should be handled and logged, and the rank should not be used for property ordering`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        @Suppress("unused")
        class TestClass {
            val p5 = 5
            val p1 = 1
            val p4 = 4
            val p3 = 3
            val p2 = 2
        }
        val testObj = TestClass()
        val expected = testObj.asString()
        val rankClassName = ThrowingPropertyRanking::class.simplifyClassName()

        asStringConfig().withPropertySorters(ThrowingPropertyRanking::class).applyAsDefault()
        // ThrowingPropertyRanking throws this exception on getRank()
        val thrown = ThrowingPropertyRanking.throwingForRanking

        // act, assert
        assertThat(testObj.asString())
            .`as`("Property ordering should not be affected because getRank() throws all the time")
            .isEqualTo(expected)

        assertThat(logMsg).contains(
            "$rankClassName threw exception while evaluating",
            "The $rankClassName will be removed from the registry (so not used anymore)",
            thrown.throwableAsString()
        )
        assertThat(propertyRankingRegistryByClass)
            .`as`("The $rankClassName instance registered should be replaced by a no-op instance, to avoid further exceptions")
            .contains(entry(ThrowingPropertyRanking::class, NoOpPropertyRanking.instance))
            .doesNotContain(entry(ThrowingPropertyRanking::class, ThrowingPropertyRanking.instance))

        logMsg = ""
        // try again
        assertThat(testObj.asString())
            .`as`("Property ordering should not be affected because getRank() throws all the time")
            .isEqualTo(expected)
        assertThat(logMsg)
            .`as`("no exceptions should occur anymore, logMsg should be empty")
            .isEmpty()
    }

}

// region ~ Classes etc. to be used for testing

/** Property sorting by 1st letter only! */
private class ReverseAlphabeticPropertyRanking: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int =
        (-propertyValueMeta.propertyName[0].code)
}

private class ReverseAlphabeticPropertyRanking2nd: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int {
        return try {
            return (-propertyValueMeta.propertyName[1].code)
        } catch (e: IndexOutOfBoundsException) {
            // It's dirty. But it's test-code ʘ‿ʘ
            Int.MAX_VALUE
        }
    }
}

private class PropertyRankingByOddEvenHashCodeOfPropName: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int =
        propertyValueMeta.propertyName.hashCode().let { if (it % 2 == 0) -it else it }
}

private class ThrowingPropertyRanking: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int = throw throwingForRanking
    companion object {
        val throwingForRanking = IllegalArgumentException("throwing IAE")
        val instance = ThrowingPropertyRanking()
    }
}

@Suppress("unused")
@AsStringClassOption(sortNamesAlphabetic = true, propertySorters = [ReverseAlphabeticPropertyRanking::class])
private open class TestClassWithSingleLetterProperties {
    open val x = "x"
    open val  b = "b"
    open val  u = "u"
    open val  d = "d"
    open val  f = "f"
    open val  z = "z"
    open val  e = "e"
    open val  p = "p"
    open val  l = "l"
    open val  w = "w"
}

@Suppress("unused")
@AsStringClassOption(sortNamesAlphabetic = false, propertySorters = [PropertyRankingByStringValueLength::class, ReverseAlphabeticPropertyRanking::class])
private open class TestClassWithSingleLetterPropertiesAndDifferentValueLengths: TestClassWithSingleLetterProperties() {
    open val a = "a".repeat(80)
    override val d: String = "d".repeat(30)
    override val l: String = ""
    override val p: String = "p".repeat(120)
}

@Suppress("NonAsciiCharacters", "PropertyName", "unused")
@AsStringClassOption(sortNamesAlphabetic = true)
class WeirdNames {
    val `®` = "registered trademark"
    val `⏰` = "alarm clock"
    val 𐔰 = "it's Caucasian"
    val `👾` = "alien monster"
    val `&` = "ampersand"
    val `👼` = "baby angel"
    val `๚` = "it's a Thai character"
    val ㆠ = "Bopomofo letter bu"
}

@Suppress("unused")
@AsStringClassOption(sortNamesAlphabetic = true)
class AsciiNames {
    // Your first Dutch lesson ;-)
    val paard = "horse"
    val kat = "cat"
    val zebra = "zebra"
    val aap = "monkey"
    val ezel = "donkey"
}

@AsStringClassOption(
    sortNamesAlphabetic = true,
    propertySorters = [PropertyRankingByCommonNames::class, PropertyRankingByStringValueLength::class]
)
class CommonNames {
    val myUuid: String = "uuid in name only :-)"
    val myRef = "31464769-3 / 347689"
    val a70String: String = RandomStringUtils.randomAlphabetic(70)
    val anObject = AsciiNames()
    val a250String: String = RandomStringUtils.randomAlphabetic(250)
    val a40String: String = RandomStringUtils.randomAlphabetic(40)
    val someVal: UUID = UUID.randomUUID()
    val id = 1234567
    val aJsonVal = """{"name":"Nathaly", "age":30, "car":"Maserati GranCabrio", "job":"CFO}"""
    val aNumber = 25
}

@Suppress("unused")
@AsStringClassOption(includeCompanion = true, sortNamesAlphabetic = true, propertySorters = [PropertyRankingByStringValueLength::class])
open class WithPropsAndCompanionProps {
    val x = "x"
    val a = "a"
    val aLongString = "aLongString ".repeat(20)
    val p = "p"
    val u: UUID = UUID.fromString("502192e9-638c-41cb-ab67-e2082f07705f")
    companion object {
        val x = "cx"
        val a = "ca"
        val aLongString = "aCompanionString ".repeat(15)
        val p = "p"
        val u: UUID = UUID.fromString("abc71969-adc6-4538-bb3b-d5aad893185f")
    }
}

@AsStringClassOption(
    includeCompanion = true,
    sortNamesAlphabetic = true,
    propertySorters = [ReverseAlphabeticPropertyRanking::class, ReverseAlphabeticPropertyRanking2nd::class]
)
class SubClassOfWithPropsAndCompanionPropsToBeReverseSorted: WithPropsAndCompanionProps()

@Suppress("unused")
class NoAsStringClassOption {
    val x = "x"
    val a = "a"
    val aLongString = "aLongString ".repeat(20)
    val p = "p"
    val u: UUID = UUID.fromString("502192e9-638c-41cb-ab67-e2082f07705f")
}

@AsStringClassOption(sortNamesAlphabetic = true)
private interface AnInterface {
    val anInterfaceProp: String
        get() = "an interface property"
}

@Suppress("unused")
class AClass: AnInterface {
    val aProp = "a property"
}

@Suppress("unused")
class AClassWithPropsThatDifferInCaseOnly {
    val aProp = "My name has an uppercase letter"
    val aprop = "My name is lower case only"
    val APROP = "My name is UPPER case only"
}

// endregion

