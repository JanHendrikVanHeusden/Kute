package nl.kute.demo.alternatives

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import nl.kute.log.log
import nl.kute.log.logger
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

class GsonDemo {

    // Set to true to enable the demos (not advised, several of them fail with stack overflow)
    private val demosEnabled = false

    private val gsonWithNulls = GsonBuilder()
        .serializeNulls().create()

    companion object {
        init {
            log(
                "\nIn some projects, Gson is used to produce toString() output, which isn't too bad at all.\n" +
                        "But Gson is not really designed for that.\n" +
                        " * It produces json, which is primarily designed for machine reading rather than human reading" +
                        " * By default, it does not include `null` values\n" +
                        " * It does not (and should not!) handle exceptions\n"
            )
        }
    }

    @Test
    fun `Gson should accept null and yield decent output`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled")
            .isTrue

        val result = Gson().toJson(null)
        assertThat(result).isEqualTo("null")
    }

    @Test
    fun `Gson should yield decent output on 'this'`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled")
            .isTrue

        @Suppress("unused")
        class MyClass {
            val myProp: String = "my prop value"
            override fun toString(): String = Gson().toJson(this)
        }

        val result = MyClass().toString()
        assertThat(result).isEqualTo("""{"myProp":"my prop value"}""")
    }

    @Test
    fun `Gson should include supertype properties`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled, Gson includes supertype properties")
            .isTrue

        @Suppress("unused")
        open class MyClass {
            val myProp: String = "my prop value"
            override fun toString(): String = Gson().toJson(this)
        }

        @Suppress("unused")
        class MySubClass: MyClass() {
            val mySubClassProp = "my subclass prop value"
            // NB: No override of toString() here!
        }

        val result = MySubClass().toString()
        assertThat(result)
            .contains(""""mySubClassProp":"my subclass prop value""", """myProp":"my prop value"""")
    }

    @Test
    fun `Object with array properties should yield decent output with Gson - same as contentDeepToString`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed when enabled, nested arrays give decent toString output with Gson")
            .isTrue

        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3)
            init {
                myArray[2] = arrayOf(4, 5, 6)
            }
        }
        val testObj = MyTestClass()
        val toString = Gson().toJson(testObj)

        println(toString)
        assertThat(toString).isEqualTo("""{"myArray":[0,1,[4,5,6],3]}""")
    }

    @Test
    fun `Arrays with self-referencing elements fails with stack overflow with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Would fail when enabled, causes StackOverflowError with Gson")
            .isTrue

        val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
        myArray[2] = arrayOf(4, 5, 6)
        myArray[3] = myArray
        myArray[4] = myArray

        val toString = Gson().toJson(myArray)
        assertThat(toString).isNotNull
    }

    @Test
    fun `Objects with array properties with self-referencing elements cause stack overflow with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Would fail with StackOverflowError with Gson when enabled")
            .isTrue

        class MyTestClass {
            val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
            init {
                myArray[2] = arrayOf(4, 5, 6)
                myArray[3] = myArray
                myArray[4] = myArray
            }

            override fun toString() = Gson().toJson(this)
        }
        val myArraysObject = MyTestClass()
        val toString = myArraysObject.toString()
        assertThat(toString).isNotNull
    }

    @Test
    fun `Objects with mutually referencing array properties cause stack overflow with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Would fail when enabled, objects with mutually referencing array properties cause StackOverflowError with Gson")
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

            override fun toString() = Gson().toJson(this)
        }
        val myArraysObject = MyTestClass()
        val toString = myArraysObject.toString()

        assertThat(toString).isNotNull
    }

    @Test
    fun `Collections with self-referencing elements cause stack overflow with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Collection with self reference causes stack overflow with Gson")
            .isTrue

        val mutableList: MutableList<Any> = mutableListOf("first", "second", "third")
        mutableList[1] = mutableList // self reference
        mutableList.add(mutableList) // self reference

        val toString = Gson().toJson(mutableList)

        assertThat(toString).isNotNull
    }

    @Test
    fun `Collections with mutually referencing elements cause stack overflow with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Would fail, mutually referencing collection elements cause stack overfow with Gson")
            .isTrue

        val list1: MutableList<Any> = mutableListOf("first 1", "second 1", "third 1")
        val list2: LinkedList<Any> = LinkedList(listOf("first 2", "second 2", "third 2"))

        list1.add(list2)
        list1.add(list2)
        list2.add(list1)
        list2.add(list1)

        val toString1 = Gson().toJson(list1)
        val toString2 = Gson().toJson(list2)
        assertThat(toString1).isNotNull
        assertThat(toString2).isNotNull
    }

    @Suppress("unused")
    private class GetSelfReference (val id: Int, var selfRef: GetSelfReference? = null, var otherRef: GetSelfReference? = null) {
        override fun toString(): String = Gson().toJson(this)
    }

    @Test
    fun `objects with self reference should yield decent output with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Will succeed if enabled, it provides useful output with Gson")
            .isTrue

        val testObj = GetSelfReference(1)
        val other = GetSelfReference(2)
        testObj.selfRef = testObj
        testObj.otherRef = other

        val toString = testObj.toString()
        // "{"id":1,"otherRef":{"id":2}}"
        println(toString)
        // selfRef is not included, seems Gson keeps self-references out
        assertThat(toString)
            // .contains("selfRef") // would fail
            .isEqualTo("""{"id":1,"otherRef":{"id":2}}""")
    }

    @Suppress("unused")
    @Test
    fun `objects with mutual reference cause stack overflow with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Would fail when enabled, throws StackOverflowError with Gson")
            .isTrue

        class Parent(val name: String, val children: MutableSet<Any> = mutableSetOf()) {
            override fun toString(): String = Gson().toJson(this)
        }

        @Suppress("CanBeParameter")
        class Child(val name: String, val mother: Parent, val father: Parent) {
            init {
                mother.children.add(this)
                father.children.add(this)
            }
            override fun toString(): String = Gson().toJson(this)
        }

        val mother = Parent(name = "M")
        val father = Parent(name = "F")
        val child1 = Child("C1", mother, father)
        val child2 = Child("C2", mother, father)

        listOf(mother, father, child1, child2).forEach {
            assertThat(it.toString())
                .contains("name=M")
                .contains("name=F")
                .contains("C1")
                .contains("C2")
        }
    }

    @Test
    fun `object with uninitialized lateinit property yields decent output with Gson`() {
        assumeThat(demosEnabled)
            .`as`("Would succeed when enabled, yields decent output for uninitialized lateinit var with Gson")
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

            override fun toString(): String = gsonWithNulls.toJson(this)
        }
        assertThat(WithLateinit().toString())
            .isEqualTo("""{"uninitializedStringVar":null,"initializedStringVar":"I am initialized"}""")
    }

    @Test
    fun `synthetic types shouldn't cause exceptions with Gson`() {
        assumeThat(demosEnabled)
            .`as`("This test would succeed if enabled: Gson handles synthetic types without exceptions")
            .isTrue

        // arrange
        val supplier: () -> String = { "a String supplier" }

        listOf(
            supplier,
            { "an other String supplier" },
            { Any() }
        ).forEach {
            // act, assert
            val toString = Gson().toJson(it)
            println(toString)
            assertThat(toString)
                .`as`("Not too informative - but no exceptions, at least")
                .isEqualTo("""{"arity":0}""")
        }
    }

    @Test
    fun `non-thread safe collections throw ConcurrentModificationException with Gson when modified concurrently`() {

        assumeThat(demosEnabled)
            .`as`("Will usually fail when enabled, Gson does not (and should not!) handle ConcurrentModificationException")
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
                assertThat(Gson().toJson(unsafeClass))
                    .isNotNull
            } finally {
                executors.forEach { it.shutdownNow() }
                threadList.forEach { it.interrupt() }
            }
        }
    }

    @Test
    fun `non-thread safe maps throw ConcurrentModificationException with Gson when modified concurrently`() {

        assumeThat(demosEnabled)
            .`as`("Will usually fail when enabled, Gson does not (and should not!) handle ConcurrentModificationException")
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
                assertThat(Gson().toJson(unsafeClass))
                    .isNotNull
            } finally {
                executors.forEach { it.shutdownNow() }
                threadList.forEach { it.interrupt() }
            }
        }
    }

}