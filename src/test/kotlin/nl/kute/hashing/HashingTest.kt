package nl.kute.hashing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.random.Random

internal class HashingTest {

    private val testStringShort = "my test string"

    // String of length 335k
    private val testStringLong = mutableListOf("abcdefghijklmnopqrstuvwxyz1234567890-=`~!@#$%^&*()_+{}[];:',./<>?")
        .also { list -> repeat(5000) { list.add(list[0]) } }.joinToString()

    private val digestMethodPatterns =
        mapOf(
            DigestMethod.JAVA_HASHCODE to Regex("^-?(\\d|[a-f]){1,8}$"),
            DigestMethod.CRC32C to Regex("^-?(\\d|[a-f]){1,8}$"),
            DigestMethod.SHA1 to Regex("^-?(\\d|[a-f]){39,40}$"),
            DigestMethod.MD5 to Regex("^-?(\\d|[a-f]){31,32}$")
        )

    @Test
    fun `test that String hashing with JAVA_HASHCODE digest yields same result as java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.JAVA_HASHCODE)
        assertThat(hashShortString).isEqualTo(testStringShort.hashCode().toString(16))

        val hashLongString = hashString(testStringLong, DigestMethod.JAVA_HASHCODE)
        assertThat(hashLongString).isEqualTo(testStringLong.hashCode().toString(16))
    }

    @Test
    fun `test that String hashing with CRC32C yields different result as java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.CRC32C)
        assertThat(hashShortString).isNotEqualTo(testStringShort.hashCode().toString(16))

        val hashLongString = hashString(testStringLong, DigestMethod.CRC32C)
        assertThat(hashLongString).isNotEqualTo(testStringLong.hashCode().toString(16))
    }

    @Test
    fun `test that results of hash methods adhere to expected format for digester`() {
        var hashResults: MutableSet<String>
        digestMethodPatterns.forEach { digestMethod, pattern ->
            hashResults = mutableSetOf()
            // nested helper method for assertion
            fun assertHashFormat(input: String) {
                // assert that results adhere to expected format
                with(hashString(input, digestMethod)) {
                    assertThat(pattern.matches(this))
                        .`as`("$digestMethod hash result [$this] does not match \"${pattern.pattern}\"; input = \"$input\"")
                        .isTrue
                }
                // duplicates (collisions) are definitely possible and acceptable, especially for java hash and CRC32(c)
                // but with the limited numbers of this test, we know that there should be no collisions
                assertThat(hashResults.add(input))
                    .`as`("$digestMethod gives duplicate result!")
                    .isTrue
            }

            var strMinimal = ""
            repeat(10) {
                assertHashFormat(strMinimal)
                strMinimal += " "
            }

            assertHashFormat(testStringShort)

            var strLong = testStringLong
            repeat(40) {
                strLong += "something"
                assertHashFormat(strLong)
            }
        }
    }

    @Test
    fun `verify that all algorithms yield a hex String`() {
        DigestMethod.values().forEach {
            with(hashString("some string", it)) {
                // correct format succeeds; wrong format would cause NumberFormatException
                this.toBigInteger(radix = 16)
            }
        }
    }

    @Test
    fun `test that Object hashing with javaHashCode yields same result as java hashCode`() {
        listOf(Any(), LocalDateTime.now(), Random.nextInt(), Random.nextBytes(200)).forEach {
            assertThat(javaHashString(it)).isEqualTo(it.hashCode().toString(16))
        }
    }

}