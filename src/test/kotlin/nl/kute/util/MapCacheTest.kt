package nl.kute.util

import nl.kute.core.asString
import nl.kute.reflection.simplifyClassName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Stream
import kotlin.random.Random

internal class MapCacheTest {

    @ParameterizedTest
    @ArgumentsSource(CacheArgumentFactory::class)
    fun `test basic cache operations`(theCache: AbstractCache<String, Boolean, Any>) {
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
        }
    }

    @Test
    fun `new cache capacity should take current size into account`() {
        // arrange
        var mockedSize = 0
        val cacheMock: ConcurrentHashMap<String, String> = mock()
        `when`(cacheMock.size).thenAnswer { mockedSize }
        val randomSupplier = { Random.nextInt(2000, 20_000) }

        arrayOf(0, 1, 2, 15, 80, 200, 201, 800, randomSupplier(), randomSupplier()).forEach { initialCapacity ->
            // subclass it to assign mock, and to make properties public
            class TestMapCache: MapCache<String, String>(initialCapacity) {
                public override var cache = cacheMock
                public override fun newCapacity() = super.newCapacity()
            }
            val mapCache = TestMapCache()

            arrayOf(0, 5, 40, 150, 200, 201, 1500, randomSupplier(), randomSupplier()).forEach {
                mockedSize = it * 10
                val sizeRelatedCapacity = minOf((mockedSize * 1.5).toInt(), mockedSize + defaultInitialCapacity)
                val newCapacity = mapCache.newCapacity()
                val expected = maxOf(defaultInitialCapacity, initialCapacity, sizeRelatedCapacity)
                assertThat(newCapacity)
                    .`as`("newCapacity should adhere to condition for initialCapacity=$initialCapacity and size=$mockedSize (expected: $expected; actual: $newCapacity)")
                    .isEqualTo(expected)
            }
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