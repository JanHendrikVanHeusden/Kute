package nl.kute.reflection.annotationfinder

import nl.kute.asstring.annotation.NonInheritedTestAnnotation
import nl.kute.asstring.annotation.option.AsStringOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

class ClassAnnotationFinderTest {

    // A bunch of classes with some random annotations sprinkled
    // Obviously, retention must be "runtime" in order to detect it

    @AsStringOption(showNullAs = "I", propMaxStringValueLength = 10)
    private interface I

    @AsStringOption(showNullAs = "T0", propMaxStringValueLength = 100)
    @NonInheritedTestAnnotation(option = NonInheritedTestAnnotation.Option.OPTION_1)
    private open class T0 : IllegalArgumentException(), I

    private open class T1 : T0()

    @AsStringOption(showNullAs = "T2", propMaxStringValueLength = 200)
    @NonInheritedTestAnnotation(option = NonInheritedTestAnnotation.Option.OPTION_2)
    private open class T2 : T1()

    private open class T3 : T2()

    /**
     * Find the annotation of type `A` on the `this` class or its super types;
     * the annotation at the lowest level is returned, if present at all
     */
    @Test
    fun `annotationOfClassInheritance should return the annotation of the deepest subclass`() {
        val anno2: NonInheritedTestAnnotation? = T2::class.annotationOfSubSuperHierarchy()
        assertThat(anno2).isNotNull
        assertThat(anno2!!.option).isEqualTo(NonInheritedTestAnnotation.Option.OPTION_2)

        // not inherited, but still found on subclass
        // That's OK for our relevant annotations, these are all inherited anyway
        assertThat(NonInheritedTestAnnotation::class.java.isAnnotationPresent(Inherited::class.java)).isFalse
        val anno3: NonInheritedTestAnnotation? = T3::class.annotationOfSubSuperHierarchy()
        assertThat(anno3).isNotNull

        val asStringOption0: AsStringOption? = T0::class.annotationOfSubSuperHierarchy()
        assertThat(asStringOption0).isNotNull
        assertThat(asStringOption0!!.showNullAs).isEqualTo("T0")

        val asStringOption2: AsStringOption? = T2::class.annotationOfSubSuperHierarchy()
        assertThat(asStringOption2).isNotNull
        assertThat(asStringOption2!!.showNullAs).isEqualTo("T2")

        // inherited, so should be found on subclass
        assertThat(AsStringOption::class.java.isAnnotationPresent(Inherited::class.java)).isTrue
        val asStringOption3: AsStringOption? = T3::class.annotationOfSubSuperHierarchy()
        assertThat(asStringOption3).isNotNull
        assertThat(asStringOption3!!.showNullAs).isEqualTo("T2")
    }

    /**
     * Find any annotation of type `A` on `this` class and its super classes.
     * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
     */
    @Test
    fun `annotationsOfClassHierarchy should return the annotations in order, and include or exclude interfaces as specified`() {
        val t2expected = AsStringOption(showNullAs = "T2", propMaxStringValueLength = 200)
        val t0expected = AsStringOption(showNullAs = "T0", propMaxStringValueLength = 100)
        val iExpected = AsStringOption(showNullAs = "I", propMaxStringValueLength = 10)

        val asStringOptionAnnotations: Map<KClass<*>, AsStringOption> =
            T3::class.annotationsOfSubSuperHierarchy()
        assertThat(asStringOptionAnnotations)
            .hasSize(3)

        var pairList = asStringOptionAnnotations.toList()
        assertThat(pairList[0]).isEqualTo(Pair(T2::class, t2expected))
        assertThat(pairList[1]).isEqualTo(Pair(T0::class, t0expected))
        assertThat(pairList[2]).isEqualTo(Pair(I::class, iExpected))

        val asStringOptionAnnotationsNoInterface = T3::class.annotationsOfSubSuperHierarchy<AsStringOption>(false)
        assertThat(asStringOptionAnnotationsNoInterface)
            .hasSize(2)

        pairList = asStringOptionAnnotationsNoInterface.toList()
        assertThat(pairList[0]).isEqualTo(Pair(T2::class, t2expected))
        assertThat(pairList[1]).isEqualTo(Pair(T0::class, t0expected))
    }

    /**
     * Find any annotation of type `A` on `this` class and its super types.
     * The annotations are ordered from lowest to highest level, so from subclass to super class / super interface.
     */
    @Test
    fun `annotationsOfClassHierarchy should find annotations regardless of Inherited annotation`() {
        assertThat(NonInheritedTestAnnotation::class.java.isAnnotationPresent(Inherited::class.java)).isFalse

        val foundAnnotations = T3::class.annotationsOfSubSuperHierarchy<NonInheritedTestAnnotation>()
        assertThat(foundAnnotations).hasSize(2)

        val pairList = foundAnnotations.toList()
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
    fun `annotationsOfClassHierarchy should include annotations on interface if specified so`() {
        val t2expected = AsStringOption(showNullAs = "T2", propMaxStringValueLength = 200)
        val t0expected = AsStringOption(showNullAs = "T0", propMaxStringValueLength = 100)
        val iExpected = AsStringOption(showNullAs = "I", propMaxStringValueLength = 10)

        val asStringOptionAnnotations = T3::class.annotationsOfSubSuperHierarchy<AsStringOption>(includeInterfaces = true)
        assertThat(asStringOptionAnnotations).hasSize(3)

        val pairList = asStringOptionAnnotations.map { Pair(it.key, it.value) }.toList()
        assertThat(pairList[0]).isEqualTo(Pair(T2::class, t2expected))
        assertThat(pairList[1]).isEqualTo(Pair(T0::class, t0expected))
        assertThat(pairList[2]).isEqualTo(Pair(I::class, iExpected))
    }
}
