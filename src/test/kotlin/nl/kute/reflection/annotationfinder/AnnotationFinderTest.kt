package nl.kute.reflection.annotationfinder

import nl.kute.core.annotation.modify.AsStringHash
import nl.kute.core.annotation.modify.AsStringMask
import nl.kute.core.annotation.modify.AsStringPatternReplace
import nl.kute.core.annotation.option.AsStringOption
import nl.kute.core.annotation.option.defaultNullString
import nl.kute.core.asString
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.MapEntry
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@Suppress("unused", "SameReturnValue", "EmptyMethod") // several properties accessed by reflection only
internal class AnnotationFinderTest {

    @AsStringOption(propMaxStringValueLength = 200)
    private open class With3AsStringOptions(@AsStringOption(propMaxStringValueLength = 100) open val someVal: String) {
        @AsStringOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    private open class With1AsStringOption(@AsStringOption(propMaxStringValueLength = 100) val someVal: String) {
        override fun toString(): String = asString()
    }

    private open class WithInheritedAsStringOption(override val someVal: String) : With3AsStringOptions(someVal) {
        override fun toString(): String = asString()

        @AsStringOption(propMaxStringValueLength = 999999, showNullAs = "")
        @Suppress("unused", "UNUSED_PARAMETER", "EmptyMethod")
        fun toString(iets: String) {}
    }

    private open class ClassWithMethodParamSubtypeInheritance<T : Number> {
        @AsStringOption(propMaxStringValueLength = 50, showNullAs = "")
        open fun getList(inList: List<T>): List<T> = emptyList()

        @AsStringOption(propMaxStringValueLength = 15, showNullAs = "geen getal")
        open fun getNum(inNum: T): T? = null

        @Suppress("EmptyMethod") // several properties accessed by reflection only
        open fun doList(inList: List<T>) {}

        @AsStringOption(propMaxStringValueLength = 250, showNullAs = "<null>")
        override fun toString(): String = asString()
    }

    @Suppress("RedundantOverride")
    private open class SubClassWithMethodParamSubtypeInheritance : ClassWithMethodParamSubtypeInheritance<Int>() {
        override fun getList(inList: List<Int>): ArrayList<Int> = ArrayList()
        override fun getNum(inNum: Int): Int = 12

        @Suppress("EmptyMethod") // several properties accessed by reflection only
        override fun doList(inList: List<Int>) {}

        override fun toString(): String = super.toString()
    }

    private open class ClassWithAnnotationOnPrivateProperty {
        @AsStringOption(propMaxStringValueLength = 5)
        @AsStringHash
        private val myPrivateVal = "value of my private val"
        override fun toString(): String = asString()
    }

    private open class ClassWithAnnotationOnPublicPropertyThatMasksPrivateProperty : ClassWithAnnotationOnPrivateProperty() {
        @AsStringOption(propMaxStringValueLength = 200)
        @AsStringMask
        val myPrivateVal = "value of the masking public val"
        override fun toString(): String = asString()
    }

    private interface I {
        @AsStringPatternReplace(pattern = "I", replacement = "i")
        val prop: String

        @AsStringOption(propMaxStringValueLength = 0)
        override fun toString(): String
    }

    private open class C1 : I {
        @AsStringPatternReplace(pattern = "C1", replacement = "c1")
        override val prop: String = "C1"

        @AsStringOption(propMaxStringValueLength = 1)
        override fun toString(): String = "C1"
    }

    private open class C2 : C1() {
        @AsStringPatternReplace(pattern = "C2", replacement = "c2")
        override val prop: String = "C2"

        @AsStringOption(propMaxStringValueLength = 2)
        override fun toString(): String = "C2"
    }

    private open class C3 : C2() {
        @AsStringPatternReplace(pattern = "C3", replacement = "c3")
        override val prop: String = "C3"

        @AsStringOption(propMaxStringValueLength = 3)
        override fun toString(): String = "C3"
    }

    @Test
    fun `find property annotations in class hierarchy in expected order`() {
        val annotationMap: Map<KClass<*>, AsStringPatternReplace> =
            C3::prop.annotationByPropertySubSuperHierarchy<AsStringPatternReplace>()
        // contract of annotationsOfProperty explicitly states the order, let's test it!
        assertThat(annotationMap.entries).containsExactly(
            MapEntry.entry(C3::class, AsStringPatternReplace(pattern = "C3", replacement = "c3")),
            MapEntry.entry(C2::class, AsStringPatternReplace(pattern = "C2", replacement = "c2")),
            MapEntry.entry(C1::class, AsStringPatternReplace(pattern = "C1", replacement = "c1")),
            MapEntry.entry(I::class, AsStringPatternReplace(pattern = "I", replacement = "i")),
        )
    }

