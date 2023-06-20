package nl.kute.core

import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.hashing.DigestMethod
import nl.kute.printable.annotation.modifiy.AsStringHash
import nl.kute.printable.annotation.modifiy.AsStringOmit
import nl.kute.printable.annotation.modifiy.AsStringPatternReplace
import nl.kute.printable.annotation.option.AsStringOption
import nl.kute.printable.namedvalues.namedVal
import nl.kute.util.toByteArray
import nl.kute.util.toHex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

const val showNullAs = "`null`"
const val showNullAs2 = "[null]"
internal class AsStringBuilderTest {

    private val testObj = ClassWithHashProperty()
    private val testSubObj = SubClassWithPrintMask()
    private val hashCode = ClassWithHashProperty().hashProperty.hashCode().toByteArray().toHex()

    @Test
    fun `AsStringBuilder without adjustments should give same result as AsString`() {
        // Arrange
        val expected = "ClassWithHashProperty(hashProperty=$hashCode, nullable=$showNullAs, privateProp=I am a private property)"
        // Act, Assert
        assertThat(testObj.asString()).isEqualTo(expected)
        assertThat(testObj.asStringBuilder().asString()).isEqualTo(expected)

        // Arrange
        val expectedSub = "SubClassWithPrintMask(nullable=$showNullAs2, replaced=xx is replaced, hashProperty=$hashCode)"
        // Act, Assert
        assertThat(testSubObj.asString()).isEqualTo(expectedSub)
        assertThat(testSubObj.asStringBuilder().asString()).isEqualTo(expectedSub)
    }

    @Test
    fun `AsStringBuilder should honour exceptProperties`() {
        // Arrange
        val expected = "ClassWithHashProperty(privateProp=I am a private property)"
        // Act, Assert
        assertThat(testObj.asStringBuilder()
            .exceptProperties(ClassWithHashProperty::nullable, testObj::hashProperty)
            .asString()
        ).isEqualTo(expected)

        // Arrange
        val expectedSub = "SubClassWithPrintMask(replaced=xx is replaced)"
        // Act, Assert
        assertThat(testSubObj.asStringBuilder()
            .exceptProperties(ClassWithHashProperty::nullable, testObj::hashProperty)
            .asString()
        ).isEqualTo(expectedSub)
    }

    @Test
    fun `AsStringBuilder should honour exceptPropertyNames`() {
        // Arrange
        val expected = "ClassWithHashProperty(nullable=$showNullAs)"
        // Act, Assert
        val asString = testObj.asStringBuilder()
            .exceptPropertyNames("privateProp", ClassWithHashProperty::hashProperty.name, "dummy")
            .asString()
        assertThat(asString).isEqualTo(expected)

        // Arrange
        val expectedSub = "SubClassWithPrintMask(replaced=xx is replaced, hashProperty=$hashCode)"
        // Act, Assert
        val asStringSub = testSubObj.asStringBuilder()
            .exceptPropertyNames("nullable", "privateProp")
            .asString()
        assertThat(asStringSub)
            .isEqualTo(expectedSub)
    }

    @Test
    fun `AsStringBuilder should honour withAlsoProperties`() {
        // Arrange
        val expected = "ClassWithHashProperty(hashProperty=$hashCode, nullable=$showNullAs, privateProp=I am a private property, replaced=xx is replaced)"
        // Act, Assert
        val asString = testObj.asStringBuilder()
            .withAlsoProperties(testSubObj::replaced)
            .asString().also { println(it) }
        assertThat(asString).isEqualTo(expected)
    }

    @Test
    fun `AsStringBuilder should honour withAlsoNamed`() {
        // Arrange
        val expected = "ClassWithHashProperty(hashProperty=$hashCode, nullable=$showNullAs, privateProp=I am a private property, nullable=$showNullAs2, I am a named value=some string)"
        val namedProp = testSubObj.namedVal(testSubObj::nullable)
        val namedValue = "some string".namedVal("I am a named value")
        // Act, Assert
        val asString = testObj.asStringBuilder()
            .withAlsoNamed(namedProp, namedValue)
            .asString()
        assertThat(asString).isEqualTo(expected)
    }

    @Test
    fun `AsStringBuilder should honour withOnlyProperties`() {
        // Arrange
        val expected = "ClassWithHashProperty(nullable=$showNullAs)"
        // Act, Assert
        val asString = testObj.asStringBuilder()
            .withOnlyProperties(ClassWithHashProperty::nullable)
            .asString()
        assertThat(asString).isEqualTo(expected)
    }

    @Test
    fun `AsStringBuilder should ignore withOnlyProperties with non-matching properties`() {
        // Arrange
        val expected = "ClassWithHashProperty(nullable=$showNullAs)"
        // Act, Assert
        val asString = testObj.asStringBuilder()
            .withOnlyProperties(SubClassWithPrintMask::replaced, testObj::nullable)
            .asString()
        assertThat(asString)
            .`as`("::replaced is not a property of testObj, so should be ignored")
            .isEqualTo(expected)
    }

    @Test
    fun `AsStringBuilder should honour withOnlyPropertyNames`() {
        // Arrange
        val expected = "SubClassWithPrintMask(replaced=xx is replaced, hashProperty=$hashCode)"
        // Act, Assert
        val asString = testSubObj.asStringBuilder()
            .withOnlyPropertyNames("replaced", "hashProperty")
            .asString()
        assertThat(asString).isEqualTo(expected)
    }


    @Test
    fun asStringBuilder() {
    }

    @AsStringOption(showNullAs = showNullAs)
    private interface InterfaceWithOmitProperty {
        @AsStringOmit
        val omitted: String
        val nullable: Any?
    }

    @Suppress("unused")
    private open class ClassWithHashProperty(override val omitted: String = "omitted"): InterfaceWithOmitProperty {
        private val privateProp: Any = object {
            override fun toString(): String = "I am a private property"
        }
        override val nullable: Any? = null
        @AsStringHash(DigestMethod.JAVA_HASHCODE)
        val hashProperty = "I am hashed with Java hashcode"
    }
    private class SubClassWithPrintMask: ClassWithHashProperty() {
        @AsStringPatternReplace("^I am ", replacement = "xx is ")
        val replaced = "I am replaced"
        @AsStringOption(showNullAs = "[null]")
        override val nullable: Any? = super.nullable
    }
}