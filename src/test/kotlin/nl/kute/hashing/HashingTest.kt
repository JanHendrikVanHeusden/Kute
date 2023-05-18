package nl.kute.hashing

import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.util.HexFormat
import kotlin.math.pow
import kotlin.random.Random

internal class HashingTest {

    private val hexFormat: HexFormat = HexFormat.of()
    private val testStringShort = "my test string"

    // String of length ~335k
    private val testStringLong = mutableListOf("abcdefghijklmnopqrstuvwxyz1234567890-=`~!@#$%^&*()_+{}[];:',./<>?")
        .also { list -> repeat(5000) { list.add(list[0]) } }.joinToString()

    /** Expected output patterns by digest method */
    private val digestMethodPatterns =
        mapOf(
            DigestMethod.JAVA_HASHCODE to Regex("^(\\d|[a-f]){8}$"),
            DigestMethod.CRC32C to Regex("^(\\d|[a-f]){1,8}$"),
            DigestMethod.SHA1 to Regex("^(\\d|[a-f]){40}$"),
            DigestMethod.MD5 to Regex("^(\\d|[a-f]){32}$")
        )

    @Test
    fun `test that results of hash methods adhere to expected formats`() {
        var hashResults: MutableSet<String>

        digestMethodPatterns.forEach { (digestMethod, pattern) ->
            hashResults = mutableSetOf()

            // nested helper method for assertion
            fun assertHashFormat(input: String) {
                // assert that results adhere to expected format
                with(hashString(input, digestMethod)) {
                    assertThat(this?.let { pattern.matches(it) })
                        .`as`("$digestMethod hash result [$this] does not match \"${pattern.pattern}\"; input = \"$input\"")
                        .isTrue
                    // convert from hex string to numeric; wrong format would cause NumberFormatException
                    this?.toBigInteger(radix = 16)

                    // duplicates (collisions) are definitely possible and acceptable,
                    // especially for the smaller hashes (java hash and CRC32(c)).
                    // But with the limited numbers of this test, we know that there should be no collisions
                    assertThat(hashResults.add(input))
                        .`as`("$digestMethod should not cause duplicate result!")
                        .isTrue
                }
            }

            var strMinimal = ""
            repeat(10) {
                assertHashFormat(strMinimal)
                strMinimal += " "
            }

            assertHashFormat(testStringShort)

            var strLong = testStringLong
            repeat(100) {
                strLong += "something"
                assertHashFormat(strLong)
            }
            (1..20).forEach {
                // Lengths up to 1_048_576 (generation & assertion of strings larger than that takes too long)
                val length = 2.0.pow(it.toDouble()).toInt()
                repeat(3) {
                    assertHashFormat(RandomStringUtils.random(length))
                }
            }
        }
    }

    @Test
    fun `test that String hashing with JAVA_HASHCODE digest yields same result as java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.JAVA_HASHCODE)
        assertThat(hashShortString).isEqualTo(hexFormat.toHexDigits(testStringShort.hashCode()))

        val hashLongString = hashString(testStringLong, DigestMethod.JAVA_HASHCODE)
        assertThat(hashLongString).isEqualTo(hexFormat.toHexDigits(testStringLong.hashCode()))
    }

    @Test
    fun `test that String hashing with CRC32C yields different result as java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.CRC32C)
        assertThat(hashShortString).isNotEqualTo(hexFormat.toHexDigits(testStringShort.hashCode()))

        val hashLongString = hashString(testStringLong, DigestMethod.CRC32C)
        assertThat(hashLongString).isNotEqualTo(hexFormat.toHexDigits(testStringLong.hashCode()))
    }

    @Test
    fun `test that Object hashing with javaHashCode yields same result as java hashCode`() {
        listOf(Any(), LocalDateTime.now(), Random.nextInt(), Random.nextBytes(200)).forEach {
            assertThat(javaHashString(it)).isEqualTo(hexFormat.toHexDigits(it.hashCode()))
        }
    }

    @Test
    fun `test that encodings are taken into account as expected`() {
        val str1 = "¥⒂"
        val str2 = "¬"
        // java hash is wilfully ignorant of encoding
        var hashGB2312 = hashString(str1, DigestMethod.JAVA_HASHCODE, charset = Charset.forName("GB2312"))
        var hashUTF8 = hashString(str1, DigestMethod.JAVA_HASHCODE, charset = Charset.forName("UTF8"))
        assertThat(hashGB2312).isEqualTo(hashUTF8)

        // other digesters take character set in account
        DigestMethod.values().filter { it != DigestMethod.JAVA_HASHCODE }.forEach {
            hashGB2312 = hashString(str1, it, charset = Charset.forName("GB2312"))
            hashUTF8 = hashString(str1, it, charset = Charset.forName("UTF8"))
            assertThat(hashGB2312).isNotEqualTo(hashUTF8)

            val hashISO88591 = hashString(str2, DigestMethod.MD5, Charset.forName("ISO-8859-1"))
            hashUTF8 = hashString(str2, DigestMethod.MD5, Charset.forName("UTF8"))
            assertThat(hashISO88591).isNotEqualTo(hashUTF8)
        }
    }

}