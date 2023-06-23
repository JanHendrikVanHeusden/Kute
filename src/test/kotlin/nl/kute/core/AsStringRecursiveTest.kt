package nl.kute.core

import nl.kute.base.ObjectsStackVerifier
import nl.kute.reflection.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.LinkedList
import java.util.PriorityQueue
import java.util.TreeSet

class AsStringRecursiveTest: ObjectsStackVerifier {

    @Test
    fun `arrays without recursion should yield same output as contentDeepToString`() {
        val myArray: Array<Any> = arrayOf(0, 1, 2, 3)
        myArray[2] = arrayOf(4, 5, 6)
        assertThat(myArray.asString())
            .`as`("Should return same result as `myArray.contentDeepToString()`")
            .isEqualTo("[0, 1, [4, 5, 6], 3]")
            .isEqualTo(myArray.contentDeepToString())
    }

    @Test
    fun `arrays with self-referencing elements should yield decent output`() {
        val myArray: Array<Any> = arrayOf(0, 1, 2, 3, 4)
        myArray[2] = arrayOf(4, 5, 6)
        myArray[3] = myArray
        myArray[4] = myArray

        val expected =
            "[0, 1, [4, 5, 6], recursive: ${myArray::class.simpleName}(...), recursive: ${myArray::class.simpleName}(...)]"
        assertThat(myArray.asString()).isEqualTo(expected)
    }

    @Test
    fun `arrays with mutually referencing elements should yield decent output`() {
        val myArray: Array<Any> = arrayOf("a0", "a1", "a2", "a3", "a4")
        val myList: MutableList<Any> = mutableListOf("L0", "L1", "L2", "L3", "L4")
        myArray[2] = myList
        myArray[3] = myList
        myList[3] = myArray
        myList[1] = myArray

        val expectedForArray = "[a0, a1, [L0, recursive: Array(...), L2, recursive: Array(...), L4], [L0, recursive: Array(...), L2, recursive: Array(...), L4], a4]"
        val expectedForList = "[L0, [a0, a1, recursive: ArrayList(...), recursive: ArrayList(...), a4], L2, [a0, a1, recursive: ArrayList(...), recursive: ArrayList(...), a4], L4]"

        assertThat(myArray.asString()).isEqualTo(expectedForArray)
        assertThat(myList.asString()).isEqualTo(expectedForList)
    }

    @Test
    fun `Collections without recursion should yield same output as their default toString method`() {
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
        val mutableList: MutableList<Any> = mutableListOf("first", "second", "third")
        mutableList[1] = mutableList // self reference
        mutableList.add(mutableList) // self reference
        assertThat(mutableList.asString())
            .isEqualTo("[first, recursive: ${mutableList::class.simpleName}(...), third, recursive: ${mutableList::class.simpleName}(...)]")
    }

    @Test
    fun `Collections with mutually referencing elements should yield decent output`() {
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

        assertThat(list1.asString()).isEqualTo(expected1)
        assertThat(list2.asString()).isEqualTo(expected2)
    }

    @Suppress("unused")
    internal class GetSelfReference (val id: Int, var selfRef: GetSelfReference? = null, var otherRef: GetSelfReference? = null) {
        override fun toString(): String = asString()
    }

    @Test
    fun `objects with self reference should yield decent output`() {
        val testObj = GetSelfReference(1)
        val other = GetSelfReference(2)
        testObj.selfRef = testObj
        testObj.otherRef = other
        val className = GetSelfReference::class.simplifyClassName()
        val expected = "$className(id=1, otherRef=$className(id=2, otherRef=null, selfRef=null), selfRef=recursive: $className(...))"
        assertThat(testObj.asString()).isEqualTo(expected)
    }

    @Suppress("unused")
    @Test
    fun `objects with mutual reference should yield decent output`() {
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

        assertThat(mother.toString()).isEqualTo(expectedMother)
        assertThat(father.toString()).isEqualTo(expectedFather)
        assertThat(child1.toString()).isEqualTo(expectedChild1)
        assertThat(child2.toString()).isEqualTo(expectedChild2)
    }

}
