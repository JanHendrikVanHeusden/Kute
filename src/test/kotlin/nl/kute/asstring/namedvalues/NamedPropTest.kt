package nl.kute.asstring.namedvalues

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringMask
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.modify.hashString
import nl.kute.asstring.annotation.modify.mask
import nl.kute.asstring.annotation.modify.replacePattern
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.core.asString
import nl.kute.hashing.DigestMethod
import nl.kute.logging.logger
import nl.kute.logging.resetStdOutLogger
import nl.kute.test.base.GarbageCollectionWaiter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.random.Random
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

class NamedPropTest: GarbageCollectionWaiter {

    @BeforeEach
    @AfterEach
    fun setUp() {
        resetStdOutLogger()
    }


    @Test
    fun `property from delegate class should be retrieved`() {
        // arrange
        val thePropValue = "the value"
        class ClassWithProp(override val myProp: String) : WithProp
        class ClassWithConstructorInjectedDelegate(val delegate: WithProp = ClassWithProp(thePropValue)): WithProp by delegate
        val withPropObject: WithProp = object: WithProp {
            override val myProp: String = "WithProp object"
        }
        class ClassWithDelegateObject: WithProp by withPropObject
        val classWithObjectDelegate = ClassWithDelegateObject()

        val testObj = ClassWithConstructorInjectedDelegate()

        // act
        val namedPropWithInjectedRef = testObj::myProp.namedProp(testObj)
        // assert
        assertThat(namedPropWithInjectedRef.value)
            .`as`("It should retrieve the delegate property value by object (testObj)")
            .isEqualTo(thePropValue)

        // act
        val namedPropWithClassRef = ClassWithConstructorInjectedDelegate::myProp.namedProp(testObj)
        // assert
        assertThat(namedPropWithClassRef.value)
            .`as`("It should retrieve the delegate property value by object class (ClassWithConstructorInjectedDelegate)")
            .isEqualTo(thePropValue)

        // act
        val namedPropWithObject = classWithObjectDelegate::myProp.namedProp(classWithObjectDelegate)
        // assert
        assertThat(namedPropWithObject.value)
            .`as`("It should retrieve the delegate property value by object class (ClassWithDelegateObject)")
            .isEqualTo(withPropObject.myProp)
    }

