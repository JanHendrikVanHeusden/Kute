package nl.kute.core

import nl.kute.core.annotation.option.AsStringClassOption
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

}

// region ~ Classes etc. to be used for testing

/** Property sorting by 1st letter only! */
private class ReverseAlphabeticPropertyRanking: PropertyRanking() {
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int =
        (-propertyValueMetaData.propertyName[0].code)
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

// endregion

