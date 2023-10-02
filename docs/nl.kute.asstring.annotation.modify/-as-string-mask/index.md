---
title: AsStringMask
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.modify](../index.html)/[AsStringMask](index.html)



# AsStringMask





@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.PROPERTY](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-p-r-o-p-e-r-t-y/index.html), [AnnotationTarget.FIELD](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-i-e-l-d/index.html)])



@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)



annotation class [AsStringMask](index.html)(val startMaskAt: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0, val endMaskAt: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = Int.MAX_VALUE, val mask: [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '*', val maskNulls: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, val minLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0, val maxLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = Int.MAX_VALUE)

The [AsStringMask](index.html) annotation can be placed on properties to indicate that the property is included in the return value of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html), but with its value masked.



- 
   Typical usage is to keep sensitive or personally identifiable out of log files etc.
- 
   This may limit exposure of such data, but on its own it must not be considered as a security feature.
- 
   [AsStringMask](index.html) is repeatable. If multiple annotations are present, they are applied in order of occurrence, with the subsequent mask working on the result of the previous one.
- - 
      If a property with [AsStringMask](index.html) is overridden, and the subclass property is also annotated with       [AsStringMask](index.html), they are applied in order from super class to subclass.




- 
   If the position of [startMaskAt](start-mask-at.html) is after that of [endMaskAt](end-mask-at.html), the full [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) will be masked.
- 
   If [minLength](min-length.html) is greater than [maxLength](max-length.html), [minLength](min-length.html) applies




#### Parameters


jvm

| | |
|---|---|
| startMaskAt | At which character index (inclusive) masking should start? Default = 0 |
| endMaskAt | At which character index (exclusive) masking should end? Default = [Int.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-a-x_-v-a-l-u-e.html) |
| mask | The char to use for masking. Default = `*` |
| maskNulls | Should nulls be masked too?<br>-     when `true` (default), nulls will be replaced by `"null"` and then be masked -     when `false`, nulls will be left as `null` |
| minLength | The minimum length of the resulting value; so masking of `"ab"` with [minLength](min-length.html) of `6` will result in `******`. Default = 0.<br>-     If [minLength](min-length.html)[Short.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-short/-m-a-x_-v-a-l-u-e.html), [Short.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-short/-m-a-x_-v-a-l-u-e.html) is used (`32767`). |
| maxLength | The maximum length of the resulting value. If less than the [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)'s length, the [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) will be truncated to the specified length. Default =[Int.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-a-x_-v-a-l-u-e.html) |



## Properties


| Name | Summary |
|---|---|
| [endMaskAt](end-mask-at.html) | [jvm]<br>val [endMaskAt](end-mask-at.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [mask](mask.html) | [jvm]<br>val [mask](mask.html): [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html) = '*' |
| [maskNulls](mask-nulls.html) | [jvm]<br>val [maskNulls](mask-nulls.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [maxLength](max-length.html) | [jvm]<br>val [maxLength](max-length.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [minLength](min-length.html) | [jvm]<br>val [minLength](min-length.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0 |
| [startMaskAt](start-mask-at.html) | [jvm]<br>val [startMaskAt](start-mask-at.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0 |
