package nl.kute.core

import nl.kute.test.base.ObjectsStackVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringWithArrayTest: ObjectsStackVerifier {

    private val names: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    @Test
    fun `test that array data is properly readable`() {
        // arrange, act
        val classWithArrayString = ClassWithArray(names).asString()
        val dataClassWithArrayString = DataClassWithArray(names).asString()

        // assert
        assertThat(classWithArrayString).contains(names.contentDeepToString())
        names.forEach {
            assertThat(classWithArrayString.contains(it))
        }
        // same as data class with same properties
        assertThat(classWithArrayString.replace(ClassWithArray::class.simpleName!!, ""))
            .isEqualTo(dataClassWithArrayString.replace(DataClassWithArray::class.simpleName!!, ""))
    }

    @Test
    fun `loooooooooooong array string representations should be capped at 500 chars`() {
        val array = IntArray(1000).map { it.toString() }.toTypedArray()
        val classWithArrayString = ClassWithArray(array).asString()
        assertThat(classWithArrayString.length).isEqualTo(500 + ClassWithArray::class.simpleName!!.length + "(array=)".length)
    }

    /////////////////////////
    // Test classes / objects
    /////////////////////////

    @Suppress("unused")
    private class ClassWithArray(val array: Array<String>) {
        override fun toString(): String = asString()
    }

    @Suppress("ArrayInDataClass") // suppress warning that equals and hashCode should be overridden
    private data class DataClassWithArray(private val array: Array<String>)
}
