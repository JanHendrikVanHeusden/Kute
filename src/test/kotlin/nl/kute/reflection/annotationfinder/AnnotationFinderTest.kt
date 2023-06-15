package nl.kute.reflection.annotationfinder

import nl.kute.core.asString
import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.printable.annotation.option.defaultNullString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.MapEntry
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Suppress("unused", "SameReturnValue", "EmptyMethod") // several properties accessed by reflection only
internal class AnnotationFinderTest {

    @PrintOption(propMaxStringValueLength = 200)
    private open class With3PrintOptions(@PrintOption(propMaxStringValueLength = 100) open val someVal: String) {
        @PrintOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    private open class With1PrintOption(@PrintOption(propMaxStringValueLength = 100) val someVal: String) {
        override fun toString(): String = asString()
    }

    private open class WithInheritedPrintOption(override val someVal: String) : With3PrintOptions(someVal) {
        override fun toString(): String = asString()

        @PrintOption(propMaxStringValueLength = 999999, showNullAs = "")
        @Suppress("unused", "UNUSED_PARAMETER", "EmptyMethod")
        fun toString(iets: String) {}
    }

    private open class ClassWithMethodParamSubtypeInheritance<T : Number> {
        @PrintOption(propMaxStringValueLength = 50, showNullAs = "")
        open fun getList(inList: List<T>): List<T> = emptyList()

        @PrintOption(propMaxStringValueLength = 15, showNullAs = "geen getal")
        open fun getNum(inNum: T): T? = null

        @Suppress("EmptyMethod") // several properties accessed by reflection only
        open fun doList(inList: List<T>) {}

        @PrintOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    @Suppress("RedundantOverride")
    private open class SubClassWithMethodParamSubtypeInheritance : ClassWithMethodParamSubtypeInheritance<Int>() {
        override fun getList(inList: List<Int>): ArrayList<Int> = ArrayList()
        override fun getNum(inNum: Int): Int = 12

        @Suppress("EmptyMethod") // several properties accessed by reflection only
        override fun doList(inList: List<Int>) {}

        override fun toString(): String = super.toString()
    }

    private open class ClassWithAnnotationOnPrivateProperty {
        @PrintOption(propMaxStringValueLength = 5)
        @PrintHash
        private val myPrivateVal = "value of my private val"
        override fun toString(): String = asString()
    }

    private open class ClassWithAnnotationOnPublicPropertyThatMasksPrivateProperty : ClassWithAnnotationOnPrivateProperty() {
        @PrintOption(propMaxStringValueLength = 200)
        @PrintMask
        val myPrivateVal = "value of the masking public val"
        override fun toString(): String = asString()
    }

    private interface I {
        @PrintPatternReplace(pattern = "I", replacement = "i")
        val prop: String

        @PrintOption(propMaxStringValueLength = 0)
        override fun toString(): String
    }

    private open class C1 : I {
        @PrintPatternReplace(pattern = "C1", replacement = "c1")
        override val prop: String = "C1"

        @PrintOption(propMaxStringValueLength = 1)
        override fun toString(): String = "C1"
    }

    private open class C2 : C1() {
        @PrintPatternReplace(pattern = "C2", replacement = "c2")
        override val prop: String = "C2"

        @PrintOption(propMaxStringValueLength = 2)
        override fun toString(): String = "C2"
    }

    private open class C3 : C2() {
        @PrintPatternReplace(pattern = "C3", replacement = "c3")
        override val prop: String = "C3"

        @PrintOption(propMaxStringValueLength = 3)
        override fun toString(): String = "C3"
    }

    @Test
    fun `find property annotations in class hierarchy in expected order`() {
        val annotationMap: Map<KClass<*>, PrintPatternReplace> =
            C3::prop.annotationsOfPropertySubSuperHierarchy<PrintPatternReplace>()
        // contract of annotationsOfProperty explicitly states the order, let's test it!
        assertThat(annotationMap.entries).containsExactly(
            MapEntry.entry(C3::class, PrintPatternReplace(pattern = "C3", replacement = "c3")),
            MapEntry.entry(C2::class, PrintPatternReplace(pattern = "C2", replacement = "c2")),
            MapEntry.entry(C1::class, PrintPatternReplace(pattern = "C1", replacement = "c1")),
            MapEntry.entry(I::class, PrintPatternReplace(pattern = "I", replacement = "i")),
        )
    }

