package nl.kute.core.namedvalues

import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NamedSupplierTest {

    @BeforeEach
    @AfterEach
    fun setUp() {
        resetStdOutLogger()
    }

    @Test
    fun `test NamedSupplier`() {
        // arrange
        val suppliedValue = "the value"
        val supplier: () -> String = { suppliedValue }
        val name = "the name"
        // act
        val namedSupplier: NamedSupplier<String> = NamedSupplier(name, supplier)
        // assert
        assertThat(namedSupplier.name).isSameAs(name)
        assertThat(namedSupplier.valueString).isSameAs(suppliedValue)
    }

    @Test
    fun `Retrieving the value of the supplier should evaluate the Supplier every time`() {
        // arrange
        var counter = 0
        val namedSupplier: NamedSupplier<Int> =
            {
                // Don't do this normally! A Supplier should not have side effects!
                // It's just for test purpose, to verify its retrieval behaviour
                ++counter
            }.namedVal("counter") as NamedSupplier<Int>

        // act, assert
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
    fun `Supplier that throws exception should be handled`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }
        var throwIt = false
        val okValue = "this is OK!"
        val errMsg = "I throw an IA Exception!"
        val supplier: () -> String = {
            if (throwIt) throw IllegalArgumentException(errMsg)
            else okValue
        }

        // act: no exception
        val thrower = supplier.namedVal("thrower")
        val okResult = thrower.valueString
        // assert
        assertThat(okResult).isEqualTo(okValue)
        assertThat(logMsg).isEmpty()

        // arrange: throw it!
        throwIt = true
        // act
        val errResult = thrower.valueString
        assertThat(errResult).isNull()
        assertThat(logMsg).contains(errMsg)

        // arrange: no exception anymore
        throwIt = false
        logMsg = ""
        assertThat(thrower.valueString).isEqualTo(okValue)
        assertThat(logMsg).isEmpty()
    }

    @Test
    fun `test namedVal`() {
        // arrange
        val suppliedValue = "supplied value"
        val supplier = { suppliedValue }
        val name = "the name"
        // act
        val namedSupplier = supplier.namedVal(name) as NamedSupplier<String>
        // assert
        assertThat(namedSupplier.name).isSameAs(name)
        assertThat(namedSupplier.valueString).isSameAs(suppliedValue)
    }

}