package nl.kute.core

import nl.kute.config.AsStringConfig
import nl.kute.config.restoreInitialAsStringClassOption
import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringMask
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.option.AsStringClassOption
import nl.kute.core.annotation.option.ToStringPreference
import nl.kute.core.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.core.annotation.option.ToStringPreference.USE_ASSTRING
import nl.kute.core.annotation.option.getAsStringClassOption
import nl.kute.core.namedvalues.namedValue
import nl.kute.core.test.helper.isObjectAsString
import nl.kute.hashing.DigestMethod
import nl.kute.reflection.simplifyClassName
import nl.kute.test.base.ObjectsStackVerifier
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(testObj::class.getAsStringClassOption().toStringPreference)
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
        assertThat(PersonWithToStringCallingAsString::class.getAsStringClassOption().toStringPreference)
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
        assertThat(testObj::class.getAsStringClassOption().toStringPreference)
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
        assertThat(testObj::class.getAsStringClassOption().toStringPreference)
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
        assertThat(testObj::class.getAsStringClassOption().toStringPreference)
            .`as`("${USE_ASSTRING} should apply because it's default")
            .isSameAs(USE_ASSTRING)

        // act, assert
        setDefaultToStringPref(PREFER_TOSTRING)
        assertThat(testObj::class.getAsStringClassOption().toStringPreference)
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
        val namedVal = "Hi!".namedValue("greeting")
        val asStringProducer = this.asStringBuilder()
            .withOnlyProperties(this::iban, this::phoneNumber)
            .withAlsoNamed(namedVal)
            .build()
        override fun toString(): String = asStringProducer.asString()
    }

    private class PersonWithAsStringBuilderAndCustomToString: Person() {
        val namedVal = "Hi!".namedValue("greeting")
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