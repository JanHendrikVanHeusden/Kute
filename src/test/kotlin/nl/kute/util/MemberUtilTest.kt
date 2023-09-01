package nl.kute.util

import nl.kute.asstring.core.asString
import nl.kute.reflection.util.classToStringMethodCache
import nl.kute.reflection.util.hasImplementedToString
import nl.kute.reflection.util.simplifyClassName
import nl.kute.reflection.util.toStringImplementingMethod
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction

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

    @Test
    fun `results of toString-method resolution should be cached`() {
        // arrange
        classToStringMethodCache.reset()
        assertThat(classToStringMethodCache.size).isZero

        class ClassWithoutToString

        open class ClassWithToString {
            override fun toString() = "I am ${this::class.simplifyClassName()}"
        }

        class SubClassOfClassWithToString: ClassWithToString()

        // act
        repeat(3) {
            ClassWithoutToString().asString()
            ClassWithToString().asString()
            SubClassOfClassWithToString().asString()
        }
        assertThat(classToStringMethodCache.cache).hasSize(3)

        assertThat(ClassWithoutToString::class.toStringImplementingMethod())
            .isNull()
        // not overridden, so Any's toString() method
        @Suppress("UNCHECKED_CAST") val toStringMethod: KFunction<String> = (ClassWithoutToString::class.java.methods
            .first { it.name == "toString" && it.returnType == String::class.java && it.parameters.isEmpty() })
            .kotlinFunction as KFunction<String>


        assertThat(ClassWithToString::class.toStringImplementingMethod()).isNotNull
        assertThat(ClassWithToString::class.toStringImplementingMethod().asString())
            .doesNotContain("Any.toString()")

        assertThat(SubClassOfClassWithToString::class.toStringImplementingMethod()).isNotNull
        assertThat(SubClassOfClassWithToString::class.toStringImplementingMethod().asString())
            .doesNotContain("Any.toString()")

        assertThat(classToStringMethodCache.cache)
            .contains(
                entry(ClassWithoutToString::class, Pair(toStringMethod, false)),
                entry(ClassWithToString::class, Pair(ClassWithToString::class.toStringImplementingMethod(), true)),
                entry(SubClassOfClassWithToString::class, Pair(SubClassOfClassWithToString::class.toStringImplementingMethod(), true)),
            )
    }

}