package nl.kute.asstring.core

import nl.kute.asstring.annotation.option.AsStringOption
import nl.kute.asstring.config.AsStringConfig
import nl.kute.asstring.config.restoreInitialAsStringOption
import nl.kute.log.log
import nl.kute.log.logger
import nl.kute.log.resetStdOutLogger
import nl.kute.test.base.ObjectsStackVerifier
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
import org.awaitility.Awaitility
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.opentest4j.TestAbortedException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream
import kotlin.random.Random

class AsStringCollectionTest: ObjectsStackVerifier {

    // will be like {0=..., 1=..., 2=..., 10=...}
    private val intStringMap = (0..10).map { it to (it+20).toString() }.toMap()

    @BeforeEach
    @AfterEach
    fun setUpAndTearDown() {
        restoreInitialAsStringOption()
        resetStdOutLogger()
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

    @Test
    fun `non-thread safe collections should be handled without exception, with defensive copy if needed`() {
        // This test depends on a race condition that is hit in most cases, but not always.
        // So it uses assumeThat() rather than assertThat() - which means it will not really fail

        val maxTries = 10
        var tryCount = 0

        // Buffer to store error message in
        val logBuffer = StringBuffer(1000)
        logger = { msg -> logBuffer.append(msg) }

        val elementsLimit = AsStringOption.defaultOption.elementsLimit

        // exits when test succeeds, or maxTries is reached
        while (true) {
            tryCount++

            try {
                // arrange
                val result: String?
                val unsafeList: ArrayList<Int> = ArrayList((0..150).toList())

                // Continuously modify list in separate threads
                val threadCount = 4
                val threadList: MutableList<Thread> = mutableListOf()
                val executors: MutableList<ExecutorService> =
                    (1..threadCount).map { Executors.newSingleThreadExecutor() }.toMutableList()
                val modifications = AtomicInteger(0)
                val listModifier = Runnable {
                    threadList.add(Thread.currentThread())
                    var i = unsafeList.size
                    while (true) {
                        Thread.sleep(0, 1)
                        try {
                            val index = Random.nextInt(0, minOf(elementsLimit, unsafeList.size-1))
                            unsafeList.add(index, i++)
                        } catch (e: ConcurrentModificationException) {
                            // ignore
                        }
                        modifications.incrementAndGet()
                    }
                }
                try {
                    // act
                    executors.forEach { it.submit(listModifier) }
                    Awaitility.await().until { modifications.get() > 0 }
                    // On ConcurrentModificationException, asString should retry with a defensive copy
                    result = unsafeList.asString()
                } finally {
                    executors.forEach { it.shutdownNow() }
                }
                // assert
                assumeThat(result)
                    .`as`("In most cases, a defensive copy of the collection can be taken," +
                            " but it may run into a ConcurrentModificationException itself." +
                            " If so, the output will be something like ArrayList@83af56e7")
                    .matches("""\[(\d+, ){$elementsLimit}\.\.\.]""")
                @Suppress("RegExpSimplifiable") // this inspection doesn't understand it actually...
                assumeThat(logBuffer.toString())
                    .`as`("ConcurrentModificationException should be logged;" +
                            " but in race conditions logging may be empty" +
                            " (notably when JVM is pre-warmed; maybe also dependent on environment)")
                    .contains(
                    "Warning: Non-thread safe collection/map was modified concurrently",
                    ConcurrentModificationException::class.simpleName
                )

                resetStdOutLogger()
                log("Test with non-threadsafe collection succeeded after $tryCount tries")
                return // success

            } catch (e: TestAbortedException) {
                if (tryCount >= maxTries) {
                    resetStdOutLogger()
                    log("Test did not cause (or did not log) ConcurrentModificationException after $tryCount tries")
                    throw e // assume failed
                }
            }
        }
    }

    @Test
    fun `non-thread safe maps should be handled without exception, with defensive copy if needed`() {
        // This test depends on a race condition that is hit in most cases, but not always.
        // So it uses assumeThat() rather than assertThat() - which means it will not really fail

        val maxTries = 10
        var tryCount = 0

        // Buffer to store error message in
        val logBuffer = StringBuffer(1000)
        logger = { msg -> logBuffer.append(msg) }

        // exits when test succeeds, or maxTries is reached
        while (true) {
            tryCount++
            try {
                // arrange
                val result: String?
                val unsafeMap: MutableMap<Int, Int> = mutableMapOf()

                // Continuously modify list in separate threads
                val threadCount = 4
                val threadList: MutableList<Thread> = mutableListOf()
                val executors: MutableList<ExecutorService> =
                    (1..threadCount).map { Executors.newSingleThreadExecutor() }.toMutableList()
                val modifications = AtomicInteger(0)
                val mapModifier = Runnable {
                    threadList.add(Thread.currentThread())
                    var i = unsafeMap.size
                    while (true) {
                        Thread.sleep(0, 1)
                        try {
                            unsafeMap[unsafeMap.size] = i++
                        } catch (e: ConcurrentModificationException) {
                            // ignore
                        }
                        modifications.incrementAndGet()
                    }
                }
                try {
                    // act
                    executors.forEach { it.submit(mapModifier) }
                    Awaitility.await().until { unsafeMap.size > 10 }
                    // On ConcurrentModificationException, asString should retry with a defensive copy
                    result = unsafeMap.asString()
                } finally {
                    executors.forEach { it.shutdownNow() }
                }
                // assert
                assumeThat(result)
                    .`as`(
                        "In most cases, a defensive copy of the map can be taken," +
                                " but it may run into a ConcurrentModificationException itself." +
                                " If so, the output will be something like LinkedHashMap@83af56e7"
                    )
                    .matches("""\{(\d+=\d+, )+((\d+=\d+)|(\.\.\.)?)}""")

                assumeThat(logBuffer.toString())
                    .`as`("ConcurrentModificationException should be logged;" +
                            " but in race conditions logging may be empty" +
                            " (notably when JVM is pre-warmed; maybe also dependent on environment)")
                    .contains(
                        "Warning: Non-thread safe collection/map was modified concurrently",
                        ConcurrentModificationException::class.simpleName
                    )

                resetStdOutLogger()
                log("Test with non-threadsafe map succeeded after $tryCount tries")
                return // success

            } catch (e: TestAbortedException) {
                if (tryCount >= maxTries) {
                    resetStdOutLogger()
                    log("Test did not cause (or did not log) ConcurrentModificationException after $tryCount tries")
                    throw e // assume failed
                }
            }
        }
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