package nl.kute.hashing

import nl.kute.util.asHexString
import nl.kute.util.hexHashCode
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.nio.charset.Charset
import java.time.LocalDateTime
import kotlin.random.Random

class HashingTest {

    private val testStringShort = "my test string"

    // String of length ~335k
    private val testStringLong = mutableListOf("abcdefghijklmnopqrstuvwxyz1234567890-=`~!@#$%^&*()_+{}[];:',./<>?")
        .also { list -> repeat(5000) { list.add(list[0]) } }.joinToString()

    /** Expected output patterns by digest method */
    private val digestMethodPatterns =
        mapOf(
            DigestMethod.JAVA_HASHCODE to Regex("^[a-f0-9]{1,8}$"),
            DigestMethod.CRC32C to Regex("^[a-f0-9]{1,8}$"),
            DigestMethod.SHA1 to Regex("^[a-f0-9]{40}$"),
            DigestMethod.MD5 to Regex("^[a-f0-9]{32}$")
        )

    @ParameterizedTest
    @EnumSource(DigestMethod::class)
    fun `results of hash methods should adhere to corresponding formats`(digestMethod: DigestMethod) {
        // arrange
        val pattern = digestMethodPatterns[digestMethod]!!
        val hashResults: MutableSet<String> = mutableSetOf()

        // nested helper method for assertion
        fun assertHashFormat(input: String) {
            // assert that results adhere to expected format
            with(hashString(input, digestMethod)) {
                assertThat(this)
                    .`as`("$digestMethod hash result [$this] should match \"${pattern.pattern}\"; input = \"$input\"")
                    .matches(pattern.pattern)
                assertThat(this!!.let { pattern.matches(it) })
                    .`as`("$digestMethod hash result [$this] should match \"${pattern.pattern}\"; input = \"$input\"")
                    .isTrue
                // convert from hex string to numeric; wrong format would cause NumberFormatException
                this.toBigInteger(radix = 16)

                // duplicates (collisions) are definitely possible and acceptable,
                // especially for the smaller hashes (java hash and CRC32(c)).
                // But with the limited numbers of this test, we know that there should be no collisions
                assertThat(hashResults.add(this))
                    .`as`("$digestMethod should not cause duplicate result!")
                    .isTrue
            }
        }

        // act, assert
        var strMinimal = ""
        repeat(10) {
            assertHashFormat(strMinimal)
            strMinimal += " "
        }

        // act, assert
        assertHashFormat(testStringShort)

        // act, assert
        var strLong = testStringLong
        repeat(10) {
            strLong += ("something" + RandomStringUtils.randomAlphabetic(2))
            assertHashFormat(strLong)
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
    fun `hashing with JAVA_HASHCODE digest should yield same result as java hashCode`() {
        val hashShortString: String = hashString(testStringShort, DigestMethod.JAVA_HASHCODE)!!
        val hcOfShort: Int = testStringShort.hashCode()
        // HexFormat is introduced in Java 17; but we want to be Java 11+ compatible
        // assertThat(hashShortString).isEqualTo(hexFormat.toHexDigits(testStringShort.hashCode()))
        assertThat(hashShortString).isEqualTo(Integer.toHexString(hcOfShort))

        val hashLongString = hashString(testStringLong, DigestMethod.JAVA_HASHCODE)
        val hcOfLong: Int = testStringLong.hashCode()
        // HexFormat is introduced in Java 17; and we want to be Java 11+ compatible
        // assertThat(hashLongString).isEqualTo(hexFormat.toHexDigits(testStringLong.hashCode()))
        assertThat(hashLongString).isEqualTo(Integer.toHexString(hcOfLong))

        repeat(100) {
            val str = RandomStringUtils.randomAlphanumeric(0, 10000)
            assertThat(hashString(str, DigestMethod.JAVA_HASHCODE)).isEqualTo(Integer.toHexString(str.hashCode()))
        }
    }

    @Test
    fun `hash result with CRC32C should differ from java hashCode`() {
        val hashShortString = hashString(testStringShort, DigestMethod.CRC32C)
        // HexFormat is introduced in Java 17; and we want to be able to run on Java 11+
        // assertThat(hashShortString).isNotEqualTo(hexFormat.toHexDigits(testStringShort.hashCode()))
        assertThat(hashShortString).isNotEqualTo(testStringShort.hashCode().asHexString)

        val hashLongString = hashString(testStringLong, DigestMethod.CRC32C)
        // assertThat(hashLongString).isNotEqualTo(hexFormat.toHexDigits(testStringLong.hashCode()))
        assertThat(hashLongString).isNotEqualTo(testStringLong.hashCode().asHexString)
    }

    @Test
    fun `object hashing with javaHashCode should yield same result as java hashCode`() {
        listOf(Any(), LocalDateTime.now(), Random.nextInt(), Random.nextBytes(200)).forEach {
            // HexFormat is introduced in Java 17; and we want to be able to run on Java 11+
            // assertThat(javaHashString(it)).isEqualTo(hexFormat.toHexDigits(it.hashCode()))
            assertThat(it.hexHashCode()).isEqualTo(it.hashCode().asHexString)
        }
    }

    @Test
    fun `hexString of null should return null`() {
        assertThat(null.hexHashCode()).isNull()
    }

    @ParameterizedTest
    @EnumSource(DigestMethod::class)
    fun `encodings should be taken into account where expected`(digestMethod: DigestMethod) {
        // arrange
        val str1 = "¥⒂"
        val str2 = "¬"
        if (digestMethod == DigestMethod.JAVA_HASHCODE) {
            // act
            // java hash is wilfully ignorant of encoding
            val hash1GB2312 = hashString(str1, digestMethod, charset = Charset.forName("GB2312"))
            val hash1UTF8 = hashString(str1, digestMethod, charset = Charsets.UTF_8)
            // assert
            assertThat(hash1GB2312).isEqualTo(hash1UTF8)
        } else {
            // act
            // other digesters take character set in account
            val hash1GB2312 = hashString(str1, digestMethod, charset = Charset.forName("GB2312"))
            val hash1UTF8 = hashString(str1, digestMethod, charset = Charsets.UTF_8)
            // assert
            assertThat(hash1GB2312).isNotEqualTo(hash1UTF8)

            // act
            val hash2ISO88591 = hashString(str2, digestMethod, Charsets.ISO_8859_1)
            val hash2UTF8 = hashString(str2, digestMethod, Charsets.UTF_8)
            // assert
            assertThat(hash2ISO88591).isNotEqualTo(hash2UTF8)
        }
    }

    @ParameterizedTest
    @EnumSource(DigestMethod::class)
    fun `hashing should be null safe`(digestMethod: DigestMethod) {
        assertThat(hashString(null, digestMethod, Charsets.UTF_8)).isNull()
    }

}