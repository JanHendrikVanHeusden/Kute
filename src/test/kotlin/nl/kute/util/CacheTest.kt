package nl.kute.util

import nl.kute.asstring.core.asString
import nl.kute.reflection.util.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream
import kotlin.random.Random

internal class MapCacheTest {

    @ParameterizedTest
    @ArgumentsSource(CacheArgumentFactory::class)
    fun `basic cache operations should work as expected`(theCache: AbstractCache<String, Boolean, Any>) {
        // initial: empty
        val key = "key"
        val cache = theCache.cache
        assertThat(theCache.size).isZero
        assertThat(theCache.contains(key)).isFalse

        // add a value
        when (theCache) {
            is MapCache -> {
                assertThat(theCache.cache).isEmpty()
                assertThat(theCache[key]).isNull()
                theCache[key] = true
                assertThat(theCache.cache).hasSize(1)
            }
            is SetCache -> {
                assertThat(theCache.cache).isEmpty()
                assertThat(theCache[key]).isFalse
                theCache.add(key)
                assertThat(theCache.cache).hasSize(1)
            }
            else -> fail { "Not implemented" }
        }

        // assert content
        assertThat(theCache.get(key)).isTrue
        assertThat(theCache[key]).isTrue
        assertThat(theCache.size).isEqualTo(1)
        assertThat(theCache.contains(key)).isTrue
        assertThat(theCache.toString())
            .startsWith("${theCache::class.simplifyClassName()}(")
            .contains(theCache.asString(), "size=1")

        // reset
        theCache.reset()
        assertThat(theCache.cache).isNotSameAs(cache)
        assertThat(theCache.size).isZero
        assertThat(theCache.contains(key)).isFalse
        when (theCache) {
            is MapCache -> {
                assertThat(theCache.cache).isEmpty()
                assertThat(theCache[key]).isNull()
            }
            is SetCache -> {
                assertThat(theCache.cache).isEmpty()
                assertThat(theCache[key]).isFalse
            }
            else -> fail { "Not implemented" }
        }
    }

    @Test
    fun `new cache capacity should take current size into account`() {
        // arrange
        var mockedSize = 0
        val cacheMock: ConcurrentHashMap<String, String> = mock()
        whenever(cacheMock.size).thenAnswer { mockedSize }
        val randomSupplier = { Random.nextInt(250, 500) }

        arrayOf(0, 1, 80, 200, 201, randomSupplier()).forEach { initialCapacity ->
            // subclass it to assign mock, and to make properties public
            class TestMapCache: MapCache<String, String>(initialCapacity) {
                public override var cache = cacheMock
                public override fun newCapacity() = super.newCapacity()
            }
            val mapCache = TestMapCache()

            arrayOf(0, 5, 150, 200, 201, randomSupplier(), randomSupplier()).forEach {
                mockedSize = it * 10
                val newCapacity = mapCache.newCapacity()
                val expected = maxOf(initialCapacity, mockedSize + defaultInitialCapacity)
                assertThat(newCapacity)
                    .`as`("newCapacity should adhere to condition for initialCapacity=$initialCapacity and size=$mockedSize (expected: $expected; actual: $newCapacity)")
                    .isEqualTo(expected)
            }
            clearInvocations(cacheMock)
        }
    }

}

internal class CacheArgumentFactory : ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(MapCache<String, Boolean>() as AbstractCache<String, Boolean, MutableMap<String, Boolean>>),
            Arguments.of(SetCache<String>() as AbstractCache<String, Boolean, MutableSet<String>>)
        )
    }
}