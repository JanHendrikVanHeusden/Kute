---
title: PropertyValueSurrounder
---
//[kute](../../../index.html)/[nl.kute.asstring.annotation.option](../index.html)/[PropertyValueSurrounder](index.html)



# PropertyValueSurrounder

enum [PropertyValueSurrounder](index.html) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[PropertyValueSurrounder](index.html)&gt; 

Enum to define prefixes and postfixes, to distinguish property value Strings from other text. E.g.

```kotlin
class MyClass {
    public val my1stVal: String = "first value"
    public val my2ndVal: String = "second value"
    public val myNullableVal: String? = null
}
// ...
asStringConfig().withSurroundPropValue(PropertyValueSurrounder.`«»`).applyAsDefault()
println(MyClass().asString()) // MyClass(my1stVal=«first value», my2ndVal=«second value», myNullableVal=null)
```


- 
   Some pre/postfixes may look a bit exotic, e.g. `¶¶`, `÷÷`, `¦¦`, this is done deliberately: these do not appear usually in normal text, so these will clearly set the value String apart. It's a matter of choice which to choose; or use [NONE](-n-o-n-e/index.html) for no prefix/postfix.
- 
   A few pre/postfixes are convenient, yet not recommended, e.g. `[]`, `{}`, `()`, `''`, `""`, `` ` ``. These are somewhat confusing because they are also used in various other stuff; e.g. toString or [nl.kute.asstring.core.asString](../../nl.kute.asstring.core/as-string.html) of [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html), [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html); SQL-Strings; plain texts, etc.
- 
   Some pre/postfixes use Ascii codes 128 - 255, these may not render properly on non-UTF-8 systems (all should render correctly on Unicode / UTF-8 compatible systems and on Microsoft Windows, though).




**Java:** Backtick-ed names, e.g.  `` `«»`, `^^`, `~~` `` cannot be used directly in Java code. The [Companion](-companion/index.html) object contains aliases for these, to allow usage of these in Java.



#### Parameters


jvm

| | |
|---|---|
| prefix | The prefix (if any) to be concatenated at the start of a property's String value |
| postfix | The postfix (if any) to be concatenated at the end of a property's String value |



## Entries


| | |
|---|---|
| [NONE](-n-o-n-e/index.html) | [jvm]<br>[NONE](-n-o-n-e/index.html)<br>No pre/postfix: empty Strings |
| [«»](«»/index.html) | [jvm]<br>[«»](«»/index.html)<br>prefix = `«`*(Ascii 171)*, postfix = `»`*(Ascii 187)*. In Java, use [Companion.GUILLEMETS](-companion/-g-u-i-l-l-e-m-e-t-s.html) |
| [^^](^^/index.html) | [jvm]<br>[^^](^^/index.html)<br>prefix/postfix = `^`*(Ascii 94)*. In Java, use [Companion.CARETS](-companion/-c-a-r-e-t-s.html) |
| [~~](~~/index.html) | [jvm]<br>[~~](~~/index.html)<br>prefix/postfix = `~`*(Ascii 126)*. In Java, use [Companion.TILDES](-companion/-t-i-l-d-e-s.html) |
| [¦¦](¦¦/index.html) | [jvm]<br>[¦¦](¦¦/index.html)<br>prefix/postfix = `¦`*(Ascii 166)*. In Java, use [Companion.BROKEN_PIPES](-companion/-b-r-o-k-e-n_-p-i-p-e-s.html) |
| [||]([124][124]/index.html) | [jvm]<br>[||]([124][124]/index.html)<br>prefix/postfix = `|`*(Ascii 124)*. In Java, use [Companion.PIPES](-companion/-p-i-p-e-s.html) |
| [§§](§§/index.html) | [jvm]<br>[§§](§§/index.html)<br>prefix/postfix = `§`*(Ascii 167)*. In Java, use [Companion.PARAGRAPHS](-companion/-p-a-r-a-g-r-a-p-h-s.html) |
| [¶¶](¶¶/index.html) | [jvm]<br>[¶¶](¶¶/index.html)<br>prefix/postfix = `¶`*(Ascii 182)*. In Java, use [Companion.ALINEA_SIGNS](-companion/-a-l-i-n-e-a_-s-i-g-n-s.html) |
| [÷÷](÷÷/index.html) | [jvm]<br>[÷÷](÷÷/index.html)<br>prefix/postfix = `÷`*(Ascii 247)*. In Java, use [Companion.DIVISION_SIGNS](-companion/-d-i-v-i-s-i-o-n_-s-i-g-n-s.html) |
| [##](##/index.html) | [jvm]<br>[##](##/index.html)<br>prefix/postfix = `#`*(Ascii 35)*. In Java, use [Companion.HASH_SIGNS](-companion/-h-a-s-h_-s-i-g-n-s.html) |
| [&quot;&quot;]([34][34]/index.html) | [jvm]<br>[&quot;&quot;]([34][34]/index.html)<br>prefix/postfix = `"`*(Ascii 34)*. In Java, use [Companion.DOUBLE_QUOTES](-companion/-d-o-u-b-l-e_-q-u-o-t-e-s.html) |
| [''](''/index.html) | [jvm]<br>[''](''/index.html)<br>prefix/postfix = `'`*(Ascii 39)*. In Java, use [Companion.SINGLE_QUOTES](-companion/-s-i-n-g-l-e_-q-u-o-t-e-s.html) |
| [ANGLED_BRACKETS](-a-n-g-l-e-d_-b-r-a-c-k-e-t-s/index.html) | [jvm]<br>[ANGLED_BRACKETS](-a-n-g-l-e-d_-b-r-a-c-k-e-t-s/index.html)<br>prefix = `<`*(Ascii 60)*, postfix = `>`*(Ascii 62)* |
| [SQUARE_BRACKETS](-s-q-u-a-r-e_-b-r-a-c-k-e-t-s/index.html) | [jvm]<br>[SQUARE_BRACKETS](-s-q-u-a-r-e_-b-r-a-c-k-e-t-s/index.html)<br>prefix = `[`*(Ascii 91)*, postfix = `]`*(Ascii 93)* |
| [()](()/index.html) | [jvm]<br>[()](()/index.html)<br>prefix = `(`*(Ascii 40)*, postfix = `)`*(Ascii 41)*. In Java, use [Companion.PARENTHESES](-companion/-p-a-r-e-n-t-h-e-s-e-s.html) |
| [{}]({}/index.html) | [jvm]<br>[{}]({}/index.html)<br>prefix = `{`*(Ascii 123)*, postfix = `}`*(Ascii 125)*. In Java, use [Companion.BRACES](-companion/-b-r-a-c-e-s.html) |
| [BACKTICKS](-b-a-c-k-t-i-c-k-s/index.html) | [jvm]<br>[BACKTICKS](-b-a-c-k-t-i-c-k-s/index.html)<br>prefix/postfix = ``` ` ```*(Ascii 96)* */ |
| [**]([42][42]/index.html) | [jvm]<br>[**]([42][42]/index.html)<br>prefix/postfix = `*`*(Ascii 42)*. In Java, use [Companion.ASTERISKS](-companion/-a-s-t-e-r-i-s-k-s.html) |


## Types


| Name | Summary |
|---|---|
| [Companion](-companion/index.html) | [jvm]<br>object [Companion](-companion/index.html)<br>Contains constants for Java compatibility |


## Properties


| Name | Summary |
|---|---|
| [entries](entries.html) | [jvm]<br>val [entries](entries.html): [EnumEntries](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.enums/-enum-entries/index.html)&lt;[PropertyValueSurrounder](index.html)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-372974862%2FProperties%2F863300109) | [jvm]<br>val [name](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-372974862%2FProperties%2F863300109): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-739389684%2FProperties%2F863300109) | [jvm]<br>val [ordinal](../../nl.kute.hashing/-digest-method/-m-d5/index.html#-739389684%2FProperties%2F863300109): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [postfix](postfix.html) | [jvm]<br>val [postfix](postfix.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [prefix](prefix.html) | [jvm]<br>val [prefix](prefix.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |


## Functions


| Name | Summary |
|---|---|
| [valueOf](value-of.html) | [jvm]<br>fun [valueOf](value-of.html)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [PropertyValueSurrounder](index.html)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.html) | [jvm]<br>fun [values](values.html)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[PropertyValueSurrounder](index.html)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |

