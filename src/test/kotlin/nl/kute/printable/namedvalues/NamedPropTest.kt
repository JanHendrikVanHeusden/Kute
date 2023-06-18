package nl.kute.printable.namedvalues

import nl.kute.hashing.DigestMethod
import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.printable.annotation.modifiy.hashString
import nl.kute.printable.annotation.modifiy.mask
import nl.kute.printable.annotation.modifiy.replacePattern
import nl.kute.printable.annotation.option.PrintOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.random.Random

class NamedPropTest {
    @Test
    fun `NamedProp should evaluate the property value on each call`() {
        // Arrange
        var propValue = "prop value 1"
        val supplier = { propValue }

        class WithProp {
            val prop: String
                get() = supplier()
        }

        val withProp = WithProp()

        // Act
        val namedProp = NamedProp(withProp, withProp::prop)
        // Assert
        assertThat(namedProp.name).isEqualTo("prop")
        assertThat(namedProp.valueString).isEqualTo(propValue)

        // Arrange
        propValue = "prop value 2"
        // Act
        assertThat(namedProp.valueString).isEqualTo(propValue)
    }

    @Test
    fun `NamedProp should honour property annotations`() {
        // Arrange
        val testObj = WithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::unchanged).valueString)
            .isEqualTo(testObj.unchanged)

        assertThat(NamedProp(testObj, testObj::omitted).valueString)
            .isEmpty()

        assertThat(NamedProp(testObj, testObj::hashed).valueString)
            .isNotEqualTo(testObj.hashed)
            .isEqualTo(PrintHash().hashString(testObj.hashed))
            .matches("[a-z0-9]{8}")

        assertThat(NamedProp(testObj, testObj::masked).valueString)
            .isNotEqualTo(testObj.masked)
            .isEqualTo(PrintMask().mask(testObj.masked))
            .isEqualTo("*".repeat(testObj.masked.length))

        assertThat(NamedProp(testObj, testObj::replaced).valueString)
            .isNotEqualTo(testObj.replaced)
            .isEqualTo(PrintPatternReplace(" ", "_").replacePattern(testObj.replaced))
            .isEqualTo(testObj.replaced.replace(" ", "_"))
    }

    @Test
    fun `NamedProp should honour non-overridable super property annotations`() {
        // Arrange
        val testObjSuper = WithPropertyAnnotations()
        val testObj = Sub1OfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::unchanged).valueString)
            .isEqualTo(testObj.unchanged)
            .isEqualTo(testObjSuper.unchanged)

        assertThat(NamedProp(testObj, testObj::omitted).valueString)
            .`as`("should be omitted due to super-property @PrintOmit annotation")
            .isEmpty()

        assertThat(NamedProp(testObj, testObj::hashed).valueString)
            .isNotEqualTo(testObj.hashed)
            .`as`("Should honour the super-property CRC32C method")
            .isEqualTo(PrintHash(DigestMethod.CRC32C).hashString(testObj.hashed))
            .matches("[a-z0-9]{8}")
            .`as`("Should not honour the subclass SHA method")
            .isNotEqualTo(PrintHash(DigestMethod.SHA1).hashString(testObj.hashed))

        assertThat(NamedProp(testObj, testObj::masked).valueString)
            .isNotEqualTo(testObj.masked)
            .`as`("Should honour the super-property mask")
            .isEqualTo(PrintMask().mask(testObj.masked))
            .isEqualTo("*".repeat(testObj.masked.length))
            .`as`("Should not honour the subclass mask")
            .isNotEqualTo(PrintMask(startMaskAt = 2, endMaskAt = -2).mask(testObj.masked))

        assertThat(NamedProp(testObj, testObj::replaced).valueString)
            .isNotEqualTo(testObj.replaced)
            .`as`("Should honour the super-property pattern")
            .isEqualTo(PrintPatternReplace(" ", "_").replacePattern(testObj.replaced))
            .isEqualTo(testObj.replaced.replace(" ", "_"))
            .`as`("Should not honour the subclass pattern")
            .isNotEqualTo(PrintPatternReplace("xyz", "xyz").replacePattern(testObj.replaced))
    }

    @Test
    fun `NamedProp should honour overridable sub property annotation`() {
        // Arrange
        val testObj = Sub2aOfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::unchanged).valueString)
            .`as`("PrintOption of sub-property should override PrintOption of super properties")
            .isEqualTo(testObj.unchanged.take(5))
    }

    @Test
    fun `NamedProp should honour class level PrintOption`() {
        // Arrange
        val testObj = Sub2bOfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::omitted).valueString!!.length)
            .isZero()

        assertThat(NamedProp(testObj, testObj::unchanged).valueString!!.length)
            .`as`("Should not honour class level PrintOption, it has a property-level PrintOption in the class hierarchy`)")
            .isEqualTo(testObj.unchanged.length)

        listOf(testObj::hashed, testObj::masked, testObj::replaced).forEach {
            assertThat(NamedProp(testObj, it).valueString!!.length)
                .`as`("Should honour class level PrintOption with max length (property: `${it.name}`)")
                .isEqualTo(7)
        }
    }

    @Test
    fun `NamedProp should honour PrintOption at toString`() {
        // Arrange
        val testObj = Sub2cOfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::omitted).valueString!!.length)
            .isZero()

        assertThat(NamedProp(testObj, testObj::unchanged).valueString!!.length)
            .`as`("Should not honour class level PrintOption, it has a property-level PrintOption in the class hierarchy`)")
            .isEqualTo(testObj.unchanged.length)

        assertThat(NamedProp(testObj, testObj::hashed).valueString!!.length)
            .`as`("Has length 8, which is less than PrintOption's max prop value length")
            .isEqualTo(8)

        listOf(testObj::masked, testObj::replaced).forEach {
            assertThat(NamedProp(testObj, it).valueString!!.length)
                .`as`("Should honour class level PrintOption with max length (property: `${it.name}`)")
                .isEqualTo(11)
        }
    }

    @Test
    fun `test namedVal`() {
        // Arrange
        class MyTestClass {
            val testProp: Int = Random.nextInt()
        }
        val myTestObj = MyTestClass()
        // Act
        val namedProp = myTestObj.namedVal(myTestObj::testProp) as NamedProp<MyTestClass, Int>
        // Assert
        assertThat(namedProp.name).isEqualTo(MyTestClass::testProp.name)
        assertThat(namedProp.valueString).isEqualTo("${myTestObj.testProp}")
    }

    ///////////////
    // Test classes
    ///////////////

    private open class WithPropertyAnnotations {
        @PrintOption(propMaxStringValueLength = 300)
        open val unchanged = "I have a non-effective property annotation"

        @PrintOmit
        open val omitted = "I am omitted"

        @PrintHash
        open val hashed = "I am hashed"

        @PrintMask
        open val masked = "I am masked"

        @PrintPatternReplace(" ", "_")
        open val replaced = "I am pattern replaced"
    }

    private open class Sub1OfClassWithPropertyAnnotations : WithPropertyAnnotations() {
        override val unchanged = super.unchanged
        override val omitted = "I am trying not to be omitted"

        @PrintHash(DigestMethod.SHA1)
        override val hashed = "I am trying to be hashed with a different hash mehod"

        @PrintMask(startMaskAt = 2, endMaskAt = -2)
        override val masked = "I am trying to be masked differently"

        @PrintPatternReplace("xyz", "xyz")
        override val replaced = "I am trying to be replaced differently"
    }

    private open class Sub2aOfClassWithPropertyAnnotations : Sub1OfClassWithPropertyAnnotations() {
        @PrintOption(propMaxStringValueLength = 5)
        override val unchanged = "My annotation will restrict my output length to 5"
    }

    @PrintOption(propMaxStringValueLength = 7)
    private open class Sub2bOfClassWithPropertyAnnotations : Sub1OfClassWithPropertyAnnotations()

    @PrintOption(propMaxStringValueLength = 7)
    private open class Sub2cOfClassWithPropertyAnnotations : Sub1OfClassWithPropertyAnnotations() {
        @PrintOption(propMaxStringValueLength = 11)
        override fun toString() = "dummy"
    }
}