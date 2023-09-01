package nl.kute.asstring.core

import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.asstring.namedvalues.NamedSupplier
import nl.kute.reflection.util.simplifyClassName
import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.test.base.validateObjectsStack
import nl.kute.testobjects.kotlin.ClassWithToStringCallingAsString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.LinkedList
import java.util.PriorityQueue
import java.util.TreeSet

/**
 * This test class particularly aims to test various objects / constructs with self-references
 * or circular mutual references: these should yield decent output, **without endless loops / stack overflow**.
 * This is accomplished by keeping track of objects being processed by means of an object stack.
 *
 * The correct begin / end states of the object stack are tested "automagically" / implicitly in the
 * `@BeforeEach` and `@AfterEach` methods of classes that implement [ObjectsStackVerifier].
 *
 * However, the tests in this class [AsStringRecursiveObjectsTest] depend even more heavy on the objects stack,
 * so in tests where `asString()` is called more than once, these tests call [validateObjectsStack]`()`
 * explicitly after each call to `asString()`
 */
class AsStringRecursiveObjectsTest: ObjectsStackVerifier {

    @Test
    fun `arrays without recursion should yield same output as contentDeepToString`() {
        // arrange
        val myArray: Array<Any> = arrayOf(0, 1, 2, 3)
        myArray[2] = arrayOf(4, 5, 6)
        // act, assert
        assertThat(myArray.asString())
            .`as`("Should return same result as `myArray.contentDeepToString()`")
            .isEqualTo("[0, 1, [4, 5, 6], 3]")
            .isEqualTo(myArray.contentDeepToString())
    }

    @Test
    fun `arrays with self-referencing elements should yield decent output`() {
        // arrange
        val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
        myArray[2] = arrayOf(4, 5, 6)
        myArray[3] = myArray
        myArray[4] = myArray

        val expected =
            "[0, 1, [4, 5, 6], recursive: ${myArray::class.simpleName}(...), recursive: ${myArray::class.simpleName}(...)]"
        // act, assert
        assertThat(myArray.asString()).isEqualTo(expected)
    }

    @Test
    fun `arrays with mutually referencing elements should yield decent output`() {
        // arrange
        val myArray: Array<Any> = arrayOf("a0", "a1", "a2", "a3", "a4")
        val myList: MutableList<Any> = mutableListOf("L0", "L1", "L2", "L3", "L4")
        myArray[2] = myList
        myArray[3] = myList
        myList[3] = myArray
        myList[1] = myArray

        val expectedForArray = "[a0, a1, [L0, recursive: Array(...), L2, recursive: Array(...), L4], [L0, recursive: Array(...), L2, recursive: Array(...), L4], a4]"
        val expectedForList = "[L0, [a0, a1, recursive: ArrayList(...), recursive: ArrayList(...), a4], L2, [a0, a1, recursive: ArrayList(...), recursive: ArrayList(...), a4], L4]"

        // act, assert
        assertThat(myArray.asString()).isEqualTo(expectedForArray)
        validateObjectsStack()
        assertThat(myList.asString()).isEqualTo(expectedForList)
        validateObjectsStack()
    }

    @Test
    fun `Objects with array properties with mutually referencing elements should yield decent output`() {
        // arrange
        class MyTestClass {
            val myArray: Array<Any> = arrayOf("a0", "a1", "a2", "a3", "a4")
            val myList: MutableList<Any> = mutableListOf("L0", "L1", "L2", "L3", "L4")
            init {
                myArray[2] = myList
                myArray[3] = myList
                myList[3] = myArray
                myList[1] = myArray
            }
        }

        // act, assert
        val myTestObj = MyTestClass()
        assertThat(myTestObj.asString())
            .isObjectAsString(
                "MyTestClass",
                "myArray=[a0, a1, [L0, recursive: Array(...), L2, recursive: Array(...), L4], [L0, recursive: Array(...), L2, recursive: Array(...), L4], a4]",
                "myList=[L0, [a0, a1, recursive: ArrayList(...), recursive: ArrayList(...), a4], L2, [a0, a1, recursive: ArrayList(...), recursive: ArrayList(...), a4], L4]"
            )
    }

    @Test
    fun `Collections without recursion should yield same output as their default toString method`() {
        // arrange
        val values = arrayOf("first", "second", "third")
        listOf(
            listOf(*values),
            setOf(*values),
            PriorityQueue(values.asList()),
            HashMap<String, String>().apply { this.putAll(values.map { it to it }) },
            LinkedList(values.asList()),
            TreeSet(values.asList())
        ).forEachIndexed { i, it ->
            assertThat(it.asString())
                .`as`("asString() of expression #$i should adhere to toString()")
                .isEqualTo(it.toString())
        }
    }

