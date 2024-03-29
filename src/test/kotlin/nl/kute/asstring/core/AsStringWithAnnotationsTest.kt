package nl.kute.asstring.core

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringMask
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.core.defaults.defaultMaxStringValueLength
import nl.kute.asstring.core.defaults.defaultNullString
import nl.kute.helper.base.ObjectsStackVerifier
import nl.kute.reflection.annotationfinder.annotationOfPropertySubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfSubSuperHierarchy
import nl.kute.reflection.annotationfinder.annotationOfToStringSubSuperHierarchy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.reflect.full.findAnnotation

class AsStringWithAnnotationsTest: ObjectsStackVerifier {

    @Test
    fun `non-default AsStringOption parameters should be honoured`() {
        // arrange
        val theObjectToPrint = ClassWithNonDefaultAsStringOptions()

        val asStringOption: AsStringOption = theObjectToPrint::class.annotationOfSubSuperHierarchy()!!
        val nullStr = "[nil]"
        assertThat(asStringOption.showNullAs).isEqualTo(nullStr)
        assertThat(asStringOption.propMaxStringValueLength).isEqualTo(6)

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
        // values capped at max length of 6, due to propMaxStringLength
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")

            .`as`("Month should be pattern-replaced, then capped at 6")
            .matches(""".+?\baDateWithMonthMasked=2023-M\b.+""")
            .doesNotContain(aDateWithMonthMasked)

            .`as`("should adhere to `showNullAs`=\"$nullStr\"")
            .matches(""".+?\baNullValue=\$nullStr.+""")
            .doesNotMatch("\bnull\b")

            .`as`("\"a\" should be replaced by \"another\", then capped at 6")
            .matches(""".+?\baStringToReplaceByAnother=anothe\b.+""")
            .doesNotContain(aStringToReplaceByAnother)

            .`as`("value should be hashed, then capped at 8")
            .matches(""".+?\bhashed=#[a-f0-9]{5}\b.+""")
            .doesNotContain(hashed)

            .`as`("should adhere to NoPrint annotation")
            .doesNotContain("noPrint=")
            .doesNotContain(noPrint)
    }

    @Test
    fun `AsSringOptions annotation on toString in subclass should be honoured`() {
        // arrange
        val theObjectToPrint = SubClassWithAsStringOptionsToString()

        val asStringOptionClass: AsStringOption = theObjectToPrint::class.annotationOfSubSuperHierarchy()!!
        val nullStrClass = "[nil]"
        assertThat(asStringOptionClass.showNullAs).isEqualTo(nullStrClass)
        assertThat(asStringOptionClass.propMaxStringValueLength).isEqualTo(6)
        // The @AsStringOption of `toString()` should take prevalence over that of the class
        val asStringOptionToString: AsStringOption = theObjectToPrint::class.annotationOfToStringSubSuperHierarchy()!!
        assertThat(asStringOptionToString.showNullAs).isEqualTo(defaultNullString)
        assertThat(asStringOptionToString.propMaxStringValueLength).isEqualTo(defaultMaxStringValueLength)

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
        // values not capped at length 6, due to overriding @AsStringOption on toString() method
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")

            .`as`("Month should be pattern-replaced, but not capped at 6")
            .matches(""".+?\baDateWithMonthMasked=2023-MM-24\b.+""")
            .doesNotContain(aDateWithMonthMasked)

            .`as`("should adhere to default `showNullAs`=\"$defaultNullString\"")
            .matches(""".+?\baNullValue=$defaultNullString\b.+""")
            .doesNotMatch("\bnull\b")

            .`as`("\"a\" should be replaced by \"another\", but not capped at 6")
            .matches(""".+?\baStringToReplaceByAnother=another String\b.+""")
            .doesNotContain(aStringToReplaceByAnother)

            .`as`("value should be hashed")
            .matches(""".+?\bhashed=#[a-f0-9]{8}\b#.+""")
            .doesNotContain(hashed)

            .`as`("should adhere to NoPrint annotation")
            .doesNotContain("noPrint=")
            .doesNotContain(noPrint)
    }

