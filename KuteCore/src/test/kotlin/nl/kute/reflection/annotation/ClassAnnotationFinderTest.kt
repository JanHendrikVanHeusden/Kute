package nl.kute.reflection.annotation

import nl.kute.printable.annotation.NonInheritedTestAnnotation
import nl.kute.printable.annotation.PrintOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.annotation.Inherited

internal class ClassAnnotationFinderTest {

    // A bunch of classes with some random annotations sprinkled
    // Obviously, retention must be "runtime" in order to detect it

    @PrintOption(showNullAs = "I", maxLength = 10)
    private interface I

    @NonInheritedTestAnnotation(option = NonInheritedTestAnnotation.Option.OPTION_1)
    @PrintOption(showNullAs = "T0", maxLength = 100)
    private open class T0 : IllegalArgumentException(), I

    private open class T1 : T0()

    @PrintOption(showNullAs = "T2", maxLength = 200)
    @NonInheritedTestAnnotation(option = NonInheritedTestAnnotation.Option.OPTION_2)
    private open class T2 : T1()

    private open class T3 : T2()

    /**
     * Find the annotation of type `A` on the `this` class or its super types;
     * the annotation at the lowest level is returned, if present at all
     */
    @Test
    fun `annotationOfClass should return the annotation of the deepest subclass`() {
        val anno2: NonInheritedTestAnnotation? = T2::class.annotationOfClass()
        assertThat(anno2).isNotNull
        assertThat(anno2!!.option).isEqualTo(NonInheritedTestAnnotation.Option.OPTION_2)

        // not inherited, so not found on subclass
        assertThat(NonInheritedTestAnnotation::class.java.isAnnotationPresent(Inherited::class.java)).isFalse
        val anno3: NonInheritedTestAnnotation? = T3::class.annotationOfClass()
        assertThat(anno3).isNull()

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
        assertThat(NonInheritedTestAnnotation::class.java.isAnnotationPresent(Inherited::class.java)).isFalse

        val apiAnnotations = T3::class.annotationsOfClass<NonInheritedTestAnnotation>()
        assertThat(apiAnnotations)
            .hasSize(2)

        val pairList = apiAnnotations.toList()
        assertThat(pairList[0].first).isEqualTo(T2::class)
        assertThat(pairList[0].second.option).isEqualTo(NonInheritedTestAnnotation.Option.OPTION_2)
        assertThat(pairList[1].first).isEqualTo(T0::class)
        assertThat(pairList[1].second.option).isEqualTo(NonInheritedTestAnnotation.Option.OPTION_1)
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
