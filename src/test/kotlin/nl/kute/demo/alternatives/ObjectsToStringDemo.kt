package nl.kute.demo.alternatives

import nl.kute.log.log
import nl.kute.reflection.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.LinkedList
import java.util.Objects

class ObjectsToStringDemo {

    // Set to true to enable the tests (not advised, several tests fail with stack overflow)
    private val testsEnabled = false

    companion object {
        init {
            log(
                "\nMost of the tests in ${ObjectsToStringDemo::class.simplifyClassName()}, when enabled, would fail! " +
                        "Demonstrating that `Objects.toString()` will cause StackOverflowError with recursive stuff " +
                        "and other advanced constructs; or otherwise fall back to non-informative output\n"
            )
        }
    }

    @Test
    fun `Object with nested array causes stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("No recursion here, just a nested array, yet `Objects.toString()` causes stack overflow")
            .isTrue

        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3)
            init {
                myArray[2] = arrayOf(4, 5, 6)
            }

            override fun toString(): String = Objects.toString(this)
        }
        val testObj = MyTestClass()
        assertThat(testObj.toString()).isNotNull
    }

    @Test
    fun `Arrays with self-referencing elements should yield decent output with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Would fail, self referencing array elements fall back to non-informative toString output with `Objects toString`")
            .isTrue

        val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
        myArray[2] = arrayOf(4, 5, 6)
        myArray[3] = myArray
        myArray[4] = myArray

