package nl.kute.core

import nl.kute.core.AsStringBuilder.Companion.asStringBuilder
import nl.kute.core.annotation.modifiy.AsStringHash
import nl.kute.core.annotation.modifiy.AsStringMask
import nl.kute.core.annotation.modifiy.AsStringOmit
import nl.kute.core.annotation.modifiy.AsStringPatternReplace
import nl.kute.core.namedvalues.NameValue
import nl.kute.core.namedvalues.NamedProp
import nl.kute.core.namedvalues.NamedSupplier
import nl.kute.core.namedvalues.NamedValue
import nl.kute.core.namedvalues.namedVal
import nl.kute.hashing.DigestMethod
import nl.kute.hashing.hexHashCode
import nl.kute.testobjects.java.JavaClassToTest
import nl.kute.testobjects.java.packagevisibility.JavaClassWithPackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.KotlinSubSubClassOfJavaClassWithAccessiblePackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.SubClassOfJavaClassWithAccessiblePackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.sub.KotlinSubSubClassOfJavaClassWithNotAccessiblePackageLevelProperty
import nl.kute.testobjects.java.packagevisibility.sub.SubClassOfJavaClassWithNotAccessiblePackageLevelProperty
import nl.kute.testobjects.java.protectedvisibility.JavaClassWithProtectedProperty
import nl.kute.testobjects.java.protectedvisibility.KotlinSubSubClassOfJavaJavaClassWithProtectedProperty
import nl.kute.testobjects.java.protectedvisibility.SubClassOfJavaClassWithProtectedProperty
import nl.kute.testobjects.kotlin.printable.protectedvisibility.SubSubClassOfClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.ClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.SubClassOfClassWithProtectedProperty
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.util.Date
import java.util.UUID

class AsStringTest {

    private val names: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

    private var classLevelCounter = 0

    @BeforeEach
    fun setUp() {
        classLevelCounter = 0
    }

    @Test
    fun `properties with loooooooooooong values should be capped at 500 chars`() {
        val longStr = RandomStringUtils.randomAlphabetic(800)
        assertThat(ClassToPrint(longStr, 1, null).toString().length)
            .isEqualTo(ClassToPrint("", 1, null).toString().length + 500)
    }

    @Test
    fun `test with extension object`() {
        // Arrange
        val classToPrint = ClassToPrint("test", 10, aPrintableDate)

        // Assert
        assertThat(classToPrint.toString())
            .isEqualTo("ClassToPrint(greet=hallo, num=10, privateToPrint=$aPrintableDate, str=test, uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027)")
        val asStringProducer = classToPrint.asStringBuilder().exceptProperties(ClassToPrint::num).build()
        assertThat(asStringProducer.asString())
            .isEqualTo("ClassToPrint(greet=hallo, privateToPrint=$aPrintableDate, str=test, uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027)")

        // Assert that it works on anonymous class
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

        // Arrange
        classToPrint.num = 20
        // Assert that updated value is there
        assertThat(classToPrint.toString())
            .isEqualTo("ClassToPrint(greet=hallo, num=20, privateToPrint=2022-01-27, str=test, uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027)")
    }

    @Test
    fun `test that toString with Mock does not break`() {
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
        val kotlinSubClass = KotlinClassToTest("my str", 35, "this is another", names)
        assertThat(JavaClassToTest::class.java.isAssignableFrom(kotlinSubClass.javaClass))
        assertThat(kotlinSubClass.toString()).isEqualTo("KotlinClassToTest(anotherStr=this is another, names=${names.contentDeepToString()})")
    }

    @Test
    fun `overriding of annotations should not be possible`() {
        val person = Person()
        val personString = person.toString()
        assertThat(personString)
            // according to annotations on interface properties, not on subclass;
            // so the "overriding" annotations are not honored
            .contains("iban=NL99 BANK *****0 7906")
            .contains("password=**********")
            .contains("phoneNumber=06123***789")
            .matches(""".+?\bsocialSecurityNumber=[a-f0-9]{40}\b.*""")
            // according to AsStringOmit annotation on subclass
            .doesNotContain("mailAddress")
    }

