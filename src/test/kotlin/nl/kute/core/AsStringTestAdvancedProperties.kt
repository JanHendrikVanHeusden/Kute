@file:Suppress("unused")

package nl.kute.core

import nl.kute.base.ObjectsStackVerifier
import nl.kute.config.restoreInitialAsStringClassOption
import nl.kute.core.annotation.option.asStringClassOptionCacheSize
import nl.kute.core.annotation.option.resetAsStringClassOptionCache
import nl.kute.core.property.propertyAnnotationCacheSize
import nl.kute.core.property.resetPropertyAnnotationCache
import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import nl.kute.reflection.simplifyClassName
import nl.kute.testobjects.java.JavaClassWithLambda
import nl.kute.util.identityHashHex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

private typealias DoubleCalculator = (Double, Double) -> Double

class AsStringTestAdvancedProperties: ObjectsStackVerifier {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()

        resetPropertyAnnotationCache()
        resetAsStringClassOptionCache()

        resetStdOutLogger()
    }

    @Test
    fun `uninitialized lateinit vars should be shown as null`() {
        // arrange
        val testObj = ClassWithLateInitVars()
        val initStr = "I am initialized!"
        testObj.myInitializedLateInitVar = initStr

        // act, assert
        assertThat(testObj.asString())
            .`as`("non-initialized lateinit var should be regarded as null")
            .contains("myUninitializedLateInitVar=null")
            .contains("myInitializedLateInitVar=I am initialized!")
    }

    /** Demonstrates that the (implicit) lazy initialization is performed only once */
    @Test
    fun `test with lazy property`() {
        val testObj = ClassWithLazyProp()
        assertThat(ClassWithLazyProp.initializationCounter).isZero

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
        assertThat(testObj.delegate.getAccessCounter).isZero
        assertThat(testObj.delegate.setAccessCounter).isZero

        assertThat(testObj.toString()).contains("delegatedProperty=0")
        assertThat(testObj.delegate.getAccessCounter).isEqualTo(1)
        assertThat(testObj.delegate.setAccessCounter).isZero

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

        assertThat(testObj.getAccessCounter).isZero

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

    @Test
    fun `kotlin synthetic types shouldn't cause exceptions`() {
        // Synthetic types etc. will cause UnsupportedOperationException on some reflective calls
        // The exception is caught, and we have to fall back to a default String representation

        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propertyAnnotationCacheSize).isZero
        assertThat(asStringClassOptionCacheSize).isZero
        val property: KProperty0<Any> = this::aPrintableDate

        // act, assert
        assertThat(property.asString())
            .`as`("Property should yield format class@identityHashCode")
            .isEqualTo("${property::class.simplifyClassName()}@${property.identityHashHex}")

        // Lambdas should not be cached (might explode the caches)
        assertThat(propertyAnnotationCacheSize).isZero
        assertThat(asStringClassOptionCacheSize).isZero

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `kotlin lambda's should yield decent output and should not cause exceptions`() {
        // Lambdas are synthetic types, and will cause UnsupportedOperationException on some reflective calls
        // The exception is caught, and as lambdas have a nice toString method, we are using that
        // It's a pity there seems no viable way to determine that it's a lambda via reflection :-(

        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propertyAnnotationCacheSize).isZero
        assertThat(asStringClassOptionCacheSize).isZero
        //      () -> kotlin.String
        val supplier: () -> String = { "a String supplier" }
        //      (kotlin.Int, kotlin.Int) -> kotlin.Int
        val multiplier: (Int, Int) -> Int = { i, j -> i * j }
        //      (kotlin.Double, kotlin.Double) -> kotlin.Double
        val doubleDivider: DoubleCalculator = { d, e -> d / e }

        listOf(
            supplier,
            { "an other String supplier" },
            multiplier,
            doubleDivider,
            { Any() }
        ).forEachIndexed { i, it ->
            // act, assert
            assertThat(it.asString())
                .`as`("Expression #$i should yield format (...) -> ...")
                .matches("""^\(.*?\) -> .+$""")
        }
        // Lambdas should not be cached (might explode the caches)
        assertThat(propertyAnnotationCacheSize).isZero
        assertThat(asStringClassOptionCacheSize).isZero

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()

        // for completeness, also test it with instance variables
        class KotlinClassWithLambda(val lambda1: () -> Int = { 5 }, val lambda2: () -> Unit = {})
        assertThat(KotlinClassWithLambda().asString())
            .isEqualTo("KotlinClassWithLambda(lambda1=() -> kotlin.Int, lambda2=() -> kotlin.Unit)")
    }

    @Test
    fun `java lambda's should not cause exceptions`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propertyAnnotationCacheSize).isZero
        assertThat(asStringClassOptionCacheSize).isZero

        val withLambda = JavaClassWithLambda()
        val theClassName = withLambda::class.simplifyClassName()
        val intSupplierIdHash = withLambda.intSupplier.identityHashHex
        val intsToStringIdHash = withLambda.intsToString.identityHashHex
        // Something like
        // `JavaClassWithLambda(intSupplier=JavaClassWithLambda$$Lambda$366@73d6d0c, intsToString=JavaClassWithLambda$$Lambda$367@238ad8c)`
        // Not really meaningful, but at least it shouldn't throw exceptions
        val dollar = """\$"""
        println(dollar)
        assertThat(withLambda.asString())
            .matches("""$theClassName\(intSupplier=$theClassName$dollar${dollar}Lambda$dollar[\d]+@$intSupplierIdHash, intsToString=$theClassName$dollar${dollar}Lambda$dollar[\d]+@$intsToStringIdHash\)""")

        // Class itself should be cached, but lambdas not (might explode the caches)
        assertThat(propertyAnnotationCacheSize).isEqualTo(1)
        assertThat(asStringClassOptionCacheSize).isEqualTo(1)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `object with uninitialized lateinit property should yield decent output with AsString`() {
        // arrange
        @Suppress("unused")
        class WithLateinit {
            lateinit var uninitializedStringVar: String
            lateinit var initializedStringVar: String
            init {
                if (LocalDate.now().isAfter(LocalDate.of(2000, 1, 1))) {
                    initializedStringVar = "I am initialized"
                }
            }
            override fun toString(): String = asString()
        }
        // act, assert
        assertThat(WithLateinit().toString())
            .contains("initializedStringVar=I am initialized")
            .contains("uninitializedStringVar=null")
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
    @Suppress("SameReturnValue")
    private data class ClassWithExtensionProperty(val str: String) {
        val String.extensionPropAtClass: String
            get() = "$this; I am a class-level extension property"
    }

    @Suppress("SameReturnValue")
    class ClassWithExtensionProperties {
        @Suppress("MemberVisibilityCanBePrivate")
        val String.classLevelStringExtProp: String
            get() = "$this; I am a class-level extension property"

        val extPropPackageLevel: String =
            "a String that calls a package level prop extension".extensionPropAtPackage

        val extPropClassLevel: String =
            "a String that calls class level prop extension".classLevelStringExtProp
    }

    private val aPrintableDate = object {
        override fun toString(): String = LocalDate.of(2022, 1, 27).toString()
    }

}

private val String.extensionPropAtPackage: String
    get() = "$this; I am a package extension property"
