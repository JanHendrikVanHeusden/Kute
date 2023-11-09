| [← 🏠](../../)                   | [← README.md](../../README.md) |
|:---------------------------------|:-------------------------------|
| [How to...]() (<i>this page</i>) | [→ FAQ](../../md/faq/0-faq.md) |
<hr>

* ### API documentation
    * [→ API docs <u>root</u>](https://janhendrikvanheusden.github.io/Kute/index.html)
    * [→ API docs of <u>`asString()`</u>](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/as-string.html)
<hr>

## Contents
* [Goal / introduction of Kute `asString()`](#goal--introduction-of-kute-asstring)
* [Start using Kute `asString()`](#start-using-kute-asstring-)
  * [Kute & build tools: Gradle / Maven](#kute--build-tools-gradle--maven)
  * [Start using `asString()` in your code](#start-using-asstring-in-your-code)
* [Use `asString()` from Java](#use-asstring-from-java-)
* [Exclude (omit) properties](#exclude-properties-)
  * [Omit specific properties](#omit-specific-properties)
  * [By applying a filter](#exclude-properties-by-applying-filters)
* [Include extra properties or values](#include-extra-properties-or-values-)
* [Modify representation of property values](#modify-representation-of-class--property-values)
  * [Mask / replace / obscure property values](#hide-replace-or-obscure-property-values-)
  * [Surround (delimit) property values](#surround--delimit--property-values-)
* [Use `AsStringBuilder`](#use-asstringbuilder-)
* [Configure default settings](#configure-default-settings-)
* [Avoid performance issues](#avoid-performance-issues)
* [Limit or extend...](#limit-or-extend)
  * [... the maximum length of property's String representation](#-the-maximum-length-of-propertys-string-representation)
  * [... the maximum number of elements of collections, arrays, etc.](#-the-maximum-number-of-elements-of-collections-arrays-etc)
* [Include identity hashes to object's String representation](#include-identity-hashes-to-objects-string-representation)
* [Prefer existing `toString()`](#prefer-existing-tostring)
  * [Specific classes](#specific-classes)
  * [By a global config setting](#by-a-global-config-setting)
* [Have properties sorted](#have-properties-sorted)
  * [Alphabetically](#sorted-alphabetically)
  * [Custom](#apply-custom-sorting)
* [Include companion properties](#include-companion-properties)
* [Have Kute's error messages directed to log framework](#have-kutes-error-messages-directed-to-a-log-framework)

<hr>

# Kute `asString`: How to ...

<hr>

### Goal & introduction of **Kute** `asString()`
As introduced in the [`README.md`→ ](../../README.md), **Kute** `asString()` is meant as:<br>
> An ease-of-use, untroubled drop-in replacement for `toString()`, without introducing any transitive dependencies other than Kotlin (JVM, reflection) itself.<br>
> More on that in [`README.md`→ ](../../README.md), or in [How is Kute better than others →](../kute-better-details.md).<br>

**Kute** `asString()` is designed with sensible defaults, so you can start using it **with very little effort**.
<br>See section [Start using Kute `asString()`](#start-using-kute-asstring-).

Still, it may be a good idea to have a glance over **Kute** `asString()`'s options.
> Especially, it may be worthwhile to have a look at the section on [avoiding performance issues](#avoid-performance-issues)

<hr>

* ## [Start using Kute `asString()` →](1-start-using-kute-asstring.md)
   [Here →](1-start-using-kute-asstring.md) your find directions for:
   * #### Kute & build tools: Gradle / Maven
   * #### Start using `asString()` in your code

* ### [Use `asString()` from Java →](use-asstring-with-java.md)
    **Kute** `asString()` is designed from scratch with Kotlin in mind.
    Even so, it can be used very well with Java.
    * In general, [the API documentation →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/as-string.html) focuses on usage with Kotlin though
    * Consult [this explanation →](use-asstring-with-java.md) to get some guidance on:
       * How the Java representation can differ seemingly from what you find in the API-documentation
       * Some specifics on how to use **Kute** `asString()` with Java

* ### [Exclude properties →](omit-values.md)
   Basically, there are 2 ways how to exclude properties from `asString()` output:
   * #### Omit specific properties
   * #### Exclude properties by applying filters
   Both ways are described in the documentation on [excluding (omitting) properties →](omit-values.md).
   > **Performance & relationships**<br> 
   Make sure to read it when you are using `asString()` with database entities and/or when your model features mutual parent-child relationships,<br>
   to avoid **performance issues** and messy output.

* ### [Include extra properties or values →](add-extra-values.md)
   You may want to include extra values in your `asString()` output, e.g. variable values, properties of a related object, static values, plain text, etc.<br>
   Click [here →](add-extra-values.md) to read more.

* ### Modify representation of class & property values
    * #### [Modify output of `asString()` →](modify-output-of-asstring.md)
       * #### [Hide, replace or obscure property values →](hide-replace-obscure-property-values.md)
       * #### [Surround (delimit) property values →](delimit-property-values.md)

* ### [Configure default settings →](configure-default-settings.md)
  * To **fine-tune** the output of `asString()` globally, according to your preferences
  * To avoid **performance issues**

* ### [Use `AsStringBuilder` →](use-asstringbuilder.md)
  * To control, on a per-class basis, what properties (or other values) are included in (or excluded from) your `asStringOutput`

* ### Avoid performance issues
  Performance issues should not occur from **Kute** `asString()` itself, but might occur from side effects.
   > Such performance issues may occur as well with IDE-generated `toString()`-methods,<br>
   > e.g. in relationships with `FetchType.LAZY` in JPA.<br>

   The <u>**main advice**</u> with regard to performance, is to **exclude properties** that, when fetched, would cause such side effects.<br>
   This can be done [on a per-property basis →](omit-values.md#exclude-omit-specific-properties), or [by applying a filter globally →](omit-values.md#exclude-properties-by-adding-a-filter-to-kutes-default-settings) (recommended).<br><br>
  
    * [**This document on excluding values →**](omit-values.md#below-some-real-world-examples-that-may-help-avoid-real-world-performance-issues-with-jpa) details how to exclude such properties. It has real-world examples on how to avoid performance issues by using filters.<br><br>
  * **Kute** `asString()` uses reflection to resolve property values. Saying goes that reflection is slow.<br>
    However, there's not much difference in getting a value from a property than getting a value by calling a getter.<br><br>
    
  In most cases, **Kute** `asString()` **is at least as fast** as IDE-generated `toString()`-methods.<br>
    **[Kute asString() performance →](asstring-performance.md)** gives more details on performance, caching / optimizations.<br> It also refers to micro-benchmark tests of **Kute** `asString()` (in comparison with others).

* ### Limit or extend...
    * #### ... the maximum length of property's String representation
    * #### ... the maximum number of elements of collections, arrays, etc.
  
  This can be done on a per-property or a per-class basis (_snippet below_), or [by means of global configuration →](configure-default-settings.md).

   ```
   @AsStringOption(propMaxStringValueLength = 20, elementsLimit = 5)
   class LengthyProps {
       val str600 = RandomStringUtils.randomAlphabetic(600)
       @AsStringOption(propMaxStringValueLength = 5)
       val bigBigDecimal = BigDecimal(RandomStringUtils.randomNumeric(400).trimStart('0'))
       val bigList: List<Int> = (0..1_000).toList()
       val hugeList: List<Int> = (0..10_000).toList()
       // Something like LengthyProps(bigBigDecimal=69525..., bigList=[0, 1, 2, 3, 4, ...], hugeList=[0, 1, 2, 3, 4, ...], str600=yOdvFyEuJSGFwoAnSUQx...)
       override fun toString(): String = asString()
   }
   ```
   See
    * [Kute ways to modify the output of `asString()` →](modify-output-of-asstring.md)
    * [Configure default settings →](configure-default-settings.md)

* ### Include identity hashes to object's String representation
  See [Kute ways to modify the output of `asString()` →](modify-output-of-asstring.md)

* ### Prefer existing `toString()`
  See [Prefer `toString()` vs. dynamic property resolution →](prefer-existing-tostring.md).<br>
  Usage models:
    * #### Specific classes
    * #### By a global config setting

* ### Have properties sorted
  See [Sorting properties →](sort-properties.md)
    * #### Sorted alphabetically
    * #### Apply custom sorting

* ### Include companion properties
  See [Include companion properties →](include-companion-properties.md)

* ### Have Kute's error messages directed to a log framework
  See [How to have Kute's error messages directed to a log framework →](direct-kute-messages-to-my-logging-framework.md)
