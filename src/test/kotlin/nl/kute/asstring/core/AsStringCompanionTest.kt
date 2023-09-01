@file:Suppress("unused")

package nl.kute.asstring.core

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringMask
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.PropertyValueSurrounder.`¦¦`
import nl.kute.asstring.annotation.option.PropertyValueSurrounder.`¶¶`
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.core.test.helper.equalSignCount
import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.hashing.DigestMethod
import nl.kute.reflection.util.simplifyClassName
import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.testobjects.java.JavaClassToTest
import nl.kute.testobjects.java.JavaClassWithStatic
import nl.kute.util.identityHashHex
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.memberProperties

class AsStringCompanionTest: ObjectsStackVerifier {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
    }

    @Test
    fun `asString of public class with named public companion properties includes these properties`() {
        val testObj = NestedPublicClassWithPublicCompanion()
        assertThat(testObj.asString())
            .isEqualTo("NestedPublicClassWithPublicCompanion(instanceProp=instance prop, companion: CompObjectName(companionProp=companion prop))")

        // assign a new value
        NestedPublicClassWithPublicCompanion.companionProp = "a new value for the companion prop"
        // assert that the new value is reflected in the output
        assertThat(testObj.asString())
            .isEqualTo("NestedPublicClassWithPublicCompanion(instanceProp=instance prop, companion: CompObjectName(companionProp=a new value for the companion prop))")
    }

    @Test
    fun `asString of public class with private companion object with properties does not include the companion object`() {
        val testObj = NestedPublicClassWithPrivateCompanion()
        assertThat(testObj.asString())
            .isEqualTo("NestedPublicClassWithPrivateCompanion(instanceProp=instance prop)")
    }

    @Test
    fun `asString of protected class with public companion object with properties includes the companion object`() {
        val testObj = NestedProtectedClassWithPublicCompanion()
        assertThat(testObj.asString())
            .isEqualTo("NestedProtectedClassWithPublicCompanion(instanceProp=instance prop, companion: CompObjectName(companionProp=companion prop))")
    }

    @Test
    fun `asString of public class with protected companion object with properties does not include the companion object`() {
        val testObj = NestedPublicClassWithProtectedCompanion()
        assertThat(testObj.asString())
            .isEqualTo("NestedPublicClassWithProtectedCompanion(instanceProp=instance prop)")
    }

    @Test
    fun `asString of private class with companion properties does not include the companion object`() {
        val testObj = PrivateClassWithCompanion()
        assertThat(testObj.asString())
            .`as`("Kotlin reflection cannot access companion object of private class, so it's omitted")
            .isEqualTo("PrivateClassWithCompanion(instanceProp=instance prop)")

        // you can work around it (sort of) by using AsStringBuilder
        assertThat(
            testObj.asStringBuilder()
                .withAlsoProperties(PrivateClassWithCompanion.Companion::companionProp)
                .asString()
        ).isEqualTo("PrivateClassWithCompanion(instanceProp=instance prop, companionProp=${NestedPrivateClassWithCompanion.companionProp})")
    }

    @Test
    fun `asString of private nested class with companion properties does not include these properties`() {
        val testObj = NestedPrivateClassWithCompanion()
        assertThat(testObj.asString())
            .`as`("Kotlin reflection cannot access companion object of private class, so it's omitted")
            .isEqualTo("NestedPrivateClassWithCompanion(instanceProp=instance prop)")

        // you can work around it (sort of) by using AsStringBuilder
        assertThat(
            testObj.asStringBuilder()
                .withAlsoProperties(NestedPrivateClassWithCompanion::companionProp)
                .asString()
        ).isEqualTo("NestedPrivateClassWithCompanion(instanceProp=instance prop, companionProp=${NestedPrivateClassWithCompanion.companionProp})")
    }

    @Test
    fun `asString with Java object does not include static variables`() {
        val testObj = JavaClassWithStatic()
        // not showing static var
        assertThat(testObj.asString())
            .`as`("does by default not contain the static var")
            .isEqualTo("JavaClassWithStatic(instanceVar=instance var)")

        // but they can work around it by using a named supplier
        assertThat(testObj.toString())
            .`as`("added static var by builder with named supplier")
            .isEqualTo("JavaClassWithStatic(instanceVar=instance var, staticVar=static var)")

        // assign a new value
        JavaClassWithStatic.staticVar = "a new value for the static var"
        // assert that the new value is reflected in the output
        assertThat(testObj.toString())
            .`as`("added static var by builder with named supplier")
            .isEqualTo("JavaClassWithStatic(instanceVar=instance var, staticVar=a new value for the static var)")
    }

    @Test
    fun `asString with properties should honour annotations`() {
        // arrange
        @Suppress("UNUSED_PARAMETER")
        class TestClass(aParamThatIsNotUsed: String) {
            val prop1 = "prop 1"
            @AsStringOmit
            val prop2 = "prop 2"
            val prop3 = "prop 3"
            @AsStringReplace(".+", "[ $0 ]", isRegexpPattern = true )
            val prop4 = "prop 4"
        }
        class AnotherClass(val otherProp: String = "another Prop")

        val testObj = TestClass("something")

        // act, assert
        assertThat(testObj.asString())
            .isObjectAsString(
                "TestClass",
                "prop1=prop 1",
                "prop3=prop 3",
                "prop4=[ prop 4 ]"
            )
            .`is`(equalSignCount(3))
        assertThat(testObj.asString(TestClass::prop1, TestClass::prop2, TestClass::prop3, TestClass::prop4))
            .isObjectAsString(
                "TestClass",
                "prop1=prop 1",
                "prop3=prop 3",
                "prop4=[ prop 4 ]"
            )
            .`is`(equalSignCount(3))

        assertThat(testObj.asString(TestClass::prop1, TestClass::prop2, TestClass::prop4))
            .isEqualTo(testObj.asString().replace(", prop3=prop 3", ""))

        // should be last test in the method, it might fail, (and be ignored); that would exit the test method
        Assumptions.assumeThat(testObj.asString(TestClass::prop3, TestClass::prop4, TestClass::prop1))
            .`as`("Demonstrate properties keep original order, regardless of order in param list.\n" +
                    "This is assumed rather than asserted, as this is implicit behaviour")
            .isEqualTo(testObj.asString())
    }

    @Test
    fun `class with property of other class should use asString for both`() {
        assertThat(ClassWithTestClassProperty().asString())
            .isObjectAsString(
                "ClassWithTestClassProperty",
                "aProp=I am a property of ClassWithTestClassProperty",
                "testClass=TestClass(someProp=I am a property of TestClass)"
            )
    }

    @Test
    fun `class with custom type property should use toString for both when PREFER_TOSTRING`() {
        AsStringConfig().withToStringPreference(ToStringPreference.PREFER_TOSTRING).applyAsDefault()
        val testObj = ClassWithTestClassProperty()
        assertThat(testObj.asString())
            .isEqualTo("This should not show up when asString() is called on me; This should not show up when asString() is called on me")
            .isEqualTo(testObj.toString())
    }

    @Test
    fun `unannotated companion properties should inherit class annotations`() {
        val testObj = WithAnnotationsAtClassAndUnannotatedCompanion()
        val testObjHash = testObj.identityHashHex
        val companionHex = testObj::class.companionObjectInstance.identityHashHex
        assertThat(testObj.asString())
            .`as`("non-annotated companion object should inherit annotations of the holder class")
            .isEqualTo("WithAnnotationsAtClassAndUnannotatedCompanion@$testObjHash(instanceProp=¶instance prop¶," +
                    " companion: CompObjectName@$companionHex(companionProp=¶companion prop¶))")
    }

    @Test
    fun `companion properties should adhere to annotations`() {
        val testObj = WithPropsAndCompanionPropsAndAnnotations()
        val testObjHash = testObj.identityHashHex
        assertThat(testObj.asString())
            .isEqualTo("WithPropsAndCompanionPropsAndAnnotations@$testObjHash(" +
                    "instanceProp=¶instance prop¶, instancePropAnnotated=¦instance prop annotated¦," +
                    " companion: Companion(companionProp=§companion prop§, companionPropAnnotated=«companion prop annotated»))")
    }

    @Test
    fun `empty companion should not be rendered`() {
        // arrange
        val testObj = WithEmptyCompanion()
        val compInstance = testObj::class.companionObjectInstance!!
        assertThat(compInstance::class.simpleName).isEqualTo("MyEmptyCompanion")
        assertThat(compInstance::class.memberProperties).isEmpty()
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("WithEmptyCompanion(instanceProp=instance prop)")
    }

    @Test
    fun `companion without properties should not be rendered`() {
        // arrange
        val testObj = NoCompanionProperties()
        val compInstance = testObj::class.companionObjectInstance!!
        assertThat(compInstance::class.simpleName).isEqualTo("Companion")
        assertThat(compInstance::class.memberProperties).isEmpty()
        assertThat(compInstance::class.functions.first { it.name == "something" }).isNotNull
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("NoCompanionProperties(instanceProp=instance prop)")
    }

