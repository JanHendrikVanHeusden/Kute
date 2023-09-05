package nl.kute.asstring.annotation.option

import nl.kute.asstring.annotation.option.PropertyValueSurrounder.NONE
import nl.kute.asstring.annotation.option.PropertyValueSurrounder.`«»`
import nl.kute.asstring.core.defaults.initialElementsLimit
import nl.kute.asstring.core.defaults.initialMaxStringValueLength
import nl.kute.asstring.core.defaults.initialShowNullAs
import nl.kute.asstring.core.defaults.initialSurroundPropValue
import nl.kute.asstring.core.asString
import nl.kute.test.base.ObjectsStackVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringOptionTest: ObjectsStackVerifier {

    @Test
    fun `when no changes yet, initial defaults should apply`() {
        with(AsStringOption.defaultOption) {
            assertThat(this.showNullAs)
                .isEqualTo(initialShowNullAs)
                .isEqualTo("null")
            assertThat(this.surroundPropValue)
                .isEqualTo(initialSurroundPropValue)
                .isSameAs(NONE)
            assertThat(this.elementsLimit)
                .isEqualTo(initialElementsLimit)
            assertThat(this.propMaxStringValueLength)
                .isEqualTo(initialMaxStringValueLength)
        }
    }

    @Test
    fun `class annotation with AsStringOption should be honoured`() {
        // arrange
        @Suppress("unused")
        @AsStringOption(showNullAs = "N/A")
        open class MyTestClass {
            val nullVal: String? = null
        }

        @AsStringOption(showNullAs = "<nothing here>")
        class MySubClass: MyTestClass()

        val myTestObj = MyTestClass()
        val mySubObj = MySubClass()

        // act, assert
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass(nullVal=N/A)")
        assertThat(mySubObj.asString()).isEqualTo("MySubClass(nullVal=<nothing here>)")
    }

    @Test
    fun `class annotation with default AsStringOption should apply default`() {
        // arrange
        @Suppress("unused")
        @AsStringOption(surroundPropValue = `«»`)
        open class MyTestClass {
            val aValue = "my value"
        }

        assertThat(AsStringOption.defaultOption.surroundPropValue).isSameAs(NONE)
        @AsStringOption // applies default (false)
        class MySubClass: MyTestClass()

        val myTestObj = MyTestClass()
        val mySubObj = MySubClass()

        // act, assert
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass(aValue=«my value»)")
        assertThat(mySubObj.asString()).isEqualTo("MySubClass(aValue=my value)")
    }

    @Test
    fun `subclass should inherit AsStringOption`() {
        // arrange
        @Suppress("unused")
        @AsStringOption(propMaxStringValueLength = 4)
        open class MyTestClass {
            val alooooooongValue = "x".repeat(100)
        }

        class MySubClass: MyTestClass()

        val myTestObj = MyTestClass()
        // act, assert
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass(alooooooongValue=xxxx...)")

        // arrange
        val mySubObj = MySubClass()
        // act, assert
        assertThat(mySubObj.asString()).isEqualTo("MySubClass(alooooooongValue=xxxx...)")
    }

    @Test
    fun `applyOption should apply AsStringOption parameters`() {
        val aString = "12345 abc"

        assertThat(AsStringOption().applyOption(aString)).isEqualTo(aString)

        assertThat(AsStringOption().applyOption(null)).isEqualTo("null")
        assertThat(AsStringOption(showNullAs = "N/A").applyOption(null)).isEqualTo("N/A")

        assertThat(AsStringOption(surroundPropValue = `«»`)
            .applyOption(aString)).isEqualTo("«$aString»")

        assertThat(AsStringOption(propMaxStringValueLength = 4)
            .applyOption(aString)).isEqualTo("${aString.take(4)}...")

        assertThat(AsStringOption(surroundPropValue = `«»`, propMaxStringValueLength = 4)
            .applyOption(aString)).isEqualTo("«${aString.take(4)}...»")

        // should apply initialMaxStringValueLength instead of negative value
        assertThat(AsStringOption(propMaxStringValueLength = -10).applyOption(aString.repeat(100)))
            .isEqualTo("${aString.repeat(100).take(initialMaxStringValueLength)}...")
    }

    @Test
    fun `applyOption should honour showNullAs and ignore other AsStringOption parameters on null`() {
        assertThat(AsStringOption().applyOption(null)).isEqualTo("null")
        assertThat(AsStringOption(showNullAs = "N/A").applyOption(null)).isEqualTo("N/A")

        // nulls should not be surrounded
        assertThat(AsStringOption(surroundPropValue = `«»`).applyOption(null))
            .isEqualTo("null")
        assertThat(AsStringOption(surroundPropValue = `«»`, showNullAs = "N/A")
            .applyOption(null)).isEqualTo("N/A")

        // propMaxStringValueLength should not be applied to nulls
        assertThat(AsStringOption(surroundPropValue = `«»`, propMaxStringValueLength = 2).applyOption(null))
            .isEqualTo("null")
        assertThat(AsStringOption(propMaxStringValueLength = 2, surroundPropValue = `«»`, showNullAs = "<This is a null value>")
            .applyOption(null)).isEqualTo("<This is a null value>")
    }

}
