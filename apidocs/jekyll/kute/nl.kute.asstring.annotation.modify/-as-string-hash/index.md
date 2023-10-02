---
title: AsStringHash
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.modify](../index.html)/[AsStringHash](index.html)



# AsStringHash





@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.PROPERTY](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-p-r-o-p-e-r-t-y/index.html), [AnnotationTarget.FIELD](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-i-e-l-d/index.html)])



@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)



annotation class [AsStringHash](index.html)(val digestMethod: [DigestMethod](../../nl.kute.hashing/-digest-method/index.html) = DigestMethod.CRC32C)

The [AsStringHash](index.html) annotation can be placed on properties to indicate that the property is included in the return value of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html), but with its String value replaced by the String's hash value.



To distinguish it from &quot;normal&quot; String content, the hash value is surrounded by `#` characters.



- 
   Usage of a hash method allows tracking of values across multiple entries, without exposing the original value.
- 
   Typical usage is to keep sensitive or personally identifiable data out of log files etc.
- - 
      This may limit exposure of such data, but on its own it must not be considered a security feature.
- 
   When applied to property in an interface or a super-class, it will also be applied to the subclass property in the subclass hierarchy; regardless whether or not the property is overridden, and whether or not it has its own [AsStringHash](index.html) annotation.
- - 
      A different [digestMethod](digest-method.html) in an overriding property is not honoured. So if an interface property has       a [AsStringHash](index.html) annotation with [DigestMethod.CRC32C](../../nl.kute.hashing/-digest-method/-c-r-c32-c/index.html), and an implementing class specifies [DigestMethod.SHA1](../../nl.kute.hashing/-digest-method/-s-h-a1/index.html)       for that property, the annotation on the overriding property is ignored, so it will still be hashed with       [DigestMethod.CRC32C](../../nl.kute.hashing/-digest-method/-c-r-c32-c/index.html) (i.e., adhering to the annotation that is highest in the class hierarchy).




#### Parameters


jvm

| | |
|---|---|
| digestMethod | The digest algorithm to use; see [DigestMethod](../../nl.kute.hashing/-digest-method/index.html). Default = [DigestMethod.CRC32C](../../nl.kute.hashing/-digest-method/-c-r-c32-c/index.html) |



## Properties


| Name | Summary |
|---|---|
| [digestMethod](digest-method.html) | [jvm]<br>val [digestMethod](digest-method.html): [DigestMethod](../../nl.kute.hashing/-digest-method/index.html) |

