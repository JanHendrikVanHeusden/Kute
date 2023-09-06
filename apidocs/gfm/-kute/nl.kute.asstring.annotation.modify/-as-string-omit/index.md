//[Kute](../../../index.md)/[nl.kute.asstring.annotation.modify](../index.md)/[AsStringOmit](index.md)

# AsStringOmit

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.PROPERTY](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-p-r-o-p-e-r-t-y/index.html), [AnnotationTarget.FIELD](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-i-e-l-d/index.html)])

@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)

annotation class [AsStringOmit](index.md)

The [AsStringOmit](index.md) annotation can be placed on properties to indicate that the property is excluded (both name and value) from the return value of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md).

- 
   Typical usage is to leave insignificant or sensitive data out of [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) representations
- - 
      It may be used to keep sensitive or personally identifiable data out of log files etc.       This may limit exposure of such data; but on its own it must **not** be considered a security feature.
- 
   When applied to a property in an interface of a super-class, it will be applied to that property in the subclass hierarchy; regardless whether or not the property is overridden, and whether or not it has its own [AsStringOmit](index.md) annotation.
