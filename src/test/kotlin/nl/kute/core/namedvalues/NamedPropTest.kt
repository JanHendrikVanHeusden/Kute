package nl.kute.core.namedvalues

import nl.kute.hashing.DigestMethod
import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import nl.kute.core.annotation.modifiy.AsStringHash
import nl.kute.core.annotation.modifiy.AsStringMask
import nl.kute.core.annotation.modifiy.AsStringOmit
import nl.kute.core.annotation.modifiy.AsStringPatternReplace
import nl.kute.core.annotation.modifiy.hashString
import nl.kute.core.annotation.modifiy.mask
import nl.kute.core.annotation.modifiy.replacePattern
import nl.kute.core.annotation.option.AsStringOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

class NamedPropTest {

    @BeforeEach
    @AfterEach
    fun setUp() {
        resetStdOutLogger()
    }

    interface WithProp {
        val myProp: String
    }

    @Test
    fun `property from delegate class should be retrieved`() {
        // Arrange
        val thePropValue = "the value"
        class ClassWithProp(override val myProp: String) : WithProp
        class ClassWithDelegate(val delegate: WithProp = ClassWithProp(thePropValue)): WithProp by delegate

        val testObj = ClassWithDelegate()

        // Act
        val namedPropWithObjectRef = testObj.namedVal(testObj::myProp) as NamedProp<ClassWithDelegate, String>
        // Assert
        assertThat(namedPropWithObjectRef.valueString)
            .`as`("It should retrieve the delegate property value by object (testObj)")
            .isEqualTo(thePropValue)

        // Act
        val namedPropWithClassRef = testObj.namedVal(ClassWithDelegate::myProp) as NamedProp<ClassWithDelegate, String>
        // Assert
        assertThat(namedPropWithClassRef.valueString)
            .`as`("It should retrieve the delegate property value by object class (ClassWithDelegate)")
            .isEqualTo(thePropValue)
    }

    @Test
    fun `incompatible property should be handled correctly`() {
        // Arrange
        var logMsg = ""
        logger = { msg: String -> logMsg += msg }
        // 2 unrelated classes
        class TestClass1(val myTestProperty: String = "this is for test")
        val testObj1 = TestClass1()
        class TestClass2
        val testObj2 = TestClass2()

        // Act
        val testClass1Property0: KProperty0<String> = testObj1::myTestProperty
        // call property of TestClass1 with object of TestClass2: unrelated, incompatible
        val namedProp0 = testObj2.namedVal(testClass1Property0)
        // Assert
        assertThat(namedProp0.valueString)
            .`as`("Somehow with KProperty0 (object reference testObj1) this succeeds, even with incompatible object (testObj2)")
            .isEqualTo(testObj1.myTestProperty)
        assertThat(logMsg).isEmpty()

        // Act
        // With KProperty1 this fails, on call with unrelated object; it should be handled properly
        val testClass1Property1: KProperty1<TestClass1, String> = TestClass1::myTestProperty
        val namedProp1 = testObj2.namedVal(testClass1Property1)
        // Assert
        assertThat(namedProp1.valueString)
            .`as`("With KProperty1 (class reference TestClass1) it will fail; is handled & returns `null`")
            .isNull()
        assertThat(logMsg)
            .`as`("Should not hit downstream ClassCastException")
            .doesNotContain("ClassCastException")
            .`as`("Should be handled early by validation in NamedProp")
            .contains("IllegalStateException")
            .`as`("Error message should give relevant information")
            .contains(TestClass1::class.simpleName)
            .contains(TestClass2::class.simpleName)
            .contains(namedProp1.name)
    }

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
            .isEqualTo(AsStringHash().hashString(testObj.hashed))
            .matches("[a-z0-9]{8}")

        assertThat(NamedProp(testObj, testObj::masked).valueString)
            .isNotEqualTo(testObj.masked)
            .isEqualTo(AsStringMask().mask(testObj.masked))
            .isEqualTo("*".repeat(testObj.masked.length))

        assertThat(NamedProp(testObj, testObj::replaced).valueString)
            .isNotEqualTo(testObj.replaced)
            .isEqualTo(AsStringPatternReplace(" ", "_").replacePattern(testObj.replaced))
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
            .`as`("should be omitted due to super-property @AsStringOmit annotation")
            .isEmpty()

        assertThat(NamedProp(testObj, testObj::hashed).valueString)
            .isNotEqualTo(testObj.hashed)
            .`as`("Should honour the super-property CRC32C method")
            .isEqualTo(AsStringHash(DigestMethod.CRC32C).hashString(testObj.hashed))
            .matches("[a-z0-9]{8}")
            .`as`("Should not honour the subclass SHA method")
            .isNotEqualTo(AsStringHash(DigestMethod.SHA1).hashString(testObj.hashed))

