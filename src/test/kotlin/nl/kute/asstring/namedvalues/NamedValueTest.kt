package nl.kute.asstring.namedvalues

import nl.kute.asstring.core.asString
import nl.kute.test.base.GarbageCollectionWaiter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NamedValueTest: GarbageCollectionWaiter {

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
            assertThat(namedValue.value.toString())
                .`as`("Subsequent call #$it should re-evaluate the expression")
                .isEqualTo("${it + 1}")
        }
    }

    @Test
    fun `namedValue should reflect name and value`() {
        // arrange
        val valueStr = "the value"
        val name = "the name"
        // act
        val namedValue = valueStr.namedValue(name) as NamedValue<String>
        // assert
        assertThat(namedValue.name).isSameAs(name)
        assertThat(namedValue.value).isEqualTo(valueStr)
    }

    @Test
    fun `NamedValue should accept null values`() {
        val name = "a null value"
        val namedValue = null.namedValue(name)
        assertThat(namedValue.name).isSameAs(name)
        assertThat(namedValue.value).isNull()
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
        val namedValue = toBeGarbageCollected.namedValue("to be garbage collected")

        val checkGarbageCollected = {namedValue.value.asString() == "null"}
        assertThat(checkGarbageCollected.invoke()).isFalse
        assertThat(namedValue.value.asString()).contains("myString=my String")

        // act
        // nullify the object, should then be eligible for garbage collection
        @Suppress("UNUSED_VALUE")
        toBeGarbageCollected = null

        // assert
        assertGarbageCollected(checkGarbageCollected)
    }

}