        val toString = Objects.toString(myArray)
        log(toString)
        assertThat(toString)
            .doesNotMatch("""\[Ljava.lang.Object;@[a-z0-9]+.*""")
    }

    @Test
    fun `Objects with array properties with self-referencing elements cause stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Would fail, self referencing array elements cause StackOverflowError with `Objects.toString()`")
            .isTrue

        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
            init {
                myArray[2] = arrayOf(4, 5, 6)
                myArray[3] = myArray
                myArray[4] = myArray
            }

            override fun toString() = Objects.toString(this)
        }
        val myArraysObject = MyTestClass()
        val stringVal = myArraysObject.toString()
        assertThat(stringVal).isNotNull
    }

    @Test
    fun `Objects with array properties with mutually referencing elements cause stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Would fail, mutually referencing array elements cause StackOverflowError with `Objects.toString()`")
            .isTrue

        class MyTestClass {
            val myArray: Array<Any> = arrayOf("a0", "a1", "a2", "a3", "a4")
            val myList: MutableList<Any> = mutableListOf("L0", "L1", "L2", "L3", "L4")
            init {
                myArray[2] = myList
                myArray[3] = myList
                myList[3] = myArray
                myList[1] = myArray
            }

            override fun toString() = Objects.toString(this)
        }
        val myArraysObject = MyTestClass()
        val stringVal = myArraysObject.toString()
        log(stringVal)
        assertThat(stringVal).isNotNull
    }

    @Test
    fun `Collections with self-referencing elements should yield decent output with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("This test would succeed with decent output if enabled: Collections.toString handles self-reference correctly")
            .isTrue

        val mutableList: MutableList<Any> = mutableListOf("first", "second", "third")
        mutableList[1] = mutableList // self reference
        mutableList.add(mutableList) // self reference

        val toString = Objects.toString(mutableList)
        log(toString)
        assertThat(toString)
            .isEqualTo("[first, (this Collection), third, (this Collection)]")
    }

    @Test
    fun `Collections with mutually referencing elements cause stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Would fail, mutually referencing collection elements cause StackOverflowError with `Objects.toString()`")
            .isTrue

        val list1: MutableList<Any> = mutableListOf("first 1", "second 1", "third 1")
        val list2: LinkedList<Any> = LinkedList(listOf("first 2", "second 2", "third 2"))

        list1.add(list2)
        list1.add(list2)
        list2.add(list1)
        list2.add(list1)

        assertThat(Objects.toString(list1)).isNotNull
        assertThat(Objects.toString(list2)).isNotNull
    }

    @Suppress("unused")
    private class GetSelfReference (val id: Int, var selfRef: GetSelfReference? = null, var otherRef: GetSelfReference? = null) {
        override fun toString(): String = Objects.toString(this)
    }

    @Test
    fun `objects with self reference cause stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Object with self reference causes stack overflow with Objects toString")
            .isTrue

        val testObj = GetSelfReference(1)
        val other = GetSelfReference(2)
        testObj.selfRef = testObj
        testObj.otherRef = other
        val expected = "GetSelfReference(id=1, otherRef=GetSelfReference(id=2, otherRef=null, selfRef=null), selfRef=recursive: GetSelfReference(...))"
        assertThat(Objects.toString(testObj)).isEqualTo(expected)
    }

    @Suppress("unused")
    @Test
    fun `objects with mutual reference cause stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Would fail, mutually referencing objects cause StackOverflowError with `Objects.toString()`")
            .isTrue

        class Parent(val name: String, val children: MutableSet<Any> = mutableSetOf()) {
            override fun toString(): String = Objects.toString(this)
        }

        @Suppress("CanBeParameter")
        class Child(val name: String, val mother: Parent, val father: Parent) {
            init {
                mother.children.add(this)
                father.children.add(this)
            }

            override fun toString(): String = Objects.toString(this)
        }

        val mother = Parent(name = "M")
        val father = Parent(name = "F")
        val child1 = Child("C1", mother, father)
        val child2 = Child("C2", mother, father)

        assertThat(mother.toString()).isNotNull
        assertThat(father.toString()).isNotNull
        assertThat(child1.toString()).isNotNull
        assertThat(child2.toString()).isNotNull
    }

    @Test
    fun `object with uninitialized lateinit property causes stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Uninitialized lateinit var causes stack overflow with `Objects.toString`, even without recursion`")
            .isTrue

        @Suppress("unused")
        class WithLateinits {
            lateinit var uninitializedStringVar: String
            lateinit var initializedStringVar: String
            init {
                if (LocalDate.now().isAfter(LocalDate.of(2000, 1, 1))) {
                    initializedStringVar = "I am initialized"
                }
            }

            override fun toString(): String = Objects.toString(this)
        }
        assertThat(WithLateinits().toString()).isNotNull
    }

    @Test
    fun `object with only uninitialized lateinit property causes stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Uninitialized lateinit var causes stack overflow with `Objects.toString`, even without recursion`")
            .isTrue

        @Suppress("unused")
        class WithUninitializedLateinit {
            lateinit var uninitializedStringVar: String
            override fun toString(): String = Objects.toString(this)
        }
        assertThat(WithUninitializedLateinit().toString()).isNotNull
    }

    @Test
    fun `object with initialized lateinit and init block causes stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Initialized lateinit var causes stack overflow with `Objects.toString`, even without recursion`")
            .isTrue

        @Suppress("unused")
        class WithInitializedLateinit {
            lateinit var initializedStringVar: String
            init {
                if (LocalDate.now().isAfter(LocalDate.of(2000, 1, 1))) {
                    initializedStringVar = "I am initialized"
                }
            }

            override fun toString(): String = Objects.toString(this)
        }
        assertThat(WithInitializedLateinit().toString()).isNotNull
    }

    @Test
    fun `object with initialized lateinit causes stack overflow with Objects toString`() {
        assumeThat(testsEnabled)
            .`as`("Initialized lateinit var causes stack overflow with `Objects.toString`, even without recursion`")
            .isTrue

        @Suppress("unused")
        class WithInitializedLateinit {
            lateinit var initializedStringVar: String
            override fun toString(): String = Objects.toString(this)
        }

        val objToTest = WithInitializedLateinit()
        objToTest.initializedStringVar = "I am initialized"
        assertThat(objToTest.toString()).isNotNull
    }

    @Test
    fun `synthetic types shouldn't cause exceptions`() {
        assumeThat(testsEnabled)
            .`as`("This test would succeed if enabled: `Objects.toString()` yields decent output on synthetic types")
            .isTrue

        // arrange
        val supplier: () -> String = { "a String supplier" }

        listOf(
            supplier,
            { "an other String supplier" },
            { Any() }
        ).forEach {
            // act, assert
            val toString = Objects.toString(it)
            // Output will be like
            // () -> kotlin.String
            // () -> kotlin.Any
            // () -> kotlin.Any
            // Nice!
            println(toString)
            assertThat(toString).isNotNull()
        }
    }
}