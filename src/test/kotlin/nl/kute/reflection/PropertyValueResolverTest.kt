package nl.kute.reflection

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import java.time.LocalDateTime
import kotlin.reflect.KProperty0

@Suppress("unused") // several properties accessed by reflection only
internal class PropertyValueResolverTest {

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

    @Test
    fun getPropValueSafe() {
        val t3 = T3(x = 12, y = "yval", z = LocalDateTime.MIN, i = 28, j = "val of j", k = LocalDateTime.MAX)
        assertThat(getPropValue(t3::x)).isEqualTo(12)
        assertThat(getPropValue(t3::z)).isEqualTo(LocalDateTime.MIN)
        assertThat(getPropValue(t3::z)).isEqualTo(LocalDateTime.MIN)
        assertThat(getPropValue(t3::i)).isEqualTo(28)
        t3.i = null
        assertThat(getPropValue(t3::i)).isNull()

        val throwingProperty: KProperty0<Unit> = mock {
            on { get() } doThrow RuntimeException()
        }
        // Demonstrate that it's safe
        assertThat(getPropValue(throwingProperty))
            .`as`("Should be safe even with contrived exception")
            .isNull()
    }

}
