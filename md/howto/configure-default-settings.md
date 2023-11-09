| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

* [Configure default settings](#configure-default-settings)
  * [Goal](#goal)
  * [Settings are _not_ persisted](#settings-are-not-persisted-beyond-application-restart)
  * [Settings: categories](#settings-categories)
  * [No builder pattern](#no-builder-pattern)
* [Methods of `AsStringConfig`](#methods-of-asstringconfig)

## Configure default settings

### Goal
**Kute** `asString()` has sensible defaults for its behaviour & output formatting.<br>
So when starting to work with `asString()`, you don't have to bother much about settings.<br>

But if you have specific preferences or requirements, there are quite some settings that you can tweak.<br>

### Settings are _not_ persisted beyond application restart
Once your configuration is applied, your settings set the defaults, application-wide.

However, **note that Kute `asString()` <u>does not feature a persistency-mechanism</u>** for its settings.<br>
So your defaults should be (re-)applied every time when your application starts.
> For instance, in a Spring Boot application, a logical choice would be to apply your settings in the `Application` class, in a method annotated with `@PostConstruct`:
> ```
> @SpringBootApplication
> class Application {
>     // ...
> 
>     @PostConstruct
>     fun applyAsStringDefaults() {
>         asStringConfig()
>             .withIncludeIdentityHash(true)
>             .withSurroundPropValue(`«»`)
>             .applyAsDefault()
>     }
> }
> ```

<hr>

### Settings: categories
The default settings of **Kute** `asString()` fall into the 5 categories shown below.
> [The method-overview at the bottom of this document](#methods-of-asstringconfig) refers to these categories. 

1. #### Tweak output
   * How `null` is represented, max lengths, surrounding values, object identity hash, companion object.<br>
   For details, see the [method-overview below](#methods-of-asstringconfig), and/or the [API-documentation →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.config/-as-string-config/index.html)

2. #### Sort properties
   * Want to have properties sorted: none, alphabetically, or by custom sorting?<br>
     See [Sorting properties →](sort-properties.md)
     > Especially useful for, say, JPA entities with many properties.<br>
     You will definitely appreciate this, if you've ever had to work with entities with 100+ or even 200+ properties! `:-()` 

3. #### Prefer existing `toString()`
   * If classes have a `toString()` method implemented, should it be preferred,<br>
     or should **Kute** `asString()` prefer dynamic property resolution by `asString()`?<br>
     See [Prefer `toString()` vs. dynamic property resolution→](prefer-existing-tostring.md)

4. #### Exclude properties globally
   * You may want to filter out certain categories of properties.
     See [Excluding properties →](omit-values.md#exclude-properties-by-adding-a-filter-to-kutes-default-settings)
     > Especially child objects in parent-child relationships.<br>
     Mainly to avoid performance issues and/or cluttered output.

5. #### Apply options being set
   * `AsStringConfig` allows chained dot-notation to set multiple options in a single statement.<br>
    Make sure to always call `applyAsDefault()` to make them effective as application defaults.

### No builder pattern
Unlike with builder-pattern, `AsStringConfig` does _not_ become effectively immutable.<br>
See the following snippet:
```
// new config object
val config = asStringConfig()
    .withIncludeIdentityHash(true)
    .withSurroundPropValue(`«»`)
// applies hash & surround
config.applyAsDefault()

// same config object
config
    .withPropertiesAlphabetic(true)
    .withElementsLimit(10)
// 2nd time, same config object: applies sorting & limit, while honouring previous changes / settings
config.applyAsDefault()

// another new config object
AsStringConfig()
    .withIncludeCompanion(true)
    // applies companion, while honouring previous changes / settings
    .applyAsDefault()

// output: AsStringOption(showNullAs=null, surroundPropValue=«», propMaxStringValueLength=500, elementsLimit=10)
println(AsStringConfig().getAsStringOptionDefault().asString())

// output: AsStringClassOption(includeIdentityHash=true, toStringPreference=USE_ASSTRING, includeCompanion=true, sortNamesAlphabetic=true, propertySorters=[])
println(AsStringConfig().getAsStringClassOptionDefault().asString())
```

<hr>

## Methods of `AsStringConfig`

The table below shows the methods of `AsStringConfig` that can be used to set new defaults for **Kute** `asString()`, application wide.
> `AsStringConfig` also has _getter_-methods to get the current defaults.<br>
> These getter-methods are not shown in the table below. 

| Goal| Signature| Description / Usage |
|:----:|----|----|
| `Tweak output`| `withShowNullAs(showNullAs: String): AsStringConfig`| How `null` is represented; default: `null` |
| `Tweak output`| `withMaxPropertyStringLength(propMaxLength: Int): AsStringConfig`| Maximum length of the string-value of a property in `asString()` output.<br> Longer strings are truncated and and an ellipsis `...` is added |
| `Tweak output`| `withElementsLimit(elementsLimit: Int): AsStringConfig`| Maximum number of elements of collections, maps, arrays etc. that are included in `asString()` output. <br> Values exceeding that number are not included, and an ellipsis `...` is added |
| `Tweak output`| `withIncludeCompanion(includeCompanion: Boolean): AsStringConfig`| By default, `companion` properties are not included in `asString()` output. <br> You can specify to have these included. <br> There are some restrictions however; see [Kute ways to modify the output of `asString() →`](modify-output-of-asstring.md#include-companion-properties) |
| `Tweak output`| `withIncludeIdentityHash(includeHash: Boolean): AsStringConfig`| Include the object's identity hash (hexadecimal) in the output. <br> You may prefer this, to facilitate tracking an object through log entries. |
| `Tweak output`| `withSurroundPropValue(surroundPropValue: PropertyValueSurrounder): AsStringConfig`| By default, string representations of property values are _not_ surrounded by delimiters. <br> E.g., `a String`, `2023-11-06`, etc.. <br> You can specify delimiters (surrounders) though, e.g. to have them render them like this: <ul><li>`"a String"`<br>`"2023-11-06"`</li> <li>`«a String»`<br>`«2023-11-06»`</li> </ul> etc. |
||| |
| `Sort properties`| `withPropertiesAlphabetic(sortNamesAlphabetic: Boolean): AsStringConfig`| By default, the order that properties appear in `asString()` output is undefined. <br> You can opt to have them ordered alphabetically. |
| `Sort properties`| `withPropertySorters(vararg propertySorters: KClass<out PropertyRankable<*>>): AsStringConfig`| By default, the order that properties appear in `asString()` output is undefined. <br> You can opt to have them ordered by an implementation of `PropertyRankable`. <br> See [sorting properties →](sort-properties.md) for details. |
||| |
| Prefer existing `toString()`| `withToStringPreference(toStringPreference: ToStringPreference): AsStringConfig`| Globally prefer one of: <ul> <li> `USE_ASSTRING` (default)<br> So `asString()` does not try to call existing `toString()` methods. <br> Properties will be resolved dynamically, so maintenance-friendly. <br> Preferred in most cases. </li><br> <li> `PREFER_TOSTRING` <br> Use this if you have specific reasons to prefer existing `toString()` implementations, if present. <br> Does not honour annotations like `@AsStringOmit`, etc.<br> Less protection against endless loops in recursive models. </li> </ul> |
| Prefer existing `toString()`| `withForceToStringFilters(varag filters: (ClassMeta) -> Boolean): AsStringConfig`| Prefer existing `toString()` for properties that match one or more of the filters specified. |
| Prefer existing `toString()`| `withForceToStringFilterPredicates(vararg predicates: Predicate<in ClassMeta>): AsStringConfig`| _Functionally identical to `withForceToStringFilters()`, but more easy to use from Java code._ |
||| |
| `Exclude properties globally`| `withPropertyOmitFilters(vararg filters: (PropertyMeta) -> Boolean): AsStringConfig`| Omit properties that match one or more of filters specified. <ul> <li>Typical usage is to avoid performance issues and/or cluttered output when parent-child relationships are involved. </li> <li> [See Excluding properties →](omit-values.md#exclude-properties-by-adding-a-filter-to-kutes-default-settings) </li>  </ul> |
| `Exclude properties globally`| `withPropertyOmitFilterPredicates(vararg predicates: Predicate<in PropertyMeta>): AsStringConfig`| _Functionally identical to `withPropertyOmitFilters()`, but more easy to use from Java code._ |
||| |
| `Apply options being set`| `applyAsDefault(): AsStringConfig`| Applies the changes set by the `with...`-methods as the new default settings, application-wide. |

