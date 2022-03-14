package nl.kute.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.full.memberProperties

@Suppress("unused") // several properties accessed by reflection only
internal class PropertyResolverTest {

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
        private val l by lazy { 7 }
    }

    private abstract class T2(override val x: Int?, override val y: String, override val z: LocalDateTime) :
        T1(x, y, z), I0 {
        abstract val a: Int
        val f: Float = 2.3F
        private var p = 'P'
    }

    private open class T3(
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

    @Test
    fun propertiesFromHierarchy() {
        // Arrange
        val t3 = T3(14, "Hi", LocalDateTime.now(), 14, "Hallo", LocalDateTime.MIN)

        val privatePString = T3::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE }
        val privateLazy = T1::class.memberProperties
            .first { it.name == "l" && it.visibility == PRIVATE }
        // shadowed by T3.p, so should not be included
        val privatePShadowed = T2::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE }

        // Act
        val properties = t3.propertiesFromHierarchy()

        // Assert
        // Shadowed property should not be included
        assertThat(properties).doesNotContain(privatePShadowed)
        assertThat(properties)
            .containsExactly(T3::a, T3::i, T3::j, T3::k, privatePString, T3::f, T3::x, T3::y, T3::z, privateLazy)
    }

    @Test
    fun `propertiesFromHierarchy on abstract class or interface`() {
        assertThat(I0::class.propertiesFromHierarchy()).containsExactly(I0::i, I0::j, I0::k)
        val privatePChar = T2::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE }
        val privateL = T1::class.memberProperties
            .first { it.name == "l" && it.visibility == PRIVATE }
        assertThat(T2::class.propertiesFromHierarchy())
            .isEqualTo(listOf(T2::a, T2::f, privatePChar, T2::x, T2::y, T2::z, T2::i, T2::j, T2::k, privateL))
    }

    @Test
    fun `propertiesFromHierarchy on anonymous class`() {
        val anon = object : T3(1, "", LocalDateTime.MIN, null, "y", LocalDateTime.MAX) {
            val q = 'q'
        }
        val anonQ = anon::class.memberProperties.first { it.name == "q" }
        val properties = anon.propertiesFromHierarchy()
        val propertiesT3 = T3::class.propertiesFromHierarchy().toMutableList()
        @Suppress("UNCHECKED_CAST")
        assertThat(properties).isEqualTo(propertiesT3.also { it.add(0, anonQ) })
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun propertyMapByHierarchy() {
        // Arrange
        val privatePString = T3::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE } as KProperty1<Any, *>
        val privateLazy = T1::class.memberProperties
            .first { it.name == "l" && it.visibility == PRIVATE } as KProperty1<Any, *>

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

        val expected = mapOf(
            Pair(
                T3::class,
                listOf(
                    T3::a as KProperty1<Any, *>,
                    T3::i as KProperty1<Any, *>,
                    T3::j as KProperty1<Any, *>,
                    T3::k as KProperty1<Any, *>,
                    privatePString
                )
            ),
            Pair(
                T2::class,
                listOf(
                    T3::f as KProperty1<Any, *>,
                    T3::x as KProperty1<Any, *>,
                    T3::y as KProperty1<Any, *>,
                    T3::z as KProperty1<Any, *>
                )
            ),
            Pair(T1::class, listOf(privateLazy))
        )

        assertThat(propertyMap).isEqualTo(expected)
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

}