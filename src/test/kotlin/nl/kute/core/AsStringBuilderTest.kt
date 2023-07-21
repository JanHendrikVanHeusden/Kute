package nl.kute.core

import nl.kute.test.base.GarbageCollectionWaiter
import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.namedvalues.namedProp
import nl.kute.core.namedvalues.namedValue
import nl.kute.core.weakreference.ObjectWeakReference
import nl.kute.hashing.DigestMethod
import nl.kute.test.helper.containsExhaustiveInAnyOrder
import nl.kute.test.helper.equalSignCount
import nl.kute.util.hexHashCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

const val showNullAs = "`null`"
const val showNullAs2 = "[null]"

class AsStringBuilderTest: ObjectsStackVerifier, GarbageCollectionWaiter {

    private val testObj = ClassWithHashProperty()
    private val testSubObj = SubClassWithPrintMask()
    private val hashCode = ClassWithHashProperty().hashProperty.hexHashCode()

    @Test
    fun `AsStringBuilder without adjustments should give same result as AsString`() {
        // ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property)
        assertThat(testObj.asStringBuilder().asString())
            .isEqualTo(testObj.asString())

        // act, assert
        // SubClassWithPrintMask(nullable=$showNullAs2, replaced=xx is replaced, hashProperty=#$hashCode#)
        assertThat(testSubObj.asStringBuilder().asString())
            .isEqualTo(testSubObj.asString())
            .`is`(containsExhaustiveInAnyOrder(
                "nullable=$showNullAs2",
                "replaced=xx is replaced",
                "hashProperty=#$hashCode#",
            ignoreChars = ", ",
            ignorePrefix = "SubClassWithPrintMask(",
            ignoreSuffix = ")"
            ))
    }

    @Test
    fun `AsStringBuilder should honour exceptProperties`() {
        // arrange
        val expected = "ClassWithHashProperty(privateProp=I am a private property)"
        // act, assert
        assertThat(testObj.asStringBuilder()
            .exceptProperties(ClassWithHashProperty::nullable, testObj::hashProperty)
            .asString()
        ).isEqualTo(expected)

        // arrange
        val expectedSub = "SubClassWithPrintMask(replaced=xx is replaced)"
        // act, assert
        assertThat(testSubObj.asStringBuilder()
            .exceptProperties(ClassWithHashProperty::nullable, testObj::hashProperty)
            .asString()
        ).isEqualTo(expectedSub)
    }

    @Test
    fun `AsStringBuilder should honour exceptPropertyNames`() {
        // arrange
        val expected = "ClassWithHashProperty(nullable=$showNullAs)"
        // act, assert
        val asString = testObj.asStringBuilder()
            .exceptPropertyNames("privateProp", ClassWithHashProperty::hashProperty.name, "dummy")
            .asString()
        assertThat(asString).isEqualTo(expected)

        // arrange, act
        // SubClassWithPrintMask(replaced=xx is replaced, hashProperty=#$hashCode#)
        val asStringSub = testSubObj.asStringBuilder()
            .exceptPropertyNames("nullable", "privateProp")
            .asString()
        // assert
        assertThat(asStringSub)
            .contains("SubClassWithPrintMask(", "replaced=xx is replaced", "hashProperty=#$hashCode#")
            .doesNotContain("nullable=", "privateProp=")
            .`is`(equalSignCount(2))
    }

    @Test
    fun `exceptPropertyNames shouldn't filter out named values`() {
        // arrange
        val namedProp = testObj.namedProp(testSubObj::hashProperty)
        val namedVal = "I call myself privateProp".namedValue(name = "privateProp")
        // act
        // ClassWithHashProperty(nullable=$showNullAs, hashProperty=#$hashCode#, privateProp=${namedVal.valueString})
        val asString = testObj.asStringBuilder()
            .exceptPropertyNames("privateProp", "hashProperty")
            .withAlsoNamed(namedProp, namedVal)
            .asString()
        // assert
        assertThat(asString)
            .contains(
                "ClassWithHashProperty(",
                "nullable=$showNullAs",
                "hashProperty=#$hashCode#",
                "privateProp=${namedVal.valueString}"
            )
            .`is`(equalSignCount(3))

        // act
        val asStringSub = testSubObj.asStringBuilder()
            .exceptPropertyNames("nullable", "privateProp")
            .asString()
        // assert
        assertThat(asStringSub)
            .contains("replaced=", "hashProperty=#${hashCode}")
            .doesNotContain("nullable=", "privateProp=")
            .`is`(equalSignCount(2))
    }

    @Test
    fun `AsStringBuilder should honour withAlsoProperties`() {
        // ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property, replaced=xx is replaced)
        val asString = testObj.asStringBuilder()
            .withAlsoProperties(testSubObj::replaced)
            .asString()
        assertThat(asString)
            .contains(
                "ClassWithHashProperty(",
                "hashProperty=#$hashCode#",
                "nullable=$showNullAs",
                "privateProp=I am a private property"
            )
            .endsWith(", replaced=xx is replaced)")
            .`is`(equalSignCount(4))
    }