    @Test
    fun `Collections with self-referencing elements should yield decent output`() {
        // arrange
        val mutableList: MutableList<Any> = mutableListOf("first", "second", "third")
        mutableList[1] = mutableList // self reference
        mutableList.add(mutableList) // self reference
        // act, assert
        assertThat(mutableList.asString())
            .isEqualTo("[first, recursive: ${mutableList::class.simpleName}(...), third, recursive: ${mutableList::class.simpleName}(...)]")
    }

    @Test
    fun `Collections with mutually referencing elements should yield decent output`() {
        // arrange
        val list1: MutableList<Any> = mutableListOf("first 1", "second 1", "third 1")
        val list2: LinkedList<Any> = LinkedList(listOf("first 2", "second 2", "third 2"))

        list1.add(list2)
        list1.add(list2)
        list2.add(list1)
        list2.add(list1)

        val expected1 =
            "[first 1, second 1, third 1, [first 2, second 2, third 2, recursive: ArrayList(...), recursive: ArrayList(...)]," +
                    " [first 2, second 2, third 2, recursive: ArrayList(...), recursive: ArrayList(...)]]"
        val expected2 =
            "[first 2, second 2, third 2, [first 1, second 1, third 1, recursive: LinkedList(...), recursive: LinkedList(...)]," +
                    " [first 1, second 1, third 1, recursive: LinkedList(...), recursive: LinkedList(...)]]"

        // act, assert
        assertThat(list1.asString()).isEqualTo(expected1)
        validateObjectsStack()
        assertThat(list2.asString()).isEqualTo(expected2)
        validateObjectsStack()
    }

    @Test
    fun `Objects with collection properties with mutually referencing elements should yield decent output`() {
        // arrange
        class MyTestClass {
            val list1: MutableList<Any> = mutableListOf("first 1", "second 1", "third 1")
            val list2: LinkedList<Any> = LinkedList(listOf("first 2", "second 2", "third 2"))

            init {
                list1.add(list2)
                list1.add(list2)
                list2.add(list1)
                list2.add(list1)
            }
        }

        val testObj = MyTestClass()

        // act, assert
        assertThat(testObj.asString())
            .isObjectAsString(
                "MyTestClass",
                "list1=[first 1, second 1, third 1, [first 2, second 2, third 2," +
                        " recursive: ArrayList(...), recursive: ArrayList(...)], [first 2, second 2, third 2," +
                        " recursive: ArrayList(...), recursive: ArrayList(...)]]",
                "list2=[first 2, second 2, third 2, [first 1, second 1, third 1," +
                        " recursive: LinkedList(...), recursive: LinkedList(...)], [first 1, second 1, third 1, " +
                        "recursive: LinkedList(...), recursive: LinkedList(...)]]"
            )
    }

    @Suppress("unused")
    private class GetSelfReference (val id: Int, var selfRef: GetSelfReference? = null, var otherRef: GetSelfReference? = null) {
        override fun toString(): String = asString()
    }

    @Test
    fun `objects with self reference should yield decent output`() {
        // arrange
        val testObj = GetSelfReference(1)
        val other = GetSelfReference(2)
        testObj.selfRef = testObj
        testObj.otherRef = other
        val className = GetSelfReference::class.simplifyClassName()
        val expected = "$className(id=1, otherRef=$className(id=2, otherRef=null, selfRef=null), selfRef=recursive: $className(...))"
        // act, assert
        assertThat(testObj.asString()).isEqualTo(expected)
            .isObjectAsString(
                className,
                "id=1",
                "otherRef=${testObj.otherRef.asString()}",
                "selfRef=recursive: $className(...)"
            )
    }

    @Test
    fun `lambdas with mutual reference should yield decent output`() {
        assertThat(MutualReferencingLambdas().asString())
            .isObjectAsString(
                "MutualReferencingLambdas",
                "lambda1=(() -> Unit) -> Unit",
                "lambda2=(() -> Unit) -> Unit"
            )
    }

    @Test
    fun `Supplier that supplies an object whose toString calls asString`() {
        val testObj = ClassWithToStringCallingAsString()
        val namedSupplier = NamedSupplier("asStringCaller") { testObj }

        val asStringBuilder = testObj.asStringBuilder()
            .withAlsoNamed(namedSupplier)
            .build()

        assertThat(asStringBuilder.asString())
            .isObjectAsString(
                "ClassWithToStringCallingAsString",
                "prop1=I am prop1",
                withLastPropertyString = "asStringCaller=recursive: ClassWithToStringCallingAsString(...)"
            )
    }

