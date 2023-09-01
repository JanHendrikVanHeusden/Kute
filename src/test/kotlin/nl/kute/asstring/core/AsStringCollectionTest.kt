package nl.kute.asstring.core

import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringOption
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
    @ArgumentsSource(CollectionLikeStuff10::class)
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
    @ArgumentsSource(CollectionLikeStuff10::class)
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
    @ArgumentsSource(CollectionLikeStuff10::class)
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
    @ArgumentsSource(CollectionLikeStuff10::class)
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
    @ArgumentsSource(CollectionLikeStuff10::class)
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

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff10::class)
    fun `collection properties should be capped at 0 if specified so`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
        // arrange
        AsStringConfig()
            .withElementsLimit(0)
            .applyAsDefault()

        // act, assert
        assertThat(collectionStuff.asString())
            .isEqualTo("[...]")
    }

    @Test
    fun `map properties should be capped at 0 if specified so`() {
        // arrange
        @Suppress("unused")
        @AsStringOption(elementsLimit = 0)
        class TestClass(var theMap: Any)
        val testObj = TestClass(intStringMap)
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={...})")

        // arrange
        AsStringConfig()
            .withElementsLimit(4)
            .applyAsDefault()
        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={...})")
    }

    @Test
    fun `maps should be capped according to class annotation`() {
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

    @ParameterizedTest
    @ArgumentsSource(CollectionLikeStuff100::class)
    fun `collection like properties should be capped to initial limit when unexpected limit value`(collectionStuff: Any) {
        // collectionStuff is collection etc. like [0, 1, 2, 3, ..., 100]

        // arrange
        val initialLimit = AsStringOption().elementsLimit
        assertThat(initialLimit).isEqualTo(50)
        // set default to negative value
        AsStringConfig()
            .withElementsLimit(-10)
            .applyAsDefault()

        @Suppress("unused")
        class TestClass(var collectionLike: Any)
        val testObj = TestClass(collectionStuff)
        val expected = (0..initialLimit-1).joinToString()

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(collectionLike=[$expected, ...])")
    }

    @Test
    fun `map properties should be capped to initial limit when unexpected limit value`() {
        // arrange
        val initialLimit = AsStringOption().elementsLimit
        assertThat(initialLimit).isEqualTo(50)
        // set default to negative value
        AsStringConfig()
            .withElementsLimit(-10)
            .applyAsDefault()

        val theMap: Map<Int, String> = (0..100).map { it to (-it*100).toString() }.toMap()

        @Suppress("unused")
        class TestClass(var theMap: Map<Int, String>)
        val testObj = TestClass(theMap)
        val expected = (0..initialLimit-1).map { "$it=${-it*100}" }.joinToString()

        // act, assert
        assertThat(testObj.asString())
            .isEqualTo("TestClass(theMap={$expected, ...})")
    }
}

internal class CollectionLikeStuff10 : ArgumentsProvider {
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

internal class CollectionLikeStuff100 : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        val range = 0..100
        // These types have the same asString output, but the handling by AsString is different
        // By providing these, all collection-like handlers are being tested
        return Stream.of(
            Arguments.of(range.toList()),  // Collection/List
            Arguments.of(range.toList().toTypedArray()), // Array
            Arguments.of(range.toList().map { it.toByte() }.toByteArray()) // ByteArray
        )
    }
}