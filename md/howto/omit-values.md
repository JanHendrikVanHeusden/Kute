| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Excluding properties
Basically, there are **2 ways** how to exclude properties from `asString()` output:
1. <b>[Exclude (omit) specific properties](#exclude-omit-specific-properties)</b>
   * [The annotation is inherited by subclasses](#subclasses-inheritance)

   Using `@AsStringOmit` is simple and straightforward.<br><br>

2. <b>[Exclude properties by adding a filter to Kute's default settings](#exclude-properties-by-adding-a-filter-to-kutes-default-settings)</b>
   * [Why? Some typical use cases](#why-some-typical-use-cases)
   * [How? Code examples](#how-code-examples)

   Excluding by means of a filter is a bit more involved, but, with a filter, you can apply the exclusion to all properties that match the filter, application-wide.

Both ways are described below, with use-cases and example-snippets.

<hr>

**Remarks**

* A 3<sup>rd</sup> alternative is to specify property exclusion [by means of `AsStringBuilder`](use-asstringbuilder.md)<br><br>

* You may want to give special attention [to the section on how to **avoid performance issues** with child-entities](#below-some-real-world-examples-that-may-help-avoid-real-world-performance-issues-with-jpa), e.g. with JPA.

<hr>

### Exclude (omit) specific properties
The easiest way to exclude a property is to add the `@AsStringOmit` annotation to the property:
```
class MyClass {
    @AsStringOmit
    val toOmit = "I will be omitted"
    val toInclude = "I will be included"
    // output: MyClass(toInclude=I will be included)
    override fun toString() = asString()
}
```
Easy enough.

#### Subclasses (inheritance)
What about inheritance / subclasses?
```
interface MyInterface {
    @AsStringOmit
    val myPropToHide: String
    val toShow: String
}
class MySubClass: MyInterface {
    override val myPropToHide =
        "My super-property is omitted, and I comply, even when not annotated by myself"
    override val toShow = "I will be shown"
    // output: MySubClass(toShow=I will be shown)
    override fun toString() = asString()
}
```
As shown above, the subclass-property inherits the annotation from the interface (or superclass).<br>
This is done intentionally, so subclasses can not (intentionally or accidentally) include properties in `asString()` output when, by design, they should be excluded.

<hr>

### Exclude properties by adding a filter to Kute's default settings

#### Why? Some typical use cases
Below some use cases where filtering out by a rule might be preferable.<br>
Below you find code examples for these.

1. You may have properties in your application, like description fields, that are potentially **verbose**, but that are not helpful in log output etc.
   * If there is a naming convention for these fields, or maybe even a specific annotation, you may exclude them<br>
    by filtering these out by a general rule rather than annotating them one-by-one with `@AsStringOmit`.
    <br><br>

2. You may have types of properties that **make no sense at** all in your logging.
    * E.g., `Gson` objects
   <br><br>

3. You may have classes (e.g. JPA-entities) with **parent-child relationships**, and your parent classes include properties that are collections of child-objects,<br>
 you typically want to exclude these children from your logging and from your `toString()` / `asString()` output.
   * Including child entities would make your logging too **verbose**
   * Without `asString()`, you might run into **endless loops** (note that `asString()` solves that problem)
   * You may run into serious **performance problems**, if your log-statement (`toString()`, `asString()`) would trigger your application to actually fetch records from the database.

<br><hr>

### How? Code examples
Filtering out properties involves a _lambda_ with the following signature: `(PropertyMeta) -> Boolean`<br>
The class `PropertyMeta` (referred to in the lambda argument), has the following attributes:

| Property           | Type           |
|--------------------|----------------|
| `property`         | `KProperty<*>` |
| `propertyName`     | `String`       |
| `returnType`       | `KType`        |
| `isBaseType`       | `Boolean`      |
| `isCollectionLike` | `Boolean`      |
| `isCharSequence`   | `Boolean`      |

**Filters to exclude properties should be applied when your application starts.**
> For instance, in a Spring Boot application, a logical choice would be to apply your filters in the `Application` class,<br>
> in a method annotated with `@PostConstruct`..

<br><hr>

### <u>Examples</u>

<hr>

* **Example 1: Filtering out Strings by their property name**

    The filter below omits properties with name `description` that are of type `CharSequence`, application-wide:

    ```
    val propFilter = { p -> p.propertyName == "description" && p.isCharSequence }
    asStringConfig().withPropertyOmitFilters(propFilter).applyAsDefault()
    ```

<hr>

* **Example 2: Filtering out properties with certain return types (by class or package)**
  
    You may have `Gson`-type properties in your classes, e.g., as result of `GsonBuilder()... .create()`.<br>
    Their `asString()`output is _extremely verbose_, and makes no sense for your application.
     <br><br>
    The filters below omit properties of type `Gson` or `GsonBuilder`:

    ```
    val gsonFilter: PropertyMetaFilter =
        { m -> m.returnType.classifier == Gson::class || m.returnType.classifier == GsonBuilder::class}
     asStringConfig().withPropertyOmitFilters(gsonFilter).applyAsDefault()
    ```
    Or, by package name:
    > more versatile because it filters out anything in the `gson` package, including subpackages

    ```
    val gsonPackageName = Gson::class.java.packageName
    val gsonFilter: PropertyMetaFilter =
        { m -> m.returnType.classifier.let { it is KClass<*> && it.java.packageName.startsWith(gsonPackageName) }}
    asStringConfig().withPropertyOmitFilters(gsonFilter).applyAsDefault()

    ```
  
    Or, you may even decide that anything that's not from your own domain should not be included:
    
    ```
    val notOurCodeFilter: PropertyMetaFilter =
        { m -> m.returnType.classifier.let { it is KClass<*> && ! it.java.packageName.startsWith("my.app.") }}
    asStringConfig().withPropertyOmitFilters(notOurCodeFilter).applyAsDefault()
    ```

<br>
<hr>

### Below some real-world examples that may help avoid real-world performance issues with JPA

>NB:<br>The examples below (3a, 3b) can well be combined in a single filter (by `OR`-ing the conditions),  or by applying them as separate filters
> * `asStringConfig().withPropertyOmitFilters()` has a vararg input parameter

<hr>

* #### **Example 3a: Filtering collection properties with annotation on the <u>field</u>**

  The filter below omits properties for which the following is true:<br>
  > 1. the property's class is annotated with `@Entity`<br>
  > 2. the property's return type is collection-like, e.g. `List`, `Map`, `Array` etc.<br>
  > 3. the property's **field** is annotated with `@OneToMany`<br>
  
  The code snippet below is shortened for brevity; a working example can be found in Kute's GitHub repository
  > See test class `nl.kute.asstring.filter.PropertyOmitFilteringTest`

  ``` 
  @Entity class Part(@Id val id: UUID, val name: String, @field:ManyToOne val includedIn: MutableList<Product>)

  // The @OneToMany annotation needs the site-target "field" to make filtering work!
  @Entity open class Product(@Id val id: UUID, val name: String, @field:OneToMany open val parts: List<Part>)
   
  @Entity class SubProduct(id: UUID, name: String, subProductParts: List<Part>): Product(id, name, subProductParts) {
      @OneToMany // implicitly applies to field
      override val parts: List<Part> = subProductParts
  }
  
  // create and apply the filter
  val entityFilterNoChildren: PropertyMetaFilter = { m ->
      /* I   */ m.objectClass.hasAnnotation<Entity>() &&
      /* II  */ m.isCollectionLike &&
      /* III */ m.property.javaField?.isAnnotationPresent(OneToMany::class.java) == true
  }
  asStringConfig().withPropertyOmitFilters(entityFilterNoChildren).applyAsDefault()
  
  // Object instantiation left out for brevity
  // Output below something like this:
  //   Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=My fancy product)
  //   SubProduct(id=d014eb80-8323-4106-816c-c025d2def974, name=the sub product)
  println(product.asString())
  println(subProduct.asString())
  ```

  There is a lot going on in the snippet above, especially to make the filtering on annotation `OneToMany` work.
  > Depending on your code base, you may leave out condition `III`, to simplify things.<br>
  > In that cases, all collection-like properties in `@Entity` classes are filtered out, should that satisfy your requirements.
  
  1. **Sub**classes need to be annotated with `@Entity` as well, to let the filter work for them too.<br>
     If that's not feasible, you need to perform the check for each superclass as well within the filter.
      <br><br>
  2. Targets the filters to be collection-like, e.g. `Collection`, `Map`, `Array`, `array`, `IntArray`, etc.
     <br><br> 
  3. JPA's `@OneToMany`, `@ManyToOne`, etc. are defined with `@Target({METHOD, FIELD})`.<br>
     So they apply to either fields or getter methods, but, alas, **not to properties**.<br>
      * If we apply the `@OneToMany` in `Product` without site-target `field:`, it would implicitly target the getter.
      * In `SubProduct`, there is no ambiguity, the `@OneToMany` will implicitly apply to the field
         * Making it explicit by adding `field:` may be a good idea, though
      * In this example, we are filtering by field.<br>
        However, Kotlin's reflection does not advertise fields, so we have to switch to Java's reflection.

<hr>

* #### **Example 3b: Filtering collection properties with annotation on the <u>getter</u>**

  The filter below omits properties for which the following is true:<br>
  > 1. the property's class is annotated with `@Entity`<br>
  > 2. the property's return type is collection-like, e.g. `List`, `Map`, `Array` etc.<br>
  > 3. the property's **getter** is annotated with `@OneToMany`<br>

  The code snippet below is shortened for brevity; a working example can be found in Kute's GitHub repository.
  > See test class `nl.kute.asstring.filter.PropertyOmitFilteringTest`

  ``` 
  @Entity class Part(@Id val id: UUID, val name: String, @get:ManyToOne val includedIn: MutableList<Product>)

  // The @OneToMany annotation needs the site-target "get" to make filtering work!
  @Entity open class Product(@Id val id: UUID, val name: String, @get:OneToMany open val parts: List<Part>)
   
  @Entity class SubProduct(id: UUID, name: String, subProductParts: List<Part>): Product(id, name, subProductParts) {
      @get:OneToMany // implicitly applies to field, so needs explicit site target
      override val parts: List<Part> = subProductParts
  }
  
  // create and apply the filter
  val entityFilterNoChildren: PropertyMetaFilter = { m ->
      /* I   */ m.objectClass.hasAnnotation<Entity>() &&
      /* II  */ m.isCollectionLike &&
      /* III */ m.property.getter.hasAnnotation<OneToMany>()
  }
  asStringConfig().withPropertyOmitFilters(entityFilterNoChildren).applyAsDefault()
  
  // Object instantiation left out for brevity
  // Output below something like this:
  //   Product(id=ce479863-9763-4de5-b1c6-196be2ecca12, name=My fancy product)
  //   SubProduct(id=d014eb80-8323-4106-816c-c025d2def974, name=the sub product)
  println(product.asString())
  println(subProduct.asString())
  ```

  Also, a lot going on in the snippet above, to make the filtering on annotation `OneToMany` work.
  > Depending on your code base, you may leave out condition `III`, to simplify things.<br>
  > In that cases, all collection-like properties in `@Entity` classes are filtered out, should that satisfy your requirements.

    1. **Sub**classes need to be annotated with `@Entity` as well, to let the filter work for them too.<br>
       If that's not feasible, you need to perform the check for each superclass as well within the filter.
       <br><br>
    2. Targets the filters to be collection-like, e.g. `Collection`, `Map`, `Array`, `array`, `IntArray`, etc.
       <br><br>
    3. JPA's `@OneToMany`, `@ManyToOne`, etc. are defined with `@Target({METHOD, FIELD})`.<br>
       So they apply to either fields or getter methods, but, alas, **not to properties**.<br>
        * If we apply the `@OneToMany` in `Product` without site-target `get:`, it would implicitly target the constructor parameter.
        * If we apply the `@OneToMany` in `SubProduct` without site-target `get:`, it would implicitly target the field.
        * Kotlin's reflection does advertise getters, so, unlike with fields, we don't have to switch to Java's reflection here.

<hr>