    @Test
    fun `find annotations on private property`() {
        val obj = ClassWithAnnotationOnPrivateProperty()
        val privateProp = obj::class.memberProperties.first { it.name == "myPrivateVal" }

        val hashAnnotation: AsStringHash = privateProp.annotationByPropertySubSuperHierarchy<AsStringHash>()[obj::class]!!
        assertThat(hashAnnotation).isNotNull

        val optionAnnotation: AsStringOption =
            privateProp.annotationByPropertySubSuperHierarchy<AsStringOption>()[obj::class]!!
        assertThat(optionAnnotation).isNotNull
        assertThat(optionAnnotation.propMaxStringValueLength).isEqualTo(5)
    }

    @Test
    fun `find annotations on public property that masks private property`() {
        val obj = ClassWithAnnotationOnPublicPropertyThatMasksPrivateProperty()
        val privateProp = obj::class.memberProperties.first { it.name == "myPrivateVal" }

        val hashAnnotation: AsStringHash? = privateProp.annotationByPropertySubSuperHierarchy<AsStringHash>()[obj::class]
        assertThat(hashAnnotation).isNull()

        val maskAnnotation: AsStringMask = privateProp.annotationByPropertySubSuperHierarchy<AsStringMask>()[obj::class]!!
        assertThat(maskAnnotation).isNotNull

        val optionAnnotation: AsStringOption =
            privateProp.annotationByPropertySubSuperHierarchy<AsStringOption>()[obj::class]!!
        assertThat(optionAnnotation).isNotNull
        assertThat(optionAnnotation.propMaxStringValueLength).isEqualTo(200)
    }

    @Test
    fun `find AsStringOption on class, property, method`() {
        val with3AsStringOptions = With3AsStringOptions("my val")

        val printOptByClass: AsStringOption = With3AsStringOptions::class.annotationOfSubSuperHierarchy()!!
        assertThat(printOptByClass.propMaxStringValueLength).isEqualTo(200)
        assertThat(printOptByClass.showNullAs).isEqualTo(defaultNullString)

        val printOptOfProperty: AsStringOption = with3AsStringOptions::someVal.annotationOfPropertySubSuperHierarchy()!!
        assertThat(printOptOfProperty.propMaxStringValueLength).isEqualTo(100)
        assertThat(printOptOfProperty.showNullAs).isEqualTo(defaultNullString)

        val printOptOfToString: AsStringOption = with3AsStringOptions::class.annotationOfToStringSubSuperHierarchy()!!
        assertThat(printOptOfToString.propMaxStringValueLength).isEqualTo(250)
        assertThat(printOptOfToString.showNullAs).isEqualTo("<null>")

        val printOptOfProperty1: AsStringOption = With1AsStringOption::someVal.annotationOfPropertySubSuperHierarchy()!!
        assertThat(printOptOfProperty1.propMaxStringValueLength).isEqualTo(100)
        assertThat(printOptOfProperty1.showNullAs).isEqualTo(defaultNullString)
    }

    @Test
    fun `find no AsStringOption where not defined`() {
        val with1AsStringOption = With1AsStringOption("my val")

        val printOptByClass: AsStringOption? = With1AsStringOption::class.annotationOfSubSuperHierarchy()
        assertThat(printOptByClass).isNull()

        val printOptOfToString: AsStringOption? = with1AsStringOption::class.annotationOfToStringSubSuperHierarchy()
        assertThat(printOptOfToString).isNull()
    }

    @Test
    fun `find AsStringOption on class, property, method of subclass by annotation's inheritance`() {
        val with3AsStringOptions = With3AsStringOptions("my val")
        val with3AsStringOptionsByInheritance = WithInheritedAsStringOption("my val on inherited")
        assertThat(with3AsStringOptionsByInheritance).isInstanceOf(With3AsStringOptions::class.java)

        val printOptByClass: AsStringOption = With3AsStringOptions::class.annotationOfSubSuperHierarchy()!!
        val printOptByClassInheritance: AsStringOption? = WithInheritedAsStringOption::class.annotationOfSubSuperHierarchy()
        assertThat(printOptByClassInheritance).isEqualTo(printOptByClass)

        val printOptOfProperty: AsStringOption = with3AsStringOptions::someVal.annotationOfPropertySubSuperHierarchy()!!
        val printOptOfPropertyByInheritance: AsStringOption? =
            with3AsStringOptionsByInheritance::someVal.annotationOfPropertySubSuperHierarchy()!!
        assertThat(printOptOfPropertyByInheritance).isSameAs(printOptOfProperty)

        val printOptOfToString: AsStringOption = with3AsStringOptions::class.annotationOfToStringSubSuperHierarchy()!!
        val printOptOfToStringByInheritance: AsStringOption? =
            with3AsStringOptionsByInheritance::class.annotationOfToStringSubSuperHierarchy()
        assertThat(printOptOfToStringByInheritance).isSameAs(printOptOfToString)
    }

}
