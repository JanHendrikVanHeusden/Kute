package nl.kute.reflection.annotationfinder

import nl.kute.core.asString
import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.option.PrintOption
import nl.kute.printable.annotation.option.defaultNullString
import nl.kute.reflection.getPropertyFromHierarchy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("unused") // several properties accessed by reflection only
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
        @Suppress("unused", "UNUSED_PARAMETER")
        fun toString(iets: String) {}
    }

    private open class ClassWithMethodParamSubtypeInheritance<T : Number> {
        @PrintOption(propMaxStringValueLength = 50, showNullAs = "")
        open fun getList(inList: List<T>): List<T> = emptyList()

        @PrintOption(propMaxStringValueLength = 15, showNullAs = "geen getal")
        open fun getNum(inNum: T): T? = null

        open fun doList(inList: List<T>) {}

        @PrintOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    @Suppress("RedundantOverride")
    private open class SubClassWithMethodParamSubtypeInheritance : ClassWithMethodParamSubtypeInheritance<Int>() {
        override fun getList(inList: List<Int>): ArrayList<Int> = ArrayList()
        override fun getNum(inNum: Int): Int = 12
        override fun doList(inList: List<Int>) {}
        override fun toString(): String = super.toString()
    }

    private open class SimpleTopClass {
        open fun <T : Number> getNum(num: T): Number = 12
    }

    private open class SimpleSubClass : SimpleTopClass() {
        override fun <Z : Number> getNum(num: Z): Int = 12
    }

    open private class ClassWithAnnotationOnPrivateProperty {
        @PrintOption(propMaxStringValueLength = 5)
        @PrintHash
        private val myPrivateVal = "value of my private val"
        override fun toString(): String = asString()
    }

    open private class ClassWithAnnotationOnPublicPrivatePropertyThatMasksPrivateProperty: ClassWithAnnotationOnPrivateProperty() {
        @PrintOption(propMaxStringValueLength = 200)
        @PrintMask
        val myPrivateVal = "value of the masking public val"
        override fun toString(): String = asString()
    }

    @Test
    fun `find annotations on private property`() {
        val obj = ClassWithAnnotationOnPrivateProperty()
        val privateProp = obj.getPropertyFromHierarchy("myPrivateVal")!!

        val hashAnnotation: PrintHash = privateProp.annotationsOfProperty<PrintHash>()[obj::class]!!
        assertThat(hashAnnotation).isNotNull

        val optionAnnotation: PrintOption = privateProp.annotationsOfProperty<PrintOption>()[obj::class]!!
        assertThat(optionAnnotation).isNotNull
        assertThat(optionAnnotation.propMaxStringValueLength).isEqualTo(5)
    }

    @Test
    fun `find annotations on public property that masks private property`() {
        val obj = ClassWithAnnotationOnPublicPrivatePropertyThatMasksPrivateProperty()
        val privateProp = obj.getPropertyFromHierarchy("myPrivateVal")!!

        val hashAnnotation: PrintHash? = privateProp.annotationsOfProperty<PrintHash>()[obj::class]
        assertThat(hashAnnotation).isNull()

        val maskAnnotation: PrintMask = privateProp.annotationsOfProperty<PrintMask>()[obj::class]!!
        assertThat(maskAnnotation).isNotNull

        val optionAnnotation: PrintOption = privateProp.annotationsOfProperty<PrintOption>()[obj::class]!!
        assertThat(optionAnnotation).isNotNull
        assertThat(optionAnnotation.propMaxStringValueLength).isEqualTo(200)
    }

    @Test
    fun `find PrintOption on class, property, method`() {
        val with3PrintOptions = With3PrintOptions("my val")

        val printOptByClass: PrintOption = With3PrintOptions::class.annotationOfClass()!!
        assertThat(printOptByClass.propMaxStringValueLength).isEqualTo(200)
        assertThat(printOptByClass.showNullAs).isEqualTo(defaultNullString)

        val printOptOfObject: PrintOption = with3PrintOptions.annotationOfClass()!!
        assertThat(printOptOfObject).isSameAs(printOptByClass)

        val printOptOfProperty: PrintOption = with3PrintOptions::someVal.annotationOfPropertyInHierarchy()!!
        assertThat(printOptOfProperty.propMaxStringValueLength).isEqualTo(100)
        assertThat(printOptOfProperty.showNullAs).isEqualTo(defaultNullString)

        val printOptOfToString: PrintOption = with3PrintOptions.annotationOfToString()!!
        assertThat(printOptOfToString.propMaxStringValueLength).isEqualTo(250)
        assertThat(printOptOfToString.showNullAs).isEqualTo("<null>")

        val printOptOfProperty1: PrintOption = With1PrintOption::someVal.annotationOfPropertyInHierarchy()!!
        assertThat(printOptOfProperty1.propMaxStringValueLength).isEqualTo(100)
        assertThat(printOptOfProperty1.showNullAs).isEqualTo(defaultNullString)
    }

    @Test
    fun `find no PrintOption where not defined`() {
        val with1PrintOption = With1PrintOption("my val")

        val printOptByClass: PrintOption? = With1PrintOption::class.annotationOfClass()
        assertThat(printOptByClass).isNull()

        val printOptOfObject: PrintOption? = with1PrintOption.annotationOfClass()
        assertThat(printOptOfObject).isNull()

        val printOptOfToString: PrintOption? = with1PrintOption.annotationOfClass()
        assertThat(printOptOfToString).isNull()
    }

    @Test
    fun `find PrintOption on class, property, method of subclass by annotation's inheritance`() {
        val with3PrintOptions = With3PrintOptions("my val")
        val with3PrintOptionsByInheritance = WithInheritedPrintOption("my val on inherited")
        assertThat(with3PrintOptionsByInheritance).isInstanceOf(With3PrintOptions::class.java)

        val printOptByClass: PrintOption = With3PrintOptions::class.annotationOfClass()!!
        val printOptByClassInheritance: PrintOption? = WithInheritedPrintOption::class.annotationOfClass()
        assertThat(printOptByClassInheritance).isEqualTo(printOptByClass)

        val printOptOfObject: PrintOption = with3PrintOptions.annotationOfClass()!!
        val printOptOfObjectByInheritance: PrintOption? = with3PrintOptionsByInheritance.annotationOfClass()
        assertThat(printOptOfObjectByInheritance).isSameAs(printOptOfObject)

        val printOptOfProperty: PrintOption = with3PrintOptions::someVal.annotationOfPropertyInHierarchy()!!
        val printOptOfPropertyByInheritance: PrintOption? =
            with3PrintOptionsByInheritance::someVal.annotationOfPropertyInHierarchy()!!
        assertThat(printOptOfPropertyByInheritance).isSameAs(printOptOfProperty)

        val printOptOfToString: PrintOption = with3PrintOptions.annotationOfToString()!!
        val printOptOfToStringByInheritance: PrintOption? = with3PrintOptionsByInheritance.annotationOfToString()
        assertThat(printOptOfToStringByInheritance).isSameAs(printOptOfToString)
    }

}
