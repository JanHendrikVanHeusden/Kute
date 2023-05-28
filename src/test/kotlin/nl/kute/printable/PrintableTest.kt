package nl.kute.printable

import nl.kute.core.asString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PrintableTest {

    private val names: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    @Test
    fun `test that array data is properly readable`() {
        val classWithArrayString = ClassWithArray(names).asString()
        val dataClassWithArrayString = DataClassWithArray(names).asString()

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

    @Suppress("unused")
    private class ClassWithArray(val array: Array<String>) {
        override fun toString(): String = asString()
    }

    @Suppress("ArrayInDataClass") // suppress warning that equals and hashCode should be overridden
    private data class DataClassWithArray(val array: Array<String>)
}