        assertThat(NamedProp(testObj, testObj::masked).valueString)
            .isNotEqualTo(testObj.masked)
            .`as`("Should honour the super-property mask")
            .isEqualTo(AsStringMask().mask(testObj.masked))
            .isEqualTo("*".repeat(testObj.masked.length))
            .`as`("Should not honour the subclass mask")
            .isNotEqualTo(AsStringMask(startMaskAt = 2, endMaskAt = -2).mask(testObj.masked))

        assertThat(NamedProp(testObj, testObj::replaced).valueString)
            .isNotEqualTo(testObj.replaced)
            .`as`("Should honour the super-property pattern")
            .isEqualTo(AsStringPatternReplace(" ", "_").replacePattern(testObj.replaced))
            .isEqualTo(testObj.replaced.replace(" ", "_"))
            .`as`("Should not honour the subclass pattern")
            .isNotEqualTo(AsStringPatternReplace("xyz", "xyz").replacePattern(testObj.replaced))
    }

    @Test
    fun `NamedProp should honour overridable sub property annotation`() {
        // Arrange
        val testObj = Sub2aOfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::unchanged).valueString)
            .`as`("AsStringOption of sub-property should override AsStringOption of super properties")
            .isEqualTo(testObj.unchanged.take(5))
    }

    @Test
    fun `NamedProp should honour class level AsStringOption`() {
        // Arrange
        val testObj = Sub2bOfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::omitted).valueString!!.length)
            .isZero()

        assertThat(NamedProp(testObj, testObj::unchanged).valueString!!.length)
            .`as`("Should not honour class level AsStringOption, it has a property-level AsStringOption in the class hierarchy`)")
            .isEqualTo(testObj.unchanged.length)

        listOf(testObj::hashed, testObj::masked, testObj::replaced).forEach {
            assertThat(NamedProp(testObj, it).valueString!!.length)
                .`as`("Should honour class level AsStringOption with max length (property: `${it.name}`)")
                .isEqualTo(7)
        }
    }

    @Test
    fun `NamedProp should honour AsStringOption at toString`() {
        // Arrange
        val testObj = Sub2cOfClassWithPropertyAnnotations()

        // Act, Assert
        assertThat(NamedProp(testObj, testObj::omitted).valueString!!.length)
            .isZero()

        assertThat(NamedProp(testObj, testObj::unchanged).valueString!!.length)
            .`as`("Should not honour class level AsStringOption, it has a property-level AsStringOption in the class hierarchy`)")
            .isEqualTo(testObj.unchanged.length)

        assertThat(NamedProp(testObj, testObj::hashed).valueString!!.length)
            .`as`("Has length 8, which is less than AsStringOption's max prop value length")
            .isEqualTo(8)

        listOf(testObj::masked, testObj::replaced).forEach {
            assertThat(NamedProp(testObj, it).valueString!!.length)
                .`as`("Should honour class level AsStringOption with max length (property: `${it.name}`)")
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
        @AsStringOption(propMaxStringValueLength = 300)
        open val unchanged = "I have a non-effective property annotation"

        @AsStringOmit
        open val omitted = "I am omitted"

        @AsStringHash
        open val hashed = "I am hashed"

        @AsStringMask
        open val masked = "I am masked"

        @AsStringPatternReplace(" ", "_")
        open val replaced = "I am pattern replaced"
    }

    private open class Sub1OfClassWithPropertyAnnotations : WithPropertyAnnotations() {
        override val unchanged = super.unchanged
        override val omitted = "I am trying not to be omitted"

        @AsStringHash(DigestMethod.SHA1)
        override val hashed = "I am trying to be hashed with a different hash mehod"

        @AsStringMask(startMaskAt = 2, endMaskAt = -2)
        override val masked = "I am trying to be masked differently"

        @AsStringPatternReplace("xyz", "xyz")
        override val replaced = "I am trying to be replaced differently"
    }

    private open class Sub2aOfClassWithPropertyAnnotations : Sub1OfClassWithPropertyAnnotations() {
        @AsStringOption(propMaxStringValueLength = 5)
        override val unchanged = "My annotation will restrict my output length to 5"
    }

    @AsStringOption(propMaxStringValueLength = 7)
    private open class Sub2bOfClassWithPropertyAnnotations : Sub1OfClassWithPropertyAnnotations()

    @AsStringOption(propMaxStringValueLength = 7)
    private open class Sub2cOfClassWithPropertyAnnotations : Sub1OfClassWithPropertyAnnotations() {
        @AsStringOption(propMaxStringValueLength = 11)
        override fun toString() = "dummy"
    }
}