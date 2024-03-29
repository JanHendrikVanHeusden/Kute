@file:Suppress("unused")

package nl.kute.asstring.core

import nl.kute.asstring.annotation.option.asStringClassOptionCache
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.property.propsWithAnnotationsCacheByClass
import nl.kute.helper.base.ObjectsStackVerifier
import nl.kute.helper.helper.dollar
import nl.kute.helper.helper.isObjectAsString
import nl.kute.logging.logger
import nl.kute.logging.resetStdOutLogger
import nl.kute.reflection.util.simplifyClassName
import nl.kute.testobjects.java.advanced.JavaClassWithAnonymousClass
import nl.kute.testobjects.java.advanced.JavaClassWithCallable
import nl.kute.testobjects.java.advanced.JavaClassWithHigherOrderFunction
import nl.kute.testobjects.java.advanced.JavaClassWithLambda
import nl.kute.testobjects.kotlin.advanced.`Callable Factory With Lambda`
import nl.kute.testobjects.kotlin.advanced.CallableFactory
import nl.kute.testobjects.kotlin.advanced.CallableFactoryWithLambda
import nl.kute.testobjects.kotlin.advanced.KotlinClassWithAnonymousClass
import nl.kute.testobjects.kotlin.advanced.KotlinClassWithAnonymousClassFactory
import nl.kute.testobjects.kotlin.advanced.KotlinClassWithCallable
import nl.kute.testobjects.kotlin.advanced.KotlinClassWithHigherOrderFunction
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

/**
 * Several of the more exotic or advanced types, e.g. anonymous inner classes, Java lambda's,
 * synthetic classes, higher order functions, etc. may be generated "on the fly" and/or
 * cause exceptions in Kotlin reflection; and, there seems no viable and reliable way to detect
 * these types beforehand.
 *
 * So these exceptions need to be handled, and for types generated "on the fly", it is important
 * that these are *NOT* cached, as this could easily blow up the cache ([OutOfMemoryError]'s).
 *
 * Because of this, several tests check the log output, to verify that no unhandled exception occurred;
 * and also check the cache sizes, to verify that these objects do not blow up the cache / impair stability.
 */
@Suppress("ClassWithTooManyDependencies")
class AsStringTestAdvancedObjects: ObjectsStackVerifier {

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()

