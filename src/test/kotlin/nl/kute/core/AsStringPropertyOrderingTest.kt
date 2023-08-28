package nl.kute.core

import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.namedvalues.namedProp
import nl.kute.core.namedvalues.namedValue
import nl.kute.core.property.ranking.PropertyRanking
import nl.kute.core.property.ranking.PropertyRankingByCommonNames
import nl.kute.core.property.ranking.PropertyRankingByLength
import nl.kute.core.property.ranking.PropertyValueMetaData
import nl.kute.core.property.ranking.ValueLengthRanking
import nl.kute.core.property.ranking.ValueLengthRanking.Companion.getRank
import nl.kute.core.property.ranking.ValueLengthRanking.L
import nl.kute.core.property.ranking.ValueLengthRanking.M
import nl.kute.core.property.ranking.ValueLengthRanking.XL
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberProperties

class AsStringPropertyOrderingTest {

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
        assertThat(asStringClassOption.propertySorters).isEqualTo(arrayOf(PropertyRankingByLength::class))
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
    fun `subclasses should not have the superclass's companion object rendered`() {
        // arrange
        val testObj = SubClassOfWithPropsAndCompanionProps()
        // check annotations
        val asStringClassOption = testObj::class.annotations.first { it is AsStringClassOption } as AsStringClassOption
        assertThat(asStringClassOption.sortNamesAlphabetic).isTrue
        assertThat(asStringClassOption.propertySorters)
            .isEqualTo(arrayOf(ReverseAlphabeticPropertyRanking::class, ReverseAlphabeticPropertyRanking2nd::class))
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("SubClassOfWithPropsAndCompanionProps" +
                "(x=x, u=502192e9-638c-41cb-ab67-e2082f07705f, p=p, aLongString=${testObj.aLongString}, a=a)"
            )
    }

    @Test
    fun `named values should be rendered at the end of the asString result, in order of input`() {
        // arrange
        val testObj = WithPropsAndCompanionProps()
        val asStringClassOption = testObj::class.annotations.first { it is AsStringClassOption } as AsStringClassOption
        assertThat(asStringClassOption.sortNamesAlphabetic).isTrue
        assertThat(asStringClassOption.propertySorters).isEqualTo(arrayOf(PropertyRankingByLength::class))
        assertThat(testObj::class.companionObject!!.annotations.isEmpty())
        val asStringBuilder = testObj
            .asStringBuilder()
            .withAlsoNamed(
                testObj.namedProp(testObj::aLongString),
                testObj.namedProp(testObj::p),
                "just something".namedValue("aNamedValue")
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

}

// region ~ Classes etc. to be used for testing

/** Property sorting by 1st letter only! */
private class ReverseAlphabeticPropertyRanking: PropertyRanking() {
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int =
        (-propertyValueMetaData.propertyName[0].code)
}

private class ReverseAlphabeticPropertyRanking2nd: PropertyRanking() {
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int {
        return try {
            return (-propertyValueMetaData.propertyName[1].code)
        } catch (e: IndexOutOfBoundsException) {
            // It's dirty. But it's test-code ʘ‿ʘ
            Int.MAX_VALUE
        }
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
@AsStringClassOption(sortNamesAlphabetic = false, propertySorters = [PropertyRankingByLength::class, ReverseAlphabeticPropertyRanking::class])
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
    propertySorters = [PropertyRankingByCommonNames::class, PropertyRankingByLength::class]
)
class CommonNames {
    val myUuid: String = "uuid in name only :-)"
    val myRef = "31464769-3 / 347689"
    val a70String = RandomStringUtils.randomAlphabetic(70)
    val anObject = AsciiNames()
    val a250String = RandomStringUtils.randomAlphabetic(250)
    val a40String = RandomStringUtils.randomAlphabetic(40)
    val someVal: UUID = UUID.randomUUID()
    val id = 1234567
    val aJsonVal = """{"name":"Nathaly", "age":30, "car":"Maserati GranCabrio", "job":"CFO}"""
    val aNumber = 25
}

@Suppress("unused")
@AsStringClassOption(sortNamesAlphabetic = true, propertySorters = [PropertyRankingByLength::class])
open class WithPropsAndCompanionProps {
    val x = "x"
    val a = "a"
    val aLongString = "aLongString ".repeat(20)
    val p = "p"
    val u = UUID.fromString("502192e9-638c-41cb-ab67-e2082f07705f")
    companion object {
        val x = "cx"
        val a = "ca"
        val aLongString = "aCompanionString ".repeat(15)
        val p = "p"
        val u = UUID.fromString("abc71969-adc6-4538-bb3b-d5aad893185f")
    }
}

@AsStringClassOption(
    sortNamesAlphabetic = true,
    propertySorters = [ReverseAlphabeticPropertyRanking::class, ReverseAlphabeticPropertyRanking2nd::class]
)
class SubClassOfWithPropsAndCompanionProps: WithPropsAndCompanionProps()

// endregion

