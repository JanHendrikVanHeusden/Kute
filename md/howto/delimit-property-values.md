| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

  * [Surround (delimit) property values](#surround-delimit-property-values)
    * [Design choice](#design-choice)
  * [Using delimiters with Kute `asString()`](#using-delimiters-with-kute-asstring)
    * [At class / property-level](#at-class--property-level)
    * [Globally](#globally)
  * [Usage in Java](#usage-in-java)

## Surround (delimit) property values

### Design choice

By default, **Kute** `asString()` formats its output the same way as Kotlin's default `toString()` of data classes.
> The order of properties may be different, though

**Kotlin's (and Kute `asString()`) starting point: clean strings**<br>
The designers of Kotlin thought they better present clean strings. **Kute**'s `asString()` follows this.

> Consider the following output of a `toString()`-method:
> 
> `MyClass(myStr="I am a String")`
> 
> * It would not be clear at first sight whether the content of `MyClass::myStr` is `I am a String` (without quotes), or rather `"I am a String"` (including the quotes).<br>
> * And what about data that's not a `String`?<br>
>   Should it be `"November 7, 2023"`, to indicate that it's a `String` representation,<br>
>   or `November 7, 2023`, to indicate that the data type isn't a `String`?
> * An XML-string, should it be `<?xml version="1.0"?><catalog> (...) </catalog>`?<br>
>   Or rather `"<?xml version="1.0"?><catalog> (...) </catalog>"`?<br>
>   And equivalent for SQL, YAML, JSON, etc.

Not adding quotes (or other delimiters) makes a clear point: _What You See Is What You Get_.

**The problem**<br>
That being said, if you have spaces, or `=`, `[`, `(` etc. in your properties' String representation, it may be difficult to figure out where a property starts / ends.

If you feel like that, you can have **Kute** `asString()` add delimiters.

## Using delimiters with Kute `asString()`

### At class / property-level

You can have **Kute** `asString()` apply delimiters on a per-property of per-class basis, by using `@AsStringOption`, like this:<br>

      @AsStringOption(surroundPropValue=`""`), @AsStringOption(surroundPropValue=`«»`), etc.

But you may not want to annotate all your classes one by one:
in most cases you 'd want to apply delimiters globally.

### Globally
Probably, if you decide to use delimiters, you want to have them application-wide.<br>
This document shows how to.
> You still can use `@AsStringOption(surroundPropValue= ... )` to override delimiters for individual properties / individual classes.

You can not simply use any character: **Kute** defines a pre-set choice of delimiters.<br>
The available delimiters are defined in enum [`PropertyValueSurrounder` →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-property-value-surrounder/index.html).<br>
The snippet below shows how to use double quotes as delimiters:


> **NB 1:** Note the backticks: all of these are identifiers, not literals!<br>
> [Click here for section **Usage in Java**](#usage-in-java), or if you don't like backtick'ed identifiers.
>
> **NB 2:** Some delimiters have ASCII codes **above 127**.<br>
> These delimiter characters are not usually present in normal text, which makes them good choices for delimiters.<br><br>
> _**But**_ they may not display as expected, if the environment (e.g. log server or log viewer) does not have `UTF-...` as default encoding<br>
(e.g. in Microsoft Windows environments). 

<br><br>

|      Enum entry      | Resulting prefix / postfix  | Remarks | ASCII > 127 ? |
|:--------------------:|:---------------------------:|:-------:|:-------------:|
|        `NONE`        |                             | Default |               |
|  &grave;`«»`&grave;  |          `«`  `»`           |         |      `*`      |
|  &grave;`^^`&grave;  |          `^`  `^`           |         |               |
|  &grave;`~~`&grave;  |          `~`  `~`           |         |               |
|  &grave;`¦¦`&grave;  |          `¦`  `¦`           |         |      `*`      |
| &grave;`\|\|`&grave; |         `\|`  `\|`          |         |               |
|  &grave;`§§`&grave;  |          `§`  `§`           |         |      `*`      |
|  &grave;`¶¶`&grave;  |          `¶`  `¶`           |         |      `*`      |
|  &grave;`÷÷`&grave;  |          `÷`  `÷`           |         |      `*`      |
|  &grave;`##`&grave;  |          `#`  `#`           |         |               |
|  &grave;`""`&grave;  |          `"`  `"`           |         |               |
|  &grave;`''`&grave;  |          `'`  `'`           |         |               |
|  `ANGLED_BRACKETS`   |          `<`  `>`           |         |               |
|  `SQUARE_BRACKETS`   |          `[`  `]`           |         |               |
|  &grave;`()`&grave;  |          `(`  `)`           |         |               |
|  &grave;`{}`&grave;  |          `{`  `}`           |         |               |
|     `BACKTICKS`      | <pre>&grave;  &grave;</pre> |         |               |
|  &grave;`**`&grave;  |          `*`  `*`           |         |               |

## Usage in Java
In Java, you can't work with backtick-ed identifiers.<br>
**Kute** offers alternative names to be used in Java, by means of [`PropertyValueSurrounder.Companion` →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-property-value-surrounder/-companion/index.html):<br>
> You can use them in Kotlin as well, if you don't like backtick-ed identifiers

> **NB:** Some delimiters have ASCII codes **above 127.**<br>
> These delimiter characters are not usually present in normal text, which makes them good choices for delimiters.<br><br>
> _**But**_ they may not display as expected, if the environment (e.g. log server or log viewer) does not have `UTF-...` as default encoding<br>
(e.g. in Microsoft Windows environments).

|    Identifier     | Resulting prefix / postfix  | Remarks | ASCII > 127 ? |
|:-----------------:|:---------------------------:|:-------:|:-------------:|
|      `NONE`       |                             | Default |               |
|   `GUILLEMETS `   |          `«`  `»`           |         |      `*`      |
|     `CARETS`      |          `^`  `^`           |         |               |
|     `TILDES`      |          `~`  `~`           |         |               |
|  `BROKEN_PIPES`   |          `¦`  `¦`           |         |      `*`      |
|      `PIPES`      |         `\|`  `\|`          |         |               |
|   `PARAGRAPHS`    |          `§`  `§`           |         |      `*`      |
|  `ALINEA_SIGNS`   |          `¶`  `¶`           |         |      `*`      |
| `DIVISION_SIGNS`  |          `÷`  `÷`           |         |      `*`      |
|   `HASH_SIGNS`    |          `#`  `#`           |         |               |
|  `DOUBLE_QUOTES`  |          `"`  `"`           |         |               |
|  `SINGLE_QUOTES`  |          `'`  `'`           |         |               |
| `ANGLED_BRACKETS` |          `<`  `>`           |         |               |
| `SQUARE_BRACKETS` |          `[`  `]`           |         |               |
|   `PARENTHESES`   |          `(`  `)`           |         |               |
|     `BRACES`      |          `{`  `}`           |         |               |
|    `BACKTICKS`    | <pre>&grave;  &grave;</pre> |         |               |
|    `ASTERISKS`    |          `*`  `*`           |         |               |
