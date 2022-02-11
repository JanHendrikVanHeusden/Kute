package nl.kute.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.rmi.Remote
import kotlin.reflect.KClass
import java.io.Serializable as javaSerializable

internal class ClassUtilTest {

    private open class Class1

    private open class Class2 : Class1(), Cloneable

    private open class Class3 : Class2(), Remote, javaSerializable

    private class Class4 : Class3(), Remote

    @Test
    fun classHierarchy() {
        val myClass4Object = Class4()
        val classHierarchy: List<KClass<*>> = myClass4Object.classHierarchy()

        // 4 classes that implement 3 interfaces, no duplicates; Any is not included
        assertThat(classHierarchy).hasSize(7)
        // interfaces at top of list
        assertThat(classHierarchy[0]).isEqualTo(Cloneable::class)
        // multiple interfaces at same level, order is unimportant
        assertThat(classHierarchy.subList(1, 3))
            .containsExactlyInAnyOrder(javaSerializable::class, Remote::class)
        // classes ordered according to hierarchy
        assertThat(classHierarchy.subList(3, 7))
            .isEqualTo(listOf(Class1::class, Class2::class, Class3::class, Class4::class))
    }

}
