package nl.kute.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.collections.MutableMap.MutableEntry
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.full.memberProperties

internal class PropertyResolverTest {

    @Suppress("unused")
    private interface I0 {
        val i: Int?
        val j: String
        val k: LocalDateTime
    }

    private interface T0 {
        val x: Int?
        val y: String
        val z: LocalDateTime
    }

    private open class T1(override val x: Int?, override val y: String, override val z: LocalDateTime) : T0 {
        private val j = 65
    }

    @Suppress("unused")
    private abstract class T2(override val x: Int?, override val y: String, override val z: LocalDateTime) :
        T1(x, y, z), I0 {
        abstract val a: Int
        val f: Float = 2.3F
        private var p = 'P'
    }

    @Suppress("unused")
    private class T3(
        override var i: Int?,
        override val j: String,
        override val k: LocalDateTime,
        x: Int?,
        y: String,
        z: LocalDateTime
    ) : T2(x, y, z) {
        override val a: Int = 12
        private val p = "private val"
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun propertiesFromHierarchy() {
        // Arrange
        val t3 = T3(14, "Hi", LocalDateTime.now(), 14, "Hallo", LocalDateTime.MIN)

        val privatePString = T3::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE }

        // shadowed by T3.p, so should not be included
        val privatePShadowed = T2::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE }

        // Act
        val properties = t3.propertiesFromHierarchy()

        // Assert
        // Shadowed property should not be included
        assertThat(properties).doesNotContain(privatePShadowed)
        assertThat(properties)
            .hasSize(9)
            .containsExactly(T3::a, T3::i, T3::j, T3::k, privatePString, T3::f, T3::x, T3::y, T3::z)
        assertThat(properties).hasSize(9)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun propertyMapByHierarchy() {
        // Arrange
        val privatePString = T3::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE } as KProperty1<Any, *>

        // shadowed by T3.p, so should not be included
        val privatePShadowed = T2::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE } as KProperty1<Any, *>
        // Act
        val propertyMap: Map<KClass<*>?, List<KProperty1<Any, *>>> =
            T3::class.propertyMapByHierarchy() as Map<KClass<*>?, List<KProperty1<Any, *>>>

        println(propertyMap.map { "${it.key}${it.value.joinToString("\n\t", "\n\t")}" }
            .joinToString("\n"))

        // Assert
        // Shadowed property should not be included
        assertThat(propertyMap.values.flatten()).doesNotContain(privatePShadowed)

        assertThat(propertyMap)
            .hasSize(2)
            .containsExactly(
                propertyMapEntry(
                    T3::class, listOf(
                        T3::a as KProperty1<Any, *>,
                        T3::i as KProperty1<Any, *>,
                        T3::j as KProperty1<Any, *>,
                        T3::k as KProperty1<Any, *>,
                        privatePString
                    )
                ),
                propertyMapEntry(
                    T2::class, listOf(
                        T3::f as KProperty1<Any, *>,
                        T3::x as KProperty1<Any, *>,
                        T3::y as KProperty1<Any, *>,
                        T3::z as KProperty1<Any, *>
                    )
                )
            )
    }

    @Test
    fun `values of propertyMapByHierarchy should correspond exactly in same order with propertiesFromHierarchy`() {
        val t3 = T3(14, "Hi", LocalDateTime.now(), 14, "Hallo", LocalDateTime.MIN)
        assertThat(t3.propertyMapByHierarchy().values.flatten()).isEqualTo(t3.propertiesFromHierarchy())
        assertThat(T2::class.propertyMapByHierarchy().values.flatten()).isEqualTo(T2::class.propertiesFromHierarchy())
    }

    @Test
    fun `values of propertyMapByHierarchy from class should be equal to those by instance`() {
        val t3 = T3(14, "Hi", LocalDateTime.now(), 14, "Hallo", LocalDateTime.MIN)
        assertThat(t3.propertyMapByHierarchy()).isEqualTo(T3::class.propertyMapByHierarchy())
    }

    @Test
    fun `values of propertiesFromHierarchy from class should be equal to those by instance`() {
        val t3 = T3(14, "Hi", LocalDateTime.now(), 14, "Hallo", LocalDateTime.MIN)
        assertThat(t3.propertiesFromHierarchy()).isEqualTo(T3::class.propertiesFromHierarchy())
    }

    private fun propertyMapEntry(
        kClass: KClass<*>,
        kPropertyList: List<KProperty1<Any, *>>
    ): MutableEntry<KClass<*>, List<KProperty1<Any, *>>> =
        object : MutableEntry<KClass<*>, List<KProperty1<Any, *>>> {
            override val key: KClass<*> = kClass
            override var value: List<KProperty1<Any, *>> = kPropertyList.toMutableList()
            override fun setValue(newValue: List<KProperty1<Any, *>>): List<KProperty1<Any, *>> {
                val oldVal = value
                value = newValue
                return oldVal
            }
        }

}