    @Test
    fun `find annotations on private property`() {
        val obj = ClassWithAnnotationOnPrivateProperty()
        val privateProp = obj::class.memberProperties.first { it.name == "myPrivateVal" }

        val hashAnnotation: PrintHash = privateProp.annotationsOfPropertySubSuperHierarchy<PrintHash>()[obj::class]!!
        assertThat(hashAnnotation).isNotNull

        val optionAnnotation: PrintOption =
            privateProp.annotationsOfPropertySubSuperHierarchy<PrintOption>()[obj::class]!!
        assertThat(optionAnnotation).isNotNull
        assertThat(optionAnnotation.propMaxStringValueLength).isEqualTo(5)
    }

    @Test
    fun `find annotations on public property that masks private property`() {
        val obj = ClassWithAnnotationOnPublicPropertyThatMasksPrivateProperty()
        val privateProp = obj::class.memberProperties.first { it.name == "myPrivateVal" }

        val hashAnnotation: PrintHash? = privateProp.annotationsOfPropertySubSuperHierarchy<PrintHash>()[obj::class]
        assertThat(hashAnnotation).isNull()

        val maskAnnotation: PrintMask = privateProp.annotationsOfPropertySubSuperHierarchy<PrintMask>()[obj::class]!!
        assertThat(maskAnnotation).isNotNull

        val optionAnnotation: PrintOption =
            privateProp.annotationsOfPropertySubSuperHierarchy<PrintOption>()[obj::class]!!
        assertThat(optionAnnotation).isNotNull
        assertThat(optionAnnotation.propMaxStringValueLength).isEqualTo(200)
    }

    @Test
    fun `find PrintOption on class, property, method`() {
        val with3PrintOptions = With3PrintOptions("my val")

        val printOptByClass: PrintOption = With3PrintOptions::class.annotationOfSubSuperHierarchy()!!
        assertThat(printOptByClass.propMaxStringValueLength).isEqualTo(200)
        assertThat(printOptByClass.showNullAs).isEqualTo(defaultNullString)

        val printOptOfProperty: PrintOption = with3PrintOptions::someVal.annotationOfPropertySubSuperHierarchy()!!
        assertThat(printOptOfProperty.propMaxStringValueLength).isEqualTo(100)
        assertThat(printOptOfProperty.showNullAs).isEqualTo(defaultNullString)

        val printOptOfToString: PrintOption = with3PrintOptions::class.annotationOfToStringSubSuperHierarchy()!!
        assertThat(printOptOfToString.propMaxStringValueLength).isEqualTo(250)
        assertThat(printOptOfToString.showNullAs).isEqualTo("<null>")

        val printOptOfProperty1: PrintOption = With1PrintOption::someVal.annotationOfPropertySubSuperHierarchy()!!
        assertThat(printOptOfProperty1.propMaxStringValueLength).isEqualTo(100)
        assertThat(printOptOfProperty1.showNullAs).isEqualTo(defaultNullString)
    }

    @Test
    fun `find no PrintOption where not defined`() {
        val with1PrintOption = With1PrintOption("my val")

        val printOptByClass: PrintOption? = With1PrintOption::class.annotationOfSubSuperHierarchy()
        assertThat(printOptByClass).isNull()

        val printOptOfToString: PrintOption? = with1PrintOption::class.annotationOfToStringSubSuperHierarchy()
        assertThat(printOptOfToString).isNull()
    }

    @Test
    fun `find PrintOption on class, property, method of subclass by annotation's inheritance`() {
        val with3PrintOptions = With3PrintOptions("my val")
        val with3PrintOptionsByInheritance = WithInheritedPrintOption("my val on inherited")
        assertThat(with3PrintOptionsByInheritance).isInstanceOf(With3PrintOptions::class.java)

        val printOptByClass: PrintOption = With3PrintOptions::class.annotationOfSubSuperHierarchy()!!
        val printOptByClassInheritance: PrintOption? = WithInheritedPrintOption::class.annotationOfSubSuperHierarchy()
        assertThat(printOptByClassInheritance).isEqualTo(printOptByClass)

        val printOptOfProperty: PrintOption = with3PrintOptions::someVal.annotationOfPropertySubSuperHierarchy()!!
        val printOptOfPropertyByInheritance: PrintOption? =
            with3PrintOptionsByInheritance::someVal.annotationOfPropertySubSuperHierarchy()!!
        assertThat(printOptOfPropertyByInheritance).isSameAs(printOptOfProperty)

        val printOptOfToString: PrintOption = with3PrintOptions::class.annotationOfToStringSubSuperHierarchy()!!
        val printOptOfToStringByInheritance: PrintOption? =
            with3PrintOptionsByInheritance::class.annotationOfToStringSubSuperHierarchy()
        assertThat(printOptOfToStringByInheritance).isSameAs(printOptOfToString)
    }

}
