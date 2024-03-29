package nl.kute.asstring.namedvalues

import nl.kute.asstring.core.asString
import nl.kute.helper.base.GarbageCollectionWaiter
import nl.kute.logging.logger
import nl.kute.logging.resetStdOutLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NamedSupplierTest: GarbageCollectionWaiter {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        resetStdOutLogger()
    }

    @Test
    fun `Retrieving the value of the supplier should evaluate the Supplier every time`() {
        // arrange
        var counter = 0
        val namedSupplier: NamedSupplier<Int?> =
            {
                // Don't do this normally! A Supplier should not have side effects!
                // It's just for test purpose, to verify its retrieval behaviour
                ++counter
            }.namedSupplier("counter")

        // act, assert
        assertThat(counter)
            .`as`("Expression should not be evaluated at construction time")
            .isZero

        repeat(10) {
            assertThat(namedSupplier.value)
                .`as`("Subsequent call #$it should re-evaluate the expression")
                .isEqualTo(it + 1)
        }
    }

    @Test
    fun `NamedSupplier should swallow exception and log it`() {
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
        val thrower = supplier.namedSupplier("thrower")
        val okResult = thrower.value
        // assert
        assertThat(okResult).isEqualTo(okValue)
        assertThat(logMsg).isEmpty()

        // arrange: throw it!
        throwIt = true
        // act
        val errResult = thrower.value
        assertThat(errResult).isNull()
        assertThat(logMsg).contains(errMsg)

        // arrange: no exception anymore
        throwIt = false
        logMsg = ""
        assertThat(thrower.value).isEqualTo(okValue)
        assertThat(logMsg).isEmpty()
    }

    @Test
    fun `namedSupplier should reflect given name and supplied value`() {
        // arrange
        val suppliedValue = "supplied value"
        val supplier = { suppliedValue }
        val name = "the name"
        // act
        val namedSupplier = supplier.namedSupplier(name)
        // assert
        assertThat(namedSupplier.name).isSameAs(name)
        assertThat(namedSupplier.value).isSameAs(suppliedValue)
    }

    @Test
    fun `NamedSupplier should accept null values`() {
        val name = "a null value"
        @Suppress("USELESS_CAST") // not useless: it won't even compile without the cast to String?
        val namedSupplier = { null as String? }.namedSupplier(name)
        assertThat(namedSupplier.name).isSameAs(name)
        assertThat(namedSupplier.value).isNull()
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun `NamedSupplier shouldn't prevent garbage collection`() {
        // arrange
        class ToBeGarbageCollected {
            @Suppress("unused")
            val myString: String = "my String"
            override fun toString(): String = asString()
        }
        var toBeGarbageCollected: ToBeGarbageCollected? = ToBeGarbageCollected()
        var supplier: ValueSupplier<ToBeGarbageCollected?>? = { toBeGarbageCollected }
        val namedSupplier: NamedSupplier<ToBeGarbageCollected?> =
            supplier!!.namedSupplier("to be garbage collected")

        val checkGarbageCollected = { namedSupplier.value == null }
        assertThat(checkGarbageCollected.invoke()).isFalse
        assertThat(namedSupplier.value.asString()).contains("myString=my String")

        // act
        // nullify the object, should then be eligible for garbage collection
        toBeGarbageCollected = null
        supplier = null

        // assert
        assertGarbageCollected(checkGarbageCollected)
    }

}