    @ParameterizedTest
    @ValueSource(strings = ["value", "prop"])
    fun `each call of asString with NamedXxx should evaluate the mutable value`(namedValueType: String) {
        // Arrange
        val valueName = "classLevelCounter"
        val namedProp = this.namedVal(::classLevelCounter) as NamedProp<*, Int>

        val mapOfNamedValues: Map<String, () -> NameValue<Int?>> = mapOf(
            // We can simply use the same NamedProp every time, it will evaluate the property value at runtime
            "prop" to { namedProp },
            // A new NamedValue needs to be constructed on every call, because it simply stores the value at time of construction.
            // If you don't like that, use `NamedProp` or `NamedSupplier` instead
            "value" to { classLevelCounter.namedVal(valueName) as NamedValue<Int> }
        )
        val namedXxx = mapOfNamedValues[namedValueType]!!

        class TestClass {
            override fun toString(): String = asStringBuilder().withAlsoNamed(namedXxx()).asString()
        }
        assertThat(classLevelCounter).isZero()
        val testObj = TestClass()

        classLevelCounter = 0
        repeat(3) {
            // Arrange
            classLevelCounter++
            // Act
            val asString = testObj.toString()

            // Assert
            assertThat(asString)
                .`as`("Should honour changed value")
                .matches("^.+\\b$valueName=$classLevelCounter\\D")
            assertThat(classLevelCounter).isEqualTo(classLevelCounter)
        }
    }

