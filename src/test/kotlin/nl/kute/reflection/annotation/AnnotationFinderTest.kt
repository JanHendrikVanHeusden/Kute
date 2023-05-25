package nl.kute.reflection.annotation

import nl.kute.printable.Printable
import nl.kute.printable.annotation.PrintOption
import nl.kute.printable.annotation.defaultNullString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("unused") // several properties accessed by reflection only
internal class AnnotationFinderTest {

    @PrintOption(propMaxStringValueLength = 200)
    private open class With3PrintOptions(@PrintOption(propMaxStringValueLength = 100) open val someVal: String) : Printable {
        @PrintOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asStringExcluding()
    }

    private open class With1PrintOption(@PrintOption(propMaxStringValueLength = 100) val someVal: String) : Printable {
        override fun toString(): String = asStringExcluding()
    }

    private open class WithInheritedPrintOption(override val someVal: String) : With3PrintOptions(someVal) {
        override fun toString(): String = asStringExcluding()

        @PrintOption(propMaxStringValueLength = 999999, showNullAs = "")
        @Suppress("unused", "UNUSED_PARAMETER")
        fun toString(iets: String) {}
    }

    private open class ClassWithMethodParamSubtypeInheritance<T : Number> : Printable {
        @PrintOption(propMaxStringValueLength = 50, showNullAs = "")
        open fun getList(inList: List<T>): List<T> = emptyList()

        @PrintOption(propMaxStringValueLength = 15, showNullAs = "geen getal")
        open fun getNum(inNum: T): T? = null

        open fun doList(inList: List<T>) {}

        @PrintOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asStringExcluding()
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
