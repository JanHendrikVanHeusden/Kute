package nl.kute.asstring.core

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.namedvalues.NamedProp
import nl.kute.asstring.namedvalues.NamedSupplier
import nl.kute.asstring.namedvalues.NamedValue
import nl.kute.asstring.namedvalues.namedProp
import nl.kute.asstring.weakreference.ObjectWeakReference
import nl.kute.hashing.DigestMethod
import nl.kute.helper.base.GarbageCollectionWaiter
import nl.kute.helper.base.ObjectsStackVerifier
import nl.kute.helper.helper.isObjectAsString
import nl.kute.reflection.util.simplifyClassName
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
            .isObjectAsString(
                "SubClassWithPrintMask",
                "nullable=$showNullAs2",
                "replaced=xx is replaced",
                "hashProperty=#$hashCode#"
            )
    }

    @Test
    fun `AsStringBuilder without effective adjustments should give same result as AsString`() {
        // ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property)
        assertThat(testObj.asStringBuilder().asString())
            .isEqualTo(testObj.asString())

        // act, assert
        // SubClassWithPrintMask(nullable=$showNullAs2, replaced=xx is replaced, hashProperty=#$hashCode#)
        assertThat(testSubObj.asStringBuilder()
            .withAlsoNamed()
            .withAlsoProperties()
            .asString())
            .isEqualTo(testSubObj.asString())
            .isObjectAsString(
                "SubClassWithPrintMask",
                "nullable=$showNullAs2",
                "replaced=xx is replaced",
                "hashProperty=#$hashCode#"
            )
    }

    @Test
    fun `AsStringBuilder should honour exceptProperties`() {
        // arrange, act, assert
        assertThat(testObj.asStringBuilder()
            .exceptProperties(ClassWithHashProperty::nullable, testObj::hashProperty)
            .asString()
        )
            .contains("privateProp=")
            .doesNotContain("nullable=", "hashProperty=")

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
            .doesNotContain("nullable=", "privateProp=")
            .isObjectAsString(
                "SubClassWithPrintMask",
                "replaced=xx is replaced",
                "hashProperty=#$hashCode#",
            )
    }

    @Test
    fun `exceptPropertyNames shouldn't filter out named values`() {
        // arrange
        val namedProp = testObj::hashProperty.namedProp(testObj)
        val namedVal = NamedValue("privateProp", "I call myself privateProp")
        // act
        // ClassWithHashProperty(nullable=$showNullAs, hashProperty=#$hashCode#, privateProp=${namedVal.valueString})
        val asString = testObj.asStringBuilder()
            .exceptPropertyNames("privateProp", "hashProperty")
            .withAlsoNamed(namedProp, namedVal)
            .asString()
        // assert
        assertThat(asString)
            .isObjectAsString(
                "ClassWithHashProperty",
                "nullable=$showNullAs",
                "hashProperty=#$hashCode#",
                "privateProp=${namedVal.value}",
            )

        // act
        val asStringSub = testSubObj.asStringBuilder()
            .exceptPropertyNames("nullable", "privateProp")
            .asString()
        // assert
        assertThat(asStringSub)
            .doesNotContain("nullable=", "privateProp=")
            .isObjectAsString(
                "SubClassWithPrintMask",
                "replaced=xx is replaced",
                "hashProperty=#${hashCode}#",
            )
    }

    @Test
    fun `AsStringBuilder should honour withAlsoProperties`() {
        // ClassWithHashProperty(hashProperty=#$hashCode#, nullable=$showNullAs, privateProp=I am a private property, replaced=xx is replaced)
        val asString = testObj.asStringBuilder()
            .withAlsoProperties(testSubObj::replaced)
            .asString()
        assertThat(asString)
            .isObjectAsString(
                "ClassWithHashProperty",
                "hashProperty=#$hashCode#",
                "nullable=$showNullAs",
                "privateProp=AsStringBuilderTest\$ClassWithHashProperty\$privateProp\$1()",
                withLastPropertyString = "replaced=xx is replaced",
            )
    }

    @Test
    fun `AsStringBuilder should honour withAlsoNamed`() {
        // arrange
        val namedProp = testSubObj::nullable.namedProp(testSubObj)
        val namedValue = NamedValue("I am a named value", "some string")
        // act, assert
        val asString = testObj.asStringBuilder()
            .withAlsoNamed(namedProp, namedValue)
            .asString()
        assertThat(asString)
            .isObjectAsString(
                "ClassWithHashProperty",
                "hashProperty=#$hashCode#",
                "privateProp=AsStringBuilderTest\$ClassWithHashProperty\$privateProp\$1()",
                "nullable=$showNullAs",
                withLastPropertyString = "nullable=$showNullAs2, I am a named value=some string"
            )
    }

    @Test
    fun `AsStringBuilder should use asString() to render a NamedValue`() {
        // arrange
        class MyClass {
            val myProp = "my prop value 1"
        }
        @Suppress("unused")
        class MyClassWithoutToString {
            val myPropNoToString = "my prop value 2"
        }
        val asString = MyClass().asStringBuilder()
            .withAlsoNamed(NamedValue("no toString()", MyClassWithoutToString()))
            .asString()
        assertThat(asString).isObjectAsString(
            MyClass::class.simplifyClassName(),
            "${MyClass::myProp.name}=${MyClass().myProp}",
            withLastPropertyString = "no toString()=${MyClassWithoutToString().asString()}"
        )
    }

    @Test
    fun `AsStringBuilder should use asString() to render a NamedSupplier`() {
        // arrange
        class MyClass {
            val myProp = "my prop value 1"
        }
        @Suppress("unused")
        class MyClassWithoutToString {
            val myPropNoToString = "my prop value 2"
        }
        val asString = MyClass().asStringBuilder()
            .withAlsoNamed(NamedSupplier("no toString()", { MyClassWithoutToString() }))
            .asString()
        assertThat(asString).isObjectAsString(
            MyClass::class.simplifyClassName(),
            "${MyClass::myProp.name}=${MyClass().myProp}",
            withLastPropertyString = "no toString()=${MyClassWithoutToString().asString()}"
        )
    }

    @Test
    fun `AsStringBuilder should use asString() to render a NamedProp`() {
        // arrange
        class MyClass {
            val myProp = "my prop value 1"
        }
        class MyClassWithoutToString {
            val myPropNoToString = MyClass()
        }
        val asString = MyClass().asStringBuilder()
            .withAlsoNamed(NamedProp(MyClassWithoutToString::myPropNoToString, MyClassWithoutToString()))
            .asString()
        assertThat(asString).isObjectAsString(
            MyClass::class.simplifyClassName(),
            "${MyClass::myProp.name}=${MyClass().myProp}",
            withLastPropertyString = "${MyClassWithoutToString::myPropNoToString.name}=${MyClass().asString()}"
        )
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
            .isObjectAsString(
                "SubClassWithPrintMask",
                "nullable=[null]",
                "replaced=xx is replaced",
                "hashProperty=#9813ac95#",
            )

        // act, assert
        assertThat(testObj.asStringBuilder()
            .withAlsoProperties(SubClassWithPrintMask::hashProperty)
            .withAlsoNamed(NamedValue("name of named value", "the value"))
            .withOnlyProperties(SubClassWithPrintMask::replaced)
            .asString())
            .doesNotContain("nullable=")
            .isObjectAsString(
                "SubClassWithPrintMask",
                "replaced=xx is replaced",
                "hashProperty=#9813ac95#",
                withLastPropertyString = "name of named value=the value"
            )
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
            .doesNotContain("nullable=")
            .isObjectAsString(
                "SubClassWithPrintMask",
                "replaced=xx is replaced",
                "hashProperty=#$hashCode#"
            )
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

// region ~ Classes, objects etc. to be used for testing
    
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

// endregion

}