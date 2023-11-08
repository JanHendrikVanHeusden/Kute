| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Kute ways to modify the output of `asString()`

### Starting point: Kotlin's `toString()` of data classes
By default, **Kute** `asString()` formats its output the same way as Kotlin's default `toString()` of data classes.
> The order of properties may be different, though

<hr>

### Tweaking the output of `asString()`
**Kute** offers various ways to modify the output of `asString()`, according to your requirements / preferences:

* [I. Globally, by configuring default settings](#i-globally-by-configuring-default-settings)
* [II. Using annotations at various levels](#ii-using-annotations-at-various-levels)
   * [At class level](#at-class-level-asstringclassoption)
   * [At class, property or method level](#at-class-property-or-method-level-asstringoption)
* [III Hide, replace, or obscure property values](#iii-hide-replace-or-obscure-property-values)

<hr>

### I. Globally, by configuring default settings
* See [Configure default settings →](configure-default-settings.md)

> <u>All configuration settings</u> can also be set per class, and/or per property, using `@AsStringOption` and `@AsStringClassOption`.<br>
> (see below under **_Using annotations at various levels_**).

<hr>

### II. Using annotations at various levels

   **Kute** `asString()` has several annotations that can be used to modify output.<br>
   > Those at property level (mainly to hide, replace or obscure property values), are described <br>
   > in section [III Hide, replace, or obscure property values](#iii-hide-replace-or-obscure-property-values).

   **Those that can be applied at class or property level, are:**
   1. At class level: `@AsStringClassOption`
   2. At class, property or method level: `@AsStringOption`
      * `@AsStringOption` can also be used to annotate a `toString()` method.<br>
        This way it also applies to the class.<br>
        Placing the annotation on any other method has no effect.

   These annotations are inherited, so if you apply it to an interface or (abstract) class, it applies to all subclasses as well.

   > **Usage / Remarks**<u>
   > 1. #### Annotation vs. global settings 
   >   </u>**All settings** of `@AsStringOption` and `@AsStringClassOption` can also be set globally,
   > by [configuring default settings →](configure-default-settings.md)<br><br><u>
   > 
   > 2. #### Inheritance / overriding
   >    </u>`@AsStringOption` and `@AsStringClassOption` are inherited by subclasses.<br>
   >    Note that for `@AsStringOption` - which can be placed at 3 levels: class, method, and proerty - there is an inheritance hierarchy,<br>
   > determined by the level where it is applied:<br><br>
   > 
   >   <u>`@AsStringOption`</u>
   >    * An `@AsStringOption` annotation <u>at class-level</u> can be overridden by another `@AsStringOption`...
   >       * ... in subclasses, at class-level, or by annotating `toString()`
   >       * ... by annotating `toString()`
   >       * ... at property-level
   >   
   >   * An `@AsStringOption` annotation of <u>`toString()`</u> can be overridden by another `@AsStringOption`...
   >       * ... in subclasses, by annotating `toString()`
   >       * ... at property-level
   >  
   >   * An `@AsStringOption` annotation at <u>property-level</u> can be overridden by another `@AsStringOption`...
   >       * ... in subclasses, by annotating the overriding property<br><br>
   > 
   >   An <u>`@AsStringClassOption`</u> annotation can be placed at class level only, and can be overridden in subclasses.


   <hr> 

   1. ### **At class level: `@AsStringClassOption`**

      See the [API-documentation of `@AsStringClassOption`  →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-as-string-class-option/index.html)

      **A summary:**
      * Include the object's identity hash in `asString()`-output
      * Prefer `toString()` over `asString()`, insofar implemented
      * Sort properties, either alphabetically or by a custom sorter
      * Include companion object's properties
   
      > #### Prefer `toString()`
      > * See [Prefer `toString()` vs. dynamic property resolution](prefer-existing-tostring.md) <br><br>
      >
      > #### Sorting properties
      > * See [Sorting properties →](sort-properties.md)<br><br>
      >
      > #### Include companion properties
      > By default, companion properties are not added in output of asString().<br>
      You can opt however to have these included.
      >
      > Due to restrictions of Kotlin's reflection, a companion object's properties are shown only if:
      > 
      > 1. The class that contains the companion object must not be private
      > 2. The companion object must be public
      > 3. The companion object has at least 1 property
      > <br><br>
      > 
      > #### Other features of `@AsStringClassOption`
      > * For the other topics mentioned above, see the [API-documentation of `@AsStringClassOption`  →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-as-string-class-option/index.html)
      
       `@AsStringClassOption` is inherited. It can be overridden by subclasses.

   2. ### **At class, property or method level: `@AsStringOption`**
   
      See the [API-documentation of `@AsStringOption`  →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-as-string-option/index.html)
      * How to show `null` values
         * Default is: `null`; but you may like something else
      * Whether you want values to be surrounded by some delimiter
         * Default is none; so a string `this is a string` will simply be shown like that, same with other things, like dates, number etc. (e.g. `3.14`, `1 November 2023`).<br>
         * Various other options are available (see [delimit property values →](delimit-property-values.md))<br>
         E.g.
            * `"this is a string"`, `"3.14"`, `"1 November 2023"`
            * `«this is a string»`, `«3.14»`, `«1 November 2023»`
      * You can limit the maximum property-value string length (default 500)
      * You can limit the maximum number of elements to be shown for `Collection`, `Map`, `Array`, etc. (default 50)
   
      <br>`@AsStringOption` is inherited, and can be overridden: see [Inheritance / overriding](#inheritance--overriding)
   
| <span style="font-weight: normal"> Note that <u>all features</u> of `@AsStringOption` and `@AsStringClassOption` can **also be set globally**:<br>see [Configure default settings →](configure-default-settings.md)</span> |
|----|

<hr>

### III Hide, replace, or obscure property values
* This can be done using the annotations in package `nl.kute.asstring.annotation.modify`:
    * `@AsStringOmit`
    * `@AsStringMask`
    * `@AsStringHash`
    * `@AsStringReplace`<br><br>

* For **usage** of those annotations:
   * See the [API-documentation →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.modify/index.html)
   * Or check out [Hide, replace, or obscure property values →](hide-replace-obscure-property-values.md) 