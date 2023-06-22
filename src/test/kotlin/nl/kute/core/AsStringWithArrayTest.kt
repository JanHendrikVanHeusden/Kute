package nl.kute.core

import nl.kute.log.log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringWithArrayTest {

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

    @Test
    fun `test array`() {
        // TODO: proper test for recursive stuff
        log("Starting test")
        println(names.asString())
        val strings = listOf("aap", "noot", "mies")
        strings.asString()
        val mutableList: MutableList<Any> = mutableListOf()
        mutableList.addAll(strings)
        mutableList.add(mutableList)
        println(mutableList.asString())
        val myArray: Array<Any> = arrayOf(1, 2, 3, 4)
        myArray[2] = arrayOf(5, 6, 7)
        myArray[3] = myArray
        println(myArray.asString())
        class Parent (val children: MutableSet<Any> = mutableSetOf()) {
            override fun toString(): String = asString()
        }
        class Child(parent: Parent) {
            init {
                parent.children.add(this)
            }
            override fun toString(): String = asString()
        }
        val parent = Parent()
        val child = Child(parent)
        println(parent)
        println()
        println(child)
    }

    @Suppress("unused")
    private class ClassWithArray(val array: Array<String>) {
        override fun toString(): String = asString()
    }

    @Suppress("ArrayInDataClass") // suppress warning that equals and hashCode should be overridden
    private data class DataClassWithArray(private val array: Array<String>)
}
