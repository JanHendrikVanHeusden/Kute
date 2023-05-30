package nl.kute.printable

import nl.kute.core.Printable
import nl.kute.core.asString
import nl.kute.test.java.printable.JavaClassToTestPrintable
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
            .doesNotContain("privateToPrint", "this is another printable") // excluded property
            .contains(
                "class ",
                "greet=hallo",
                "str=a string",
                "uuidToPrint=c27ab2db-3f72-4603-9e46-57892049b027",
                "extensionProperty=my extension property",
                "num=80", // overridden value
            )

        assertThat(extensionObject.asString())
            .contains(
                "class ",
                "greet=hallo",
                "privateToPrint=this is another printable",
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
        assertThat(kotlinSubClass.toString()).isEqualTo("KotlinClassToTestPrintable(str=my str, num=35, anotherStr=this is another, names=${names.contentDeepToString()})")
        assertThat(kotlinSubClass.asString()).isEqualTo("KotlinClassToTestPrintable(str=my str, num=35, anotherStr=this is another, names=${names.contentDeepToString()})")
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

    @Suppress("unused")
    open private class ClassToPrint(val str: String, open var num: Int, private val privateToPrint: Any?): Printable {
        // getter should be called, not the internal value. Private should be included
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

}