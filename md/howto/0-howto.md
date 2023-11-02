| [← 🏠](../../)                   | [← README.md](../../README.md) |
|:---------------------------------|:-------------------------------|
| [How to...]() (<i>this page</i>) | [→ FAQ](../../md/faq/0-faq.md) |
<hr>

* ### API documentation
    * [→ API docs <u>root</u>](https://janhendrikvanheusden.github.io/Kute/index.html)
    * [→ API docs of <u>`asString()`</u>](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/as-string.html)
<hr>

## Contents
* [Start using Kute `asString()`](#start-using-kute-asstring)
  * [Kute & build tools: Gradle / Maven](#kute--build-tools-gradle--maven)
  * [Start using `asString()` in your code](#start-using-asstring-in-your-code)
* [Use `asString()` from Java](#use-asstring-from-java)
* [Exclude (omit) properties](#exclude-properties)
  * [Omit specific properties](#omit-specific-properties)
  * [By applying a filter](#exclude-properties-by-applying-filters)
* [Include extra properties or values](#include-extra-properties-or-values)
* [Modify representation of property values](#modify-representation-of-property-values)
  * [Mask / replace / obscure property values](#mask--replace--obscure-property-values)
  * [Surround (delimit) property values](#surround-delimit-property-values)
* [Use `AsStringBuilder`](#use-asstringbuilder)
* [Configure default settings](#configure-default-settings)
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
* [Have Kute's error messages directed to log framework](#have-kutes-error-messages-directed-to-log-framework)

<hr>

# Kute `asString`: How to ...

* ## [Start using Kute `asString()`](1-start-using-kute-asstring.md)
   [Here](1-start-using-kute-asstring.md) your find directions for:
   * #### Kute & build tools: Gradle / Maven
   * #### Start using `asString()` in your code

* ### [Use `asString()` from Java](use-asstring-with-java.md)
    **Kute** `asString()` is designed from scratch with Kotlin in mind.
    Even so, it can be used very well with Java.
    * In general, [the API documentation](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/as-string.html) focuses on usage with Kotlin though
    * Consult [this explanation](use-asstring-with-java.md) to get some guidance on:
       * How the Java representation can differ seemingly from what you find in the API-documentation
       * Some specifics on how to use **Kute** `asString()` with Java

* ### [Exclude properties](omit-values.md)
   Basically, there are 2 ways how to exclude properties from `asString()` output:
   * #### Omit specific properties
   * #### Exclude properties by applying filters
   Both ways are described in the documentation on [excluding (omitting) properties](omit-values.md).
   > **Performance & relationships**<br> 
   Make sure to read it when you are using `asString()` with database entities and/or when your model features mutual parent-child relationships, to avoid **performance issues** and messy output.

* ### [Include extra properties or values](add-extra-values.md)
   You may want to include extra values in your `asString()` output, e.g. variable values, properties of a related object, static values, plain text, etc.<br>
   Click [here](add-extra-values.md) to read more.

* ### Modify representation of class & property values
    * #### [Modify output of `asString()`](modify-output-of-asstring.md)
       * #### [Hide, replace or obscure property values](hide-replace-obscure-property-values.md)
       * #### [Surround (delimit) property values](delimit-property-values.md)

* ### Use `AsStringBuilder`

* ### Configure default settings

* ### Avoid performance issues

* ### Limit or extend...
    * #### ... the maximum length of property's String representation
    * #### ... the maximum number of elements of collections, arrays, etc.

* ### Include identity hashes to object's String representation

* ### Prefer existing `toString()`
    * #### Specific classes
    * #### By a global config setting

* ### Have properties sorted
    * #### Sorted alphabetically
    * #### Apply custom sorting

* ### Include companion properties

* ### Have Kute's error messages directed to log framework
