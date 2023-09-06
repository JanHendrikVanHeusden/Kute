//[Kute](../../../index.md)/[nl.kute.asstring.annotation.modify](../index.md)/[AsStringReplace](index.md)

# AsStringReplace

@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.PROPERTY](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-p-r-o-p-e-r-t-y/index.html), [AnnotationTarget.FIELD](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-f-i-e-l-d/index.html)])

@[Inherited](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/Inherited.html)

annotation class [AsStringReplace](index.md)(val pattern: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val replacement: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val isRegexpPattern: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

The [AsStringReplace](index.md) annotation can be placed on properties to indicate that the property is included in the return value of [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.md), but with its value modified. This can be done either by means of regular expression replacement, or by means of literal replacement.

- 
   Typical usage is to keep value parts with sensitive or personally identifiable out of logging etc.; e.g.
- - 
      in IBAN European bank numbers you may want to keep the country and bank identifiers, but hide the personal part
   - 
      in a URL, you may for instance keep the URL data but leave out query parameters (or the opposite)
- 
   This may limit exposure of such data, but on its own it must not be considered as a security feature.
- 
   [AsStringReplace](index.md) is repeatable. If multiple annotations are present, they are applied in order of occurrence, with the subsequent replacements working on the result of the previous one.
- - 
      If a property with [AsStringReplace](index.md) is overridden, and the subclass property is also annotated with       [AsStringReplace](index.md), they are applied in order from super class to subclass.

As always with regular expressions:

- 
   Design your expressions properly; stay away from so-called catastrophic backtracking!
   
   
   
   [https://regex101.com/r/iXSKTs/1/debugger](https://regex101.com/r/iXSKTs/1/debugger),     [https://www.regular-expressions.info/catastrophic.html](https://www.regular-expressions.info/catastrophic.html)

#### Parameters

jvm

| | |
|---|---|
| pattern | The expression to replace, either as regular expression (when [isRegexpPattern](is-regexp-pattern.md) is `true`; default), or as a literal (when [isRegexpPattern](is-regexp-pattern.md) is `false`). Each occurrence will be replaced by [replacement](replacement.md).     * Invalid regular expression will result in an empty String being returned. |
| replacement | The replacement; back references are allowed. Default is an empty string `""`<br>-     If [isRegexpPattern](is-regexp-pattern.md) is `true`, regex group capture is supported, Java style (`$1`, `$2` etc.). |
| isRegexpPattern | -     If `true` (default), the [pattern](pattern.md) will be considered a [Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html) pattern -     If `false`, the [pattern](pattern.md) and [replacement](replacement.md) will be treated as a literals. |

## Properties

| Name | Summary |
|---|---|
| [isRegexpPattern](is-regexp-pattern.md) | [jvm]<br>val [isRegexpPattern](is-regexp-pattern.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [pattern](pattern.md) | [jvm]<br>val [pattern](pattern.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [replacement](replacement.md) | [jvm]<br>val [replacement](replacement.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
