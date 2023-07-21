@file:Suppress("unused")

package nl.kute.core

import nl.kute.test.base.ObjectsStackVerifier
import nl.kute.config.AsStringConfig
import nl.kute.config.restoreInitialAsStringClassOption
import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringMask
import nl.kute.core.annotation.modify.AsStringOmit
import nl.kute.core.annotation.modify.AsStringReplace
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.resetAsStringClassOptionCache
import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.NamedProp
import nl.kute.core.namedvalues.NamedSupplier
import nl.kute.core.namedvalues.NamedValue
import nl.kute.core.namedvalues.namedProp
import nl.kute.core.namedvalues.namedSupplier
import nl.kute.core.namedvalues.namedValue
import nl.kute.core.property.resetPropertyAnnotationCache
import nl.kute.hashing.DigestMethod
import nl.kute.test.helper.equalSignCount
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

    private val people: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    private var classLevelCounter = 0

    @BeforeEach
    fun setUp() {
        classLevelCounter = 0
    }

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringClassOption()
        resetPropertyAnnotationCache()
        resetAsStringClassOptionCache()
    }

    @Test
    fun `properties with loooooooooooong values should be capped at 500 chars`() {
        val longStr = RandomStringUtils.randomAlphabetic(800)
        assertThat(ClassToPrint(longStr, 1, null).toString().length)
            .isEqualTo(ClassToPrint("", 1, null).toString().length + 500)
    }

    @Test
    fun `test with extension object`() {
        // arrange
        val classToPrint = ClassToPrint("test", 10, aPrintableDate)
        // act
        val toString = classToPrint.toString()
        // assert
        assertThat(toString)
            .contains("ClassToPrint(",
                "greet=hallo",
                "num=10",
                "privateToPrint=$aPrintableDate",
                "str=test",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027"
            )
            .`is`(equalSignCount(5))

        // arrange, act
        val asStringProducer = classToPrint.asStringBuilder().exceptProperties(ClassToPrint::num).build()
        // assert
        val asString = asStringProducer.asString()
        assertThat(asString)
            .contains(
                "ClassToPrint(",
                "greet=hallo",
                "privateToPrint=$aPrintableDate",
                "str=test",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027"
            )
            .`is`(equalSignCount(4))

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
            .contains(
                "ClassToPrint(",
                "greet=hallo",
                "num=20",
                "privateToPrint=2022-01-27",
                "str=test",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027)"
            )
            .`is`(equalSignCount(5))
    }

    @Test
    fun `test that asString with Mockito mock does not break`() {
        val testMock: ClassToPrint = mock(arrayOf(Printable::class)) {
            on { num } doReturn 35
            on { asString() } doReturn "mock as String"
        }
        assertThat(testMock.asString()).isEqualTo("mock as String")
        // mocks have their own toString; should just not break
        assertThat(testMock.toString()).isNotNull
    }

    @Test
    fun `test with Kotlin subclass of Java class`() {
        val kotlinSubClass = KotlinClassToTest("my str", 35, "this is another", people)
        assertThat(JavaClassToTest::class.java.isAssignableFrom(kotlinSubClass.javaClass))
        assertThat(kotlinSubClass.toString())
            .contains("KotlinClassToTest(", "anotherStr=this is another", "names=${people.contentDeepToString()}")
    }

    @Test
    fun `overriding of non-repeatable annotations should not be honoured`() {
        val person = Person()
        val personString = person.toString()
        assertThat(personString)
            // according to annotations on interface properties, not on subclass;
            // so the "overriding" annotations are not honored
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
        val namedProp = this.namedProp(::classLevelCounter) as NamedProp<*, Int>

        val mapOfNamedValues: Map<String, () -> NameValue<Int?>> = mapOf(
            // We can simply use the same NamedProp every time, it will evaluate the property value at runtime
            "prop" to { namedProp },
            // A new NamedValue needs to be constructed on every call, because it simply stores the value at time of construction.
            // If you don't like that, use `NamedProp` or `NamedSupplier` instead
            "value" to { classLevelCounter.namedValue(valueName) as NamedValue<Int> }
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
            assertThat(classLevelCounter).isEqualTo(classLevelCounter)
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
            val namedProp = this.namedProp(this::aProp)
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
    fun `output with Java class with package visibility property`() {
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
    fun `output with Java class with protected visibility property`() {
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
    fun `output with Kotlin class with protected visibility property`() {
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
    }

    @Test
    fun `system classes without overridden toString should yield output with class name`() {
        // arrange
        val testObj = Any()
        assumeThat(testObj.toString()).startsWith("java.lang.Object@")
        // act, assert
        assertThat(testObj.asString()).isEqualTo("Any()")
    }

    @Test
    fun `system classes without overridden toString should include identity when set`() {
        // arrange
        AsStringConfig().withIncludeIdentityHash(true).applyAsDefault()
        val testObj = Any()
        val identityHashHex = testObj.identityHashHex
        // act, assert
        assertThat(testObj.asString()).isEqualTo("Any@$identityHashHex()")
        // just to prove that it's the same value
        assumeThat(testObj.toString()).isEqualTo("java.lang.Object@$identityHashHex")
    }

    @Test
    fun `annotations should yield output without package name`() {
        val asStringOption = AsStringOption(showNullAs = "<null>", propMaxStringValueLength = 12)
        assertThat(asStringOption.asString())
            .startsWith("AsStringOption(")
            .contains(
                "propMaxStringValueLength=12",
                "showNullAs=<null>",
            )
            .endsWith(")")
            .`is`(equalSignCount(2))
            .doesNotContain("@")
            .doesNotContain()
        // just to show the difference
        assumeThat(asStringOption.toString())
            .contains("@nl.kute.core.annotation.option.AsStringOption(propMaxStringValueLength=12, showNullAs=<null>)")
    }

    @Test
    fun `collections should yield same output as default toString`() {
        // This applies to several Java and Kotlin built in types. Not tested all (there are 30+ of them)
        // It may not apply to non-Java/Kotlin collections, e.g. from Google (Guave) or Apache (not tested)
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
                override fun toString(): String = asString()
            }
        }
    }

    @Test
    fun `asString should give proper output for nested objects`() {
        // arrange
        @Suppress("CanBeParameter")
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
    fun `asString with Kotlin companion properties does not include companion properties`() {
        val testObj = WithCompanion()
        // not showing static var
        assertThat(testObj.asString())
            .`as`("does by default not contain the companion property")
            .isEqualTo("WithCompanion(instanceProp=instance prop)")

        // but they can work around it by adding it as a property
        assertThat(testObj.toString())
            .`as`("added companion prop by builder with additional property")
            .isEqualTo("WithCompanion(instanceProp=instance prop, companionProp=companion prop)")

        // assign a new value
        WithCompanion.companionProp = "a new value for the companion prop"
        // assert that the new value is reflected in the output
        assertThat(testObj.toString())
            .`as`("added static var by builder with named supplier")
            .isEqualTo("WithCompanion(instanceProp=instance prop, companionProp=a new value for the companion prop)")
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
        class AnotherClass(val otherProp: String = "another Prop")

        val testObj = TestClass("something")

        // act, assert
        assertThat(testObj.asString())
            .contains(
                "TestClass(",
                "prop1=prop 1",
                "prop3=prop 3",
                "prop4=[ prop 4 ]"
            )
            .`is`(equalSignCount(3))
        assertThat(testObj.asString(TestClass::prop1, TestClass::prop2, TestClass::prop3, TestClass::prop4))
            .contains(
                "TestClass(",
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

    // ------------------------------------
    // Classes etc. to be used in the tests
    // ------------------------------------

    private val aPrintableDate = object {
        override fun toString(): String = LocalDate.of(2022, 1, 27).toString()
    }
    private val anotherPrintable = object {
        override fun toString(): String = "this is another printable"
    }

    @Suppress("unused", "SameReturnValue")
    private open class ClassToPrint(val str: String, open var num: Int, private val privateToPrint: Any?) : Printable {
        // getter should be called, not the internal value. Private should be included, but not for subclasses
        @Suppress("SuspiciousVarProperty")
        private var greet: String? = "hi"
            get() = "hallo"

        // protected, should be included as well
        protected val uuidToPrint: UUID = UUID.fromString("c27ab2db-3f72-4603-9e46-57892049b027")

        // method return values should not be included
        fun getUuidNotToPrint(): UUID = UUID.fromString("97f52d73-2da2-4c0d-af23-9eb2156eea96")

        override fun toString(): String = asString() // default
    }

    // anonymous nested class
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

    private interface PersonallyIdentifiableData : Printable {
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

    private class WithCompanion {
        val instanceProp = "instance prop"

        private val producer: AsStringProducer by lazy {
            asStringBuilder()
                .withAlsoProperties(WithCompanion::companionProp)
                .build()
        }

        override fun toString(): String = producer.asString()
        companion object {
            var companionProp = "companion prop"
    }
}

}