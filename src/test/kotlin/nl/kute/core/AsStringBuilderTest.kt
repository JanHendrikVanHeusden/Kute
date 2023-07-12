package nl.kute.core

import nl.kute.base.GarbageCollectionWaiter
import nl.kute.base.ObjectsStackVerifier
import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.namedvalues.namedVal
import nl.kute.core.weakreference.ObjectWeakReference
import nl.kute.hashing.DigestMethod
import nl.kute.util.hexHashCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

const val showNullAs = "`null`"
const val showNullAs2 = "[null]"

internal class AsStringBuilderTest: ObjectsStackVerifier {

    private val testObj = ClassWithHashProperty()
    private val testSubObj = SubClassWithPrintMask()
    private val hashCode = ClassWithHashProperty().hashProperty.hexHashCode()

    @Test
    fun `AsStringBuilder without adjustments should give same result as AsString`() {
        // arrange
        val expected = "ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property)"
        // act, assert
        assertThat(testObj.asString()).isEqualTo(expected)
        assertThat(testObj.asStringBuilder().asString()).isEqualTo(expected)

        // arrange
        val expectedSub = "SubClassWithPrintMask(nullable=$showNullAs2, replaced=xx is replaced, hashProperty=#$hashCode#)"
        // act, assert
        assertThat(testSubObj.asString()).isEqualTo(expectedSub)
        assertThat(testSubObj.asStringBuilder().asString()).isEqualTo(expectedSub)
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

        // arrange
        val expectedSub = "SubClassWithPrintMask(replaced=xx is replaced, hashProperty=#$hashCode#)"
        // act, assert
        val asStringSub = testSubObj.asStringBuilder()
            .exceptPropertyNames("nullable", "privateProp")
            .asString()
        assertThat(asStringSub)
            .isEqualTo(expectedSub)
    }

    @Test
    fun `exceptPropertyNames shouldn't filter out named values`() {
        // arrange
        val namedProp = testObj.namedVal(testSubObj::hashProperty)
        val namedVal = "I call myself privateProp".namedVal(name = "privateProp")
        val expected = "ClassWithHashProperty(nullable=$showNullAs, hashProperty=#$hashCode#, privateProp=${namedVal.valueString})"
        // act, assert
        val asString = testObj.asStringBuilder()
            .exceptPropertyNames("privateProp", "hashProperty")
            .withAlsoNamed(namedProp, namedVal)
            .asString()
        assertThat(asString).isEqualTo(expected)

        // arrange
        val expectedSub = "SubClassWithPrintMask(replaced=xx is replaced, hashProperty=#$hashCode#)"
        // act, assert
        val asStringSub = testSubObj.asStringBuilder()
            .exceptPropertyNames("nullable", "privateProp")
            .asString()
        assertThat(asStringSub)
            .isEqualTo(expectedSub)
    }

    @Test
    fun `AsStringBuilder should honour withAlsoProperties`() {
        // arrange
        val expected = "ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property, replaced=xx is replaced)"
        // act, assert
        val asString = testObj.asStringBuilder()
            .withAlsoProperties(testSubObj::replaced)
            .asString()
        assertThat(asString).isEqualTo(expected)
    }

    @Test
    fun `AsStringBuilder should honour withAlsoNamed`() {
        // arrange
        val expected = "ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property, nullable=$showNullAs2, I am a named value=some string)"
        val namedProp = testSubObj.namedVal(testSubObj::nullable)
        val namedValue = "some string".namedVal("I am a named value")
        // act, assert
        val asString = testObj.asStringBuilder()
            .withAlsoNamed(namedProp, namedValue)
            .asString()
        assertThat(asString).isEqualTo(expected)
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
        // arrange
        val expected = "SubClassWithPrintMask(replaced=xx is replaced, hashProperty=#$hashCode#)"
        // act, assert
        val asString = testSubObj.asStringBuilder()
            .withOnlyPropertyNames("replaced", "hashProperty")
            .asString()
        assertThat(asString).isEqualTo(expected)
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

        // nullify any references wihtin the test, so eligible to garbage collection
        // NB: builder is NOT nullified, the test should prove that it doesn't prevent garbage collection
        objRefProperty = null
        toBeGarbageCollected = null

        // assert
        GarbageCollectionWaiter.waitUntilGarbageCollected({objectWeakReference.get() == null})
        // assume the condition
        // * if condition met, the test will be marked as success
        // * if condition not met, `assumeThat` will mark the test as ignored
        assumeThat(objectWeakReference.get())
            .`as`(GarbageCollectionWaiter.explanationOnFailGcTest)
            .isNull()
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