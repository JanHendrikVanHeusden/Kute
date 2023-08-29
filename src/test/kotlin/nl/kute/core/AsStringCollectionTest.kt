package nl.kute.core

import nl.kute.config.AsStringConfig
import nl.kute.config.restoreInitialAsStringOption
import nl.kute.core.annotation.option.AsStringOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class AsStringCollectionTest {

    // will be like {0=..., 1=..., 2=..., 10=...}
    private val intStringMap = (0..10).map { it to (it+20).toString() }.toMap()

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringOption()
    }

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff::class)
    fun `collection like properties should be capped at default elementLimit if not annotated`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        // arrange
        @Suppress("unused")
        class TestClass(var collectionLike: Any)
        val testObj = TestClass(collectionStuff)
        AsStringConfig()
            .withElementsLimit(5)
            .applyAsDefault()

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(collectionLike=[0, 1, 2, 3, 4, ...])")

        // arrange
        AsStringConfig()
            .withElementsLimit(4)
            .applyAsDefault()
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(collectionLike=[0, 1, 2, 3, ...])")
    }

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff::class)
    fun `collection like properties should be capped according to class annotation`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        // arrange
        // set default to low value
        AsStringConfig()
            .withElementsLimit(2)
            .applyAsDefault()

        @Suppress("unused")
        @AsStringOption(elementsLimit = 5)
        class TestClass(var collectionLike: Any)
        val testObj = TestClass(collectionStuff)

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(collectionLike=[0, 1, 2, 3, 4, ...])")
    }

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff::class)
    fun `collection like properties should be capped according to toString annotation`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        // arrange
        // set default to low value
        AsStringConfig()
            .withElementsLimit(2)
            .applyAsDefault()

        @Suppress("unused")
        @AsStringOption(elementsLimit = 100)
        class TestClass(var collectionLike: Any) {
            @AsStringOption(elementsLimit = 5)
            override fun toString(): String = asString()
        }
        val testObj = TestClass(collectionStuff)

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(collectionLike=[0, 1, 2, 3, 4, ...])")
    }

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff::class)
    fun `collection like properties should be capped according to property annotation`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        // arrange
        // set default to low value
        AsStringConfig()
            .withElementsLimit(2)
            .applyAsDefault()

        @Suppress("unused")
        @AsStringOption(elementsLimit = 100)
        class TestClass(
            @AsStringOption(elementsLimit = 4)
            var collectionLike: Any)
        val testObj = TestClass(collectionStuff)

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(collectionLike=[0, 1, 2, 3, ...])")
    }

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff::class)
    fun `stand-alone collections should be capped at default elementLimit`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        // arrange
        AsStringConfig()
            .withElementsLimit(5)
            .applyAsDefault()

        // act, assert
        assertThat(collectionStuff.asString())
            .isEqualTo("[0, 1, 2, 3, 4, ...]")

        // arrange
        AsStringConfig()
            .withElementsLimit(4)
            .applyAsDefault()
        // act, assert
        assertThat(collectionStuff.asString())
            .isEqualTo("[0, 1, 2, 3, ...]")
    }

    @Test
    fun `maps should be capped at default elementLimit if not annotated`() {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        // arrange
        @Suppress("unused")
        class TestClass(var theMap: Any)
        val testObj = TestClass(intStringMap)
        AsStringConfig()
            .withElementsLimit(5)
            .applyAsDefault()

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={0=20, 1=21, 2=22, 3=23, 4=24, ...})")

        // arrange
        AsStringConfig()
            .withElementsLimit(4)
            .applyAsDefault()
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={0=20, 1=21, 2=22, 3=23, ...})")
    }

    @Test
    fun `maps should be capped according to class annotation`() {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        // arrange
        // set default to low value
        AsStringConfig()
            .withElementsLimit(2)
            .applyAsDefault()

        @Suppress("unused")
        @AsStringOption(elementsLimit = 5)
        class TestClass(var theMap: Any)
        val testObj = TestClass(intStringMap)

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={0=20, 1=21, 2=22, 3=23, 4=24, ...})")
    }

    @Test
    fun `maps should be capped according to toString annotation`() {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        // arrange
        // set default to low value
        AsStringConfig()
            .withElementsLimit(2)
            .applyAsDefault()

        @Suppress("unused")
        @AsStringOption(elementsLimit = 100)
        class TestClass(var theMap: Any) {
            @AsStringOption(elementsLimit = 5)
            override fun toString(): String = asString()
        }
        val testObj = TestClass(intStringMap)

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={0=20, 1=21, 2=22, 3=23, 4=24, ...})")
    }

    @Test
    fun `maps should be capped according to property annotation`() {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

        // arrange
        // set default to low value
        AsStringConfig()
            .withElementsLimit(2)
            .applyAsDefault()

        @Suppress("unused")
        @AsStringOption(elementsLimit = 100)
        class TestClass(
            @AsStringOption(elementsLimit = 4)
            var theMap: Any)
        val testObj = TestClass(intStringMap)

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={0=20, 1=21, 2=22, 3=23, ...})")
    }

    @Test
    fun `stand-alone map should be capped at default elementLimit`() {
        // arrange
        AsStringConfig()
            .withElementsLimit(4)
            .applyAsDefault()

        // act, assert
        assertThat(intStringMap.asString())
            .isEqualTo("{0=20, 1=21, 2=22, 3=23, ...}")

        // arrange
        AsStringConfig()
            .withElementsLimit(5)
            .applyAsDefault()

        // act, assert
        assertThat(intStringMap.asString())
            .isEqualTo("{0=20, 1=21, 2=22, 3=23, 4=24, ...}")
    }

}

internal class CollectionLikeStuff : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        val range = 0..10
        // These types have the same asString output, but the handling by AsString is different
        // By providing these, all collection-like handlers are being tested
        return Stream.of(
            Arguments.of(range.toList()),  // Collection/List
            Arguments.of(range.toList().toTypedArray()), // Array
            Arguments.of(range.toList().map { it.toByte() }.toByteArray()) // ByteArray
        )
    }
}