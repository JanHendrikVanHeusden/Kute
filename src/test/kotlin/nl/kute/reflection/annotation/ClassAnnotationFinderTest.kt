package nl.kute.reflection.annotation

import nl.kute.printable.annotation.PrintOption
import org.apiguardian.api.API
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.annotation.Inherited

internal class ClassAnnotationFinderTest {

    // A bunch of classes with some random annotations sprinkled
    // Obviously, retention must be "runtime" in order to detect it

    @PrintOption(showNullAs = "I", maxLength = 10)
    private interface I

    @API(status = API.Status.INTERNAL, consumers = [])
    @PrintOption(showNullAs = "T0", maxLength = 100)
    private open class T0 : IllegalArgumentException(), I

    private open class T1 : T0()

    @PrintOption(showNullAs = "T2", maxLength = 200)
    @API(status = API.Status.STABLE, consumers = [])
    private open class T2 : T1()

    private open class T3 : T2()

    /**
     * Find the annotation of type `A` on the `this` class or its super types;
     * the annotation at the lowest level is returned, if present at all
     */
    @Test
    fun `annotationOfClass should return the annotation of the deepest subclass`() {
        val api2: API? = T2::class.annotationOfClass()
        assertThat(api2).isNotNull
        assertThat(api2!!.status).isEqualTo(API.Status.STABLE)

        // not inherited, so not found on subclass
        assertThat(API::class.java.isAnnotationPresent(Inherited::class.java)).isFalse
        val api3: API? = T3::class.annotationOfClass()
        assertThat(api3).isNull()

        val printOption0: PrintOption? = T0::class.annotationOfClass()
        assertThat(printOption0).isNotNull
        assertThat(printOption0!!.showNullAs).isEqualTo("T0")

        val printOption2: PrintOption? = T2::class.annotationOfClass()
        assertThat(printOption2).isNotNull
        assertThat(printOption2!!.showNullAs).isEqualTo("T2")

        // inherited, so should be found on subclass
        assertThat(PrintOption::class.java.isAnnotationPresent(Inherited::class.java)).isTrue
        val printOption3: PrintOption? = T3::class.annotationOfClass()
        assertThat(printOption3).isNotNull
        assertThat(printOption3!!.showNullAs).isEqualTo("T2")
    }

    /**
     * Find any annotation of type `A` on `this` class and its super classes.
     * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
     */
    @Test
    fun `annotationsOfClass should return the annotations in order, and include or exclude interfaces as specified`() {
        val t2expected = PrintOption(showNullAs = "T2", maxLength = 200)
        val t0expected = PrintOption(showNullAs = "T0", maxLength = 100)
        val iExpected = PrintOption(showNullAs = "I", maxLength = 10)

        val printOptionAnnotations = T3::class.annotationsOfClass<PrintOption>()
        assertThat(printOptionAnnotations)
            .hasSize(3)
            .isEqualTo(T3::class.annotationsOfClass<PrintOption>(includeInterfaces = true))

        var pairList = printOptionAnnotations.toList()
        assertThat(pairList[0]).isEqualTo(Pair(T2::class, t2expected))
        assertThat(pairList[1]).isEqualTo(Pair(T0::class, t0expected))
        assertThat(pairList[2]).isEqualTo(Pair(I::class, iExpected))

        val printOptionAnnotationsNoInterface = T3::class.annotationsOfClass<PrintOption>(false)
        assertThat(printOptionAnnotationsNoInterface)
            .hasSize(2)

        pairList = printOptionAnnotationsNoInterface.toList()
        assertThat(pairList[0]).isEqualTo(Pair(T2::class, t2expected))
        assertThat(pairList[1]).isEqualTo(Pair(T0::class, t0expected))
    }

    /**
     * Find any annotation of type `A` on `this` class and its super types.
     * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
     */
    @Test
    fun `annotationsOfClass should find annotations regardless of Inherited annotation`() {
        assertThat(API::class.java.isAnnotationPresent(Inherited::class.java)).isFalse

        val apiAnnotations = T3::class.annotationsOfClass<API>()
        assertThat(apiAnnotations)
            .hasSize(2)

        val pairList = apiAnnotations.toList()
        assertThat(pairList[0].first).isEqualTo(T2::class)
        assertThat(pairList[0].second.status).isEqualTo(API.Status.STABLE)
        assertThat(pairList[1].first).isEqualTo(T0::class)
        assertThat(pairList[1].second.status).isEqualTo(API.Status.INTERNAL)
    }

    /**
     * Find any annotation of type `A` on `this` class and its super types.
     * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
     */
    @Test
    fun `annotationsOfClass should include annotations on interface if specified so`() {
        val t2expected = PrintOption(showNullAs = "T2", maxLength = 200)
        val t0expected = PrintOption(showNullAs = "T0", maxLength = 100)
        val iExpected = PrintOption(showNullAs = "I", maxLength = 10)

        val printOptionAnnotations = T3::class.annotationsOfClass<PrintOption>(includeInterfaces = true)
        assertThat(printOptionAnnotations).hasSize(3)

        val pairList = printOptionAnnotations.map { Pair(it.key, it.value) }.toList()
        assertThat(pairList[0]).isEqualTo(Pair(T2::class, t2expected))
        assertThat(pairList[1]).isEqualTo(Pair(T0::class, t0expected))
        assertThat(pairList[2]).isEqualTo(Pair(I::class, iExpected))
    }
}
