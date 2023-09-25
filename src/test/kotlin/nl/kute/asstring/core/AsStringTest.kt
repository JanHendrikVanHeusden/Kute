package nl.kute.asstring.core

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.modify.AsStringMask
import nl.kute.asstring.annotation.modify.AsStringOmit
import nl.kute.asstring.annotation.modify.AsStringReplace
import nl.kute.asstring.annotation.option.AsStringClassOption
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.annotation.option.PropertyValueSurrounder
import nl.kute.asstring.annotation.option.ToStringPreference.PREFER_TOSTRING
import nl.kute.asstring.annotation.option.asStringClassOptionCache
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringClassOption
import nl.kute.asstring.config.restoreInitialAsStringOption
import nl.kute.asstring.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.asstring.core.test.helper.equalSignCount
import nl.kute.asstring.core.test.helper.isObjectAsString
import nl.kute.asstring.namedvalues.NamedSupplier
import nl.kute.asstring.namedvalues.NamedValue
import nl.kute.asstring.namedvalues.namedProp
import nl.kute.asstring.namedvalues.namedSupplier
import nl.kute.asstring.property.propsWithAnnotationsCacheByClass
import nl.kute.hashing.DigestMethod
import nl.kute.reflection.util.simplifyClassName
import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.testobjects.java.JavaClassToTest
import nl.kute.testobjects.java.JavaClassWithStatic
import nl.kute.testobjects.java.packagevisibility.JavaClassWithPackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.KotlinSubSubClassOfJavaClassWithAccessiblePackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.SubClassOfJavaClassWithAccessiblePackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.sub.KotlinSubSubClassOfJavaClassWithNotAccessiblePackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.sub.SubClassOfJavaClassWithNotAccessiblePackageLevelProperty
import nl.kute.testobjects.java.protectedvisibility.JavaClassWithProtectedProperty
import nl.kute.testobjects.java.protectedvisibility.KotlinSubSubClassOfJavaJavaClassWithProtectedProperty
import nl.kute.testobjects.java.protectedvisibility.SubClassOfJavaClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.ClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.SubClassOfClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.SubSubClassOfClassWithProtectedProperty
import nl.kute.util.identityHashHex
import nl.kute.exception.throwableAsString
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.util.Date
import java.util.Stack
import java.util.UUID
import java.util.concurrent.ArrayBlockingQueue

class AsStringTest: ObjectsStackVerifier {

    private var classLevelCounter = 0