    @Test
    fun `AsStringOptions annotation on class should be honoured in subclass`() {
        // arrange
        val theObjectToPrint = SubClassWithAsStringOptionsOnClass()

        val asStringOption: AsStringOption = theObjectToPrint::class.annotationOfSubSuperHierarchy()!!
        val nullStr = "nothing here"
        assertThat(asStringOption.showNullAs).isEqualTo(nullStr)
        assertThat(asStringOption.propMaxStringValueLength).isEqualTo(defaultMaxStringValueLength)

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
        // values not capped at length 6, due to overriding @AsStringOption on toString() method
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")

            .`as`("Month should be pattern-replaced, but not capped at 6")
            .matches(""".+?\baDateWithMonthMasked=2023-MM-24\b.+""")
            .doesNotContain(aDateWithMonthMasked)

            .`as`("should adhere to default `showNullAs`=\"$nullStr\"")
            .matches(""".+?\baNullValue=$nullStr\b.+""")
            .doesNotMatch("\bnull\b")

            .`as`("\"a\" should be replaced by \"another\", but not capped at 6")
            .matches(""".+?\baStringToReplaceByAnother=another String\b.+""")
            .doesNotContain(aStringToReplaceByAnother)

            .`as`("value should be hashed")
            .matches(""".+?\bhashed=#[a-f0-9]{8}\b#.+""")
            .doesNotContain(hashed)

            .`as`("should adhere to NoPrint annotation")
            .doesNotContain("noPrint=")
            .doesNotContain(noPrint)
    }


    @Test
    fun `AsStringOption on class should not override those on toString`() {
        // arrange
        val theObjectToPrint = SubSubClassOfAsStringOptionsToStringWithAsStringOptionsOnClass()

        val asStringOptionClass: AsStringOption = theObjectToPrint::class.annotationOfSubSuperHierarchy()!!
        assertThat(asStringOptionClass.showNullAs).isEqualTo(defaultNullString)
        assertThat(asStringOptionClass.propMaxStringValueLength).isEqualTo(2)
        // The @AsStringOption of `toString()` in the superclass should still take prevalence over that of the subclass
        val asStringOptionToString: AsStringOption = theObjectToPrint::class.annotationOfToStringSubSuperHierarchy()!!
        assertThat(asStringOptionToString.showNullAs).isEqualTo(defaultNullString)
        assertThat(asStringOptionToString.propMaxStringValueLength).isEqualTo(defaultMaxStringValueLength)

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
        // values not capped at length 6, due to overriding @AsStringOption on toString() method
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")

            .`as`("Value should be hashed")
            .matches(""".+?\baDateWithMonthMasked=#[a-f0-9]{8}\b#.+""")
            .doesNotContain(aDateWithMonthMasked)

            .`as`("should adhere to default `showNullAs`=\"$defaultNullString\"")
            .matches(""".+?\baNullValue=$defaultNullString\b.+""")
            .doesNotMatch("\bnull\b")

            .`as`("\"a\" should be replaced by \"another\"")
            .matches(""".+?\baStringToReplaceByAnother=another String\b.+""")
            .doesNotContain(aStringToReplaceByAnother)

            .`as`("value should be hashed")
            .matches(""".+?\bhashed=#[a-f0-9]{8}\b#.+""")
            .`as`("should not contain literal value of the property to hash")
            .doesNotContain(hashed)

            .`as`("should adhere to NoPrint annotation")
            .doesNotContain("noPrint=")
            .doesNotContain(noPrint)
    }

    @Test
    fun `overriding AsStringOption on toString should be honoured`() {
        // arrange
        val theObjectToPrint = SubSubClassOfAsStringOptionsToStringWithAsStringOptionsOnToString()

        val asStringOptionToString: AsStringOption = theObjectToPrint::class.annotationOfToStringSubSuperHierarchy()!!
        assertThat(asStringOptionToString.showNullAs).isEqualTo(defaultNullString)
        assertThat(asStringOptionToString.propMaxStringValueLength).isEqualTo(2)

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
        // values not capped at length 6, due to overriding @AsStringOption on toString() method
        assertThat(stringVal)
            .startsWith("${theObjectToPrint::class.java.simpleName}(")
            .endsWith(")")

            .`as`("Month should be hashed, then capped at length 2")
            .matches(""".+?\baDateWithMonthMasked=[a-f0-9]{2}\b.+""")
            .doesNotContain(aDateWithMonthMasked)

            .`as`("should adhere to default `showNullAs`=\"$defaultNullString\"")
            .matches(""".+?\baNullValue=$defaultNullString\b.+""")
            .doesNotMatch("\bnull\b")

            .`as`("\"a\" should be replaced by \"another\", then capped at length 2")
            .matches(""".+?\baStringToReplaceByAnother=an\b.+""")
            .doesNotContain(aStringToReplaceByAnother)


            .`as`("value should be hashed, then capped at length 2")
            .matches(""".+?\bhashed=#[a-f0-9]\b.+""")
            .doesNotContain(hashed)

            .`as`("should adhere to NoPrint annotation")
            .doesNotContain("noPrint=")
            .doesNotContain(noPrint)
    }

