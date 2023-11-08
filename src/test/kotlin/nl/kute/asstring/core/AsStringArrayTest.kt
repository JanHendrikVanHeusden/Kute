package nl.kute.asstring.core

import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.helper.base.ObjectsStackVerifier
import nl.kute.helper.helper.isObjectAsString
import nl.kute.reflection.util.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AsStringArrayTest: ObjectsStackVerifier {

    private val names: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    @Test
    fun `non-recursive array data should be rendered like contentDeepToString`() {
        // arrange, act
        val classWithArrayString =
            ClassWithArrayNoElementsLimit(names, "a string", 3.5F).asString()
        val dataClassWithArrayString =
            DataClassWithArray(names, "a string", 3.5F).asString()

        // assert
        assertThat(classWithArrayString).contains(names.contentDeepToString())
        names.forEach {
            assertThat(classWithArrayString.contains(it))
        }

        assertThat(dataClassWithArrayString).isObjectAsString(
            "DataClassWithArray",
            "array=${names.contentDeepToString()}",
            "aStr=a string",
            "aFloat=3.5"
        )
        // same as data class with same properties
        assertThat(classWithArrayString).isObjectAsString(
            "ClassWithArrayNoElementsLimit",
            "array=${names.contentDeepToString()}",
            "aStr=a string",
            "aFloat=3.5"
        )
    }

    @Test
    fun `loooooooooooong array string representations should be capped at 500 chars`() {
        val array = IntArray(1000).map { it.toString() }.toTypedArray()
        @Suppress("unused")
        @AsStringOption(elementsLimit = Int.MAX_VALUE)
        class ClassWithArray(val array: Array<String>)
        val classWithArrayString = ClassWithArray(array).asString()
        assertThat(classWithArrayString.length)
            .isEqualTo(500 + ClassWithArray::class.simpleName!!.length + "(array=)".length + "...".length)
    }

    @Test
    fun `array elements with elementLimit at class should be capped according to the class's annotation`() {
        // arrange
        val array = (4..1000).map { it.toString() }.toList().toTypedArray()
        assertThat((ClassWithArrayAndElementLimit::class.annotations.first { it is AsStringOption } as AsStringOption).elementsLimit)
            .isEqualTo(3)
        // act
        val classWithArrayString = ClassWithArrayAndElementLimit(array).asString()
        // assert
        assertThat(classWithArrayString).isEqualTo("${ClassWithArrayAndElementLimit::class.simplifyClassName()}(array=[4, 5, 6, ...])")
    }

    @Test
    fun `array elements with elementLimit at property should be capped according the property's annotation`() {
        // arrange
        val array = (2..1000).map { it.toString() }.toList().toTypedArray()
        assertThat((ClassWithArrayAndPropertyElementLimit::class.annotations.first { it is AsStringOption } as AsStringOption).elementsLimit)
            .isEqualTo(3)
        assertThat((ClassWithArrayAndPropertyElementLimit::array.annotations.first { it is AsStringOption } as AsStringOption).elementsLimit)
            .isEqualTo(8)
        // act
        val classWithArrayString = ClassWithArrayAndPropertyElementLimit(array).asString()
        // assert
        assertThat(classWithArrayString)
            .isEqualTo(ClassWithArrayAndPropertyElementLimit::class.simplifyClassName() +
                    "(array=[2, 3, 4, 5, 6, 7, 8, 9, ...])")
    }

    @Test
    fun `array elements should be capped at elementLimit as of the default if not annotated`() {
        // arrange
        val array = (1..1000).map { it.toString() }.toList().toTypedArray()

        assertThat(ClassWithArray::class.annotations.filterIsInstance<AsStringOption>()).isEmpty()
        assertThat(array.size).isGreaterThan(AsStringOption.defaultOption.elementsLimit)

        val expected = array.toList().subList(0, AsStringOption.defaultOption.elementsLimit).joinToString()

        // act
        val classWithArrayString = ClassWithArray(array).asString()
        // assert
        assertThat(classWithArrayString).endsWith("[$expected, ...])")
    }

// region ~ Classes, objects etc. to be used for testing

    @Suppress("unused")
    private class ClassWithArray(val array: Array<String>) {
        override fun toString(): String = asString()
    }

    @Suppress("unused")
    @AsStringOption(elementsLimit = Int.MAX_VALUE)
    private class ClassWithArrayNoElementsLimit(val array: Array<String>, val aStr: String, val aFloat: Float) {
        override fun toString(): String = asString()
    }

    @Suppress("unused")
    @AsStringOption(elementsLimit = 3)
    private class ClassWithArrayAndElementLimit(val array: Array<String>) {
        override fun toString(): String = asString()
    }

    @Suppress("unused")
    @AsStringOption(elementsLimit = 3)
    private class ClassWithArrayAndPropertyElementLimit(@AsStringOption(elementsLimit = 8) val array: Array<String>) {
        override fun toString(): String = asString()
    }

    @Suppress("ArrayInDataClass") // suppress warning that equals and hashCode should be overridden
    private data class DataClassWithArray(private val array: Array<String>, val aStr: String, val aFloat: Float)
}

// endregion