    @Test
    fun `incoherent property should be handled correctly`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }
        // 2 unrelated classes
        class TestClass1(val myTestProperty: String = "this is for test")
        val testObj1 = TestClass1()
        class TestClass2
        val testObj2 = TestClass2()

        // act
        val testClass1Property0: KProperty0<String> = testObj1::myTestProperty
        // call property of TestClass1 with object of TestClass2: unrelated, incompatible
        val namedProp0 = testClass1Property0.namedProp(testObj2)
        // assert
        assertThat(namedProp0.value)
            .`as`("Somehow with KProperty0 (object reference testObj1) this succeeds, even with incompatible object (testObj2)")
            .isEqualTo(testObj1.myTestProperty)
        assertThat(logMsg).isEmpty()

        // act
        // With KProperty1 this fails, on call with unrelated object; it should be handled properly
        val testClass1Property1: KProperty1<TestClass1, String> = TestClass1::myTestProperty
        val namedProp1 = testClass1Property1.namedProp(testObj2)
        // assert
        assertThat(namedProp1.value)
            .`as`("With KProperty1 (class reference TestClass1) it will fail; is handled & returns `null`")
            .isNull()
        assertThat(logMsg)
            .`as`("Should not hit downstream ClassCastException")
            .doesNotContain("ClassCastException")
            .`as`("Should be handled early by validation in NamedProp")
            .contains("IllegalStateException")
            .`as`("Error message should contain relevant information")
            .contains(TestClass1::class.simpleName)
            .contains(TestClass2::class.simpleName)
            .contains(namedProp1.name)
        logMsg = ""
        namedProp1.value
        assertThat(logMsg)
            .`as`("subsequent attempts should not log anymore")
            .isEmpty()
    }

    @Test
    fun `NamedProp should evaluate the property value on each call`() {
        // arrange
        var propValue = "prop value 1"
        val supplier = { propValue }

        class WithProp {
            val prop: String
                get() = supplier()
        }

        val withProp = WithProp()

        // act
        val namedProp = withProp::prop.namedProp(withProp)
        // assert
        assertThat(namedProp.name).isEqualTo("prop")
        assertThat(namedProp.value).isEqualTo(propValue)

        // arrange
        propValue = "prop value 2"
        // act
        assertThat(namedProp.value).isEqualTo(propValue)
    }

    @Test
    fun `NamedProp should honour property annotations`() {
        // arrange
        val testObj = WithPropertyAnnotations()

        // act, assert
        assertThat(testObj::unchanged.namedProp(testObj).value)
            .isEqualTo(testObj.unchanged)

        assertThat(testObj::omitted.namedProp(testObj).value)
            .isEmpty()

        assertThat(testObj::hashed.namedProp(testObj).value)
            .isNotEqualTo(testObj.hashed)
            .isEqualTo(AsStringHash().hashString(testObj.hashed))
            .matches("#[a-z0-9]{8}#")

        assertThat(testObj::masked.namedProp(testObj).value)
            .isNotEqualTo(testObj.masked)
            .isEqualTo(AsStringMask().mask(testObj.masked))
            .isEqualTo("*".repeat(testObj.masked.length))

        assertThat(testObj::replaced.namedProp(testObj).value)
            .isNotEqualTo(testObj.replaced)
            .isEqualTo(AsStringReplace(" ", "_").replacePattern(testObj.replaced))
            .isEqualTo(testObj.replaced.replace(" ", "_"))
    }

    @Test
    fun `NamedProp should honour non-overridable super property annotations`() {
        // arrange
        val testObjSuper = WithPropertyAnnotations()
        val testObj = Sub1OfClassWithPropertyAnnotations()

        // act, assert
        assertThat(testObj::unchanged.namedProp(testObj).value)
            .isEqualTo(testObj.unchanged)
            .isEqualTo(testObjSuper.unchanged)

        assertThat(testObj::omitted.namedProp(testObj).value)
            .`as`("should be omitted due to super-property @AsStringOmit annotation")
            .isEmpty()

        assertThat(testObj::hashed.namedProp(testObj).value)
            .isNotEqualTo(testObj.hashed)
            .`as`("Should honour the super-property CRC32C method")
            .isEqualTo(AsStringHash(DigestMethod.CRC32C).hashString(testObj.hashed))
            .matches("#[a-z0-9]{8}#")
            .`as`("Should not honour the subclass SHA method")
            .doesNotContain(AsStringHash(DigestMethod.SHA1).hashString(testObj.hashed))

        assertThat(testObj::masked.namedProp(testObj).value)
            .isNotEqualTo(testObj.masked)
            .`as`("Should honour the super-property mask")
            .isEqualTo(AsStringMask().mask(testObj.masked))
            .isEqualTo("*".repeat(testObj.masked.length))
            .`as`("Should not honour the subclass mask")
            .isNotEqualTo(AsStringMask(startMaskAt = 2, endMaskAt = -2).mask(testObj.masked))

        assertThat(testObj::replaced.namedProp(testObj).value)
            .isNotEqualTo(testObj.replaced)
            .`as`("Should honour the super-property pattern")
            .isEqualTo(AsStringReplace(" ", "_").replacePattern(testObj.replaced))
            .isEqualTo(testObj.replaced.replace(" ", "_"))
            .`as`("Should not honour the subclass pattern")
            .isNotEqualTo(AsStringReplace("xyz", "xyz").replacePattern(testObj.replaced))
    }

    @Test
    fun `NamedProp should honour overridable sub property annotation`() {
        // arrange
        val testObj = Sub2aOfClassWithPropertyAnnotations()

        // act, assert
        assertThat(testObj::unchanged.namedProp(testObj).value)
            .`as`("AsStringOption of sub-property should override AsStringOption of super properties")
            .isEqualTo(testObj.unchanged.take(5) + "...")
    }

    @Test
    fun `NamedProp should honour class level AsStringOption`() {
        // arrange
        val testObj = Sub2bOfClassWithPropertyAnnotations()

        // act, assert
        assertThat(testObj::omitted.namedProp(testObj).value!!.length)
            .isZero

        assertThat(testObj::unchanged.namedProp(testObj).value!!.length)
            .`as`("Should not honour class level AsStringOption, it has a property-level AsStringOption in the class hierarchy`)")
            .isEqualTo(testObj.unchanged.length)

        listOf(testObj::hashed, testObj::masked, testObj::replaced).forEach {
            assertThat(it.namedProp(testObj).value!!.length)
                .`as`("Should honour class level AsStringOption with max length 7 (property: `${it.name}`)")
                .isEqualTo(10) // 7 + 3 for `...`
        }
    }

    @Test
    fun `NamedProp should honour AsStringOption at toString`() {
        // arrange
        val testObj = Sub2cOfClassWithPropertyAnnotations()

        // act, assert
        assertThat(testObj::omitted.namedProp(testObj).value!!.length)
            .isZero

        assertThat(testObj::unchanged.namedProp(testObj).value!!.length)
            .`as`("Should not honour class level AsStringOption, it has a property-level AsStringOption in the class hierarchy`)")
            .isEqualTo(testObj.unchanged.length)

        assertThat(testObj::hashed.namedProp(testObj).value!!.length)
            .`as`("Has length 10, which is less than AsStringOption's max prop value length")
            .isEqualTo(10)

        listOf(testObj::masked, testObj::replaced).forEach {
            assertThat(it.namedProp(testObj).value!!.length)
                .`as`("Should honour class level AsStringOption with max length 11 (property: `${it.name}`)")
                .isEqualTo(14) // 11 + 3 for `...`
        }
    }

    @Test
    fun `namedProp should reflect name and value of source property`() {
        // arrange
        class MyTestClass {
            val testProp: Int = Random(Date().time.toInt()).nextInt()
        }
        val myTestObj = MyTestClass()
        // act
        val namedProp = myTestObj::testProp.namedProp(myTestObj)
        // assert
        assertThat(namedProp.name).isEqualTo(MyTestClass::testProp.name)
        assertThat(namedProp.value).isEqualTo("${myTestObj.testProp}")
    }

    @Test
    fun `NamedProp shouldn't prevent garbage collection`() {
        // arrange
        class ToBeGarbageCollected {
            val myString: String = "my String"
            override fun toString(): String = asString()
        }
        var toBeGarbageCollected: ToBeGarbageCollected? = ToBeGarbageCollected()
        @Suppress("UNCHECKED_CAST")
        val namedProp: NamedProp<ToBeGarbageCollected, String?> =
            ToBeGarbageCollected::myString.namedProp(toBeGarbageCollected) as NamedProp<ToBeGarbageCollected, String?>
        
        val checkGarbageCollected = {namedProp.value == null}
        assertThat(checkGarbageCollected.invoke()).isFalse
        assertThat(namedProp.value).isEqualTo("my String")

        // act
        // nullify the object, should then be eligible for garbage collection
        @Suppress("UNUSED_VALUE")
        toBeGarbageCollected = null

        // assert
        assertGarbageCollected(checkGarbageCollected)
    }

// region ~ Classes, objects etc. to be used for testing

    interface WithProp {
        val myProp: String
    }

    private open class WithPropertyAnnotations {
        @AsStringOption(propMaxStringValueLength = 300)
        open val unchanged = "I have a non-effective property annotation"

        @AsStringOmit
        open val omitted = "I am omitted"

        @AsStringHash
        open val hashed = "I am hashed"

        @AsStringMask
        open val masked = "I am masked"

        @AsStringReplace(" ", "_")
        open val replaced = "I am pattern replaced"
    }

    private open class Sub1OfClassWithPropertyAnnotations : WithPropertyAnnotations() {
        override val unchanged = super.unchanged
        override val omitted = "I am trying not to be omitted"

        @AsStringHash(DigestMethod.SHA1)
        override val hashed = "I am trying to be hashed with a different hash method"

        @AsStringMask(startMaskAt = 2, endMaskAt = -2)
        override val masked = "I am trying to be masked differently"

        @AsStringReplace("xyz", "xyz")
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

// endregion

}