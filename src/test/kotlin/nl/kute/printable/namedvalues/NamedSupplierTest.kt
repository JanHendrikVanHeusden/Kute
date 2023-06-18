package nl.kute.printable.namedvalues

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NamedSupplierTest {

    @Test
    fun `test NamedSupplier`() {
        // Arrange
        val suppliedValue = "the value"
        val supplier: () -> String = { suppliedValue }
        val name = "the name"
        // Act
        val namedSupplier: NamedSupplier<String> = NamedSupplier(name, supplier)
        // Assert
        assertThat(namedSupplier.name).isSameAs(name)
        assertThat(namedSupplier.valueString).isSameAs(suppliedValue)
    }

    @Test
    fun `Retrieving the value of the supplier should evaluate the Supplier every time`() {
        // Arrange
        var counter = 0
        val namedSupplier: NamedSupplier<Int> =
            {
                // Don't do this normally! A Supplier should not have side effects!
                // It's just for test purpose, to verify its retrieval behaviour
                ++counter
            }.namedVal("counter") as NamedSupplier<Int>

        // Act, assert
        assertThat(counter)
            .`as`("Expression should not be evaluated at construction time")
            .isZero()

        repeat(10) {
            assertThat(namedSupplier.valueString)
                .`as`("Subsequent call #$it should re-evaluate the expression")
                .isEqualTo("${it + 1}")
        }
    }

    @Test
    fun `test namedVal`() {
        // Arrange
        val suppliedValue = "supplied value"
        val supplier = { suppliedValue }
        val name = "the name"
        // Act
        val namedSupplier = supplier.namedVal(name) as NamedSupplier<String>
        // Assert
        assertThat(namedSupplier.name).isSameAs(name)
        assertThat(namedSupplier.valueString).isSameAs(suppliedValue)
    }

}