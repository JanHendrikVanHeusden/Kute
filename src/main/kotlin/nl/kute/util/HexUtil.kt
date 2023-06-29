package nl.kute.util

import java.nio.ByteBuffer

// With Java 17, we would use `java.util.HexFormat` for most of these.
// But we want to be able to run on Java 11

internal fun Long.toByteArray(): ByteArray = ByteBuffer
    .allocate(Long.SIZE_BYTES)
    .putLong(this)
    .array()


internal fun Int.toByteArray(): ByteArray = ByteBuffer
    .allocate(Int.SIZE_BYTES)
    .putInt(this)
    .array()


internal fun ByteArray.toHex(): String = joinToString(separator = "") { b -> "%02x".format(b) }
