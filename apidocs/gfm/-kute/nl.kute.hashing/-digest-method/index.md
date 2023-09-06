//[Kute](../../../index.md)/[nl.kute.hashing](../index.md)/[DigestMethod](index.md)

# DigestMethod

enum [DigestMethod](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[DigestMethod](index.md)&gt; 

Hash algorithms; typical usage within Kute is to avoid exposing sensitive or personally identifiable data in logging etc.

- 
   **Note that hashing data like this is** ***NOT*** **meant to be a security mechanism.**     It is merely a way to avoid that plain text data is exposed in logging etc.,     and should only be used like that.
   
   
   
   Securing data would require much more hardening than this library is intended for.
- 
   Given that, the hashing algorithms of this enum are selected for practicality: compact output (at most 40) and performance; not in the first place for security.

#### Parameters

jvm

| | |
|---|---|
| instanceProvider | A lambda to supply the message digester (e.g., an instance of [java.security.MessageDigest](https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html) or of [java.util.zip.CRC32C](https://docs.oracle.com/javase/8/docs/api/java/util/zip/CRC32C.html)) |

#### See also

| |
|---|
| [AsStringHash](../../nl.kute.asstring.annotation.modify/-as-string-hash/index.md) |

## Entries

| | |
|---|---|
| [JAVA_HASHCODE](-j-a-v-a_-h-a-s-h-c-o-d-e/index.md) | [jvm]<br>[JAVA_HASHCODE](-j-a-v-a_-h-a-s-h-c-o-d-e/index.md)<br>Simply using the java hashCode. Length is 1 to 8 when represented as a hex String. Security is nearly absent |
| [CRC32C](-c-r-c32-c/index.md) | [jvm]<br>[CRC32C](-c-r-c32-c/index.md)<br>CRC32C is a fast hash methods with compact output (length 1 to 8), but security is nearly absent. |
| [SHA1](-s-h-a1/index.md) | [jvm]<br>[SHA1](-s-h-a1/index.md)<br>SHA1 is considered as one of the better performing hash methods; but not considered secure. |
| [MD5](-m-d5/index.md) | [jvm]<br>[MD5](-m-d5/index.md)<br>MD5 is considered as one of the better performing hash methods; but not considered secure. |

## Properties

| Name | Summary |
|---|---|
| [name](-m-d5/index.md#-372974862%2FProperties%2F-1216412040) | [jvm]<br>val [name](-m-d5/index.md#-372974862%2FProperties%2F-1216412040): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](-m-d5/index.md#-739389684%2FProperties%2F-1216412040) | [jvm]<br>val [ordinal](-m-d5/index.md#-739389684%2FProperties%2F-1216412040): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [valueOf](value-of.md) | [jvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [DigestMethod](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [jvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[DigestMethod](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
