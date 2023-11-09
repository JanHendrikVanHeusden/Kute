| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Prefer `toString()` vs. dynamic property resolution

* [Posing the problem](#posing-the-problem)
* [Default behaviour: `asString()` calls `asString()`](#default-behaviour-asstring-calls-asstring)
   * [Reasons for this design-choice](#reasons-for-this-design-choice)
* [Alternative: prefer `toString()`](#alternative-prefer-tostring)
   * [Per class](#per-class)
   * [Globally](#globally)
   * [By using filters](#by-using-filters)

### Posing the problem

> Hey, you just convinced us to use `asString()`, so what's this talking about preferring `toString()`?

Say, you have a class which depends on another class, like this:

```
class MyClass(val id: Long, val name: String, val details: MutableCollection<MyDetailClass>) {
    override fun toString(): String = asString()
}
class MyDetailClass(val id: Long, val detailName: String, val detailAmount: BigDecimal?, val description: String?) {
    override fun toString(): String {
        return "MyDetailClass(id=$id, detailName='$detailName', detailAmount=$detailAmount)"
    }
}
```

## Default behaviour: `asString()` calls `asString()`
Given the snippet above, the output of `println(MyClass( ... ))` might be something like this:

```
MyClass(id=3554, name=My first, details=[MyDetailClass(id=73543, detailAmount=2475.55, description=lots and lots of descriptive text, detailName=My first detail)])
```

As the output shows, `MyDetailClass.description` is included, even while this is not included in `MyDetailsClass`'s `toString()` method.

When `asString()` encounters an object with a `toString()` method, by default it calls <u>`asString()`</u> on it, ignoring the existing `toString()`.<br>

| By default, `asString()` chooses to resolve properties dynamically, regardless of an existing `toString()`-methods |
|--------------------------------------------------------------------------------------------------------------------|

### Reasons for this design-choice
* Maintenance friendly: adding, renaming, removing and refactoring of properties and classes are reflected automagically
* Improved representation of lambdas, , `Throwable`, arrays like `Object[]`, `int[]` etc.
* It allows users to control the output by means of `@AsStringOmit`, `@AsStringMask`, `@AsStringHash`, `@AsStringOption`, etc.<br>
  and by [filtering out certain property types](omit-values.md#below-some-real-world-examples-that-may-help-avoid-real-world-performance-issues-with-jpa), and other features
* `asString()` has built-in protection against endless loops in recursive models etc.

**Remarks**
> * This choice, for dynamic property resolution by `asString()` rather than calling `toString()`, only applies to your custom objects.<br>
> * For all other things **Kute**`asString()` does not try to resolve properties dynamically, but simply calls the `toString()`-method<br>
>    * Java & Kotlin built-in stuff, date/time, `UUID`, etc.
>    * Some get a special treatment, like collections, maps, arrays, lambdas, synthetic classes, `Throwable`s, etc.

<br>

___ 

## Alternative: prefer `toString()`
If you feel that `asString()` should honour existing `toString()` implementations, you can do so:
1. **Per class**
2. **Globally**
3. **By using filters**

**Remarks**
> * If you prefer `toString()`, in one way or another, and the class has <u>not implemented</u> `toString()`:
>   1. `asString()` calls the `toString()`-method of the most nearby superclass (not being `Any` or `Object`)
>   2. If no superclass has implemented `toString()`, `asString()` switches to dynamic property resolution<br>
>      by calling `asString()` on the object<br><br>
> 
> * You effectively lose features of **Kute** `asString()`, like maintenance friendliness,<br>
>   usage of `@AsStringMask` and other annotations, protection against endless loops, etc.

___

1. ### Per class
   Notice the snippet below, where now class `MyDetailClass` is annotated:
   ```
   class MyClass(val id: Long, val name: String, val details: MutableCollection<MyDetailClass>) {
       override fun toString(): String = asString()
   }
   @AsStringClassOption(toStringPreference = PREFER_TOSTRING)
   class MyDetailClass(val id: Long, val detailName: String, val detailAmount: BigDecimal?, val description: String?) {
       override fun toString(): String {
           return "MyDetailClass(id=$id, detailName='$detailName', detailAmount=$detailAmount)"
       }
   }
   ```
   Now the `toString()`-method of `MyDetailClass` is honoured: 
   ```
   MyClass(id=3554, name=My first, details=[MyDetailClass(id=73543, detailName='My first detail', detailAmount=2475.55)])
   ```

   Using `@AsStringClassOption` like above is useful when you want to prefer `toString()` for specific classes.

   <hr>

2. ### Globally
   If you prefer usage of `toString()` globally, simply use this snippet:

   ```
   AsStringConfig()
       .withToStringPreference(PREFER_TOSTRING)
       .applyAsDefault()
   ```
   This may be a good option if you have carefully crafted `toString()` implementations all over your code base, especially when they have additional business value.
   > E.g., in some web-applications, a `SESSION_ID` or request headers are added to all `toString()`-implementations.<br>
   > That's very useful information for debugging and tracing, you don't want to lose that.
   > * NB: `asString()` also offers the option to add additional values.<br>
   >   See [Including extra values in `asString()` output](add-extra-values.md)

   See also [Configure default settings](configure-default-settings.md) for more on configuring defaults.

   <hr>

3. ### By using filters
   Maybe you prefer usage of `toString()` only for specific classes, e.g. entity classes, web requests, etc.
   
   In such cases, applying one or more filters might be the best idea.<br><br>

   * Such filters have the signature `(ClassMeta) -> Boolean`
   * `ClassMeta` has the following public properties:

     | Signature                  | Description / usage                                             |
     |:---------------------------|:----------------------------------------------------------------|
     | `objectClass: KCLass<*>`   | The object's class                                              |
     | `objectClassName: String?` | The object's class name (_may be null for synthetic classes_)   |
     | `packageName: String?`     | The object's package name (_may be null for synthetic classes_) |
   
   <br>

   Say, as suggested above, that you want to have `toString()` honoured for your entity classes and your web requests.

   **Suppose that:**
    * Your entity classes all are annotated with `@Entity`
    * Your web request classes are in package `my.app.rest`, and that their names all end with `...Request`<br><br>
    
    Then your filters may be applied like this:
    ```
    val entityFilter = { meta: ClassMeta -> meta.objectClass.hasAnnotation<Entity>() }
    val requestFilter = { meta: ClassMeta -> 
        meta.objectClassName?.endsWith("Request") == true &&
        meta.packageName?.startsWith("my.app.rest") == true   
    }
    asStringConfig()
       .withForceToStringFilters(entityFilter, requestFilter)
       .applyAsDefault()
    ```

   See also [Configure default settings](configure-default-settings.md) for more on configuring defaults.