    @Suppress("unused")
    @Test
    fun `objects with mutual reference should yield decent output`() {
        // arrange
        class Parent(val name: String, val children: MutableSet<Any> = mutableSetOf()) {
            override fun toString(): String = asString()
        }

        @Suppress("CanBeParameter")
        class Child(val name: String, val mother: Parent, val father: Parent) {
            init {
                mother.children.add(this)
                father.children.add(this)
            }

            override fun toString(): String = asString()
        }

        val mother = Parent(name = "M")
        val father = Parent(name = "F")
        val child1 = Child("C1", mother, father)
        val child2 = Child("C2", mother, father)

        //@formatter:off
        val expectedMother =
            "Parent(children=[" +
                    "Child(father=Parent(children=[" +
                                "recursive: Child(...), " +
                                "Child(father=recursive: Parent(...), mother=recursive: Parent(...), name=C2)], " +
                           "name=F), " +
                           "mother=recursive: Parent(...), " +
                    "name=C1), " +
                    "Child(father=Parent(children=[" +
                                "Child(father=recursive: Parent(...), mother=recursive: Parent(...), name=C1), " +
                                "recursive: Child(...)], " +
                            "name=F), " +
                            "mother=recursive: Parent(...), " +
                    "name=C2)], " +
            "name=M)"
        val expectedFather =
            "Parent(children=[" +
                    "Child(father=recursive: Parent(...), " +
                            "mother=Parent(children=[" +
                                "recursive: Child(...), " +
                                "Child(father=recursive: Parent(...), mother=recursive: Parent(...), name=C2)], " +
                            "name=M), " +
                    "name=C1), " +
                    "Child(father=recursive: Parent(...), " +
                            "mother=Parent(children=[" +
                                "Child(father=recursive: Parent(...), mother=recursive: Parent(...), name=C1), " +
                                "recursive: Child(...)], " +
                            "name=M), " +
                    "name=C2)], " +
            "name=F)"
        val expectedChild1 =
            "Child(father=Parent(children=[" +
                        "recursive: Child(...), " +
                        "Child(father=recursive: Parent(...), " +
                                "mother=Parent(children=[" +
                                    "recursive: Child(...), " +
                                    "recursive: Child(...)], " +
                                "name=M), " +
                        "name=C2)], " +
                    "name=F), " +
                    "mother=Parent(children=[" +
                        "recursive: Child(...), " +
                        "Child(father=Parent(children=[" +
                                    "recursive: Child(...), " +
                                    "recursive: Child(...)], " +
                                "name=F), " +
                                "mother=recursive: Parent(...), " +
                        "name=C2)], " +
                    "name=M), " +
            "name=C1)"
        val expectedChild2 =
            "Child(father=Parent(children=[" +
                        "Child(father=recursive: Parent(...), " +
                                "mother=Parent(children=[" +
                                    "recursive: Child(...), " +
                                    "recursive: Child(...)], " +
                                "name=M), " +
                        "name=C1), " +
                        "recursive: Child(...)], " +
                    "name=F), " +
                    "mother=Parent(children=[" +
                        "Child(father=Parent(children=[" +
                                    "recursive: Child(...), " +
                                    "recursive: Child(...)], " +
                                "name=F), " +
                                "mother=recursive: Parent(...), " +
                        "name=C1), " +
                        "recursive: Child(...)], " +
                    "name=M), " +
            "name=C2)"
        //@formatter:on

        // act, assert
        assertThat(mother.toString()).isEqualTo(expectedMother)
        validateObjectsStack()

        assertThat(father.toString()).isEqualTo(expectedFather)
        validateObjectsStack()

        assertThat(child1.toString()).isEqualTo(expectedChild1)
        validateObjectsStack()

        assertThat(child2.toString()).isEqualTo(expectedChild2)
        validateObjectsStack()
    }

}

private class MutualReferencingLambdas {
    lateinit var lambda1: (() -> Unit) -> Unit
    lateinit var lambda2: (() -> Unit) -> Unit
    init {
        lambda1 = {alambda -> println("${alambda.asString()}, lambda1 = ${lambda1.asString()}, lambda2 = ${lambda2.asString()}") }
        lambda2 = {alambda -> println("${alambda.asString()}, lambda1 = ${lambda1.asString()}, lambda2 = ${lambda2.asString()}") }
    }
}