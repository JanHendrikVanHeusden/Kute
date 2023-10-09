package nl.kute.asstring.core

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringMask
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.ToStringPreference
import nl.kute.asstring.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.asstring.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.asstring.annotation.option.asStringClassOption
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.namedvalues.NamedValue
import nl.kute.hashing.DigestMethod
import nl.kute.reflection.util.simplifyClassName
import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.test.helper.isObjectAsString
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AsStringPreferToStringTest: ObjectsStackVerifier {
    
    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
        assertThat(AsStringClassOption.defaultOption.toStringPreference).isEqualTo(USE_ASSTRING)
    }

    @Test
    fun `A class annotated with PREFER_TOSTRING but without toString implementation should use asString`() {
        val testObj = WithoutToString()
        assertThat(testObj::class.asStringClassOption().toStringPreference)
            .isSameAs(PREFER_TOSTRING)

        repeat(3) {
            // repeated because subsequent calls skip some processing steps due to caching etc.
            // should give same result each time
            assertThat(testObj.asString())
                .`as`("Should meet condition in iteration $it")
                .isObjectAsString(
                    "WithoutToString",
                    "prop1=${testObj::prop1.get()}",
                    "prop2=${testObj::prop2.get()}"
                )
        }
    }

    @Test
    fun `asString on a class where toString calls asString and with PREFER_TOSTRING should be handled correctly`() {
        assertThat(PersonWithToStringCallingAsString::class.asStringClassOption().toStringPreference)
            .isSameAs(PREFER_TOSTRING)

        repeat(3) {
            // repeated because subsequent calls skip some processing steps due to caching etc.
            // should give same result each time
            assertThat(PersonWithToStringCallingAsString().toString())
                .`as`("Should meet condition in iteration $it")
                .isObjectAsString(
                    "PersonWithToStringCallingAsString",
                    "iban=NL99 BANK *****0 7906",
                    "password=**********",
                    "phoneNumber=06123***789",
                    "socialSecurityNumber=#f1f94451ae5a9b30b187ee18f790fdf5ea9c9b06#"
                )
        }
    }

    @Test
    fun `asString on a class with PREFER_TOSTRING annotation should honour toString`() {
        val testObj = PersonWithToStringImplementation()
        assertThat(testObj::class.asStringClassOption().toStringPreference)
            .isSameAs(PREFER_TOSTRING)

        repeat(3) {
            // repeated because subsequent calls skip some processing steps due to caching etc.
            // should give same result each time
            assertThat(testObj.asString())
                .`as`("Should meet condition in iteration $it")
                .isEqualTo(
                    "PersonWithToStringImplementation(phone='06123456789', iban='NL29 ABNA 6708 40 7906', mail='someone@example.com', BSN='617247018')"
                )
        }
    }

    @Test
    fun `asString on a subclass of a class that is annotated with PREFER_TOSTRING should honour toString`() {
        setDefaultToStringPref(USE_ASSTRING) // should be ignored, class is annotated with PREFER_TOSTRING

        val testObj = SubClassOfPersonWithToStringImplementation()
        assertThat(testObj::class.asStringClassOption().toStringPreference)
            .isSameAs(PREFER_TOSTRING)

        repeat(3) {
            // repeated because subsequent calls skip some processing steps due to caching etc.
            // should give same result each time
            assertThat(testObj.asString())
                .`as`("Should meet condition in iteration $it")
                .isEqualTo(
                    "SubClassOfPersonWithToStringImplementation(phone='06123456789', iban='NL29 ABNA 6708 40 7906', mail='someone@example.com', BSN='617247018')"
                )
            setDefaultToStringPref(PREFER_TOSTRING) // shouldn't make any difference, class is annotated with PREFER_TOSTRING
        }
    }

    @Test
    fun `toString should be called by asString when default is set to PREFER_TOSTRING`() {
        // arrange
        val testObj = PersonWithToString()

        assertThat(testObj::class.annotations.firstOrNull { it is AsStringClassOption })
            .`as`("Demonstrates that ${testObj::class.simplifyClassName()} is not annotated with" +
                    AsStringClassOption::class.simplifyClassName()
            )
            .isNull()
        assertThat(testObj::class.asStringClassOption().toStringPreference)
            .`as`("${USE_ASSTRING} should apply because it's default")
            .isSameAs(USE_ASSTRING)

        // act, assert
        setDefaultToStringPref(PREFER_TOSTRING)
        assertThat(testObj::class.asStringClassOption().toStringPreference)
            .`as`("now ${PREFER_TOSTRING} should apply because of changed default")
            .isSameAs(PREFER_TOSTRING)

        assertThat(testObj.asString())
            .isEqualTo("I am the ${PersonWithToString::class.simplifyClassName()} toString() result")
            .isEqualTo(testObj.toString())

        // act, assert
        setDefaultToStringPref(USE_ASSTRING)
        assertThat(testObj.asString())
            .isNotEqualTo(testObj.toString())
            .isNotEqualTo("I am the ${PersonWithToString::class.simplifyClassName()} toString() result")
    }

    @Test
    fun `AsStringBuilder should be honoured when used inside toString - 1`() {
        setDefaultToStringPref(PREFER_TOSTRING)
        val testObj = PersonWithAsStringBuilder()

        repeat(3) {
            // repeated because subsequent calls skip some processing steps due to caching etc.
            // should give same result each time
            assertThat(testObj.toString())
                .isObjectAsString(
                    "PersonWithAsStringBuilder",
                    "iban=NL99 BANK *****0 7906",
                    "phoneNumber=06123***789",
                    withLastPropertyString = "greeting=Hi!"
                )
                .isEqualTo(PersonWithAsStringBuilder().asStringProducer.asString())
        }
    }

    @Test
    fun `AsStringBuilder should be honoured when used inside toString - 2`() {
        setDefaultToStringPref(PREFER_TOSTRING)
        val testObj = PersonWithAsStringBuilderAndCustomToString()

        repeat(3) {
            // repeated because subsequent calls skip some processing steps due to caching etc.
            // should give same result each time
            assertThat(testObj.toString())
                .isObjectAsString(
                    "${testObj.toStringCustomPrefix}${testObj::class.simplifyClassName()}",
                    "iban=NL99 BANK *****0 7906",
                    "phoneNumber=06123***789",
                    withLastPropertyString = "greeting=Hi!"
                )
        }
    }

    @Test
    fun `asString with prefix should be honoured when called from toString`() {
        val testObj = PersonWithAsStringAndCustomPrefix()
        assertThat(testObj.toString())
            .isObjectAsString(
                "${testObj.toStringCustomPrefix}${testObj::class.simplifyClassName()}",
                "toStringCustomPrefix=My custom toString(): "
                , "iban=NL99 BANK *****0 7906",
                "password=**********",
                "phoneNumber=06123***789"
                , "socialSecurityNumber=#f1f94451ae5a9b30b187ee18f790fdf5ea9c9b06#"
            )
    }

    @Test
    fun `Usage of toString should be cached`() {
        // arrange
        useToStringByClass.reset()
        assertThat(useToStringByClass.cache).isEmpty()
        val randomStringSupplier = { RandomStringUtils.randomAlphanumeric(20) }

        open class TestClass(var something: String = randomStringSupplier())
        // act
        repeat(3) { TestClass(randomStringSupplier()).asString() }
        // assert
        assertThat(useToStringByClass.cache)
            .hasSize(1)
            // it has default, so USE_ASSTRING
            .contains(entry(TestClass::class, USE_ASSTRING))

        // arrange
        @AsStringClassOption(toStringPreference = PREFER_TOSTRING)
        open class SubTestClass(something: String): TestClass(something)
        // act
        repeat(3) { SubTestClass(randomStringSupplier()).asString() }
        // assert
        assertThat(useToStringByClass.cache)
            .hasSize(2)
            .contains(
                // it has default, so USE_ASSTRING
                entry(TestClass::class, USE_ASSTRING),
                // it has PREFER_TOSTRING annotation, but no toString() method
                entry(SubTestClass::class, USE_ASSTRING)
            )

        // arrange
        open class SubSubTestClass(something: String): SubTestClass(something) {
            override fun toString(): String = "I am the sub-sub-Class with a toString"
        }
        // act
        repeat(3) { SubSubTestClass(randomStringSupplier()).asString() }

        // assert
        assertThat(useToStringByClass.cache)
            .hasSize(3)
            .contains(
                // it has default, so USE_ASSTRING
                entry(TestClass::class, USE_ASSTRING),
                // it has PREFER_TOSTRING, but no toString() method
                entry(SubTestClass::class, USE_ASSTRING),
                // it inherits PREFER_TOSTRING, and has a toString() method
                entry(SubSubTestClass::class, PREFER_TOSTRING)
            )

        // arrange
        val testObjectWithToString = object : TestClass() {
            override fun toString(): String = "this object's toString()"
        }
        // act
        repeat(3) { testObjectWithToString.asString() }
        // assert
        assertThat(useToStringByClass.cache)
            .hasSize(4)
            // not annotated, so using default: USE_ASSTRING
            .contains(entry(testObjectWithToString::class, USE_ASSTRING))

        // arrange
        setDefaultToStringPref(PREFER_TOSTRING)
        assertThat(useToStringByClass.cache).isEmpty()
        // act
        repeat(3) { testObjectWithToString.asString() }
        // assert
        assertThat(useToStringByClass.cache)
            .hasSize(1)
            // it has a toString() method, and default is PREFER_TOSTRING, but it's class's simpleName is null,
            // this kind of classes is excluded because it may not be supported by Kotlin's reflection
            .contains(entry(testObjectWithToString::class, USE_ASSTRING))

        // arrange
        class TestClassWithToString: TestClass() {
            override fun toString(): String = "this ${this::class.simpleName}'s toString()"
        }
        // act
        repeat(3) { TestClassWithToString().asString() }
        // assert
        assertThat(TestClassWithToString().asString())
            .isEqualTo("this ${TestClassWithToString::class.simpleName}'s toString()")

        assertThat(useToStringByClass.cache)
            .hasSize(2)
            .contains(
                entry(testObjectWithToString::class, USE_ASSTRING),
                // class feasible for reflection, it has a toString(), and default is PREFER_TOSTRING,
                entry(TestClassWithToString::class, PREFER_TOSTRING)
            )

        // arrange
        class TestClassWithAsString: TestClass() {
            override fun toString(): String = asString()
        }
        // act
        repeat(3) { TestClassWithAsString().asString() }
        val testClassWithAsString = TestClassWithAsString()
        // assert
        assertThat(testClassWithAsString.asString())
            .isObjectAsString(
                TestClassWithAsString::class.simplifyClassName(),
                "something=${testClassWithAsString.something}"
            )

        assertThat(useToStringByClass.cache)
            .hasSize(3)
            .contains(
                // it's toString() calls asString(), so recursion will be detected,
                // so it will be set to USE_ASSTRING
                entry(TestClassWithAsString::class, USE_ASSTRING)
            )
    }

