package nl.kute.hashing

import nl.kute.util.toByteArray
import nl.kute.util.toHex
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.charset.Charset
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.random.Random

internal class HashingTest {

    // HexFormat is introduced in Java 17; and we want to be able to run on Java 11+
    // private val hexFormat: HexFormat = HexFormat.of()

    private val testStringShort = "my test string"

    // String of length ~335k
    private val testStringLong = mutableListOf("abcdefghijklmnopqrstuvwxyz1234567890-=`~!@#$%^&*()_+{}[];:',./<>?")
        .also { list -> repeat(5000) { list.add(list[0]) } }.joinToString()

    /** Expected output patterns by digest method */
    private val digestMethodPatterns =
        mapOf(
            DigestMethod.JAVA_HASHCODE to Regex("^[a-f0-9]{8}$"),
            DigestMethod.CRC32C to Regex("^[a-f0-9]{1,8}$"),
            DigestMethod.SHA1 to Regex("^[a-f0-9]{40}$"),
            DigestMethod.MD5 to Regex("^[a-f0-9]{32}$")
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
            (1..4).forEach {
                // Lengths up to 1_048_576 (generation & assertion of strings larger than that takes too long)
                val length = 2.0.pow((it*5).toDouble()).toInt()
                repeat(2) {
                    assertHashFormat(RandomStringUtils.random(length))
                }
            }
        }
    }

    @Test
    fun `test MD5 hash`() {
        // as verified by an online MD5 generator
        assertThat(hashString("This is a string to test MD5", DigestMethod.MD5, Charset.defaultCharset()))
            .isEqualTo("f38ee19be50bdd71d7a64066f60fcf24")
    }

    @Test
    fun `test SHA1 hash`() {
        // as verified by an online SHA1 generator
        assertThat(hashString("This is a string to test SHA1", DigestMethod.SHA1, Charset.defaultCharset()))
            .isEqualTo("f8e1709fdc12217e524496eafbcb110b1a9485b9")
    }

    @Test
    fun `test CRC32C hash`() {
        // as verified by an online CRC32C generator
        assertThat(hashString("This is a string to test CRC32C", DigestMethod.CRC32C, Charset.defaultCharset()))
            .isEqualTo("59c26997")
    }

    @Test
    fun `test that String hashing with JAVA_HASHCODE digest yields same result as java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.JAVA_HASHCODE)
        // HexFormat is introduced in Java 17; and we want to be able to run on Java 11+
        // assertThat(hashShortString).isEqualTo(hexFormat.toHexDigits(testStringShort.hashCode()))
        assertThat(hashShortString).isEqualTo(testStringShort.hashCode().toByteArray().toHex())

        val hashLongString = hashString(testStringLong, DigestMethod.JAVA_HASHCODE)
        // assertThat(hashLongString).isEqualTo(hexFormat.toHexDigits(testStringLong.hashCode()))
        assertThat(hashLongString).isEqualTo(testStringLong.hashCode().toByteArray().toHex())
    }

    @Test
    fun `test that String hashing with CRC32C yields different result as java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.CRC32C)
        // HexFormat is introduced in Java 17; and we want to be able to run on Java 11+
        // assertThat(hashShortString).isNotEqualTo(hexFormat.toHexDigits(testStringShort.hashCode()))
        assertThat(hashShortString).isNotEqualTo(testStringShort.hashCode().toByteArray().toHex())

        val hashLongString = hashString(testStringLong, DigestMethod.CRC32C)
        // assertThat(hashLongString).isNotEqualTo(hexFormat.toHexDigits(testStringLong.hashCode()))
        assertThat(hashLongString).isNotEqualTo(testStringLong.hashCode().toByteArray().toHex())
    }

    @Test
    fun `test that Object hashing with javaHashCode yields same result as java hashCode`() {
        listOf(Any(), LocalDateTime.now(), Random.nextInt(), Random.nextBytes(200)).forEach {
            // HexFormat is introduced in Java 17; and we want to be able to run on Java 11+
            // assertThat(javaHashString(it)).isEqualTo(hexFormat.toHexDigits(it.hashCode()))
            assertThat(javaHashString(it)).isEqualTo(it.hashCode().toByteArray().toHex())
        }
    }

    @ParameterizedTest
    @EnumSource(DigestMethod::class)
    fun `test that encodings are taken into account as expected`(digestMethod: DigestMethod) {
        val str1 = "¥⒂"
        val str2 = "¬"
        if (digestMethod == DigestMethod.JAVA_HASHCODE) {
            // java hash is wilfully ignorant of encoding
            val hash1GB2312 = hashString(str1, digestMethod, charset = Charset.forName("GB2312"))
            val hash1UTF8 = hashString(str1, digestMethod, charset = Charsets.UTF_8)
            assertThat(hash1GB2312).isEqualTo(hash1UTF8)
        }
        else {
            // other digesters take character set in account
                val hash1GB2312 = hashString(str1, digestMethod, charset = Charset.forName("GB2312"))
                val hash1UTF8 = hashString(str1, digestMethod, charset = Charsets.UTF_8)
                assertThat(hash1GB2312).isNotEqualTo(hash1UTF8)

                val hash2ISO88591 = hashString(str2, digestMethod, Charsets.ISO_8859_1)
                val hash2UTF8 = hashString(str2, digestMethod, Charsets.UTF_8)
                assertThat(hash2ISO88591).isNotEqualTo(hash2UTF8)
        }
    }

    @ParameterizedTest
    @EnumSource(DigestMethod::class)
    fun `test null safety of hashing`(digestMethod: DigestMethod) {
        assertThat(hashString(null, digestMethod, Charsets.UTF_8)).isNull()
    }

}