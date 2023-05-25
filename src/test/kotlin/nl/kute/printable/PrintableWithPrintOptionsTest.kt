package nl.kute.printable

import nl.kute.printable.annotation.PrintHash
import nl.kute.printable.annotation.PrintMask
import nl.kute.printable.annotation.PrintOmit
import nl.kute.printable.annotation.PrintOption
import nl.kute.printable.annotation.PrintPatternReplace
import nl.kute.printable.annotation.defaultMaxStringValueLength
import nl.kute.printable.annotation.defaultNullString
import nl.kute.reflection.annotation.annotationOfClass
import nl.kute.reflection.annotation.annotationOfToString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PrintableWithPrintOptionsTest {

    @Test
    fun `test Printable with PrintOptions`() {
        // arrange
        val theObjectToPrint = ClassWithNonDefaultPrintOptions()

        val printOption: PrintOption = theObjectToPrint::class.annotationOfClass<PrintOption>()!!
        val nullStr = "[nil]"
        assertThat(printOption.showNullAs).isEqualTo(nullStr)
        assertThat(printOption.propMaxStringValueLength).isEqualTo(6)

        val aStringToReplaceByAnother = theObjectToPrint.aStringToReplaceByAnother
        assertThat(aStringToReplaceByAnother).isEqualTo("a String")
        val aDateWithMonthMasked = theObjectToPrint.aDateWithMonthMasked.toString()
        assertThat(aDateWithMonthMasked).isEqualTo("2023-05-24")
        val noPrint = theObjectToPrint.noPrint.toString()
        assertThat(theObjectToPrint.noPrint).isExactlyInstanceOf(Any::class.java)
        val hashed = theObjectToPrint.hashed
        assertThat(hashed).isEqualTo("a string that should be hashed")
        val aNullValue = theObjectToPrint.aNullValue
        assertThat(aNullValue).isNull()

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")
            // values capped at max length of 6, due to propMaxStringLength
            .matches(""".+?\baDateWithMonthMasked=2023-M\b.+""").`as`("Month should be pattern-replaced, then capped at 6")
            .doesNotContain(aDateWithMonthMasked)
            .matches(""".+?\baNullValue=\$nullStr.+""").`as`("should adhere to `showNullAs`=\"$nullStr\"")
            .doesNotMatch("\bnull\b")
            .matches(""".+?\baStringToReplaceByAnother=anothe\b.+""").`as`("\"a\" should be replaced by \"another\", then capped at 6")
            .doesNotContain(aStringToReplaceByAnother)
            .matches(""".+?\bhashed=[a-f0-9]{6}\b.+""").`as`("value should be hashed, then capped at 8")
            .doesNotContain(hashed)
            .doesNotContain("noPrint=").`as`("should adhere to NoPrint annotation")
            .doesNotContain(noPrint)
    }
    @Test
    fun `test subclass with PrintOptions using annotation on toString`() {
        // arrange
        val theObjectToPrint = SubClassWithPrintOptionsToString()

        val printOptionClass: PrintOption = theObjectToPrint::class.annotationOfClass<PrintOption>()!!
        val nullStrClass = "[nil]"
        assertThat(printOptionClass.showNullAs).isEqualTo(nullStrClass)
        assertThat(printOptionClass.propMaxStringValueLength).isEqualTo(6)
        // The @PrintOption of `toString()` should take prevalence over that of the class
        val printOptionToString: PrintOption = theObjectToPrint.annotationOfToString()!!
        assertThat(printOptionToString.showNullAs).isEqualTo(defaultNullString)
        assertThat(printOptionToString.propMaxStringValueLength).isEqualTo(defaultMaxStringValueLength)

        val aStringToReplaceByAnother = theObjectToPrint.aStringToReplaceByAnother
        assertThat(aStringToReplaceByAnother).isEqualTo("a String")
        val aDateWithMonthMasked = theObjectToPrint.aDateWithMonthMasked.toString()
        assertThat(aDateWithMonthMasked).isEqualTo("2023-05-24")
        val noPrint = theObjectToPrint.noPrint.toString()
        val hashed = theObjectToPrint.hashed
        assertThat(hashed).isEqualTo("a string that should be hashed")
        val aNullValue = theObjectToPrint.aNullValue
        assertThat(aNullValue).isNull()

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")
            // values not capped at length 6, due to overriding @PrintOption on toString() method
            .matches(""".+?\baDateWithMonthMasked=2023-MM-24\b.+""").`as`("Month should be pattern-replaced, but not capped at 6")
            .doesNotContain(aDateWithMonthMasked)
            .matches(""".+?\baNullValue=$defaultNullString\b.+""").`as`("should adhere to default `showNullAs`=\"$defaultNullString\"")
            .doesNotMatch("\bnull\b")
            .matches(""".+?\baStringToReplaceByAnother=another String\b.+""").`as`("\"a\" should be replaced by \"another\", but not capped at 6")
            .doesNotContain(aStringToReplaceByAnother)
            .matches(""".+?\bhashed=[a-f0-9]{8}\b.+""").`as`("value should be hashed")
            .doesNotContain(hashed)
            .doesNotContain("noPrint=").`as`("should adhere to NoPrint annotation")
            .doesNotContain(noPrint)
    }

    @Test
    fun `test subclass with PrintOtions using annotation on class`() {
        // arrange
        val theObjectToPrint = SubClassWithPrintOptionsOnClass()

        val printOption: PrintOption = theObjectToPrint::class.annotationOfClass<PrintOption>()!!
        val nullStr = "nothing here"
        assertThat(printOption.showNullAs).isEqualTo(nullStr)
        assertThat(printOption.propMaxStringValueLength).isEqualTo(defaultMaxStringValueLength)

        val aStringToReplaceByAnother = theObjectToPrint.aStringToReplaceByAnother
        assertThat(aStringToReplaceByAnother).isEqualTo("a String")
        val aDateWithMonthMasked = theObjectToPrint.aDateWithMonthMasked.toString()
        assertThat(aDateWithMonthMasked).isEqualTo("2023-05-24")
        val noPrint = theObjectToPrint.noPrint.toString()
        val hashed = theObjectToPrint.hashed
        assertThat(hashed).isEqualTo("a string that should be hashed")
        val aNullValue = theObjectToPrint.aNullValue
        assertThat(aNullValue).isNull()

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")
            // values not capped at length 6, due to overriding @PrintOption on toString() method
            .matches(""".+?\baDateWithMonthMasked=2023-MM-24\b.+""").`as`("Month should be pattern-replaced, but not capped at 6")
            .doesNotContain(aDateWithMonthMasked)
            .matches(""".+?\baNullValue=$nullStr\b.+""").`as`("should adhere to default `showNullAs`=\"$nullStr\"")
            .doesNotMatch("\bnull\b")
            .matches(""".+?\baStringToReplaceByAnother=another String\b.+""").`as`("\"a\" should be replaced by \"another\", but not capped at 6")
            .doesNotContain(aStringToReplaceByAnother)
            .matches(""".+?\bhashed=[a-f0-9]{8}\b.+""").`as`("value should be hashed")
            .doesNotContain(hashed)
            .doesNotContain("noPrint=").`as`("should adhere to NoPrint annotation")
            .doesNotContain(noPrint)
    }


    @Test
    fun `test sub-subclass of PrintOptions on toString, with non-effective overriding PrintOption on class`() {
        // arrange
        val theObjectToPrint = SubSubClassOfPrintOptionsToStringWithPrintOptionsOnClass()

        val printOptionClass: PrintOption = theObjectToPrint::class.annotationOfClass<PrintOption>()!!
        assertThat(printOptionClass.showNullAs).isEqualTo(defaultNullString)
        assertThat(printOptionClass.propMaxStringValueLength).isEqualTo(2)
        // The @PrintOption of `toString()` in the superclass should still take prevalence over that of the subclass
        val printOptionToString: PrintOption = theObjectToPrint.annotationOfToString()!!
        assertThat(printOptionToString.showNullAs).isEqualTo(defaultNullString)
        assertThat(printOptionToString.propMaxStringValueLength).isEqualTo(defaultMaxStringValueLength)

        val aStringToReplaceByAnother = theObjectToPrint.aStringToReplaceByAnother
        assertThat(aStringToReplaceByAnother).isEqualTo("a String")
        val aDateWithMonthMasked = theObjectToPrint.aDateWithMonthMasked.toString()
        assertThat(aDateWithMonthMasked).isEqualTo("2023-05-24")
        val noPrint = theObjectToPrint.noPrint.toString()
        val hashed = theObjectToPrint.hashed
        assertThat(hashed).isEqualTo("a string that should be hashed")
        val aNullValue = theObjectToPrint.aNullValue
        assertThat(aNullValue).isNull()

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")
            // values not capped at length 6, due to overriding @PrintOption on toString() method
            .matches(""".+?\baDateWithMonthMasked=[a-f0-9]{8}\b.+""").`as`("Month should be hashed")
            .doesNotContain(aDateWithMonthMasked)
            .matches(""".+?\baNullValue=$defaultNullString\b.+""").`as`("should adhere to default `showNullAs`=\"$defaultNullString\"")
            .doesNotMatch("\bnull\b")
            .matches(""".+?\baStringToReplaceByAnother=another String\b.+""").`as`("\"a\" should be replaced by \"another\"")
            .doesNotContain(aStringToReplaceByAnother)
            .matches(""".+?\bhashed=[a-f0-9]{8}\b.+""").`as`("value should be hashed")
            .doesNotContain(hashed)
            .doesNotContain("noPrint=").`as`("should adhere to NoPrint annotation")
            .doesNotContain(noPrint)
    }

    @Test
    fun `test sub-subclass of PrintOptions on toString, with effective overriding PrintOption on toString`() {
        // arrange
        val theObjectToPrint = SubSubClassOfPrintOptionsToStringWithPrintOptionsOnToString()

        val printOptionToString: PrintOption = theObjectToPrint.annotationOfToString()!!
        assertThat(printOptionToString.showNullAs).isEqualTo(defaultNullString)
        assertThat(printOptionToString.propMaxStringValueLength).isEqualTo(2)

        val aStringToReplaceByAnother = theObjectToPrint.aStringToReplaceByAnother
        assertThat(aStringToReplaceByAnother).isEqualTo("a String")
        val aDateWithMonthMasked = theObjectToPrint.aDateWithMonthMasked.toString()
        assertThat(aDateWithMonthMasked).isEqualTo("2023-05-24")
        val noPrint = theObjectToPrint.noPrint.toString()
        val hashed = theObjectToPrint.hashed
        assertThat(hashed).isEqualTo("a string that should be hashed")
        val aNullValue = theObjectToPrint.aNullValue
        assertThat(aNullValue).isNull()

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")
            // values not capped at length 6, due to overriding @PrintOption on toString() method
            .matches(""".+?\baDateWithMonthMasked=[a-f0-9]{2}\b.+""").`as`("Month should be hashed, then capped at length 2")
            .doesNotContain(aDateWithMonthMasked)
            .matches(""".+?\baNullValue=$defaultNullString\b.+""").`as`("should adhere to default `showNullAs`=\"$defaultNullString\"")
            .doesNotMatch("\bnull\b")
            .matches(""".+?\baStringToReplaceByAnother=an\b.+""").`as`("\"a\" should be replaced by \"another\", then capped at length 2")
            .doesNotContain(aStringToReplaceByAnother)
            .matches(""".+?\bhashed=[a-f0-9]{2}\b.+""").`as`("value should be hashed, then capped at length 2")
            .doesNotContain(hashed)
            .doesNotContain("noPrint=").`as`("should adhere to NoPrint annotation")
            .doesNotContain(noPrint)
    }

    @PrintOption(propMaxStringValueLength = 6, showNullAs = "[nil]")
    private open class ClassWithNonDefaultPrintOptions: Printable {

        @PrintPatternReplace("^a", "another")
        val aStringToReplaceByAnother: String = "a String"

        @PrintMask(startMaskAt = 5, endMaskAt = 7, mask = 'M')
        open val aDateWithMonthMasked: LocalDate = LocalDate.of(2023, 5, 24)

        @PrintOmit
        open val noPrint: Any = Any()

        @PrintHash
        val hashed: String = "a string that should be hashed"

        val aNullValue: Any? = null

        override fun toString(): String = asString()
    }

    private open class SubClassWithPrintOptionsToString: ClassWithNonDefaultPrintOptions() {

        override val noPrint: Any = "should not print this"
        @PrintOption // defaults
        override fun toString(): String = asString()
    }

    @PrintOption(showNullAs = "nothing here")
    private open class SubClassWithPrintOptionsOnClass: ClassWithNonDefaultPrintOptions() {

        @PrintHash // Should not override the super method's @PrintOmit annotation, so should not be included
        override val noPrint: Any = "should not print this"
    }

    @PrintOption(propMaxStringValueLength = 2)
    private open class SubSubClassOfPrintOptionsToStringWithPrintOptionsOnClass: SubClassWithPrintOptionsToString() {

        @PrintHash
        override val aDateWithMonthMasked = super.aDateWithMonthMasked
        override fun toString(): String = asString()
    }

    private open class SubSubClassOfPrintOptionsToStringWithPrintOptionsOnToString: SubClassWithPrintOptionsToString() {

        @PrintOption(propMaxStringValueLength = 2)
        override fun toString(): String = asString()
    }

}
