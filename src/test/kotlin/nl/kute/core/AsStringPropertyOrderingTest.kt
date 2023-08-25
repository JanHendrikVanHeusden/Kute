package nl.kute.core

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.property.ranking.PropertyRanking
import nl.kute.core.property.ranking.PropertyRankingByLength
import nl.kute.core.property.ranking.PropertyValueMetaData
import nl.kute.core.property.ranking.ValueLengthRanking
import nl.kute.core.property.ranking.ValueLengthRanking.Companion.getRank
import nl.kute.core.property.ranking.ValueLengthRanking.L
import nl.kute.core.property.ranking.ValueLengthRanking.M
import nl.kute.core.property.ranking.ValueLengthRanking.XL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringPropertyOrderingTest {

    @Test
    fun `properties should be in expected order with single custom ranking`() {
        assertThat(TestClassWithSingleLetterProperties().asString())
            .isEqualTo("TestClassWithSingleLetterProperties(z=z, x=x, w=w, u=u, p=p, l=l, f=f, e=e, d=d, b=b)")
    }

    @Test
    fun `properties should be in expected order with multiple custom rankings`() {
        val testObj = TestClassWithSingleLetterPropertiesAndDifferentValueLengths()
        assertThat(testObj.a.getRank()).isSameAs(L)
        assertThat(testObj.d.getRank()).isSameAs(M)
        assertThat(testObj.l.getRank()).isSameAs(ValueLengthRanking.S)
        assertThat(testObj.p.getRank()).isSameAs(XL)
        assertThat(testObj.asString())
            .isEqualTo("TestClassWithSingleLetterPropertiesAndDifferentValueLengths(" +
                    "z=z, x=x, w=w, u=u, l=, f=f, e=e, b=b, d=${"d".repeat(30)}, a=${"a".repeat(80)}, p=${"p".repeat(120)})")
    }

}

// region ~ Classes etc. to be used for testing

/** Property sorting by 1st letter only! */
private class ReverseAlphabeticPropertyRanking: PropertyRanking() {
    override fun getRank(propertyValueMetaData: PropertyValueMetaData): Int =
        (-propertyValueMetaData.propertyName[0].code)
}

@Suppress("unused")
@AsStringClassOption(propertySorters = [ReverseAlphabeticPropertyRanking::class])
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

// endregion

