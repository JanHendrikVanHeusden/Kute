//[Kute](../../../index.md)/[nl.kute.asstring.annotation.option](../index.md)/[AsStringOption](index.md)

# AsStringOption

@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html), [AnnotationTarget.PROPERTY](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-p-r-o-p-e-r-t-y/index.html), [AnnotationTarget.FUNCTION](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-u-n-c-t-i-o-n/index.html)])

@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)

annotation class [AsStringOption](index.md)(val showNullAs: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = initialShowNullAs, val surroundPropValue: [PropertyValueSurrounder](../-property-value-surrounder/index.md) = NONE, val propMaxStringValueLength: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = initialMaxStringValueLength, val elementsLimit: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = initialElementsLimit)

The [AsStringOption](index.md) annotation can be placed:

- 
   on classes
- 
   on the toString method of classes
- 
   on properties of classes

Besides that, the [AsStringOption.defaultOption](-default-option/default-option.md) is used as a default when no explicit [AsStringOption](index.md) annotation is applied.

It allows specifying how property values are to be parsed in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) return value.

#### Parameters

jvm

| | |
|---|---|
| showNullAs | How to show nulls? Default is &quot;`"null"`&quot; (by [initialShowNullAs](../../nl.kute.asstring.core.defaults/initial-show-null-as.md)), but you may opt for something else |
| surroundPropValue | Defines prefix and postfix of the property value String. Default = [NONE](../-property-value-surrounder/-n-o-n-e/index.md).<br>-     `null` values are not pre-/postfixed. See [PropertyValueSurrounder](../-property-value-surrounder/index.md) for available surrounding pairs. |
| propMaxStringValueLength | The maximum String value length **per property**.<br>-     default is 500 (by [initialMaxStringValueLength](../../nl.kute.asstring.core.defaults/initial-max-string-value-length.md)) -     0 results in an empty String; -     negative values mean: [initialMaxStringValueLength](../../nl.kute.asstring.core.defaults/initial-max-string-value-length.md) (default value) |
| elementsLimit | limits the number of elements of collection like properties to be represented in the [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md) return value. Default = 50 (by [initialElementsLimit](../../nl.kute.asstring.core.defaults/initial-elements-limit.md))<br>-     Applies to repeating values, e.g. for [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)s, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)s, [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)s -     When capped, the resulting String is appended with ellipsis `...` -     negative values mean: [initialElementsLimit](../../nl.kute.asstring.core.defaults/initial-elements-limit.md) (default value). -     **NB**: The String representation is also capped by [propMaxStringValueLength](prop-max-string-value-length.md) |

## Types

| Name | Summary |
|---|---|
| [DefaultOption](-default-option/index.md) | [jvm]<br>object [DefaultOption](-default-option/index.md)<br>Static holder for [defaultOption](-default-option/default-option.md) |

## Properties

| Name | Summary |
|---|---|
| [elementsLimit](elements-limit.md) | [jvm]<br>val [elementsLimit](elements-limit.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [propMaxStringValueLength](prop-max-string-value-length.md) | [jvm]<br>val [propMaxStringValueLength](prop-max-string-value-length.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [showNullAs](show-null-as.md) | [jvm]<br>val [showNullAs](show-null-as.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [surroundPropValue](surround-prop-value.md) | [jvm]<br>val [surroundPropValue](surround-prop-value.md): [PropertyValueSurrounder](../-property-value-surrounder/index.md) |
