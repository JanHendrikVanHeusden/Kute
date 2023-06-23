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
        //   `class nl.kute.demo.alternatives.ApacheToStringRecursiveDemo -
        //   nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$Object with array properties should yield decent output
        //   with Apache ToStringBuilder - same as contentDeepToString$MyTestClass@4b7dc788[myArray={0,1,{4,5,6},3}]`
        // But in production situations, it would be much shorter & more useful
        log(toString)
        // Anyway, it contains the info we want
        assertThat(toString).contains("[myArray={0,1,{4,5,6},3}]")
    }

    @Test
    @Disabled("Would succeed when enabled, contains more or less useful output with `Apache ToStringBuilder`")
    fun `Arrays with self-referencing elements should yield decent output with Apache ToStringBuilder`() {
        val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
        myArray[2] = arrayOf(4, 5, 6)
        myArray[3] = myArray
        myArray[4] = myArray

        val toString = ToStringBuilder.reflectionToString(myArray)
        // Something like
        //   `class nl.kute.demo.alternatives.ApacheToStringRecursiveDemo -
        //   [Ljava.lang.Object;@7486b455[{0,1,{4,5,6},[Ljava.lang.Object;@7486b455,[Ljava.lang.Object;@7486b455}]`
        log(toString)
        // at least it contains some useful output
        assertThat(toString)
            .matches(""".*\{0,1,\{4,5,6}.*""")
    }

    @Test
    @Disabled("Would succeed if enabled, self referencing array elements are handled `Apache ToStringBuilder()`")
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
        // Something like this:
        //   `class nl.kute.demo.alternatives.ApacheToStringRecursiveDemo -
        //    nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$Objects with array properties with self-referencing elements should yield decent output
        //    with Apache ToStringBuilder$MyTestClass@3e78b6a5[myArray={0,1,{4,5,6},[Ljava.lang.Object;@e3b3b2f,[Ljava.lang.Object;@e3b3b2f}]`
        log(toString)
        // but at least it contains the meaningful info
        assertThat(toString)
            .matches(""".*?myArray=\{0,1,\{4,5,6}.*""")
    }

    @Test
    @Disabled("Would succeed when enabled, more or less useful output with `Apache ToStringBuilder`")
    fun `Objects with array properties with self-referencing elements should yield decent output with Apache ToStringBuilder - non-reflective`() {
        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
            init {
                myArray[2] = arrayOf(4, 5, 6)
                myArray[3] = myArray
                myArray[4] = myArray
            }

            override fun toString() = ToStringBuilder(this)
                .append("myArray", myArray).toString()
        }
        val myArraysObject = MyTestClass()
        val toString = myArraysObject.toString()
        // output may be something like
        //   `class nl.kute.demo.alternatives.ApacheToStringRecursiveDemo -
        //    nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$Objects with array properties with self-referencing elements
        //    should yield decent output with Apache ToStringBuilder - non-reflective$MyTestClass@30c8681
        //    [myArray={0,1,{4,5,6},{0,1,{4,5,6},[Ljava.lang.Object;@6d026701,[Ljava.lang.Object;@6d026701},{
        //    0,1,{4,5,6},[Ljava.lang.Object;@6d026701,[Ljava.lang.Object;@6d026701}}]`
        log(toString)
        // but at least contains some useful info
        assertThat(toString)
            .matches("""^.*?myArray=\{0,1,\{4,5,6}.*$""")
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
        // Not sufficiently useful output
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
    @Disabled("This test would succeed if enabled, it provides some useful output with ToStringBuilder.reflectionToString")
    fun `objects with self reference should yield decent output with Apache ToStringBuilder`() {
        val testObj = GetSelfReference(1)
        val other = GetSelfReference(2)
        testObj.selfRef = testObj
        testObj.otherRef = other

        val toString = testObj.toString()
        // something like
        //   `"nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$GetSelfReference@30bce90b
        //   [id=1,otherRef=nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$GetSelfReference@2474f125
        //   [id=2,otherRef=<null>,selfRef=<null>],selfRef=nl.kute.demo.alternatives.ApacheToStringRecursiveDemo$GetSelfReference@30bce90b]"`
        println(toString)

        // not really an in depth test, but good enough to show it at least holds some useful info
        // it does not traverse into the referenced object though, so some room for improvement yet!
        assertThat(toString).contains("selfRef=")
    }

    @Suppress("unused")
    @Test
    @Disabled("Would succeed when enabled, provides useful output with `Apache ToStringBuilder`")
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

        // Could be better, somehow for the parents it uses name=... - OK
        // But for the children it only contains the name, without prefix name= - less useful
        // At least it contains the info, so succeeds
        listOf(mother, father, child1, child2).forEach {
            assertThat(it.toString())
                .contains("name=M")
                .contains("name=F")
                .contains("C1")
                .contains("C2")
        }
    }

}
