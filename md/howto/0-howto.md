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
* [Exclude (omit) properties](#exclude-omit-properties)
  * [Specific properties](#exclude-specific-properties)
  * [By a global config setting](#exclude-properties-by-adding-a-filter-to-kutes-default-settings)
* [Add extra properties or values](#add-extra-properties-or-values)
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
  * [By a global config setting](#by-a-global-config-setting-1)
* [Have properties sorted](#have-properties-sorted)
  * [Alphabetically](#alphabetically)
  * [Custom](#custom)
* [Include companion properties](#include-companion-properties)
* [Have Kute's error messages directed to log framework](#have-kutes-error-messages-directed-to-log-framework)

<hr>

# Kute `asString`: How to ...

* ## [Start using Kute `asString()`→](1-start-using-kute-asstring.md)
   [Here](1-start-using-kute-asstring.md) your find directions for:
   * #### Kute & build tools: Gradle / Maven
   * #### Start using `asString()` in your code

* ### [Use `asString()` from Java →](use-asstring-with-java.md)
    **Kute** `asString()` is designed from scratch with Kotlin in mind.
    Even so, it can be used very well with Java.
    * In general, [the API documentation](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/as-string.html) focuses on usage with Kotlin though
    * [Consult this explanation](use-asstring-with-java.md) to get some guidance on:
       * How the Java representation can differ seemingly from what you find in the API-documentation
       * Some specifics on how to use **Kute** `asString()` with Java

* ### [Exclude (omit) properties →](omit-values.md)
   Basically, there are 2 ways how to exclude properties from `asString()` output:
   * #### Exclude specific properties
   * #### Exclude properties by adding a filter to Kute's default settings
  Both ways are described in the documentation on [omitting properties](omit-values.md).
  > Make sure to read it when you are using `asString()` with database entities and/or when your model features parent-child relationships, to avoid **performance issues** and messy output.

* ### Add extra properties or values →

* ### Modify representation of property values →
    * #### Mask / replace / obscure property values
    * #### Surround (delimit) property values

* ### Use `AsStringBuilder` →

* ### Configure default settings →

* ### Avoid performance issues →

* ### Limit or extend...
    * #### ... the maximum length of property's String representation →
    * #### ... the maximum number of elements of collections, arrays, etc. →

* ### Include identity hashes to object's String representation →

* ### Prefer existing `toString()` →
    * #### Specific classes
    * #### By a global config setting

* ### Have properties sorted →
    * #### Alphabetically
    * #### Custom

* ### Include companion properties →

* ### Have Kute's error messages directed to log framework →
