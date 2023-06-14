package nl.kute.printable

import nl.kute.core.Printable
import nl.kute.core.asString
import nl.kute.core.asStringWithOnly
import nl.kute.hashing.DigestMethod
import nl.kute.printable.annotation.modifiy.PrintHash
import nl.kute.printable.annotation.modifiy.PrintMask
import nl.kute.printable.annotation.modifiy.PrintOmit
import nl.kute.printable.annotation.modifiy.PrintPatternReplace
import nl.kute.testobjects.java.printable.JavaClassToTestPrintable
import nl.kute.testobjects.java.printable.packagevisibility.JavaClassWithPackageLevelProperty
import nl.kute.testobjects.java.printable.packagevisibility.KotlinSubSubClassOfJavaClassWithAccessiblePackageLevelProperty
import nl.kute.testobjects.java.printable.packagevisibility.SubClassOfJavaClassWithAccessiblePackageLevelProperty
import nl.kute.testobjects.java.printable.packagevisibility.sub.KotlinSubSubClassOfJavaClassWithNotAccessiblePackageLevelProperty
import nl.kute.testobjects.java.printable.packagevisibility.sub.SubClassOfJavaClassWithNotAccessiblePackageLevelProperty
import nl.kute.testobjects.java.printable.protectedvisibility.JavaClassWithProtectedProperty
import nl.kute.testobjects.java.printable.protectedvisibility.KotlinSubSubClassOfJavaJavaClassWithProtectedProperty
import nl.kute.testobjects.java.printable.protectedvisibility.SubClassOfJavaClassWithProtectedProperty
import nl.kute.testobjects.java.printable.protectedvisibility.SubSubClassOfClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.ClassWithProtectedProperty
import nl.kute.testobjects.kotlin.protectedvisibility.SubClassOfClassWithProtectedProperty
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.util.UUID

class PrintableTest {

    private val names: Array<String> = arrayOf("Rob", "William", "Marcel", "Theo", "Jan-Hendrik")

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
        assertThat(classToPrint.asStringExcluding(ClassToPrint::num))
            .isEqualTo("ClassToPrint(greet=hallo, privateToPrint=$aPrintableDate, str=test, uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027)")

        // Assert that it works on anonymous class
        assertThat(extensionObject.toString())
            .doesNotContain("privateToPrint", "this is another printable", // // excluded properties
                "greet=hallo", "privateToPrint=this is another printable") // private properties
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
        val kotlinSubClass = KotlinClassToTestPrintable("my str", 35, "this is another", names)
        assertThat(JavaClassToTestPrintable::class.java.isAssignableFrom(kotlinSubClass.javaClass))
        assertThat(kotlinSubClass.toString()).isEqualTo("KotlinClassToTestPrintable(anotherStr=this is another, names=${names.contentDeepToString()})")
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
            // according to PrintOmit annotation on subclass
            .doesNotContain("mailAddress")
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
    fun `test asStringWithOnly`() {
        val testObj = ClassToPrint(num = 12, privateToPrint = UUID.randomUUID(), str = "this is str")
        val testObj2 = aPrintableDate
        val someString = "just some string"
        // FIXME: WIP
        println(testObj.asStringWithOnly(testObj::num, "aPrintableDate" to testObj2, someString))
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
    private open class ClassToPrint(val str: String, open var num: Int, private val privateToPrint: Any?): Printable {
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
        override fun toString(): String = asStringExcludingNames("privateToPrint")

        @Suppress("unused")
        private val extensionProperty = "my extension property"
        override var num = 80
    }

    @Suppress("unused", "CanBeParameter")
    private class KotlinClassToTestPrintable(str: String, num: Int, val anotherStr: String, val names: Array<String>) :
        JavaClassToTestPrintable(str, num, names) {

        override fun toString() = asString()
    }

    private interface PersonallyIdentifiableData: Printable {
        @PrintMask(startMaskAt = 5, endMaskAt = -3)
        val phoneNumber: String

        @PrintPatternReplace("""\s*([a-zA-Z]{2})\s*\d{2}\s*[a-zA-Z]{4}\s*((\d|\s){6})(.*)""", """$1\99 BANK *****$4""")
        val iban: String

        @PrintHash(DigestMethod.CRC32C)
        val mailAddress: String

        @PrintHash(DigestMethod.SHA1)
        val socialSecurityNumber: String

        @PrintMask(minLength = 10, maxLength = 10)
        val password: Array<Char>
    }

    private class Person: PersonallyIdentifiableData {
        // Trying to override the annotations on the interface should not be possible:
        // the annotations in the overriding class should be ignored (except for PrintOption)

        @PrintMask(startMaskAt = 0, endMaskAt = 0)
        override val phoneNumber: String = "06123456789"

        @PrintPatternReplace("""(.*)""", """$1""")
        override val iban: String = "NL29 ABNA 6708 40 7906"

        @PrintOmit
        override val mailAddress: String = "someone@example.com"

        @PrintHash(DigestMethod.JAVA_HASHCODE)
        override val socialSecurityNumber: String = "617247018"

        @PrintMask(minLength = 0, maxLength = Int.MAX_VALUE, startMaskAt = 0, endMaskAt = Int.MAX_VALUE)
        override val password: Array<Char> =
            arrayOf('m', 'y', ' ', 'v', 'e', 'r', 'y', ' ', 's', 'e', 'c', 'r', 'e', 't', ' ', 'p', 'a', 's', 's', 'w', 'o', 'r', 'd')

        override fun toString(): String = asString()
    }
}