    @Test
    fun `each call of asString with NamedSupplier should evaluate the value exactly once`() {
        // Arrange
        val counterName = "counter"
        var counter = 0

        val supplier = {
            // Don't do this normally! A Supplier should not have side effects!
            // It's just for test purposes, to verify that it's called only once during asString() processing
            ++counter
        }

        val namedSupplier = supplier.namedVal(counterName) as NamedSupplier<Int>
        // Arrange
        counter = 0

        open class TestClass {
            val asStringProducer = asStringBuilder()
                .withAlsoNamed(namedSupplier)
                .build()
            override fun toString(): String {
                return asStringProducer.asString()
            }
        }
        assertThat(counter).isZero()
        val testObj = TestClass()

        // Act
        var asString = testObj.toString()

        // Assert
        assertThat(asString)
            .`as`("Supplier expression should be retrieved only once during processing")
            .matches("^.+\\b$counterName=1\\D")
        assertThat(counter).isEqualTo(1)

        // Act
        asString = testObj.toString()
        // Assert
        assertThat(asString)
            .`as`("")
            .matches("^.+\\b$counterName=2\\D")
        assertThat(counter).isEqualTo(2)

        // Arrange
        counter = 0
        // Act
        asString = testObj.asStringBuilder()
            .exceptPropertyNames(counterName)
            .withAlsoNamed(namedSupplier)
            .build()
            .asString()
        // Assert
        assertThat(asString).matches("^.+\\b$counterName=1\\D")
        assertThat(counter)
            .`as`("propertyNamesToExclude should exclude properties only, not named values")
            .isEqualTo(1)

        // Arrange
        counter = 0
        class SubTestClass(var aProp: String = "a Prop"): TestClass()

        with(SubTestClass()) {
            val namedProp = this.namedVal(this::aProp)
            // Act
            asString = this.asStringBuilder()
                .exceptPropertyNames(aProp, counterName)
                .withAlsoNamed(namedProp, namedSupplier)
                .asString()
            // Assert
            assertThat(asString)
                .matches("^.+\\b${namedProp.name}=${this.aProp}\\D.*")
                .matches("^.+\\b$counterName=1\\D.*")
            assertThat(counter)
                .`as`("propertyNamesToExclude should exclude properties only, not named values")
                .isEqualTo(1)
        }
        // Arrange
        counter = 0
        // Act - not excluding "counter"
        asString = testObj.asStringBuilder()
            .exceptPropertyNames("count")
            .withAlsoNamed(namedSupplier)
            .asString()
        // Assert
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
        val objToTest = JavaClassWithPackageLevelProperty()
        val subObjToTestNotAccessibleProp = SubClassOfJavaClassWithNotAccessiblePackageLevelProperty()
        val subSubObjToTestNotAccessibleProp = KotlinSubSubClassOfJavaClassWithNotAccessiblePackageLevelProperty()

        val subObjToTestAccessibleProp = SubClassOfJavaClassWithAccessiblePackageLevelProperty()
        val subSubObjToTestAccessibleProp = KotlinSubSubClassOfJavaClassWithAccessiblePackageLevelProperty()

        val packLevelAttrOutput = "myPackageLevelAttribute=my package level attribute"
        val publicAttrOutput = "myPublicAccessibleString=my public accessible String"

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
                .`as`("Package level attribute is not shown for ${it::class.simpleName}, Kotlin regards this as private even when accessible in Java class")
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

        val objToTest = JavaClassWithProtectedProperty()
        val subObjToTestProtectedProp = SubClassOfJavaClassWithProtectedProperty()
        val subSubObjToTestProtectedProp = KotlinSubSubClassOfJavaJavaClassWithProtectedProperty()

        val protectedAttrOutput = "myProtectedAttribute=my protected attribute"
        val publicAttrOutput = "myPublicAccessibleString=my public accessible String"

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
                .`as`("Protected attribute shown in subclass ${it::class.simpleName} output because the instance variable is protected (not just the getter)")
                .contains(protectedAttrOutput)
        }
    }

    @Test
    fun `output with Kotlin class with protected visibility property`() {

        val objToTest = ClassWithProtectedProperty()
        val subObjToTestProtectedProp = SubClassOfClassWithProtectedProperty()
        val subSubObjToTestProtectedProp = SubSubClassOfClassWithProtectedProperty()

        val protectedAttrOutput = "myProtectedAttribute=my protected attribute"
        val publicAttrOutput = "myPublicAccessibleString=my public accessible String"

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
            assertThat(it.asString()).isEqualTo(it.toString())
        }
    }

    @Test
    fun `null asString should yield 'null'`() {
        assertThat(null.asString()).isEqualTo("null")
    }

    @Test
    fun `synthetic types shouldn't cause exceptions`() {
        val supplier: () -> String = { "a String supplier" }

        listOf(
            this@AsStringTest::aPrintableDate,
            supplier,
            { "an other String supplier" },
            { Any() }
        ).forEachIndexed {i, it ->
            assertThat(it.asString())
                .`as`("Expression #$i should yield format class@hashCode")
                .isEqualTo("${it::class.toString().replace("class ", "")}@${it.hexHashCode()}")
        }
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

        @Suppress("unused")
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

        @AsStringPatternReplace("""\s*([a-zA-Z]{2})\s*\d{2}\s*[a-zA-Z]{4}\s*((\d|\s){6})(.*)""", """$1\99 BANK *****$4""")
        val iban: String

        @AsStringHash(DigestMethod.CRC32C)
        val mailAddress: String

        @AsStringHash(DigestMethod.SHA1)
        val socialSecurityNumber: String

        @AsStringMask(minLength = 10, maxLength = 10)
        val password: Array<Char>
    }

    private class Person : PersonallyIdentifiableData {
        // Trying to override the annotations on the interface should not be possible:
        // the annotations in the overriding class should be ignored (except for AsStringOption)

        @AsStringMask(startMaskAt = 0, endMaskAt = 0)
        override val phoneNumber: String = "06123456789"

        @AsStringPatternReplace("""(.*)""", """$1""")
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

}