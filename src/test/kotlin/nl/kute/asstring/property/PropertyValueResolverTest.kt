package nl.kute.asstring.property

import nl.kute.asstring.annotation.modify.AsStringHash
import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.core.asString
import nl.kute.hashing.DigestMethod
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PropertyValueResolverTest {

    @Test
    fun `property and annotation resolution should be cached`() {
        // arrange
        propsWithAnnotationsCacheByClass.reset()
        assertThat(propsWithAnnotationsCacheByClass.size).isZero

        open class TestClass {
            @AsStringHash(DigestMethod.SHA1)
            open val hashed = "I will be hashed"
            @AsStringOption(propMaxStringValueLength = 9)
            val withOption = "I will be truncated"
            override fun toString() = asString()
        }

        @AsStringOption(showNullAs = "<null>")
        open class SubClass: TestClass() {
            @AsStringHash(DigestMethod.CRC32C)
            override val hashed: String = super.hashed
        }

        @AsStringOption(propMaxStringValueLength = 5000)
        class SubSubClass: SubClass() {
            @Suppress("unused")
            val aSubSubProperty  = 12
        }

        // act - TestClass
        repeat(3) { TestClass().asString() }
        // assert
        assertThat(propsWithAnnotationsCacheByClass.cache)
            .hasSize(1)
            .containsKey(TestClass::class)
        var propAnnotationMap = propsWithAnnotationsCacheByClass[TestClass::class]!!
        assertThat(propAnnotationMap)
            .hasSize(2)
        assertThat(propAnnotationMap.keys.map { it.name })
            .containsExactlyInAnyOrder("hashed", "withOption")

        var annotations = propAnnotationMap.filterKeys { it.name == "hashed" }.values.first()
        assertThat(annotations)
            .containsExactlyInAnyOrder(
                AsStringOption.defaultOption,
                AsStringHash(DigestMethod.SHA1)
            )
        annotations = propAnnotationMap.filterKeys { it.name == "withOption" }.values.first()
        assertThat(annotations)
            .containsExactly(
                AsStringOption(propMaxStringValueLength = 9)
            )

        // act - SubClass
        repeat(3) {SubClass().asString()}
        // assert
        assertThat(propsWithAnnotationsCacheByClass.cache)
            .hasSize(2)
            .containsKey(SubClass::class)
        propAnnotationMap = propsWithAnnotationsCacheByClass[SubClass::class]!!
        assertThat(propAnnotationMap)
            .hasSize(2)
        assertThat(propAnnotationMap.keys.map { it.name })
            .containsExactlyInAnyOrder("hashed", "withOption")

        annotations = propAnnotationMap.filterKeys { it.name == "hashed" }.values.first()
        assertThat(annotations)
            .containsExactlyInAnyOrder(
                AsStringOption(showNullAs = "<null>"),
                // ignores "override" on subclass
                AsStringHash(DigestMethod.SHA1)
            )

        annotations = propAnnotationMap.filterKeys { it.name == "withOption" }.values.first()
        assertThat(annotations)
            .containsExactly(
                AsStringOption(propMaxStringValueLength = 9)
            )

        // act - SubSubClass
        repeat(3) {SubSubClass().asString()}
        // assert
        assertThat(propsWithAnnotationsCacheByClass.cache)
            .hasSize(3)
            .containsKey(SubSubClass::class)
        propAnnotationMap = propsWithAnnotationsCacheByClass[SubSubClass::class]!!
        assertThat(propAnnotationMap)
            .hasSize(3)
        assertThat(propAnnotationMap.keys.map { it.name })
            .containsExactlyInAnyOrder("hashed", "withOption", "aSubSubProperty")

        annotations = propAnnotationMap.filterKeys { it.name == "hashed" }.values.first()
        assertThat(annotations)
            .containsExactlyInAnyOrder(
                AsStringOption(propMaxStringValueLength = 5000),
                // ignores "override" on subclass
                AsStringHash(DigestMethod.SHA1)
            )

        annotations = propAnnotationMap.filterKeys { it.name == "withOption" }.values.first()
        assertThat(annotations)
            .containsExactly(
                AsStringOption(propMaxStringValueLength = 9)
            )

        annotations = propAnnotationMap.filterKeys { it.name == "aSubSubProperty" }.values.first()
        assertThat(annotations)
            .containsExactlyInAnyOrder(
                AsStringOption(propMaxStringValueLength = 5000)
            )
    }

}
