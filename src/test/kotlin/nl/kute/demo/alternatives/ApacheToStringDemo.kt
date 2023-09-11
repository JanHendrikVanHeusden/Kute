package nl.kute.demo.alternatives

import nl.kute.log.log
import nl.kute.log.logger
import nl.kute.reflection.util.simplifyClassName
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.ToStringStyle
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class ApacheToStringDemo {

    // Set to true to enable the demos (not advised, several of them fail with stack overflow)
    private val demosEnabled = false

    companion object {
        init {
            log(
                "\nSeveral of the tests in ${ApacheToStringDemo::class.simplifyClassName()}, when enabled, would fail!\n" +
                        "Demonstrating that `Apache ToStringBuilder().reflectionToString()`:\n" +
                        " * causes StackOverflowError with recursive stuff\n" +
                        " * throws several other exceptions\n" +
                        " * may fall back to non-informative output\n"
            )
        }
    }

    @Test
    fun `Apache's reflective ToStringBuilder should accept null and yield decent output`() {
        assumeThat(demosEnabled)
            .`as`("Will fail with NullPointerException when enabled")
            .isTrue

        assertThat(ToStringBuilder.reflectionToString(null).toString()).isNotNull
    }

    @Test
    fun `Apache's non-reflective ToStringBuilder should accept null and give decent output`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed with decent output")
            .isTrue

        assertThat(ToStringBuilder(null).toString())
            .isEqualTo("<null>")
    }

    @Test
    fun `Apache's reflective ToStringBuilder should yield decent output on 'this'`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled")
            .isTrue

        @Suppress("unused")
        class MyClass {
            val myProp: String = "my prop value"
            override fun toString(): String = ToStringBuilder.reflectionToString(this)
        }

        val result = MyClass().toString()
        assertThat(result).endsWith("[myProp=my prop value]")
    }

    @Test
    fun `Apache's reflective ToStringBuilder should include supertype properties`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled, Apache's `ToStringBuilder` includes supertype properties")
            .isTrue

        @Suppress("unused")
        open class MyClass {
            val myProp: String = "my prop value"
            override fun toString(): String = ToStringBuilder.reflectionToString(this)
        }

        @Suppress("unused")
        class MySubClass: MyClass() {
            val mySubClassProp = "my subclass prop value"
            // NB: No override of toString() here!
        }

        val result = MySubClass().toString()
        assertThat(result)
            .contains("mySubClassProp=my subclass prop value", "myProp=my prop value")
    }

    @Test
    fun `Object with array properties should yield decent output with Apache ToStringBuilder - same as contentDeepToString`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled, nested arrays give decent toString output with `Apache ToStringBuilder`")
            .isTrue

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
    fun `Arrays with self-referencing elements should yield decent output with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed when enabled, contains more or less useful output with `Apache ToStringBuilder`")
            .isTrue

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
    fun `Objects with array properties with self-referencing elements should yield decent output with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed if enabled, self referencing array elements are handled `Apache ToStringBuilder()`")
            .isTrue

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
    fun `Objects with array properties with self-referencing elements should yield decent output with Apache ToStringBuilder - non-reflective`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed when enabled, more or less useful output with `Apache ToStringBuilder`")
            .isTrue

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
    fun `Objects with mutually referencing array properties fall back to non-informative toString output with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed, objects with mutually referencing array properties do not cause exceptions," +
                    " but fall back to non-informative toString output with `Apache ToStringBuilder`")
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

            override fun toString() = ToStringBuilder.reflectionToString(this)
        }
        val myArraysObject = MyTestClass()
        val toString = myArraysObject.toString()

        log(toString)
        // Not sufficiently useful output
        assertThat(toString).matches(""".*\[Ljava.lang.Object;@[a-z0-9]+.*""")
    }

    @Test
    fun `Collections with self-referencing elements cause stack overflow with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Collection with self reference causes stack overflow with `Apache ToStringBuilder`")
            .isTrue

        val mutableList: MutableList<Any> = mutableListOf("first", "second", "third")
        mutableList[1] = mutableList // self reference
        mutableList.add(mutableList) // self reference

        val toString = ToStringBuilder.reflectionToString(mutableList)
        log(toString)

        assertThat(toString)
            .contains("java.util.ArrayList", "[size=")
    }

    @Test
    fun `Collections with mutually referencing elements cause stack overflow with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Would fail, mutually referencing collection elements cause StackOverflowError with `Apache ToStringBuilder()`")
            .isTrue

        val list1: MutableList<Any> = mutableListOf("first 1", "second 1", "third 1")
        val list2: LinkedList<Any> = LinkedList(listOf("first 2", "second 2", "third 2"))

        list1.add(list2)
        list1.add(list2)
        list2.add(list1)
        list2.add(list1)

        val toString1 = ToStringBuilder.reflectionToString(list1)
        val toString2 = ToStringBuilder.reflectionToString(list2)
        assertThat(toString1).isNotNull
        assertThat(toString2).isNotNull
    }

    @Suppress("unused")
    private class GetSelfReference (val id: Int, var selfRef: GetSelfReference? = null, var otherRef: GetSelfReference? = null) {
        override fun toString(): String = ToStringBuilder.reflectionToString(this)
    }

    @Test
    fun `objects with self reference should yield decent output with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed if enabled, it provides some useful output with `Apache ToStringBuilder`")
            .isTrue

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
    fun `objects with mutual reference should yield decent output with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed when enabled, provides useful output with `Apache ToStringBuilder`")
            .isTrue

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

    @Test
    fun `object with uninitialized lateinit property yields decent output with Apache ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed when enabled, yields decent output for uninitialized lateinit var with `Apache ToStringBuilder`")
            .isTrue

        @Suppress("unused")
        class WithLateinit {
            lateinit var uninitializedStringVar: String
            lateinit var initializedStringVar: String
            init {
                if (LocalDate.now().isAfter(LocalDate.of(2000, 1, 1))) {
                    initializedStringVar = "I am initialized"
                }
            }

            override fun toString(): String = ToStringBuilder.reflectionToString(this)
        }
        assertThat(WithLateinit().toString())
            .contains("initializedStringVar=I am initialized")
            .contains("uninitializedStringVar=<null>")
    }

    @Test
    fun `synthetic types shouldn't cause exceptions with Apache's ToStringBuilder`() {
        assumeThat(demosEnabled)
            .`as`("This test would succeed if enabled: `ToStringBuilder.reflectionToString` handles synthetic types without exceptions")
            .isTrue

        // arrange
        val supplier: () -> String = { "a String supplier" }

        listOf(
            supplier,
            { "an other String supplier" },
            { Any() }
        ).forEach {
            // act, assert
            val toString = ToStringBuilder.reflectionToString(it)
            println(toString)
            assertThat(toString).isNotNull()
        }
    }

    @Test
    fun `non-thread safe collections should be handled without ConcurrentModificationException`() {

        assumeThat(demosEnabled)
            .`as`("Will usually fail when enabled, `Apache ToStringBuilder` does not handle ConcurrentModificationException")
            .isTrue

        // This test depends on a race condition that is hit in most cases, but not always.
        // So sometimes the test might succeed
        repeat(10) {
            // arrange
            class UnsafeClass {
                val unsafeList: ArrayList<Int> = ArrayList((0..150).toList())
            }

            val unsafeClass = UnsafeClass()

            // Continuously modify list in separate threads
            val threadList: MutableList<Thread> = mutableListOf()
            val executors: MutableList<ExecutorService> =
                (1..32).map { Executors.newSingleThreadExecutor() }.toMutableList()
            val modifications = AtomicInteger(0)
            val listModifier = Runnable {
                threadList.add(Thread.currentThread())
                var i = unsafeClass.unsafeList.size
                while (true) {
                    Thread.sleep(0, 1)
                    try {
                        val index = Random.nextInt(0, unsafeClass.unsafeList.size - 1)
                        unsafeClass.unsafeList.add(index, i++)
                    } catch (e: ConcurrentModificationException) {
                        // ignore
                    }
                    modifications.incrementAndGet()
                }
            }
            try {
                executors.forEach { it.submit(listModifier) }
                Awaitility.await().until { modifications.get() > 0 }
                // act, assert
                assertThat(ToStringBuilder.reflectionToString(unsafeClass))
                    .`as`("ConcurrentModificationException should be handled")
                    .isNotNull
            } finally {
                executors.forEach { it.shutdownNow() }
                threadList.forEach { it.interrupt() }
            }
        }
    }

    @Test
    fun `non-thread safe maps should be handled without ConcurrentModificationException`() {

        assumeThat(demosEnabled)
            .`as`("Will usually fail when enabled, `Apache ToStringBuilder` does not handle ConcurrentModificationException")
            .isTrue

        // This test depends on a race condition that is hit in most cases, but not always.
        // So sometimes the test might succeed
        repeat(10) {
            // arrange
            class UnsafeClass {
                val unsafeMap: MutableMap<Int, Int> = mutableMapOf()
            }

            val unsafeClass = UnsafeClass()
            // Buffer to store error message in
            val logBuffer = StringBuffer()
            logger = { msg -> logBuffer.append(msg) }

            // Continuously modify list in separate threads
            val threadList: MutableList<Thread> = mutableListOf()
            val executors: MutableList<ExecutorService> =
                (1..32).map { Executors.newSingleThreadExecutor() }.toMutableList()
            val modifications = AtomicInteger(0)
            val mapModifier = Runnable {
                threadList.add(Thread.currentThread())
                var i = unsafeClass.unsafeMap.size
                while (true) {
                    Thread.sleep(0, 1)
                    try {
                        unsafeClass.unsafeMap[unsafeClass.unsafeMap.size + 1] = i++
                    } catch (e: ConcurrentModificationException) {
                        // ignore
                    }
                    modifications.incrementAndGet()
                }
            }
            try {
                executors.forEach { it.submit(mapModifier) }
                Awaitility.await().until { unsafeClass.unsafeMap.size > 10 }
                // act, assert
                assertThat(ToStringBuilder.reflectionToString(unsafeClass))
                    .`as`("ConcurrentModificationException should be handled")
                    .isNotNull
            } finally {
                executors.forEach { it.shutdownNow() }
                threadList.forEach { it.interrupt() }
            }
        }
    }

    @Test
    fun `ToStringBuilder (non-reflective) should give proper output after refactoring property names` () {
        assumeThat(demosEnabled)
            .`as`("Will fail, demonstrating that Apache's non-reflective `ToStringBuilder` is not too maintenance-friendly")
            .isTrue

        class Person {
            var name: String? = null
            var age = 0
            var isSmoker = true // refactored from `smoker` to `isSmoker`
            private var willStopSmoking: () -> Boolean = { isSmoker && Random.nextBoolean() }
            override fun toString(): String {
                return ToStringBuilder(this)
                    .append("name", name)
                    .append("age", age)
                    .append("smoker", isSmoker) // refactored
                    .append("willStopSmoking", willStopSmoking)
                    .append("some string to be appended")
                    .toString()
            }
        }
        val result = Person().toString()
        assertThat(result)
            .contains("willStopSmoking=() -> kotlin.Boolean") // will succeed
            .endsWith("some string to be appended]") // will succeed
            .`as`("Should contain refactored property name")
            .contains("isSmoker=smoker") // will fail, showing why it's less maintenance-friendly
    }
}
