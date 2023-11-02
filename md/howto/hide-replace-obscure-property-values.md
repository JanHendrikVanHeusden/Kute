| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Hide, replace, or obscure property values

**Kute** offers various ways to hide, replace or obscure properties.
> Searching for other things than hiding / replacing / obscuring?<br>
> See [Modify output of `asString()` →](modify-output-of-asstring.md)

These are specified on a per-property basis, using the annotations in package<br>`nl.kute.asstring.annotation.modify`:

* [`@AsStringOmit`](#asstringomit)
* [`@AsStringMask`](#asstringmask)
* [`@AsStringHash`](#asstringhash)
* [`@AsStringReplace`](#asstringreplace)
 
> In Java, you can annotate the **field** (by absence of properties in Java)

These specific at **property level** are described below.

These annotations are well-documented by their [API-documentation](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.modify/index.html).<br>
The descriptions below gives some more context about **usage** or **code snippets** of property-level annotations.

<hr>

* ### `@AsStringOmit`
  The goal of `@AsStringOmit` is simply to exclude a property from `asString()` output.<br>
  See [Exclude properties →](omit-values.md) for examples (and alternatives).<br><br>
  
  **Usage remarks:**
  * `@AsStringOmit` can not be "overridden".<br>
    So when a property is declared in an `interface` and it is annotated `@AsStringOmit`,<br>
    all its implementing properties down the hierarchy are omitted from `asString()` output,<br>
    regardless whether or not they are annotated with `@AsStringOmit`.

<hr>

* ### `@AsStringMask`
  With `@AsStringMask`, as the name says, you can mask property values, completely, or partially.
  > Think of **_Personally Identifying Information_**, **_bank account numbers_**, **_passport numbers_**, **_password hashes_**, etc. that you better keep out of your log files.<br>
  > In many cases, you will even have a <u>legal obligation to keep this kind of data out of your log files</u>; e.g. because of EU **GDPR**-regulation.
  
  ```
  @AsStringMask(startMaskAt = 8, endMaskAt = 14)
  val iBan: String = "NL29ABNA4408407916"
  // resulting output of asString(): iBan=NL29ABNA******7916
  ```
  **Usage remarks:**
  * By default (i.e., no parameter values specified), `@AsStringMask` fully masks the  value.
  * `AsStringMask` has various options, e.g.:
     * negative values (to count back from the end of the String instead of from the start)
     * choice of a different mask character
     * limit the output length
  * `@AsStringMask` is annotated `@Repeatable`
    * If multiple `@AsStringMask` annotations are specified, these are applied in order of declaration.
  * `@AsStringMask` can not be "overridden"; additional masks can be added down the hierarchy, though.<br> E.g.:
    ```
    open class MaskVal(@AsStringMask(2, 4) open val toBeMasked: String)
    class MaskValSub(@AsStringMask(5, 7) override val toBeMasked: String): MaskVal(toBeMasked)
    // result: MaskValSub(toBeMasked=12**5**890)
    println(MaskValSub("1234567890").asString())
    ```

  More details: see [the kDoc of `@AsStringMask`](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.modify/-as-string-mask/index.html).

<hr>

* ### `@AsStringHash`
   `@AsStringHash` has a similar goal as `@AsStringMask`: to hide plain values from prying eyes.
  ```
  @AsStringHash
  val hashed: String = "a string that should be hashed"
  // resulting output of asString(): hashed=#76afb354#
  ```
  The following hashing algorithms can be chosen:
   * `CRC32C` (default)
   * Java hashcode
   * `SHA1`
   * `MD5`
  
  > * For non-string objects, the `toString()` value is used as input for the hash algorithm.
  > * Anyone aware of safety will notice that these are not really cryptographically safe algorithms. That's not the goal, actually.<br>
  The goal is just to keep prying eyes from literal values.<br>
  So the algorithms are chosen for performance and brevity of output, rather than being hacker-proof.
  > * Hashing has the advantage that it completely obscures the value, but the hash value can still be traced throughout log files,<br>
    insofar the String value to be hashed stays the same (like with most personal data).
  >    * But do not confuse the hash of a property value with the identity hash code of Java (and Kotlin JVM) objects 

  Example with SHA1:
  ```
  @AsStringHash(DigestMethod.SHA1)
  val hashed: String = "a string that should be hashed"
  // resulting output of asString(): hashed=#11b5988542bd6baccf82d2d7dd4ca6148945e42b#
  ```

  **Usage remarks:**
  * `@AsStringHash` can not be "overridden".<br>
    So when a property is declared in an `interface` and it is annotated `@AsStringHash`,<br>
    all its implementing properties down the hierarchy are hashed with the algorithm specified at the top level annotation,<br>
    regardless whether or not they are annotated with `@AsStringHash`.

<hr>

* ### `@AsStringReplace`
   `@AsStringReplace` does what you'd expect by its name: replacing (parts of) strings in `asString()` output.

  * By default, `@AsStringReplace` replaces **regular expression patterns**.<br>
    A somewhat contrived example:
    ```
    @AsStringReplace("""\s\s+""", " ")
    // All sequences of more than one whitespace will be replaced by a single space
    val aStringWithWhiteapace = "I  have too     many whitespaces  \t !   !"
    ```
    Or, more realistic:
    ```
    @AsStringReplace("""\s*([a-zA-Z]{2})\s*\d{2}\s*[a-zA-Z]{4}\s*((\d|\s){6})(.*)""", """$1\99 BANK *****$4""")
    // Result: iban=NL99 BANK *****0 7916
    val iban: String = "NL29 ABNA 6708 40 7916"
    ```
  
  * `@AsStringReplace` can also be used to replace literals.<br>
    The following example shows this, and also demonstrates that the annotation is `@Repeatable`:
    ```
    // Line endings and tabs will be replaced by a single space each
    @AsStringReplace("\n", " ")
    @AsStringReplace("\r", " ")
    @AsStringReplace("\t", " ")
    val spacey = "I have \n some \r\n line endings and \t also \t tabs, but I will get rid of \r them"
    ```
  
  **Usage remarks:**
     * `@AsStringReplace` is annotated `@Repeatable`
       * If multiple replacements are specified, these are applied in order of declaration.
     * When `@AsStringReplace` is used with a regular expression pattern (default), the regular expression will be compiled only once.<br>
       The resulting `Regex` is cached for reuse, to optimize performance.
     * As always with regular expressions, test and review them thoroughly to avoid performance issues<br>
       (e.g. because of so-called "_catastrophic backtracking_")
     * Syntax errors in patterns for `@AsStringReplace` will result in empty string output for the property involved. The exception will be logged, but not rethrown.
     * `@AsStringReplace` can not be "overridden"; more replacements can be added down the hierarchy, though. E.g.:
       ```
       open class ReplaceMyVal(@AsStringReplace("bc", "BC") open val toBeReplaced: String)
       class ReplaceValSub(@AsStringReplace("fg", "FG") override val toBeReplaced: String): ReplaceMyVal(toBeReplaced)
       // result: ReplaceValSub(toBeReplaced=aBCdeFGhij)
       println(ReplaceValSub("abcdefghij").asString())
       ```

<hr>

## Using `@AsStringClassOption`
`@AsStringClassOption` lets you specify some options to apply at class level, rather than at property level.<br>
It has the following parameters:

| name                                          | type                              | default        |
|-----------------------------------------------|-----------------------------------|----------------|
| [`includeIdentityHash`](#includeidentityhash) | `Boolean`                         | `false`        |
| [`toStringPreference`](#tostringpreference)   | `ToStringPreference`              | `USE_ASSTRING` |
| [`includeCompanion`](#includecompanion)       | `Boolean`                         | `false`        |
| [`sortNamesAlphabetic`](#sortnamesalphabetic) | `Boolean`                         | `false`        |
| [`propertySorters`](#propertysorters)         | `KClass<out PropertyRankable<*>>` | (empty)        |

<hr>

* #### `includeIdentityHash`
If `true`, the class's object identity is included, as a hexadecimal number.<br>
E.g.
```
@AsStringClassOption(includeIdentityHash = true)
class MyClass{ myProp: String = "my prop value" }
// Output of asString(): something like MyClass@1968a49c(myProp=my prop value)
```

The hexadecimal value is the same one that you would get when your class doesn't have `toString()` implemented.<br>
You also will see this hex value in your IDE's debugger.

<hr>

* #### `toStringPreference`
See the specific documentation page on this topic [here →](prefer-existing-tostring.md).

<hr>

* #### `includeCompanion`
By default, companion properties are not added in output of `asString()`.<br>
Specify `true` for `includeCompanion` to have them included.<br>

Due to restrictions of Kotlin's reflection, a companion object's properties are shown only if:
1. The **class that contains** the companion object must **not be private**
2. The **companion object** must be **public**
3. The companion object has at least 1 property

<hr>

* #### `sortNamesAlphabetic`


<hr>

* #### `propertySorters`

<hr>



<hr>

## Using `@AsStringOption`
