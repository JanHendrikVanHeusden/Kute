package nl.kute.core.annotation

import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.PropertyValueSurrounder.NONE
import nl.kute.core.annotation.option.PropertyValueSurrounder.`«»`
import nl.kute.core.asString
import nl.kute.test.base.ObjectsStackVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringOptionTest: ObjectsStackVerifier {

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
        assertThat(myTestObj.asString()).isEqualTo("MyTestClass(alooooooongValue=xxxx)")

        // arrange
        val mySubObj = MySubClass()
        // act, assert
        assertThat(mySubObj.asString()).isEqualTo("MySubClass(alooooooongValue=xxxx)")
    }

}