// region ~ Test helpers

    private fun setDefaultToStringPref(preference: ToStringPreference) {
    AsStringConfig()
        .withToStringPreference(preference)
        .applyAsDefault()
    }

// endregion

// region ~ Classes, objects etc. to be used for testing

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

    private class PersonWithToString: Person() {
        override fun toString(): String = "I am the ${this::class.simplifyClassName()} toString() result"
    }

    private class PersonWithAsStringBuilder: Person() {
        val namedVal = NamedValue("greeting", "Hi!")
        val asStringProducer = this.asStringBuilder()
            .withOnlyProperties(this::iban, this::phoneNumber)
            .withAlsoNamed(namedVal)
            .build()
        override fun toString(): String = asStringProducer.asString()
    }

    private class PersonWithAsStringBuilderAndCustomToString: Person() {
        val namedVal = NamedValue("greeting", "Hi!")
        val toStringCustomPrefix = "My custom toString(): "
        val asStringBuilder by lazy {
            this.asStringBuilder()
                .withOnlyProperties(this::iban, this::phoneNumber)
                .withAlsoNamed(namedVal)
        }
        override fun toString(): String = toStringCustomPrefix + asStringBuilder.asString()
    }

    @AsStringClassOption(toStringPreference = PREFER_TOSTRING)
    private class PersonWithAsStringAndCustomPrefix: Person() {
        val toStringCustomPrefix = "My custom toString(): "
        override fun toString(): String = toStringCustomPrefix + asString()

    }

    @AsStringClassOption(toStringPreference = PREFER_TOSTRING)
    private class PersonWithToStringCallingAsString: Person()

    @AsStringClassOption(toStringPreference = PREFER_TOSTRING)
    private open class PersonWithToStringImplementation : Person() {
        override fun toString(): String {
            return "${this::class.simplifyClassName()}(phone='$phoneNumber', iban='$iban', mail='$mailAddress', BSN='$socialSecurityNumber')"
        }
    }

    /** Should inherit [PREFER_TOSTRING] from superclass */
    private class SubClassOfPersonWithToStringImplementation : PersonWithToStringImplementation()

    @AsStringClassOption(toStringPreference = PREFER_TOSTRING)
    private class WithoutToString(val prop1: String = "I am property 1") {
        val prop2 = "As property 2, I am as good as anything else"
    }

// endregion

}