        propsWithAnnotationsCacheByClass.reset()
        asStringClassOptionCache.reset()

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
            .isObjectAsString(
                "ClassWithLateInitVars",
                "myUninitializedLateInitVar=null",
                "myInitializedLateInitVar=I am initialized!"
            )
    }

    /** Demonstrates that the (implicit) lazy initialization is performed only once */
    @Test
    fun `lazy property should be initialized lazily implicitly once, on rendering by asString`() {
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
    fun `delegated property should be rendered - 1`() {
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
    fun `delegate property should be rendered - 2`() {
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
    fun `property getter return value should be rendered, not the internal fied value`() {
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
    fun `companion object properties should not be included`() {
        val testObj = ClassWithCompanionObject()

        assertThat(testObj.asString()).isEqualTo("ClassWithCompanionObject(instanceVar=my instance var)")

        val valueOfCompanionVar = ClassWithCompanionObject.companionVar
        assertThat(testObj.asString())
            .doesNotContain("companionVar")
            .doesNotContain(valueOfCompanionVar)
    }

    @Test
    fun `extension properties should be rendered as expected`() {
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
    fun `kotlin synthetic types shouldn't cause exceptions and not blow up the cache`() {
        // Synthetic types etc. will cause UnsupportedOperationException on some reflective calls
        // The exception is caught, and we have to fall back to a default String representation

        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero
        val property: KProperty0<Any> = this::aPrintableDate

        // act, assert
        assertThat(property.asString())
            .`as`("Property should yield format class@identityHashCode")
            .isEqualTo("${property::class.simplifyClassName()}@${property.identityHashHex}")

        // Lambdas should not be cached (might explode the caches)
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `kotlin lambda's should yield decent output and should not cause exceptions and not blow up the cache`() {
        // Lambdas are synthetic types, and will cause UnsupportedOperationException on some reflective calls
        // The exception is caught, and as lambdas have a nice toString method, we are using that
        // It's a pity there seems no viable way to determine that it's a lambda via reflection :-(

        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero
        //      () -> String
        val supplier: () -> String = { "a String supplier" }
        //      (Int, Int) -> Int
        val multiplier: (Int, Int) -> Int = { i, j -> i * j }
        //      (Double, Double) -> Double
        val doubleDivider: DoubleCalculator = { d, e -> d / e }

        assertThat(supplier.asString()).isEqualTo("() -> String")

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
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `kotlin class with lambda property should yield decent output and should not cause exceptions and not blow up the cache`() {
        // A lambda that is a property of a class does NOT yield UnsupportedOperationException
        // So it's handled differently from local lambda variables.
        class KotlinClassWithLambda {
            val lambda1: () -> Int = { 5 }
            @Suppress("UNUSED_LAMBDA_EXPRESSION")
            val lambda2: (Int, Double) -> Unit = { _, _ -> { /**/ } }
        }

        val testObj = KotlinClassWithLambda()
        val identityHashHex = testObj.identityHashHex
        assertThat(testObj.asString())
            .isObjectAsString(
                "KotlinClassWithLambda",
                "lambda1=() -> Int",
                "lambda2=(Int, Double) -> Unit"
            )
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        assertThat(testObj.asString())
            .isObjectAsString(
                "KotlinClassWithLambda@$identityHashHex",
                "lambda1=() -> Int",
                "lambda2=(Int, Double) -> Unit"
            )
    }

    @Test
    fun `Kotlin lambda's val should yield decent output`() {
        assertThat(supplier.asString()).isEqualTo("() -> String")
    }

    @Test
    fun `java lambda's should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        val withLambda = JavaClassWithLambda()
        val theClassName = withLambda::class.simplifyClassName()
        val intSupplierLambdaTypeName = withLambda.intSupplier.getLambdaTypeName()
        val intsToStringLambdaTypeName = withLambda.intsToString.getLambdaTypeName()
        // Something like
        // `JavaClassWithLambda(intSupplier=JavaClassWithLambda$$Lambda$396=Supplier<T>() @1adb7478,
        //  intsToString=JavaClassWithLambda$$Lambda$397=BiFunction<T,U,R>() @3ae66c85)`
        // Not super meaningful, but at least it shouldn't throw exceptions
        assertThat(withLambda.asString())
            .matches("""$theClassName\(.+""")
            .matches(""".+intSupplier=$theClassName$dollar${dollar}Lambda$dollar\d+=\Q$intSupplierLambdaTypeName()\E.+""")
            .matches(""".+intsToString=$theClassName$dollar${dollar}Lambda$dollar\d+=\Q$intsToStringLambdaTypeName()\E.+""")

        // Class itself should be cached, but lambdas not (might explode the caches)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

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
            .isObjectAsString(
                "WithLateinit",
                "initializedStringVar=I am initialized",
                "uninitializedStringVar=null"
            )
    }

    @Test
    fun `Java class with anonymous inner class should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        val testObj = JavaClassWithAnonymousClass()
        val prop1IdHash = testObj.propWithAnonymousInnerClass.identityHashHex
        val lambdaTypeName = testObj.propWithLambda.getLambdaTypeName()
        val className = testObj::class.simplifyClassName()

        // act
        val asStringResult = testObj.asString()

        // assert
        // something like JavaClassWithAnonymousClass(propWithAnonymousInnerClass=JavaClassWithAnonymousClass$1@7d3e8655,
        //  propWithLambda=JavaClassWithAnonymousClass$$Lambda$367=AFunctionalInterface() @626abbd0)
        assertThat(asStringResult)
            .matches("""^$className\(.+$""")
            .matches("""^.+?propWithAnonymousInnerClass=$className$dollar\d+@$prop1IdHash.+?$""")
            .matches("""^.+?propWithLambda=$className$dollar${dollar}Lambda$dollar\d+=\Q$lambdaTypeName()\E.+?$""")

        // Only the class should be cached, not the properties (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Java class with Callable should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        val testObj = JavaClassWithCallable()
        val propIdHash = testObj.myCallable.identityHashHex
        val className = testObj::class.simplifyClassName()

        // act
        val asStringResult = testObj.asString()

        // assert
        // something like JavaClassWithCallable(myCallable=JavaClassWithCallable$1@7fc4780b)
        assertThat(asStringResult)
            .matches("""$className\(myCallable=$className$dollar\d+@$propIdHash\)""")

        // Only the class should be cached, not the properties (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `weird names should be honoured`() {
        `Callable Factory With Lambda`()
        val testObj = `Callable Factory With Lambda`().getCallable()
        assertThat(testObj.asString())
            .matches("""${`Callable Factory With Lambda`::class.simpleName}$dollar${dollar}Lambda$dollar\d+=Callable<[A-Z]>\(\)""")
    }

    @Test
    fun `Kotlin class with anonymous inner class should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        repeat(5) {
            val testObj = KotlinClassWithAnonymousClass()
            val lambdaTypeName = testObj.propWithLambda.getLambdaTypeName()
            val asStringResult = testObj.asString()
            val className = testObj::class.simplifyClassName()
            // something like
            // KotlinClassWithAnonymousClass(propWithAnonymousInnerClass=KotlinClassWithAnonymousClass$propWithAnonymousInnerClass$1(),
            //  propWithLambda=KotlinClassWithAnonymousClass$$Lambda$372@1f9e9475)
            assertThat(asStringResult)
                .matches(
                    """$className\(propWithAnonymousInnerClass=$className${dollar}propWithAnonymousInnerClass${dollar}\d+\(\),"""
                            + """ propWithLambda=$className$dollar${dollar}Lambda$dollar\d+=\Q$lambdaTypeName()\E\)"""
                )
        }
        // Only the classes should be cached, not the properties (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(2)
        assertThat(asStringClassOptionCache.size).isEqualTo(2)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Kotlin class with anonymous inner class factory should not cause exceptions and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero
        val testObj = KotlinClassWithAnonymousClassFactory()
        assertThat(testObj.asString()).isNotBlank

        repeat(5) {
            assertThat(testObj.createLambda().asString()).isNotBlank
            assertThat(testObj.createAnonymousInnerClass().asString()).isNotBlank
        }
        // Only the classes should be cached, not the properties (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(2)
        assertThat(asStringClassOptionCache.size).isEqualTo(2)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Kotlin class with Callable should not cause exception`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        val testObj = KotlinClassWithCallable()
        val asStringResult = testObj.asString()
        val className = testObj::class.simplifyClassName()

        // act, assert
        // something like KotlinClassWithCallable(myCallable=KotlinClassWithCallable$myCallable$1())
        assertThat(asStringResult)
            .matches("""$className\(myCallable=$className${dollar}myCallable$dollar\d+\(\)\)""")

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Kotlin class with SAM conversion should not cause exceptions and not blow up the cache`() {
        // SAM = Single Abstract Method. See https://www.baeldung.com/kotlin/sam-conversions
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        repeat(10) {
            val callableFactory = CallableFactoryWithLambda()
            callableFactory.getCallable().asString()
            callableFactory.getCallable().asString()
        }
        // The callables should not be cached (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Kotlin class with Callable object factory should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        val callableFactory = CallableFactory()
        repeat(10) {
            callableFactory.createCallable().asString()
        }
        // Only the class should be cached, not the properties (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Java class with higher order function should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        val withHigherOrder = JavaClassWithHigherOrderFunction()
        repeat(10) {
            assertThat(withHigherOrder.asString()).isNotBlank
            assertThat(withHigherOrder.higherOrderFunction.apply(withHigherOrder.reverseIt)).isNotNull
            assertThat(withHigherOrder.higherOrderFunction.apply(withHigherOrder.toUpper).asString()).isNotNull
        }
        // The higher order functions should not be cached (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `Kotlin class with higher order function should not cause exception and not blow up the cache`() {
        // arrange
        var logMsg = ""
        logger = { msg: String? -> logMsg += msg }

        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero

        val withHigherOrder =
            KotlinClassWithHigherOrderFunction()
        assertThat(withHigherOrder.asString())
            .isObjectAsString(
                "KotlinClassWithHigherOrderFunction(",
                "higherOrderLambda=((String) -> String) -> String",
                "reverseIt=(String) -> String",
                "toUpper=(String) -> String"
            )

        repeat(10) {
            assertThat(withHigherOrder.asString()).isNotBlank
            assertThat(withHigherOrder.higherOrderLambda (withHigherOrder.reverseIt)).isNotNull
            assertThat(withHigherOrder.higherOrderLambda (withHigherOrder.toUpper).asString()).isNotNull
        }
        // The higher order functions should not be cached (might explode the cache)
        assertThat(propsWithAnnotationsCacheByClass.size).isEqualTo(1)
        assertThat(asStringClassOptionCache.size).isEqualTo(1)

        class KotlinClassWithHigherOrderLambda(aDouble: Double, anotherDouble: Double) {
            val higherOrderLambda: ((Double, Double) -> () -> Int) -> () -> Int =
                { doublesToInt -> doublesToInt.invoke(aDouble, anotherDouble) }
        }
        assertThat(KotlinClassWithHigherOrderLambda(1.23456, 2.34567).asString())
            .isEqualTo("KotlinClassWithHigherOrderLambda(higherOrderLambda=((Double, Double) -> () -> Int) -> () -> Int)")

        assertThat(logMsg)
            .`as`("No exception should be logged")
            .isEmpty()
    }

    @Test
    fun `local classes should yield a decent result`() {
        open class LocalTestClass {
            private val propA = "prop A"
            protected val propB = "prop B"
        }
        assertThat(LocalTestClass().asString()).isObjectAsString(
            "LocalTestClass",
            "propA=prop A",
            "propB=prop B"
        )
    }

    @Test
    fun `private nested  classes should yield a decent result`() {
        assertThat(PrivateLocalTestClass().asString()).isObjectAsString(
            "PrivateLocalTestClass",
            "propA=prop A",
            "propB=prop B"
        )
    }

    @Test
    fun `anonymous classes should yield decent output`() {
        // Something like AsStringTestAdvancedObjectsKt$anonymousDoc$1
        val className = anonymousDoc::class.simplifyClassName()
        assertThat(anonymousDoc.asString())
            .isObjectAsString(className,
                "author=Kai",
                "title=A nice article",
                "words=420"
            )
    }

    @Test
    fun `fun interfaces should be handled correctly`() {
        assertThat(isAnswer.asString()).endsWith("Answer<T>()")
    }

    @Test
    fun `properties with type fun interface should be handled correctly`() {
        val testObj = TestClassWithAnswer()
        assertThat(testObj.asString())
            .matches("""TestClassWithAnswer\(answer=TestClassWithAnswer$dollar${dollar}Lambda$dollar\d+=Answer<T>\(\)\)""")
    }


// region ~ Classes, objects, helpers  etc. to be used for testing

    private val modifiersRegex = Regex("^.+ ")

    private fun Any.getLambdaTypeName(): String =
        this::class.java.interfaces.firstOrNull()
            ?.toGenericString()?.simplifyClassName()?.replace(modifiersRegex, "") ?: ""

    internal abstract class Doc(val title: String, val author: String, var words: Long = 0L) {
        abstract fun summary(): String
    }

    private open class PrivateLocalTestClass {
        private val propA = "prop A"
        protected val propB = "prop B"
    }

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

private val anonymousDoc = object : AsStringTestAdvancedObjects.Doc(title = "A nice article", author = "Kai", words = 420) {
    override fun summary() = "Title: <$title> ($words words) By $author"
}

val supplier: () -> String = { "a String supplier" }

private fun interface Answer<T> {
    fun accept(element: T): Boolean
}

// implements fun interface
private val isAnswer = Answer<Int> { i -> i == 42 }

private class TestClassWithAnswer {
    val answer = Answer<Int> { i -> i == 42 }
}

// endregion