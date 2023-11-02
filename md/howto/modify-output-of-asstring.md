| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Kute ways to modify the output of `asString()`

**Kute** offers various ways to modify the output of `asString()`.

* [I. Globally, by configuring default settings](#i-globally-by-configuring-default-settings)
* [II. Using annotations at various levels](#ii-using-annotations-at-various-levels)
   * [At class level](#at-class-level)
  * [At class, property or method level](#at-class-property-or-method-level)
* [III Hide, replace, or obscure property values](#iii-hide-replace-or-obscure-property-values)

<hr>

### I. Globally, by configuring default settings
* See [Configure default settings →](configure-default-settings.md)

<u>All of these</u> can also be set per class, and/or per property (see below under **_Using annotations at various levels_**).

<hr>

### II. Using annotations at various levels
   1. #### **At class level**

      See the [API-documentation of `@AsStringClassOption`](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-as-string-class-option/index.html)

      **A summary:**
      * Include the object's identity hash in `asString()`-output
      * Prefer `toString()` over `asString()`, insofar implemented
      * Include companion object's properties
      * Sort properties, either alphabetically or by a custom sorter<br>
   
      > #### Sorting properties
      > * See [Sorting properties →](sort-properties.md)
      > 
      > #### Other features of `@AsStringClassOption`
      > * For the other topics mentioned above, see the [API-documentation of `@AsStringClassOption`](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-as-string-class-option/index.html)
      
       `@AsStringClassOption` is inherited, and can be overridden.

   2. #### **At class, property or method level**
   
      See the [API-documentation of `@AsStringOption`](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-as-string-option/index.html)
      * How to show `null` values
         * Default is: `null`; but you may like something else
      * Whether you want values to be surrounded by some delimiter
         * Default is none; so a string `this is a string` will simply be shown like that, same with other things, like dates, number etc. (e.g. `3.14`, `1 November 2023`)
         * Various other options are available (see [delimit property values →](delimit-property-values.md))<br>
         E.g.
            * `"this is a string"`, `"3.14"`, `"1 November 2023"`
            * `«this is a string»`, `«3.14»`, `«1 November 2023»`
      * You can limit the maximum property-value string length (default 500)
      * You can limit the maximum number of elements to be shown for `Collection`, `Map`, `Array`, etc. (default 50)
   
      <br>`@AsStringOption` is inherited, and can be overridden.
   
| <span style="font-weight: normal"> Note that <u>all features</u> of `@AsStringOption` and `@AsStringClassOption` can **also be set globally**:<br>see [Configure default settings →](configure-default-settings.md)</span> |
|----|

<hr>

### III Hide, replace, or obscure property values
* This can be done using the annotations in package `nl.kute.asstring.annotation.modify`:
    * [`@AsStringOmit`](#asstringomit)
    * [`@AsStringMask`](#asstringmask)
    * [`@AsStringHash`](#asstringhash)
    * [`@AsStringReplace`](#asstringreplace)<br><br>

* For usage of those annotations:
   * see the [API-documentation](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.modify/index.html)
   * ... and/or check out [Hide, replace, or obscure property values →](hide-replace-obscure-property-values.md) 