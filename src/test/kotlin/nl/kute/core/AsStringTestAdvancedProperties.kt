@file:Suppress("unused")

package nl.kute.core

import nl.kute.base.ObjectsStackVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class AsStringTestAdvancedProperties: ObjectsStackVerifier {

    /** Demonstrates that a non-initialized lateinit var is regarded as null */
    @Test
    fun `test with lateinit vars`() {
        // arrange
        val testObj = ClassWithLateInitVars()
        val initStr = "I am initialized!"
        testObj.myInitializedLateInitVar = initStr

        // act, assert
        assertThat(testObj.asString())
            .contains("myUninitializedLateInitVar=null")
            .contains("myInitializedLateInitVar=I am initialized!")
    }

    /** Demonstrates that the (implicit) lazy initialization is performed only once */
    @Test
    fun `test with lazy property`() {
        val testObj = ClassWithLazyProp()
        assertThat(ClassWithLazyProp.initializationCounter).isZero()

        assertThat(testObj.asString()).contains("lazyValue=I am lazily initialized")
        assertThat(ClassWithLazyProp.initializationCounter).isEqualTo(1)

        // 2nd attempt, counter should not be increased because of lazy initialization
        assertThat(testObj.asString()).contains("lazyValue=I am lazily initialized")
        assertThat(ClassWithLazyProp.initializationCounter).isEqualTo(1)
    }

    /** Demonstrates that the delegated getter is called implicitly every time */
    @Test
    fun `test with delegate property - 1`() {
        val testObj = ClassWithDelegatedProperty()
        assertThat(testObj.delegate.getAccessCount).isZero
        assertThat(testObj.delegate.setAccessCount).isZero

        assertThat(testObj.toString()).contains("delegatedProperty=0")
        assertThat(testObj.delegate.getAccessCount).isEqualTo(1)
        assertThat(testObj.delegate.setAccessCount).isZero

        testObj.delegatedProperty = 25
        assertThat(testObj.toString()).contains("delegatedProperty=25")
        assertThat(testObj.delegate.getAccessCount).isEqualTo(2)
        assertThat(testObj.delegate.setAccessCount).isEqualTo(1)
    }

    /** Demonstrates that the delegated getter is called implicitly every time */
    @Test
    fun `test with delegate property - 2`() {
        val testObj = ClassWithDelegatedPropertyWithOpFuns(PropertyDelegateWithOperatorFuns())
        assertThat(testObj.delegate.getAccessCounter).isZero()
        assertThat(testObj.delegate.setAccessCounter).isZero()

        assertThat(testObj.toString()).contains("delegatedProperty=0")
        assertThat(testObj.delegate.getAccessCounter).isEqualTo(1)
        assertThat(testObj.delegate.setAccessCounter).isZero()

        testObj.delegatedProperty = 25
        assertThat(testObj.toString()).contains("delegatedProperty=25")
        assertThat(testObj.delegate.getAccessCounter).isEqualTo(2)
        assertThat(testObj.delegate.setAccessCounter).isEqualTo(1)
    }

    /**
     * Demonstrates that
     *  1. the property getter is called implicitly every time
     *  2. the `get()` result is in the output, and not the internal value
     */
    @Test
    fun `test with property getter`() {
        // arrange
        val testObj = ClassWithPropertyGet()
        val halloInternalValue = "hallo internal value"
        val halloValueFromGetter = "hallo from get"

        assertThat(testObj.getAccessCounter).isZero()

        // demonstrate that the internal value is as expected
        val halloProperty = testObj::class.memberProperties.first { it.name == "hallo" }
        val javaField = halloProperty.javaField!!
        javaField.trySetAccessible()
        assertThat(javaField.get(testObj)).isEqualTo(halloInternalValue)

        // act, assert
        // demonstrate that the value from the property's get() method is used in the output, and not the internal value
        assertThat(testObj.asString()).contains(halloValueFromGetter)
        assertThat(testObj.asString()).doesNotContain(halloInternalValue)
        assertThat(testObj.getAccessCounter).isEqualTo(2)
    }

    /** Demonstrates that companion vars are not included in the output */
    @Test
    fun `test with companion object`() {
        val testObj = ClassWithCompanionObject()

        assertThat(testObj.asString()).isEqualTo("ClassWithCompanionObject(instanceVar=my instance var)")

        val valueOfCompanionVar = ClassWithCompanionObject.companionVar
        assertThat(testObj.asString())
            .doesNotContain("companionVar")
            .doesNotContain(valueOfCompanionVar)
    }

    @Test
    fun `test with extension properties`() {
        // arrange
        val testObj = ClassWithExtensionProperties()

        val extPropClassLevelValue = testObj.extPropClassLevel
        val expectedExtPropClassLevel =
            "a String that calls class level prop extension; I am a class-level extension property"
        assertThat(extPropClassLevelValue).isEqualTo(expectedExtPropClassLevel)

        val extPropPackageLevelValue = testObj.extPropPackageLevel
        val expectedExtPropPackageLevelValue = "a String that calls a package level prop extension; I am a package extension property"
        assertThat(extPropPackageLevelValue).isEqualTo(expectedExtPropPackageLevelValue)

        // act
        val actual = testObj.asString()

        // assert
        assertThat(actual)
            .`as`("""the String with extension property should be shown with its value
                | and with the value provided by the extension property""".trimMargin())
            .contains("extPropClassLevel=$expectedExtPropClassLevel")

        assertThat(actual)
            .`as`("""the String with extension property should be shown with its value
                | and with the value provided by the extension property""".trimMargin())
            .contains("extPropPackageLevel=$expectedExtPropPackageLevelValue")

        assertThat(actual)
            .`as`("The extension property itself should not be shown")
            .doesNotContain("classLevelStringExtProp")

    }

    // ------------------------------------
    // Classes etc. to be used in the tests
    // ------------------------------------

    private class ClassWithLateInitVars {
        lateinit var myUninitializedLateInitVar: String
        lateinit var myInitializedLateInitVar: String
        override fun toString(): String = asString()
    }

    private class ClassWithLazyProp {
        val lazyValue: String by lazy {
            initializationCounter++
            "I am lazily initialized"
        }

        companion object {
            var initializationCounter: Int = 0
        }
    }

    private class ClassWithDelegatedProperty(val delegate: PropertyDelegate = PropertyDelegate()) {
        var delegatedProperty: Int by delegate
        override fun toString(): String = asString()
    }

    private class ClassWithDelegatedPropertyWithOpFuns(
        val delegate: PropertyDelegateWithOperatorFuns = PropertyDelegateWithOperatorFuns()
    ) {
        var delegatedProperty: Int by delegate
        override fun toString(): String = asString()
    }

    private class PropertyDelegate : ReadWriteProperty<Any, Int> {

        var myDelegateVal = 0

        var getAccessCount: Int = 0
        var setAccessCount: Int = 0

        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            ++getAccessCount
            return myDelegateVal
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            ++setAccessCount
            myDelegateVal = value
        }

        override fun toString(): String = asString()
    }

    private class PropertyDelegateWithOperatorFuns {

        var myDelegateVal = 0

        var getAccessCounter: Int = 0
        var setAccessCounter: Int = 0

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            ++getAccessCounter
            return myDelegateVal
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            ++setAccessCounter
            myDelegateVal = value
        }

        override fun toString(): String = asString()
    }

    private class ClassWithPropertyGet {

        var getAccessCounter: Int = 0

        @Suppress("SuspiciousVarProperty", "SameReturnValue")
        var hallo: String = "hallo internal value"
            get() {
                ++getAccessCounter
                return "hallo from get"
            }
    }

    private class ClassWithCompanionObject {
        var instanceVar: String = "my instance var"

        companion object {
            var companionVar: String = "my companion var"
        }
    }
    @Suppress("unused", "SameReturnValue")
    private data class ClassWithExtensionProperty(val str: String) {
        val String.extensionPropAtClass: String
            get() = "$this; I am a class-level extension property"
    }

    @Suppress("unused", "SameReturnValue")
    class ClassWithExtensionProperties {
        @Suppress("MemberVisibilityCanBePrivate")
        val String.classLevelStringExtProp: String
            get() = "$this; I am a class-level extension property"

        val extPropPackageLevel: String =
            "a String that calls a package level prop extension".extensionPropAtPackage

        val extPropClassLevel: String =
            "a String that calls class level prop extension".classLevelStringExtProp
    }
}

private val String.extensionPropAtPackage: String
    get() = "$this; I am a package extension property"

