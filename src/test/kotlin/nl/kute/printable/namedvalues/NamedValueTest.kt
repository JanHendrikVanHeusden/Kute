package nl.kute.printable.namedvalues

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NamedValueTest {

    @Test
    fun `test NamedValue`() {
        // Arrange
        val str = StringBuffer("supplied value")
        val theValue = StringBuffer(str)
        val name = "the name"
        // Act
        val namedValue: NamedValue<StringBuffer> = NamedValue(name, theValue)
        // Assert
        assertThat(namedValue.name).isSameAs(name)
        assertThat(namedValue.valueString).isEqualTo(theValue.toString())
    }

    @Test
    fun `Retrieving the value of the NamedValue should evaluate the value every time`() {
        // Arrange
        var counter = 0
        class WithValue() {
            override fun toString(): String {
                return "${++counter}"
            }
        }
        val namedValue = NamedValue("withValue", WithValue())

        // Act, assert
        assertThat(counter)
            .`as`("Expression should not be evaluated at construction time")
            .isZero()

        repeat(10) {
            assertThat(namedValue.valueString)
                .`as`("Subsequent call #$it should re-evaluate the expression")
                .isEqualTo("${it + 1}")
        }
    }

    @Test
    fun `test namedVal`() {
        // Arrange
        val valueStr = "the value"
        val name = "the name"
        // Act
        val namedValue = valueStr.namedVal(name) as NamedValue<String>
        // Assert
        assertThat(namedValue.name).isSameAs(name)
        assertThat(namedValue.valueString).isEqualTo(valueStr)
    }

}