    @Test
    fun `AsStringOption on property should be honoured`() {
        // arrange
        val maxStringLenght = 10
        val theObjectToPrint = ClassWithAsStringOptionOnProperty()
        assertThat(theObjectToPrint.propWithAsStringOption.length).isGreaterThan(maxStringLenght)

        assertThat(theObjectToPrint::class.annotationOfToStringSubSuperHierarchy<AsStringOption>()).isNull()
        with(theObjectToPrint::propWithAsStringOption.annotationOfPropertySubSuperHierarchy<AsStringOption>()!!) {
            assertThat(this.propMaxStringValueLength).isEqualTo(maxStringLenght)
        }

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .`as`("Should adhere to @AsStringOption annotation on property")
            .isEqualTo("ClassWithAsStringOptionOnProperty(propWithAsStringOption=" +
                    "${theObjectToPrint.propWithAsStringOption.take(maxStringLenght)}...)")
    }

    @Test
    fun `AsStringOption on property should override AsStringOption on toString`() {
        // arrange
        val maxStringLenght = 10
        val theObjectToPrint = ClassWithAsStringOptionsOnPropertyAndToString()
        assertThat(theObjectToPrint.propWithAsStringOption.length).isGreaterThan(maxStringLenght)

        assertThat(theObjectToPrint::class.annotationOfToStringSubSuperHierarchy<AsStringOption>()!!.propMaxStringValueLength)
            .isLessThan(maxStringLenght)
        with(theObjectToPrint::propWithAsStringOption.annotationOfPropertySubSuperHierarchy<AsStringOption>()!!) {
            assertThat(this.propMaxStringValueLength).isEqualTo(maxStringLenght)
        }

        // act
        val stringVal = theObjectToPrint.toString()

        // assert
        assertThat(stringVal)
            .`as`("Should ignore the @AsStringOption on toString, but should adhere to @AsStringOption annotation on property")
            .isEqualTo("ClassWithAsStringOptionsOnPropertyAndToString(propWithAsStringOption=${theObjectToPrint.propWithAsStringOption.take(maxStringLenght)}...)")
    }

    @Test
    fun `test replace & mask 1`() {
        val phony = Phony()
        assertThat(phony.toString())
            .`as`("Masking should be applied according to ${Phony::phoneNumberMask1
                .findAnnotation<AsStringMask>()}")
            .contains("phoneNumberMask1= +31 6 123********9 0 ")
            .doesNotContain(phony.phoneNumberMask1)

            .`as`("Masking should be applied according to ${Phony::phoneNumberMask2
                .findAnnotation<AsStringMask>()}")
            .contains("phoneNumberMask2= +31 6 123 45 ****9 0 ")
            .doesNotContain(phony.phoneNumberMask2)

            .`as`("Should apply full masking by default ${Phony::phoneNumberMask5.findAnnotation<AsStringMask>()}")
            .contains("phoneNumberMask5=**********************")
            .doesNotContain(phony.phoneNumberMask5)

            .`as`("Should apply partial masking by custom mask 'x' ${Phony::phoneNumberMask6
                .findAnnotation<AsStringMask>()}")
            .contains("phoneNumberMask6= +xxxx 123 45 67 89 0 ")
            .doesNotContain(phony.phoneNumberMask6)

            .`as`("Masking should be applied according to ${Phony::phoneNumberMask7
                .findAnnotation<AsStringMask>()}")
            .contains("phoneNumberMask7= +31******")
            .doesNotContain(phony.phoneNumberMask7)

            .`as`("Pattern replacement should be applied according to ${Phony::phoneNumberPatternReplace
                .findAnnotation<AsStringReplace>()}")
            .contains("phoneNumberPatternReplace= +31 6 123 ****89 0")
            .doesNotContain(phony.phoneNumberPatternReplace)
    }

    @Test
    fun `test replace & mask 2`() {
        val banky = Banky()
        assertThat(banky.asString())
            .`as`("Pattern replacement should be applied according to ")
            .contains("bankNumberPatternReplace=NL37<bank>*****48739")
            .doesNotContain(banky.bankNumberPatternReplace)

        assertThat(banky.asString())
            .`as`("Mask should adhere to minLength=4, even with maxLength=2")
            .containsPattern("""\bcountryCode=\*{4}([),])""")
            .doesNotContain("countryCode=${banky.countryCode}")
    }

