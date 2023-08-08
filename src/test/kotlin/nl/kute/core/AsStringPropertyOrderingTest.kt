package nl.kute.core

import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.ordering.AbstractPropertyRanking
import nl.kute.core.ordering.PropertyRanking
import nl.kute.core.ordering.registerPropertyRankingClass
import nl.kute.core.property.PropertyValueInformation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AsStringPropertyOrderingTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUpClass() {
            registerPropertyRankingClass(ReverseAlphabeticPropertyRanking::class to ReverseAlphabeticPropertyRanking.instance)
        }
    }

    @Test
    fun `properties should be in expected order with custom ranking`() {
        assertThat(TestClassWithSingleLetterProperties().asString())
            .isEqualTo("TestClassWithSingleLetterProperties(z=z, x=x, w=w, u=u, p=p, l=l, f=f, e=e, d=d, b=b)")
    }

// region ~ Classes etc. to be used for testing

    @Suppress("unused")
    @AsStringClassOption(propertySorters = [ReverseAlphabeticPropertyRanking::class])
    private class TestClassWithSingleLetterProperties {
        val x = "x"
        val b = "b"
        val u = "u"
        val d = "d"
        val f = "f"
        val z = "z"
        val e = "e"
        val p = "p"
        val l = "l"
        val w = "w"
    }

}

private class ReverseAlphabeticPropertyRanking: AbstractPropertyRanking() {
    override fun getRank(propertyValueInfo: PropertyValueInformation): Short =
        (-propertyValueInfo.propertyName[0].code).toShort()

    override fun instance(): PropertyRanking = instance

    companion object {
        val instance = ReverseAlphabeticPropertyRanking()
    }

}

// endregion

