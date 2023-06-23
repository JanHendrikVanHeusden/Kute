package nl.kute.demo.alternatives

import nl.kute.log.log
import nl.kute.reflection.simplifyClassName
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.LinkedList

class ApacheToStringRecursiveDemo {

    companion object {
        init {
            log(
                "Most of the tests in ${ApacheToStringRecursiveDemo::class.simplifyClassName()}, when enabled, would fail! " +
                        "Demonstrating that `Apache ToStringBuilder()` will cause StackOverflowError with recursive stuff; " +
                        "or otherwise fall back to non-informative or insane verbose output"
            )
        }
    }

    @Test
    @Disabled("Will succeed when enabled, nested arrays give decent toString output with `Apache ToStringBuilder`")
    fun `Object with array properties should yield decent output with Apache ToStringBuilder - same as contentDeepToString`() {
        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3)
            init {
                myArray[2] = arrayOf(4, 5, 6)
            }
        }
        val testObj = MyTestClass()
        val toString = ToStringBuilder.reflectionToString(testObj)

        // quite verbose, starts with full class + test method name:
        // class nl.kute.demo.alternatives.ApacheToStringRecursiveDemo - nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$Object with array properties should yield decent output with Apache ToStringBuilder - same as contentDeepToString$MyTestClass@60db1c0e
        log(toString)
        // but anyway, it also contains the useful info we want
        assertThat(toString).contains("[myArray={0,1,{4,5,6},3}]")
    }

    @Test
    @Disabled("Would fail, self referencing elements fall back to non-informative toString output with `Apache ToStringBuilder`")
    fun `Arrays with self-referencing elements should yield decent output with Apache ToStringBuilder`() {
        val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
        myArray[2] = arrayOf(4, 5, 6)
        myArray[3] = myArray
        myArray[4] = myArray

        val toString = ToStringBuilder.reflectionToString(myArray)
        log(toString)
        assertThat(toString)
            .doesNotMatch(""".*\[Ljava.lang.Object;@[a-z0-9]+.*""")
    }

    @Test
    @Disabled("Would fail, self referencing array elements cause StackOverflowError with `Apache ToStringBuilder()`")
    fun `Objects with array properties with self-referencing elements should yield decent output with Apache ToStringBuilder`() {
        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
            init {
                myArray[2] = arrayOf(4, 5, 6)
                myArray[3] = myArray
                myArray[4] = myArray
            }

            override fun toString() = ToStringBuilder.reflectionToString(this)
        }
        val myArraysObject = MyTestClass()
        val toString = myArraysObject.toString()
        log(toString)
        assertThat(toString)
            .doesNotMatch(""".*\[Ljava.lang.Object;@[a-z0-9]+.*""")
    }

    @Test
    @Disabled("Would fail, objects with array properties fall back to non-informative toString output with `Apache ToStringBuilder`")
    fun `Objects with array properties fall back to non-informative toString output with Apache ToStringBuilder`() {
        class MyTestClass {
            val myArray: Array<Any> = arrayOf("a0", "a1", "a2", "a3", "a4")
            val myList: MutableList<Any> = mutableListOf("L0", "L1", "L2", "L3", "L4")
            init {
                myArray[2] = myList
                myArray[3] = myList
                myList[3] = myArray
                myList[1] = myArray
            }

            override fun toString() = ToStringBuilder.reflectionToString(this)
        }
        val myArraysObject = MyTestClass()
        val toString = myArraysObject.toString()
        log(toString)
        assertThat(toString).doesNotMatch(""".*\[Ljava.lang.Object;@[a-z0-9]+.*""")
    }

    @Test
    @Disabled("Collection with self reference causes stack overflow with Apache ToStringBuilder")
    fun `Collections with self-referencing elements cause stack overflow with Apache ToStringBuilder`() {
        val mutableList: MutableList<Any> = mutableListOf("first", "second", "third")
        mutableList[1] = mutableList // self reference
        mutableList.add(mutableList) // self reference

        val toString = ToStringBuilder.reflectionToString(mutableList)
        log(toString)

        assertThat(toString)
            .isEqualTo("[first, (this Collection), third, (this Collection)]")
    }

    @Test
    @Disabled("Would fail, mutually referencing collection elements cause StackOverflowError with `Apache ToStringBuilder()`")
    fun `Collections with mutually referencing elements cause stack overflow with Apache ToStringBuilder`() {
        val list1: MutableList<Any> = mutableListOf("first 1", "second 1", "third 1")
        val list2: LinkedList<Any> = LinkedList(listOf("first 2", "second 2", "third 2"))

        list1.add(list2)
        list1.add(list2)
        list2.add(list1)
        list2.add(list1)

        val toString1 = ToStringBuilder.reflectionToString(list1)
        val toString2 = ToStringBuilder.reflectionToString(list2)
        assertThat(toString1).isNotNull()
        assertThat(toString2).isNotNull()
    }

    @Suppress("unused")
    private class GetSelfReference (val id: Int, var selfRef: GetSelfReference? = null, var otherRef: GetSelfReference? = null) {
        override fun toString(): String = ToStringBuilder.reflectionToString(this)
    }

    @Test
    @Disabled("This test would succeed with decent output if enabled: ToStringBuilder.reflectionToString handles self-reference correctly")
    fun `objects with self reference should yield decent output with Apache ToStringBuilder`() {
        val testObj = GetSelfReference(1)
        val other = GetSelfReference(2)
        testObj.selfRef = testObj
        testObj.otherRef = other
        val expected = "GetSelfReference(id=1, otherRef=GetSelfReference(id=2, otherRef=null, selfRef=null), selfRef=recursive: GetSelfReference(...))"
        assertThat(testObj.toString()).isEqualTo(expected)
    }

    @Suppress("unused")
    @Test
//    @Disabled("Would fail, objects with collection properties fall back to non-informative toString output with `Apache ToStringBuilder`")
    fun `objects with mutual reference should yield decent output with Apache ToStringBuilder`() {
        class Parent(val name: String, val children: MutableSet<Any> = mutableSetOf()) {
            override fun toString(): String = ToStringBuilder.reflectionToString(this)
        }

        @Suppress("CanBeParameter")
        class Child(val name: String, val mother: Parent, val father: Parent) {
            init {
                mother.children.add(this)
                father.children.add(this)
            }

            // output with default style is really, really verbose: 8029 char, because every object contains the full class & method name,
            // like this: `nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$objects with mutual reference should yield decent output with Apache ToStringBuilder$Child@6e535154`
            // so using SIMPLE_STYLE
            // But even then is the output really verbose (961 char) and incomplete
            override fun toString(): String = ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE)
        }

        val mother = Parent(name = "M")
        val father = Parent(name = "F")
        val child1 = Child("C1", mother, father)
        val child2 = Child("C2", mother, father)

        log("$mother\n")
        log("$father\n")
        log("$child1\n")
        log("$child2\n")

        assertThat(mother.toString()).doesNotContain("java.util.LinkedHashSet@")
        assertThat(father.toString()).doesNotContain("java.util.LinkedHashSet@")
        assertThat(child1.toString()).doesNotContain("java.util.LinkedHashSet@")
        assertThat(child2.toString()).doesNotContain("java.util.LinkedHashSet@")
    }

}
