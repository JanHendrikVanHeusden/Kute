---
title: DigestMethod
---
//[kute](../../../index.html)/[nl.kute.hashing](../index.html)/[DigestMethod](index.html)



# DigestMethod

enum [DigestMethod](index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[DigestMethod](index.html)&gt; 

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
| [AsStringHash](../../nl.kute.asstring.annotation.modify/-as-string-hash/index.html) |


## Entries


| | |
|---|---|
| [JAVA_HASHCODE](-j-a-v-a_-h-a-s-h-c-o-d-e/index.html) | [jvm]<br>[JAVA_HASHCODE](-j-a-v-a_-h-a-s-h-c-o-d-e/index.html)<br>Simply using the java hashCode. Length is 1 to 8 when represented as a hex String. Security is nearly absent |
| [CRC32C](-c-r-c32-c/index.html) | [jvm]<br>[CRC32C](-c-r-c32-c/index.html)<br>CRC32C is a fast hash methods with compact output (length 1 to 8), but security is nearly absent. |
| [SHA1](-s-h-a1/index.html) | [jvm]<br>[SHA1](-s-h-a1/index.html)<br>SHA1 is considered as one of the better performing hash methods; but not considered secure. |
| [MD5](-m-d5/index.html) | [jvm]<br>[MD5](-m-d5/index.html)<br>MD5 is considered as one of the better performing hash methods; but not considered secure. |


## Properties


| Name | Summary |
|---|---|
| [entries](entries.html) | [jvm]<br>val [entries](entries.html): [EnumEntries](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.enums/-enum-entries/index.html)&lt;[DigestMethod](index.html)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](-m-d5/index.html#-372974862%2FProperties%2F863300109) | [jvm]<br>val [name](-m-d5/index.html#-372974862%2FProperties%2F863300109): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](-m-d5/index.html#-739389684%2FProperties%2F863300109) | [jvm]<br>val [ordinal](-m-d5/index.html#-739389684%2FProperties%2F863300109): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |


## Functions


| Name | Summary |
|---|---|
| [valueOf](value-of.html) | [jvm]<br>fun [valueOf](value-of.html)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [DigestMethod](index.html)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.html) | [jvm]<br>fun [values](values.html)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[DigestMethod](index.html)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |

