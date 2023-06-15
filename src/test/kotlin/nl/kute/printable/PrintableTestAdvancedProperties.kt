@file:Suppress("unused")

package nl.kute.printable

import nl.kute.core.asString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

class PrintableTestAdvancedProperties {

    /** Demonstrates that a non-initialized lateinit var is regarded as null */
    @Test
    fun `test with lateinit vars`() {
        val testObj = ClassWithLateInitVars()
        val initStr = "I am initialized!"
        testObj.myInitializedLateInitVar = initStr

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
        assertThat(testObj.delegate.getCounter).isZero()
        assertThat(testObj.delegate.setCounter).isZero()

        assertThat(testObj.toString()).contains("delegatedProperty=0")
        assertThat(testObj.delegate.getCounter).isEqualTo(1)
        assertThat(testObj.delegate.setCounter).isZero()

        testObj.delegatedProperty = 25
        assertThat(testObj.toString()).contains("delegatedProperty=25")
        assertThat(testObj.delegate.getCounter).isEqualTo(2)
        assertThat(testObj.delegate.setCounter).isEqualTo(1)
    }

    /** Demonstrates that the delegated getter is called implicitly every time */
    @Test
    fun `test with delegate property - 2`() {
        val testObj = ClassWithDelegatedPropertyWithOpFuns(PropertyDelegateWithOperatorFuns())
        assertThat(testObj.delegate.getCounter).isZero()
        assertThat(testObj.delegate.setCounter).isZero()

        assertThat(testObj.toString()).contains("delegatedProperty=0")
        assertThat(testObj.delegate.getCounter).isEqualTo(1)
        assertThat(testObj.delegate.setCounter).isZero()

        testObj.delegatedProperty = 25
        assertThat(testObj.toString()).contains("delegatedProperty=25")
        assertThat(testObj.delegate.getCounter).isEqualTo(2)
        assertThat(testObj.delegate.setCounter).isEqualTo(1)
    }

    /**
     * Demonstrates that
     *  1. the property getter is called implicitly every time
     *  2. the `get()` result is in the output, and not the internal value
     */
    @Test
    fun `test with property getter`() {
        val testObj = ClassWithPropertyGet()
        val halloInternalValue = "hallo internal value"
        val halloValueFromGetter = "hallo from get"

        assertThat(testObj.getCounter).isZero()

        // demonstrate that the internal value is as expected
        val halloProperty = testObj::class.memberProperties.first { it.name == "hallo" }
        val javaField = halloProperty.javaField!!
        javaField.trySetAccessible()
        assertThat(javaField.get(testObj)).isEqualTo(halloInternalValue)

        // demonstrate that the value from the property's get() method is used in the output, and not the internal value
        assertThat(testObj.asString()).contains(halloValueFromGetter)
        assertThat(testObj.asString()).doesNotContain(halloInternalValue)
        assertThat(testObj.getCounter).isEqualTo(2)
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

        var getCounter: Int = 0
        var setCounter: Int = 0

        override fun getValue(thisRef: Any, property: KProperty<*>): Int {
            ++getCounter
            return myDelegateVal
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
            ++setCounter
            myDelegateVal = value
        }

        override fun toString(): String = asString()
    }

    private class PropertyDelegateWithOperatorFuns {

        var myDelegateVal = 0

        var getCounter: Int = 0
        var setCounter: Int = 0

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            ++getCounter
            return myDelegateVal
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            ++setCounter
            myDelegateVal = value
        }

        override fun toString(): String = asString()
    }

    private class ClassWithPropertyGet {

        var getCounter: Int = 0

        @Suppress("SuspiciousVarProperty")
        var hallo: String = "hallo internal value"
            get() {
                ++getCounter
                return "hallo from get"
            }
    }

    private class ClassWithCompanionObject {
        var instanceVar: String = "my instance var"

        companion object {
            var companionVar: String = "my companion var"
        }
    }
}
