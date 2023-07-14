package nl.kute.core.namedvalues

import nl.kute.base.GarbageCollectionWaiter
import nl.kute.core.asString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NamedValueTest: GarbageCollectionWaiter {

    @Test
    fun `test NamedValue`() {
        // arrange
        val str = StringBuffer("supplied value")
        val theValue = StringBuffer(str)
        val name = "the name"
        // act
        val namedValue: NamedValue<StringBuffer> = NamedValue(name, theValue)
        // assert
        assertThat(namedValue.name).isSameAs(name)
        assertThat(namedValue.valueString).isEqualTo(theValue.toString())
    }

    @Test
    fun `Retrieving the value of the NamedValue should evaluate the value every time`() {
        // arrange
        var counter = 0
        class WithValue {
            override fun toString(): String {
                return "${++counter}"
            }
        }
        val namedValue = NamedValue("withValue", WithValue())

        // act, assert
        assertThat(counter)
            .`as`("Expression should not be evaluated at construction time")
            .isZero

        repeat(10) {
            assertThat(namedValue.valueString)
                .`as`("Subsequent call #$it should re-evaluate the expression")
                .isEqualTo("${it + 1}")
        }
    }

    @Test
    fun `test namedVal`() {
        // arrange
        val valueStr = "the value"
        val name = "the name"
        // act
        val namedValue = valueStr.namedVal(name) as NamedValue<String>
        // assert
        assertThat(namedValue.name).isSameAs(name)
        assertThat(namedValue.valueString).isEqualTo(valueStr)
    }

    @Test
    fun `NamedValue shouldn't prevent garbage collection`() {
        // arrange
        class ToBeGarbageCollected {
            @Suppress("unused")
            val myString: String = "my String"
            override fun toString(): String = asString()
        }
        var toBeGarbageCollected: ToBeGarbageCollected? = ToBeGarbageCollected()
        val namedValue: NamedValue<ToBeGarbageCollected> =
            toBeGarbageCollected.namedVal("to be garbage collected") as NamedValue

        val checkGarbageCollected = {namedValue.valueString == "null"}
        assertThat(checkGarbageCollected.invoke()).isFalse
        assertThat(namedValue.valueString).contains("myString=my String")

        // act
        // nullify the object, should then be eligible for garbage collection
        @Suppress("UNUSED_VALUE")
        toBeGarbageCollected = null

        // assert
        assertGarbageCollected(checkGarbageCollected)
    }

}