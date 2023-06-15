package nl.kute.reflection

import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import java.time.LocalDateTime
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

@Suppress("unused") // several properties accessed by reflection only
internal class PropertyValueResolverTest {

    @BeforeEach
    @AfterEach
    fun setUp() {
        resetStdOutLogger()
    }

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
    fun `test getPropValue`() {
        val t3 = T3(x = 12, y = "yval", z = LocalDateTime.MIN, i = 28, j = "val of j", k = LocalDateTime.MAX)
        val properties: Collection<KProperty1<T3, *>> = T3::class.memberProperties
        fun getPropByName(name: String): KProperty1<T3, *> {
            return properties.first{ it.name == name }
        }
        assertThat(t3.getPropValue(getPropByName("x"))).isEqualTo(12)
        assertThat(t3.getPropValue(getPropByName("z"))).isEqualTo(LocalDateTime.MIN)
        assertThat(t3.getPropValue(getPropByName("i"))).isEqualTo(28)
        t3.i = null
        assertThat(t3.getPropValue(getPropByName("i"))).isNull()
    }

    @Test
    fun `no exception should be raised from getPropValue`() {
        val t3 = T3(x = 12, y = "yval", z = LocalDateTime.MIN, i = 28, j = "val of j", k = LocalDateTime.MAX)

        // set StringBuffer to retrieve log message
        val logBuffer = StringBuffer()
        logger = { msg -> logBuffer.append(msg).append("\n")}

        val throwingProperty: KProperty1<T3, *> = mock {
            on { this.get(t3) } doThrow RuntimeException()
        }

        // Demonstrate that it's safe
        assertThat(t3.getPropValue(throwingProperty))
            .`as`("Should be safe even with contrived exception")
            .isNull()
        // examine the logging, to verify that the exception was actually hit
        assertThat(logBuffer).contains("Exception occurred when retrieving value of property")
    }

}