    @Test
    fun `repeating AsStringMask annotations should be honoured, in order`() {
        @Suppress("unused")
        class Iban {
            @AsStringMask(startMaskAt = 2, endMaskAt = 4, mask = '0')
            @AsStringMask(startMaskAt = 11, endMaskAt = -3, mask = '*', minLength = 40)
            @AsStringMask(startMaskAt = 31, endMaskAt = 36, mask = '-', maxLength = 32)
            // fake Maltese IBAN number
            val iban = "MT52QCGK45148414861965929692444"
        }
        assertThat(Iban().asString()).isEqualTo("Iban(iban=MT00QCGK451*****************444-)")
    }

    @Test
    fun `repeating AsStringReplace annotations should be honoured, in order`() {
        @Suppress("unused")
        class Words {
            // removes leading/trailing whitespace and first and last words
            @AsStringReplace(pattern = """^\s*\S+\s+(.+?)\s+\S+\s*$""", "$1")
            @AsStringReplace(pattern = """five$""", "three")
            val fiveWords = " this String contains five words "
        }
        assertThat(Words().asString()).isEqualTo("Words(fiveWords=String contains three)")
    }

// region ~ Classes, objects etc. to be used for testing

    @AsStringOption(showNullAs = "[nil]", propMaxStringValueLength = 6)
    private open class ClassWithNonDefaultAsStringOptions {

        @AsStringReplace("^a", "another")
        val aStringToReplaceByAnother: String = "a String"

        @AsStringMask(startMaskAt = 5, endMaskAt = 7, mask = 'M')
        open val aDateWithMonthMasked: LocalDate = LocalDate.of(2023, 5, 24)

        @AsStringOmit
        open val noPrint: Any = Any()

        @AsStringHash
        val hashed: String = "a string that should be hashed"

        val aNullValue: Any? = null

        override fun toString(): String = asString()
    }

    private open class SubClassWithAsStringOptionsToString : ClassWithNonDefaultAsStringOptions() {

        override val noPrint: Any = "should not print this"

        @AsStringOption // defaults
        override fun toString(): String = asString()
    }

    @AsStringOption(showNullAs = "nothing here")
    private open class SubClassWithAsStringOptionsOnClass : ClassWithNonDefaultAsStringOptions() {

        @AsStringHash // Should not override the super method's @AsStringOmit annotation, so should not be included
        override val noPrint: Any = "should not print this"
    }

    @AsStringOption(propMaxStringValueLength = 2)
    private open class SubSubClassOfAsStringOptionsToStringWithAsStringOptionsOnClass :
        SubClassWithAsStringOptionsToString() {

        @AsStringHash
        override val aDateWithMonthMasked = super.aDateWithMonthMasked
        override fun toString(): String = asString()
    }

    private open class SubSubClassOfAsStringOptionsToStringWithAsStringOptionsOnToString :
        SubClassWithAsStringOptionsToString() {

        @AsStringOption(propMaxStringValueLength = 2)
        override fun toString(): String = asString()
    }

    private open class ClassWithAsStringOptionOnProperty {

        @AsStringOption(propMaxStringValueLength = 10)
        val propWithAsStringOption: String = "I am a property with @AsStringOption"

        override fun toString(): String = asString()
    }

    private open class ClassWithAsStringOptionsOnPropertyAndToString {

        @AsStringOption(propMaxStringValueLength = 10)
        val propWithAsStringOption: String = "I am a property with @AsStringOption"

        @AsStringOption(propMaxStringValueLength = 5)
        override fun toString(): String = asString()
    }

    private class Phony {
        @AsStringOmit
        private val phoneNumber: String = " +31 6 123 45 67 89 0 "

        @AsStringReplace(pattern = """(\d\s*){4}((\d\s*){3}\s*)$""", replacement = "****$2")
        val phoneNumberPatternReplace = phoneNumber

        @AsStringMask(startMaskAt = 10, endMaskAt = -4)
        val phoneNumberMask1 = phoneNumber

        @AsStringMask(startMaskAt = -8, endMaskAt = -4)
        val phoneNumberMask2 = phoneNumber

        // Should fully mask (default)
        @AsStringMask
        val phoneNumberMask5 = phoneNumber

        @AsStringMask(startMaskAt = 2, endMaskAt = 6, mask = 'x')
        val phoneNumberMask6 = phoneNumber

        @AsStringMask(startMaskAt = 4, maxLength = 10)
        val phoneNumberMask7 = phoneNumber

        override fun toString() = asString()
    }

    private class Banky {
        @AsStringReplace(pattern = """^(..\d\d)....\d{5}(.+)""", replacement = "$1<bank>*****$2")
        val bankNumberPatternReplace = "NL37DUMM5273748739"

        @AsStringMask(minLength = 4, maxLength = 2)
        val countryCode = "NL"
        override fun toString(): String = asString()
    }

// endregion

}
