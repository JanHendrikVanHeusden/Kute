@file:Suppress("unused")  // several properties accessed by reflection only

package nl.kute.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.reflect.KVisibility.PRIVATE
import kotlin.reflect.full.memberProperties

class PropertyResolverTest {

    @Test
    fun `propertiesFromHierarchy on abstract class or interface`() {
        // arrange
        assertThat(I0::class.propertiesFromSubSuperHierarchy()).containsExactly(I0::i, I0::j, I0::k)
        val privateL = T1::class.memberProperties
            .first { it.name == "l" && it.visibility == PRIVATE }
        val privatePChar = T2::class.memberProperties
            .first { it.name == "p" && it.visibility == PRIVATE }

        // act, assert
        assertThat(T1::class.propertiesFromSubSuperHierarchy()).contains(privateL)
        assertThat(T2::class.propertiesFromSubSuperHierarchy())
            .isEqualTo(listOf(T2::a, T2::f, privatePChar, T2::x, T2::y, T2::z, T2::i, T2::j, T2::k))
    }

    @Test
    fun `propertiesFromHierarchy on anonymous class`() {
        // arrange
        val anon = object : T3(1, "", LocalDateTime.MIN, null, "y", LocalDateTime.MAX) {
            val q = 'q'
        }

        // act
        val propertiesAnon = anon::class.propertiesFromSubSuperHierarchy()
        val propQAnon = anon::class.memberProperties.first { it.name == "q" }

        val propertiesT3 = T3::class.propertiesFromSubSuperHierarchy()
        val propP = T3::class.memberProperties.first { it.name == "p" }

        // assert
        assertThat(propertiesT3).contains(propP)
        assertThat(propertiesT3.firstOrNull { it.name == "q" }).isNull()

        assertThat(propertiesAnon).contains(propQAnon)
        assertThat(propertiesAnon.firstOrNull { it.name == "p" }).isNull()
    }

    /////////////////////////
    // Test classes / objects
    /////////////////////////

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

    @Suppress("PropertyName")
    private open class T4 : T3(1, "j", LocalDateTime.MAX, null, "y", LocalDateTime.now()) {
        val `a value with spaces`: Any = ""
        var aValueWithUpperCase: Any? = null
    }

}