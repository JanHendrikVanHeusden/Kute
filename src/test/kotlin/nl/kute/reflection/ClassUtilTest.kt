package nl.kute.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.rmi.Remote
import kotlin.reflect.KClass
import java.io.Serializable as javaSerializable

internal class ClassUtilTest {

    private interface Intf1
    private interface Intf2 : Intf1

    private open class Class1
    private open class Class2 : Class1(), Cloneable
    private open class Class3 : Class2(), Remote, javaSerializable
    private open class Class4 : Class3(), Intf2

    @Test
    fun `topDownTypeHierarchy for Class`() {
        val classHierarchy: List<Class<*>> = Class4::class.java.topDownTypeHierarchy()

        // 4 classes that implement 5 interfaces, no duplicates; Any is not included
        assertThat(classHierarchy).hasSize(9)

        // interfaces first
        assertThat(classHierarchy[0]).isEqualTo(Cloneable::class.java)

        // same level, mutual order is unimportant
        assertThat(classHierarchy.subList(1, 3))
            .containsExactlyInAnyOrder(javaSerializable::class.java, Remote::class.java)

        assertThat(classHierarchy[3]).isEqualTo(Intf1::class.java)
        assertThat(classHierarchy[4]).isEqualTo(Intf2::class.java)

        // classes ordered according to hierarchy
        assertThat(classHierarchy.subList(5, 9))
            .isEqualTo(listOf(Class1::class.java, Class2::class.java, Class3::class.java, Class4::class.java))

        assertThat(Class4::class.java.topDownTypeHierarchy(includeInterfaces = true)).isEqualTo(classHierarchy)
    }

    @Test
    fun `topDownTypeHierarchy for Class - no interfaces`() {
        val classHierarchy: List<Class<*>> = Class4::class.java.topDownTypeHierarchy(includeInterfaces = false)

        // 4 classes that implement 5 interfaces, no duplicates; Any is not included
        assertThat(classHierarchy).hasSize(4)

        // classes ordered according to hierarchy
        assertThat(classHierarchy)
            .isEqualTo(listOf(Class1::class.java, Class2::class.java, Class3::class.java, Class4::class.java))
    }

    @Test
    fun `topDownTypeHierarchy for KClass`() {
        val classHierarchy: List<KClass<*>> = Class4::class.topDownTypeHierarchy()
        assertThat(classHierarchy).isEqualTo(Class4::class.java.topDownTypeHierarchy().map { it.kotlin })
        assertThat(Class4::class.topDownTypeHierarchy(includeInterfaces = true)).isEqualTo(classHierarchy)
    }

    @Test
    fun `topDownTypeHierarchy for KClass - no interfaces`() {
        val classHierarchy: List<KClass<*>> = Class4::class.topDownTypeHierarchy(includeInterfaces = false)
        assertThat(classHierarchy)
            .isEqualTo(Class4::class.java.topDownTypeHierarchy(includeInterfaces = false).map { it.kotlin })
    }

    @Test
    fun `topDownTypeHierarchy for Kotlin object`() {
        val myClass4Object = Class4()
        val classHierarchy: List<KClass<*>> = myClass4Object.topDownTypeHierarchy()
        assertThat(classHierarchy).isEqualTo(myClass4Object::class.java.topDownTypeHierarchy().map { it.kotlin })
        assertThat(myClass4Object.topDownTypeHierarchy(includeInterfaces = true)).isEqualTo(classHierarchy)
    }

    @Test
    fun `topDownTypeHierarchy for Kotlin object - no interfaces`() {
        val myClass4Object = Class4()
        val classHierarchy: List<KClass<*>> = myClass4Object.topDownTypeHierarchy(includeInterfaces = false)
        assertThat(classHierarchy)
            .isEqualTo(myClass4Object.topDownTypeHierarchy(includeInterfaces = false))
    }

    @Test
    fun `bottomUpTypeHierarchy for Class`() {
        val classHierarchy: List<Class<*>> = Class4::class.java.bottomUpTypeHierarchy()
        assertThat(classHierarchy).isEqualTo(Class4::class.java.topDownTypeHierarchy().reversed())
        assertThat(Class4::class.java.bottomUpTypeHierarchy(includeInterfaces = true)).isEqualTo(classHierarchy)
    }

    @Test
    fun `bottomUpTypeHierarchy for Class - no interfaces`() {
        val classHierarchy: List<Class<*>> = Class4::class.java.bottomUpTypeHierarchy(includeInterfaces = false)
        assertThat(classHierarchy).isEqualTo(
            Class4::class.java.topDownTypeHierarchy(includeInterfaces = false).reversed()
        )
    }

    @Test
    fun `bottomUpTypeHierarchy for KClass`() {
        val classHierarchy: List<KClass<*>> = Class4::class.bottomUpTypeHierarchy()
        assertThat(classHierarchy).isEqualTo(Class4::class.java.topDownTypeHierarchy().map { it.kotlin }.reversed())
        assertThat(Class4::class.bottomUpTypeHierarchy(includeInterfaces = true)).isEqualTo(classHierarchy)
    }

    @Test
    fun `bottomUpTypeHierarchy for KClass - no interfaces`() {
        val classHierarchy: List<KClass<*>> = Class4::class.bottomUpTypeHierarchy(includeInterfaces = false)
        assertThat(classHierarchy)
            .isEqualTo(Class4::class.java.topDownTypeHierarchy(includeInterfaces = false).map { it.kotlin }.reversed())
    }

    @Test
    fun `bottomUpTypeHierarchy for Kotlin object`() {
        val myClass4Object = Class4()
        val classHierarchy: List<KClass<*>> = myClass4Object.bottomUpTypeHierarchy()
        assertThat(classHierarchy).isEqualTo(Class4::class.java.topDownTypeHierarchy().map { it.kotlin }.reversed())
        assertThat(myClass4Object.bottomUpTypeHierarchy(includeInterfaces = true)).isEqualTo(classHierarchy)
    }

    @Test
    fun `bottomUpTypeHierarchy for Kotlin object - no interfaces`() {
        val myClass4Object = Class4()
        val classHierarchy: List<KClass<*>> = myClass4Object.bottomUpTypeHierarchy(includeInterfaces = false)
        assertThat(classHierarchy)
            .isEqualTo(Class4::class.java.topDownTypeHierarchy(includeInterfaces = false).map { it.kotlin }.reversed())
    }

}
