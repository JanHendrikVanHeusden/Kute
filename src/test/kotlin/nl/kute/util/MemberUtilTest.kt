package nl.kute.util

import nl.kute.reflection.hasImplementedToString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MemberUtilTest {

    @Test
    fun `hasToStringMethod should find toString in declaring class`() {
        class MyClass {
            override fun toString() = "This is MyClass"
        }
        assertThat(MyClass::class.hasImplementedToString()).isTrue
    }

    @Test
    fun `hasToStringMethod should find toString in super class`() {
        open class MyClass {
            override fun toString() = "This is MyClass"
        }
        class MySubClass: MyClass()
        assertThat(MySubClass::class.hasImplementedToString()).isTrue
    }

    @Test
    fun `hasToStringMethod should not find toString of Any`() {
        class MyClass
        assertThat(MyClass::class.hasImplementedToString()).isFalse
    }

    @Test
    fun `hasToStringMethod should not find toString overload`() {
        class MyClass {
            // overload, not override!
            @Suppress("unused")
            fun toString(dummy: String = "") = "This is MyClass ($dummy)"
        }
        assertThat(MyClass::class.hasImplementedToString()).isFalse
    }

    @Test
    fun `hasToStringMethod should not find toString of 'Any' object`() {
        // Any.toString() is ignored, it does not give meaningful info (e.g. `java.lang.Object@510f3d34`).
        assertThat(Any::class.hasImplementedToString()).isFalse
    }


}