// region ~ Classes etc. to be used for testing

    private val people: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    private val aPrintableDate = object {
        override fun toString(): String = LocalDate.of(2022, 1, 27).toString()
    }
    private val anotherPrintable = object {
        override fun toString(): String = "this is another printable"
    }

    private class TestClass {
        val someProp = "I am a property of ${this::class.simplifyClassName()}"
        override fun toString() = "This should not show up when asString() is called on me"
    }

    private class ClassWithTestClassProperty {
        val aProp = "I am a property of ${this::class.simplifyClassName()}"
        val testClass = TestClass()
        override fun toString() = "This should not show up when asString() is called on me; ${testClass.asString()}"
    }

    private interface WithNum {
        var num: Int
        fun asString(): String
    }

    @Suppress("unused", "SameReturnValue")
    private open class ClassToPrint(val str: String, override var num: Int, private val privateToPrint: Any?): WithNum {
        // getter should be called, not the internal value. Private should be included, but not for subclasses
        @Suppress("SuspiciousVarProperty")
        private var greet: String? = "hi"
            get() = "hallo"

        // protected, should be included as well
        protected val uuidToPrint: UUID = UUID.fromString("c27ab2db-3f72-4603-9e46-57892049b027")

        // method return values should not be included
        fun getUuidNotToPrint(): UUID = UUID.fromString("97f52d73-2da2-4c0d-af23-9eb2156eea96")

        override fun asString(): String = this.asStringBuilder().asString()

        override fun toString(): String = asString() // default
    }

    // anonymous nested class
    private val extensionObject: Any = object : ClassToPrint("a string", 25, anotherPrintable) {
        val asStringProducer = this.asStringBuilder()
            .exceptPropertyNames("privateToPrint")
            .build()

        override fun toString(): String = asStringProducer.asString()

        private val extensionProperty = "my extension property"
        override var num = 80
    }

    @Suppress("unused", "CanBeParameter")
    private class KotlinClassToTest(str: String, num: Int, val anotherStr: String, val names: Array<String>) :
        JavaClassToTest(str, num, *names) {

        override fun toString() = asString()
    }

    private interface PersonallyIdentifiableData {
        @AsStringMask(startMaskAt = 5, endMaskAt = -3)
        val phoneNumber: String

        @AsStringReplace(
            """\s*([a-zA-Z]{2})\s*\d{2}\s*[a-zA-Z]{4}\s*((\d|\s){6})(.*)""",
            """$1\99 BANK *****$4"""
        )
        val iban: String

        @AsStringHash(DigestMethod.CRC32C)
        val mailAddress: String

        @AsStringHash(DigestMethod.SHA1)
        val socialSecurityNumber: String

        @AsStringMask(minLength = 10, maxLength = 10)
        val password: Array<Char>
    }

    private open class Person : PersonallyIdentifiableData {
        @AsStringMask(startMaskAt = 0, endMaskAt = 0)
        override val phoneNumber: String = "06123456789"

        @AsStringReplace("""(.*)""", """$1""")
        override val iban: String = "NL29 ABNA 6708 40 7906"

        @AsStringOmit
        override val mailAddress: String = "someone@example.com"

        @AsStringHash(DigestMethod.JAVA_HASHCODE)
        override val socialSecurityNumber: String = "617247018"

        //@formatter:off
        @AsStringMask(minLength = 0, maxLength = Int.MAX_VALUE, startMaskAt = 0, endMaskAt = Int.MAX_VALUE)
        override val password: Array<Char> =
            arrayOf('m', 'y', ' ', 'v', 'e', 'r', 'y', ' ', 's', 'e', 'c', 'r', 'e', 't', ' ', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd')
        //@formatter:on

        override fun toString(): String = asString()
    }

    private class SpecialPerson: Person() {
        // "iban=NL99 BANK *****0 7906"
        @AsStringReplace("""\s+""", "_")
        @AsStringReplace("^NL", """XX""")
        override val iban: String = super.iban

        @AsStringMask(startMaskAt = -1, mask = 'x')
        override val phoneNumber: String = super.phoneNumber
        override fun toString(): String = asString()
    }

    private open class RepeatedAnnotations {
        @AsStringReplace("^I", "It")
        @AsStringReplace("triple ", "")
        @AsStringReplace("$", " three times")
        open val tripleReplaced = "I will be triple replaced"

        override fun toString(): String = asString()
    }

    private class SubOfRepeatedAnnotations: RepeatedAnnotations() {
        @AsStringReplace("$", "!!!")
        override val tripleReplaced = super.tripleReplaced

        override fun toString(): String = asString()
    }

    class NestedPublicClassWithPublicCompanion {
        val instanceProp = "instance prop"
        companion object CompObjectName {
            var companionProp = "companion prop"
        }
    }

    protected open class NestedProtectedClassWithPublicCompanion {
        val instanceProp = "instance prop"
        companion object CompObjectName {
            var companionProp = "companion prop"
        }
    }

    class NestedPublicClassWithPrivateCompanion {
        val instanceProp = "instance prop"
        private companion object CompObjectName {
            var companionProp = "companion prop"
        }
    }

    class NestedPublicClassWithProtectedCompanion {
        val instanceProp = "instance prop"
        protected companion object CompObjectName {
            var companionProp = "companion prop"
        }
    }

    private class NestedPrivateClassWithCompanion {
        val instanceProp = "instance prop"
        companion object CompObjectName {
            var companionProp = "companion prop"
        }
    }

    @AsStringOption(surroundPropValue = `¶¶`)
    @AsStringClassOption(includeIdentityHash = true)
    class WithAnnotationsAtClassAndUnannotatedCompanion {
        val instanceProp = "instance prop"
        companion object CompObjectName {
            var companionProp = "companion prop"
        }
    }

    @AsStringOption(surroundPropValue = `¶¶`)
    @AsStringClassOption(includeIdentityHash = true, sortNamesAlphabetic = true)
    class WithPropsAndCompanionPropsAndAnnotations {

        val instanceProp = "instance prop"
        @AsStringOption(surroundPropValue = `¦¦`)
        val instancePropAnnotated = "instance prop annotated"

        @AsStringOption(surroundPropValue = PropertyValueSurrounder.`§§`)
        @AsStringClassOption(includeIdentityHash = false)
        companion object {
            var companionProp = "companion prop"
            @AsStringOption(surroundPropValue = PropertyValueSurrounder.`«»`)
            var companionPropAnnotated = "companion prop annotated"
        }
    }

    class WithEmptyCompanion {
        val instanceProp = "instance prop"
        companion object MyEmptyCompanion
    }

    class NoCompanionProperties {
        val instanceProp = "instance prop"
        companion object {
            fun something() = "some thing"
        }
    }
}

private class PrivateClassWithCompanion {
    val instanceProp = "instance prop"
    companion object {
        var companionProp = "companion prop"
    }
}

// endregion