    @BeforeEach
    fun setUp() {
        classLevelCounter = 0
    }

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
        restoreInitialAsStringOption()
        propsWithAnnotationsCacheByClass.reset()
        asStringClassOptionCache.reset()
    }

    @Test
    fun `properties with loooooooooooong values should be capped at 500 chars`() {
        val longStr = RandomStringUtils.randomAlphabetic(800)
        assertThat(ClassToPrint(longStr, 1, null).toString().length)
            .isEqualTo(ClassToPrint("", 1, null).toString().length + 500 + "...".length)
    }

    @Test
    fun `extension object should be rendered correctly`() {
        // arrange
        val classToPrint = ClassToPrint("test", 10, aPrintableDate)
        // act
        val toString = classToPrint.toString()
        // assert
        assertThat(toString)
            .isObjectAsString(
                "ClassToPrint",
                "greet=hallo",
                "num=10",
                "privateToPrint=AsStringTest\$aPrintableDate$1()",
                "str=test",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027",
            )

        // arrange, act
        val asStringProducer = classToPrint.asStringBuilder().exceptProperties(ClassToPrint::num).build()
        // assert
        val asString = asStringProducer.asString()
        assertThat(asString)
            .isObjectAsString(
                "ClassToPrint",
                "greet=hallo",
                "privateToPrint=AsStringTest\$aPrintableDate$1()",
                "str=test",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027",
            )

        // assert that it works on anonymous class
        assertThat(extensionObject.toString())
            .doesNotContain(
                "privateToPrint", "this is another printable", // excluded properties
                "greet=hallo", "privateToPrint=this is another printable" // private properties
            )
            .contains(
                "str=a string",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027",
                "extensionProperty=my extension property",
                "num=80", // overridden value
            )
            .`is`(equalSignCount(4))

        // arrange
        classToPrint.num = 20
        // assert that updated value is there
        // "ClassToPrint(greet=hallo, num=20, privateToPrint=2022-01-27, str=test, uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027)"
        assertThat(classToPrint.toString())
            .isObjectAsString(
                "ClassToPrint",
                "greet=hallo",
                "num=20",
                "privateToPrint=AsStringTest\$aPrintableDate$1()",
                "str=test",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027",
            )
            .`is`(equalSignCount(5))
    }

    @Test
    fun `asString should not break with Mockito mock`() {
        val testMock: ClassToPrint = mock(arrayOf(WithNum::class)) {
            on { num } doReturn 35
            on { asString() } doReturn "mock as String"
        }
        assertThat(testMock.asString()).isEqualTo("mock as String")
    }

    @Test
    fun `Kotlin subclass of Java class should be rendered as expected`() {
        // num is private in the super class, so not included in the subclass asString()
        val kotlinSubClass = KotlinClassToTest("my str", 35, "this is another", people)
        assertThat(JavaClassToTest::class.java.isAssignableFrom(kotlinSubClass.javaClass))
        assertThat(kotlinSubClass.toString())
            .isObjectAsString(
                "KotlinClassToTest(",
                "anotherStr=this is another",
                "names=${people.contentDeepToString()}",
            )
    }

    @Test
    fun `overriding of non-repeatable annotations should not be honoured`() {
        val person = Person()
        val personString = person.toString()
        assertThat(personString)
            // according to annotations on interface properties, not on subclass;
            // so the "overriding" annotations are not honoured
            .startsWith("Person(")
            .contains("iban=NL99 BANK *****0 7906")
            .contains("phoneNumber=06123***789")
            .contains("password=**********")
            .matches(""".+?\bsocialSecurityNumber=#[a-f0-9]{40}\b#.*""")
            // according to AsStringOmit annotation on subclass
            .doesNotContain("mailAddress")
            .`is`(equalSignCount(4))
    }

    @Test
    fun `repeatable annotations on sub-property should be applied after those of the super-property, in order`() {
        val specialPerson = SpecialPerson()
        val personString = specialPerson.toString()
        assertThat(personString)
            .contains("phoneNumber=06123***78x")
            .contains("iban=XX99_BANK_*****0_7906")
            .`is`(equalSignCount(4))
    }

    @Test
    fun `Repeated annotations should be honoured in order, also honour sub-property annotations`() {
        assertThat(RepeatedAnnotations().toString())
            .isEqualTo("RepeatedAnnotations(tripleReplaced=It will be replaced three times)")
        assertThat(SubOfRepeatedAnnotations().toString())
            .isEqualTo("SubOfRepeatedAnnotations(tripleReplaced=It will be replaced three times!!!)")
    }

    @ParameterizedTest
    @ValueSource(strings = ["value", "prop"])
    fun `each call of asString with NamedXxx should evaluate the mutable value`(namedValueType: String) {
        // arrange
        val valueName = "classLevelCounter"
        val namedProp = ::classLevelCounter.namedProp(this)

        val mapOfNamedValues = mapOf(
            // We can simply use the same NamedProp every time, it will evaluate the property value at runtime
            "prop" to { namedProp },
            // A new NamedValue needs to be constructed on every call, because it simply stores the value at time of construction.
            // If you don't like that, use `NamedProp` or `NamedSupplier` instead
            "value" to { NamedValue(valueName, classLevelCounter) }
        )
        val namedXxx = mapOfNamedValues[namedValueType]!!

        class TestClass {
            override fun toString(): String = asStringBuilder().withAlsoNamed(namedXxx()).asString()
        }
        assertThat(classLevelCounter).isZero
        val testObj = TestClass()

        classLevelCounter = 0
        repeat(3) {
            // arrange
            classLevelCounter++
            // act
            val asString = testObj.toString()

            // assert
            assertThat(asString)
                .`as`("Should honour changed value")
                .matches("^.+\\b$valueName=$classLevelCounter\\D")
        }
    }

    @Test
    fun `each call of asString with NamedSupplier should evaluate the value exactly once`() {
        // arrange
        val counterName = "counter"
        var counter = 0

        val supplier = {
            // Don't do this normally! A Supplier should not have side effects!
            // It's just for testing purposes, to verify that it's called only once during asString() processing
            ++counter
        }
        val namedSupplier = supplier.namedSupplier(counterName) as NamedSupplier<Int>

        counter = 0

        open class TestClass {
            val asStringProducer = asStringBuilder()
                .withAlsoNamed(namedSupplier)
                .build()
            override fun toString(): String {
                return asStringProducer.asString()
            }
        }
        assertThat(counter).isZero
        val testObj = TestClass()

        // act
        var asString = testObj.toString()

        // assert
        assertThat(asString)
            .`as`("Supplier expression should be retrieved only once during processing")
            .matches("^.+\\b$counterName=1\\D")
        assertThat(counter).isEqualTo(1)

        // act
        asString = testObj.toString()
        // assert
        assertThat(asString)
            .`as`("")
            .matches("^.+\\b$counterName=2\\D")
        assertThat(counter).isEqualTo(2)

        // arrange
        counter = 0
        // act
        asString = testObj.asStringBuilder()
            .exceptPropertyNames(counterName)
            .withAlsoNamed(namedSupplier)
            .build()
            .asString()
        // assert
        assertThat(asString).matches("^.+\\b$counterName=1\\D")
        assertThat(counter)
            .`as`("propertyNamesToExclude should exclude properties only, not named values")
            .isEqualTo(1)

        // arrange
        counter = 0
        class SubTestClass(var aProp: String = "a Prop"): TestClass()

        with(SubTestClass()) {
            val namedProp = this::aProp.namedProp(this)
            // act
            asString = this.asStringBuilder()
                .exceptPropertyNames(aProp, counterName)
                .withAlsoNamed(namedProp, namedSupplier)
                .asString()
            // assert
            assertThat(asString)
                .matches("^.+\\b${namedProp.name}=${this.aProp}\\D.*")
                .matches("^.+\\b$counterName=1\\D.*")
            assertThat(counter)
                .`as`("propertyNamesToExclude should exclude properties only, not named values")
                .isEqualTo(1)
        }
        // arrange
        counter = 0
        // act - not excluding "counter"
        asString = testObj.asStringBuilder()
            .exceptPropertyNames("count")
            .withAlsoNamed(namedSupplier)
            .asString()
        // assert
        assertThat(asString).matches("^.+\\b$counterName=1\\D")
        assertThat(counter).isEqualTo(1)
    }

    /**
     * This test demonstrates the behaviour with accessible or inaccessible package level properties
     * with Java classes, and Java and Kotlin subclasses of these.
     * As Kute is meant for Kotlin, the behaviour with Java is taken as a matter of fact:
     * Kotlin does not know or honour package visibility.
     *
     * So this test demonstrates the behaviour rather than prescribing it; and it demonstrates that
     * at least Kute can be used properly in conjunction with Java.
     */
    @Test
    fun `package visiblity of variables of Java class should be honoured`() {
        // arrange
        val objToTest = JavaClassWithPackageLevelProperty()
        val subObjToTestNotAccessibleProp = SubClassOfJavaClassWithNotAccessiblePackageLevelProperty()
        val subSubObjToTestNotAccessibleProp = KotlinSubSubClassOfJavaClassWithNotAccessiblePackageLevelProperty()

        val subObjToTestAccessibleProp = SubClassOfJavaClassWithAccessiblePackageLevelProperty()
        val subSubObjToTestAccessibleProp = KotlinSubSubClassOfJavaClassWithAccessiblePackageLevelProperty()

        val packLevelAttrOutput = "myPackageLevelAttribute=my package level attribute"
        val publicAttrOutput = "myPublicAccessibleString=my public accessible String"

        // act, assert
        assertThat(objToTest.asString())
            .contains(publicAttrOutput)

        assertThat(objToTest.asString())
            .`as`("Package level attribute is included in class where it is defined")
            .contains(packLevelAttrOutput)

        listOf(
            subObjToTestNotAccessibleProp,
            subSubObjToTestNotAccessibleProp,
            subObjToTestAccessibleProp,
            subSubObjToTestAccessibleProp
        ).forEach {
            assertThat(it.asString())
                .`as`("public instance variable should be shown in subclass ${it::class.simpleName} output")
                .contains(publicAttrOutput)
                .`as`("Package level attribute is not shown for ${it::class.simpleName}," +
                        " Kotlin regards this as private even when accessible in Java class")
                .doesNotContain(packLevelAttrOutput)
        }
    }

    /**
     * This test demonstrates the behaviour with accessible or inaccessible protected properties
     * with Java classes, and Java and Kotlin subclasses of these.
     * As Kute is meant for Kotlin, the behaviour with Java is taken as a matter of fact:
     * Kotlin handles protected properties differently (getter accessible from subclass, property itself not accessible).
     *
     * So this test demonstrates the behaviour rather than prescribing it; and it demonstrates that
     * at least Kute can be used properly in conjunction with Java.
     */
    @Test
    fun `protected visibility of variables of Java class should be honoured`() {
        // arrange
        val objToTest = JavaClassWithProtectedProperty()
        val subObjToTestProtectedProp = SubClassOfJavaClassWithProtectedProperty()
        val subSubObjToTestProtectedProp = KotlinSubSubClassOfJavaJavaClassWithProtectedProperty()

        val protectedAttrOutput = "myProtectedAttribute=my protected attribute"
        val publicAttrOutput = "myPublicAccessibleString=my public accessible String"

        // act, assert
        assertThat(objToTest.asString())
            .contains(publicAttrOutput)

        assertThat(objToTest.asString())
            .`as`("Protected attribute is included in class where it is defined")
            .contains(protectedAttrOutput)

        listOf(
            subObjToTestProtectedProp,
            subSubObjToTestProtectedProp
        ).forEach {
            assertThat(it.asString())
                .`as`("public instance variable should be shown in subclass ${it::class.simpleName} output")
                .contains(publicAttrOutput)
                .`as`("Protected attribute shown in subclass ${it::class.simpleName} output" +
                        " because the instance variable is protected (not just the getter)")
                .contains(protectedAttrOutput)
        }
    }

    @Test
    fun `protected visibility of property should be hounoured, so rendered with subclass`() {
        // arrange
        val objToTest = ClassWithProtectedProperty()
        val subObjToTestProtectedProp = SubClassOfClassWithProtectedProperty()
        val subSubObjToTestProtectedProp = SubSubClassOfClassWithProtectedProperty()

        val protectedAttrOutput = "myProtectedAttribute=my protected attribute"
        val publicAttrOutput = "myPublicAccessibleString=my public accessible String"

        // act, assert
        assertThat(objToTest.asString())
            .contains(publicAttrOutput)

        assertThat(objToTest.asString())
            .`as`("Protected attribute is included in class where it is defined")
            .contains(protectedAttrOutput)

        listOf(
            subObjToTestProtectedProp,
            subSubObjToTestProtectedProp
        ).forEach {
            assertThat(it.asString())
                .`as`("public instance variable should be shown in subclass ${it::class.simpleName} output")
                .contains(publicAttrOutput)
                .`as`("Protected attribute shown in subclass ${it::class.simpleName} output")
                .contains(protectedAttrOutput)
        }
    }

    @Test
    fun `Java and Kotlin types should adhere to their original toString`() {
        // arrange
        asStringClassOptionCache.reset()
        propsWithAnnotationsCacheByClass.reset()
        listOf(
            123,
            "123",
            'c',
            Date(),
            StringBuffer("abc"),
            StringBuilder("a builder"),
            LocalDate.now(),
            null,
            this::class,
            this::class.java,
        ).forEach {
            // act, assert
            assertThat(it.asString()).isEqualTo(it.toString())
        }
        // should not be cached
        assertThat(propsWithAnnotationsCacheByClass.size).isZero
        assertThat(asStringClassOptionCache.size).isZero
    }

    @Test
    fun `system classes without overridden toString should yield output with class name`() {
        // arrange
        val testObj = Any()
        assumeThat(testObj.toString()).startsWith("java.lang.Object@")
        // act, assert
        assertThat(testObj.asString()).isEqualTo("Any")
    }

    @Test
    fun `system classes without overridden toString should include identity when set`() {
        // arrange
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        val testObj = Any()
        val identityHashHex = testObj.identityHashHex
        // act, assert
        assertThat(testObj.asString()).isEqualTo("Any@$identityHashHex")
        // just to prove that it's the same value
        assumeThat(testObj.toString()).isEqualTo("java.lang.Object@$identityHashHex")
    }

    @Test
    fun `annotations should yield output without package name`() {
        val asStringOption = AsStringOption(showNullAs = "<null>", propMaxStringValueLength = 12, elementsLimit = 83, surroundPropValue = PropertyValueSurrounder.`**`)

        assertThat(asStringOption.asString())
            .isObjectAsString("AsStringOption",
                "showNullAs=<null>",
                "surroundPropValue=**",
                "propMaxStringValueLength=12",
                "elementsLimit=83"
            )

        // just to show the difference
        assumeThat(asStringOption.toString())
            .isObjectAsString(
                "@${AsStringOption::class.java.packageName}.AsStringOption",
                "showNullAs=<null>",
                "surroundPropValue=**",
                "propMaxStringValueLength=12",
                "elementsLimit=83"
            )
    }

    @Test
    fun `collections should yield same output as default toString`() {
        // AsString mimics toString() output of collection-like Java and Kotlin built-in types.
        //
        // This test succeeds with some (arbitrarily chosen) Java and Kotlin built in types
        // (Map, List, Queue, ArrayBlockingQueue, Stack); in other tests also Array, IntArray, etc.
        // Not tested all, though: there are 30+ of them.
        //
        // Non-Java/Kotlin collections, e.g. from Google (Guave) or Apache, are not tested.

        val map = hashMapOf(1 to "first", 2 to "second", 3 to "third")
        assertThat(map.asString())
            .isEqualTo(map.toString())
            .isEqualTo("{1=first, 2=second, 3=third}")

        val list = listOf("one", "two", "three")
        assertThat(list.asString())
            .isEqualTo(list.toString())
            .isEqualTo("[one, two, three]")

        val queue = ArrayBlockingQueue<String>(20)
        queue.addAll(listOf("one", "two", "three"))
        assertThat(queue.asString())
            .isEqualTo(queue.toString())
            .isEqualTo("[one, two, three]")

        val stack = Stack<String>()
        stack.push("one")
        stack.push("two")
        stack.push("three")
        assertThat(stack.asString())
            .isEqualTo(stack.toString())
            .isEqualTo("[one, two, three]")
    }

    @Test
    fun `null asString should yield 'null'`() {
        assertThat(null.asString()).isEqualTo("null")
    }

    @Test
    fun `asString should honour provided properties and their annotations`() {
        @Suppress("unused")
        open class TestClass {
            @AsStringMask(endMaskAt = 1)
            val prop1: String = "prop1"
            val prop2: Date = java.sql.Date.valueOf(LocalDate.of(2022, 6, 14))
            @AsStringOmit
            val prop3: Int = 3
            val prop4: LocalDate = LocalDate.of(2023, 7, 15)
        }
        class SubClass: TestClass() {
            val prop5: Exception = object: IllegalArgumentException("that's wrong") {
                override fun toString(): String = throwableAsString()
            }
            override fun toString(): String = asString()
        }

        val testObj = SubClass()
        assertThat(testObj.toString())
            .startsWith("SubClass(")
            .contains(
                "prop1=*rop1",
                "prop2=${testObj.prop2}",
                "prop4=${testObj.prop4}",
                "prop5=${testObj.prop5.throwableAsString().lines().first()}"
            ).doesNotContain("prop3=")
    }

    @Test
    fun `asString should give proper output for nested objects`() {
        // arrange
        @Suppress("CanBeParameter", "unused")
        class MyTestClass(val level: String) {
            val someProp = "some prop at level: $level"
            lateinit var nested: MyTestClass
        }
        val testObj = MyTestClass("outer")
        testObj.nested = MyTestClass("nested")

        // act, assert
        // "MyTestClass(level=outer, nested=MyTestClass(level=nested, nested=null,
        //  someProp=some prop at level: nested), someProp=some prop at level: outer)
        assertThat(testObj.asString())
            .contains(
                "MyTestClass(",
                "level=outer",
                "nested=MyTestClass(",
                "level=nested",
                "nested=null",
                "someProp=some prop at level: nested",
                "someProp=some prop at level: outer"
            )
            .`is`(equalSignCount(6))
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
        assumeThat(testObj.asString(TestClass::prop3, TestClass::prop4, TestClass::prop1))
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
        AsStringConfig().withToStringPreference(PREFER_TOSTRING).applyAsDefault()
        val testObj = ClassWithTestClassProperty()
        assertThat(testObj.asString())
            .isEqualTo("This should not show up when asString() is called on me; This should not show up when asString() is called on me")
            .isEqualTo(testObj.toString())
    }

    @Test
    fun `properties of Kute types should not be rendered`() {
        // arrange
        @Suppress("unused")
        class AClass: AnInterface {
            val aProp = "a property"
            // This Kute stuff should be excluded (not rendered):
            val namedProp = ::aProp.namedProp(this)
            val namedVal = NamedValue("a name for an Int", 25)
            val namedSupplier = { UUID.randomUUID()}.namedSupplier("a UUID")
            val asStringBuilder = asStringBuilder()
                .withAlsoNamed(namedProp, namedVal, namedSupplier)
                .withAlsoProperties(::anInterfaceProp)
        }
        // act, assert
        assertThat(AClass().asString()).isEqualTo("AClass(anInterfaceProp=an interface property, aProp=a property)")
    }

// region ~ Classes etc. to be used for testing

    @AsStringClassOption(sortNamesAlphabetic = true)
    private interface AnInterface {
        val anInterfaceProp: String
            get() = "an interface property"
    }

    private val people: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    private val aPrintableDate = object {
        override fun toString(): String = LocalDate.of(2022, 1, 27).toString()
    }
    private val anotherPrintable = object {
        override fun toString(): String = "this is another printable"
    }

    @Suppress("unused")
    private class TestClass {
        val someProp = "I am a property of ${this::class.simplifyClassName()}"
        override fun toString() = "This should not show up when asString() is called on me"
    }

    @Suppress("unused")
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
    @Suppress("unused")
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

}

// endregion
