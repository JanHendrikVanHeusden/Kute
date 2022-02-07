package nl.kute.hashing

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class HashTest {

    private val testStringShort = "my test string"
    // String of length 335k
    private val testStringLong = mutableListOf("abcdefghijklmnopqrstuvwxyz1234567890-=`~!@#$%^&*()_+{}[];:',./<>?")
        .also { list -> repeat(5000) { list.add(list[0]) } }.joinToString()

    @Test
    fun `hash - java hash`() {
        val pattern = "^-?(\\d|[a-z]){8}$"

        val hashShortString = hash(testStringShort, DigestMethod.JAVA_HASHCODE)
        assertThat(hashShortString).isEqualTo(testStringShort.hashCode().toString(16))
        assertThat(hashShortString).matches(pattern)

        val hashLongString = hash(testStringLong, DigestMethod.JAVA_HASHCODE)
        assertThat(hashLongString).isEqualTo(testStringLong.hashCode().toString(16))
        assertThat(hashLongString).isNotEqualTo(hashShortString)
        assertThat(hashLongString).matches(pattern)
    }

    @Test
    fun `hash - CRC32C`() {
        val pattern = "^-?(\\d|[a-z]){1,8}$"

        val hashShortString = hash(testStringShort, DigestMethod.CRC32C)
        assertThat(hashShortString).isNotEqualTo(testStringShort.hashCode().toString(16))
        assertThat(hashShortString).matches(pattern)

        val hashLongString = hash(testStringLong, DigestMethod.CRC32C)
        assertThat(hashLongString).isNotEqualTo(testStringLong.hashCode().toString(16))
        assertThat(hashLongString).isNotEqualTo(hashShortString)
        var str = testStringLong
        repeat(40) {
            str += "something"
            assertThat(hash(str, DigestMethod.CRC32C)).matches(pattern)
        }
    }

    @Test
    fun `hash - SHA-1`() {
        val pattern = "^(-?(\\d|[a-z]){39,40})$"

        val hashShortString = hash(testStringShort, DigestMethod.SHA1)
        assertThat(hashShortString).matches(pattern)

        val hashLongString = hash(testStringLong, DigestMethod.SHA1)
        assertThat(hashLongString).isNotEqualTo(hashShortString)
        var str = testStringLong
        repeat(40) {
            str += "something"
            assertThat(hash(str, DigestMethod.SHA1)).matches(pattern)
        }
    }

    @Test
    fun `hash - MD5`() {
        val pattern = "^(-?(\\d|[a-z]){31,32})$"

        val hashShortString = hash(testStringShort, DigestMethod.MD5)
        assertThat(hashShortString).matches(pattern)

        val hashLongString = hash(testStringLong, DigestMethod.MD5)
        assertThat(hashLongString).isNotEqualTo(hashShortString)
        var str = testStringLong
        repeat(40) {
            str += "something"
            assertThat(hash(str, DigestMethod.MD5)).matches(pattern)
        }

    }

}