package nl.kute.reflection.annotation

import nl.kute.printable.Printable
import nl.kute.printable.annotation.PrintOption
import nl.kute.printable.annotation.PrintOption.Defaults.defaultNullString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0


internal class AnnotationFinderTest {

    @PrintOption(maxLength = 200)
    private open class With3PrintOptions(@PrintOption(maxLength = 100) open val someVal: String) : Printable {
        @PrintOption(maxLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    private open class With1PrintOption(@PrintOption(maxLength = 100) val someVal: String) : Printable {
        override fun toString(): String = asString()
    }

    private open class WithInheritedPrintOption(override val someVal: String) : With3PrintOptions(someVal) {
        override fun toString(): String = asString()

        @PrintOption(maxLength = 999999, showNullAs = "")
        fun toString(iets: String) {
        }
    }

    private open class ClassWithMethodParamSubtypeInheritance<T : Number> : Printable {
        @PrintOption(maxLength = 50, showNullAs = "")
        open fun getList(inList: List<T>): List<T> = emptyList()

        @PrintOption(maxLength = 15, showNullAs = "geen getal")
        open fun getNum(inNum: T): T? = null

        open fun doList(inList: List<T>) {}

        @PrintOption(maxLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    @Suppress("RedundantOverride")
    private open class SubClassWithMethodParamSubtypeInheritance : ClassWithMethodParamSubtypeInheritance<Int>() {
        override fun getList(inList: List<Int>): ArrayList<Int> = ArrayList()
        override fun getNum(inNum: Int): Int = 12
        override fun doList(inList: List<Int>) {}
        override fun toString(): String = super.toString()
    }

    @Suppress("unused")
    private open class SimpleTopClass {
        open fun <T : Number> getNum(num: T): Number = 12
    }

    @Suppress("unused")
    private open class SimpleSubClass : SimpleTopClass() {
        override fun <Z : Number> getNum(num: Z): Int = 12
    }


    @Test
    fun `find PrintOption on class, property, method`() {
        val with3PrintOptions = With3PrintOptions("my val")

        val printOptByClass: PrintOption = With3PrintOptions::class.annotationOfClass()!!
        assertThat(printOptByClass.maxLength).isEqualTo(200)
        assertThat(printOptByClass.showNullAs).isEqualTo(defaultNullString)

        val printOptOfObject: PrintOption = with3PrintOptions.annotationOfClass()!!
        assertThat(printOptOfObject).isSameAs(printOptByClass)

        val kProperty: KProperty<String> = with3PrintOptions::someVal
        val kProperty0: KProperty0<String> = with3PrintOptions::someVal

        val printOptOfProperty: PrintOption = with3PrintOptions::someVal.annotationOfProperty()!!
        assertThat(printOptOfProperty.maxLength).isEqualTo(100)
        assertThat(printOptOfProperty.showNullAs).isEqualTo(defaultNullString)

        val printOptOfToString: PrintOption = with3PrintOptions.annotationOfToString()!!
        assertThat(printOptOfToString.maxLength).isEqualTo(250)
        assertThat(printOptOfToString.showNullAs).isEqualTo("<null>")

        val printOptOfProperty1: PrintOption = With1PrintOption::someVal.annotationOfProperty()!!
        assertThat(printOptOfProperty1.maxLength).isEqualTo(100)
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
        val printOptByClassInheritance2 = WithInheritedPrintOption::class.java.getAnnotation(PrintOption::class.java)
        assertThat(printOptByClassInheritance).isEqualTo(printOptByClass)

        val printOptOfObject: PrintOption = with3PrintOptions.annotationOfClass()!!
        val printOptOfObjectByInheritance: PrintOption? = with3PrintOptionsByInheritance.annotationOfClass()
        assertThat(printOptOfObjectByInheritance).isSameAs(printOptOfObject)

        val printOptOfProperty: PrintOption = with3PrintOptions::someVal.annotationOfProperty()!!
        val printOptOfPropertyByInheritance: PrintOption? = with3PrintOptionsByInheritance::someVal.annotationOfProperty()!!
        assertThat(printOptOfPropertyByInheritance).isSameAs(printOptOfProperty)

        val printOptOfToString: PrintOption = with3PrintOptions.annotationOfToString()!!
        val printOptOfToStringByInheritance: PrintOption? = with3PrintOptionsByInheritance.annotationOfToString()
        assertThat(printOptOfToStringByInheritance).isSameAs(printOptOfToString)
    }

}