    @Test
    fun `AsStringBuilder should honour withAlsoNamed`() {
        // arrange
        val namedProp = testSubObj.namedProp(testSubObj::nullable)
        val namedValue = "some string".namedValue("I am a named value")
        // act, assert
        val asString = testObj.asStringBuilder()
            .withAlsoNamed(namedProp, namedValue)
            .asString()
        assertThat(asString)
            .contains(
                "ClassWithHashProperty(",
                "hashProperty=#$hashCode#",
                "nullable=$showNullAs",
                "privateProp=I am a private property",
                "nullable=$showNullAs2"
            )
            .endsWith(", I am a named value=some string)")
            .`is`(equalSignCount(5))
    }

    @Test
    fun `AsStringBuilder should honour withOnlyProperties`() {
        // arrange
        val expected = "ClassWithHashProperty(nullable=$showNullAs)"
        // act, assert
        val asString = testObj.asStringBuilder()
            .withOnlyProperties(ClassWithHashProperty::nullable)
            .asString()
        assertThat(asString).isEqualTo(expected)
    }

    @Test
    fun `withOnlyProperties should not filter out values added by withAlsoNamed or withAlsoProperties`() {
        // arrange
        val testObj = SubClassWithPrintMask()
        assertThat(testObj.asString())
            .contains(
                "SubClassWithPrintMask(",
                "nullable=[null]",
                "replaced=xx is replaced",
                "hashProperty=#9813ac95#)"
            )

        // act, assert
        assertThat(testObj.asStringBuilder()
            .withAlsoProperties(SubClassWithPrintMask::hashProperty)
            .withAlsoNamed("the value".namedValue("name of named value"))
            .withOnlyProperties(SubClassWithPrintMask::replaced)
            .asString())
            .contains(
                "SubClassWithPrintMask(",
                "replaced=xx is replaced",
                "hashProperty=#9813ac95#",
                "name of named value=the value"
            )
            .doesNotContain("nullable=")
            .`is`(equalSignCount(3))
    }
    @Test
    fun `AsStringBuilder should ignore non-matching properties in withOnlyProperties`() {
        // arrange
        val expected = "ClassWithHashProperty(hashProperty=#$hashCode#)"
        // act, assert
        val asString = testObj.asStringBuilder()
            .withOnlyProperties(testSubObj::nullable, SubClassWithPrintMask::nullable, testObj::hashProperty)
            .asString()
        assertThat(asString)
            .`as`("The specified ::nullable is not a property of testObj, so should be ignored")
            .isEqualTo(expected)
    }

    @Test
    fun `AsStringBuilder should honour withOnlyPropertyNames`() {
        // act, assert
        // "SubClassWithPrintMask(replaced=xx is replaced, hashProperty=#$hashCode#)"
        val asString = testSubObj.asStringBuilder()
            .withOnlyPropertyNames("replaced", "hashProperty")
            .asString()
        assertThat(asString)
            .contains("SubClassWithPrintMask(","replaced=xx is replaced", "hashProperty=#$hashCode#")
            .doesNotContain("nullable=")
            .`is`(equalSignCount(2))
    }

    @Test
    fun `AsStringBuilder should yield correctly when no properties included`() {
        // arrange
        val expected = "SubClassWithPrintMask()"
        // act, assert
        assertThat(testSubObj.asStringBuilder()
            .withOnlyProperties() // nothing there
            .withAlsoNamed() // nothing there
            .asString()
        ).isEqualTo(expected)
    }

    @Test
    @Suppress("UNUSED_VALUE")
    fun `AsStringBuilder's shouldn't prevent garbage collection after being built`() {
        // arrange
        // this object will be eligible for garbage collection after builder.build():
        var toBeGarbageCollected: ToBeGarbageCollected? = ToBeGarbageCollected()
        val builder: AsStringBuilder = toBeGarbageCollected.asStringBuilder()
        builder.build()

        @Suppress("UNCHECKED_CAST")
        var objRefProperty: KProperty1<AsStringBuilder, ObjectWeakReference<ToBeGarbageCollected>>? =
            builder::class.memberProperties.first { it.name == "objectReference" } as
                    KProperty1<AsStringBuilder, ObjectWeakReference<ToBeGarbageCollected>>
        objRefProperty!!.isAccessible = true
        val objectWeakReference: ObjectWeakReference<ToBeGarbageCollected> = objRefProperty.get(builder)

        assertThat(objectWeakReference.get()).isSameAs(toBeGarbageCollected)
        val checkGarbageCollected = {objectWeakReference.get() == null}
        assertThat(checkGarbageCollected.invoke()).isFalse

        // nullify any references within the test, so eligible to garbage collection
        // NB: builder is NOT nullified, the test should prove that it doesn't prevent garbage collection
        objRefProperty = null
        toBeGarbageCollected = null

        // assert
        assertGarbageCollected(checkGarbageCollected)
    }

    /////////////////////////////
    // Test classes, objects etc.
    /////////////////////////////
    
    private class ToBeGarbageCollected {
        override fun toString(): String = "not garbage collected yet!"
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
        @AsStringReplace("^I am ", replacement = "xx is ")
        val replaced = "I am replaced"
        @AsStringOption(showNullAs = showNullAs2)
        override val nullable: Any? = super.nullable
    }
}