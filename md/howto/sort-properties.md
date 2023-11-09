| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Sorting properties

* [Do you need sorting?](#do-you-need-sorting)
* [2 ways to sort properties](#2-ways-to-sort-properties)
  * [Alphabetically](#alphabetically)
  * [By some custom order](#by-some-custom-order)
    * [<u>Kute's pre-defined implementations</u>](#ukutes-pre-defined-implementationsu)
    * [<u>Write it yourself</u><br>](#uwrite-it-yourself)
    * [Example 1: nulls first](#example-1-nulls-first)
    * [Example 2: `@Id` first](#example-2-id-first)
  * [Kute pre-defined implementations of `PropertyRanking`](#kute-pre-defined-implementations-of-propertyranking)


### Do you need sorting?
* If your classes have a not too large number of properties, and not too verbose values, sorting might not be needed
* But if you ever had to work with entities with 100+ or even 200+ properties `:-()` you will definitely appreciate the option to have them sorted.

## 2 ways to sort properties
* **[Alphabetically](#alphabetically)**
   * Easy, but limited 
* **[By some custom order](#by-some-custom-order)**
   * A bit more involved, but more possibilities to suit your needs 

<hr>

### Alphabetically
Having properties sorted alphabetically, by name, is really simple.

* On a **per-class basis**<br>
  Simply annotate your class with `@AsStringClassOption(sortNamesAlphabetic = true)`
   ```
   // Will cause asString() to yield properties in order a, p, u, x
   @AsStringClassOption(sortNamesAlphabetic = true)
   class MyClass {
       val x = "x"
       val a = "a"
       val p = "p"
       val u: UUID = UUID.fromString("502192e9-638c-41cb-ab67-e2082f07705f")
   }
   ```
  This is the recommended way to sort alphabetically: you only apply the annotation to classes with lots of properties.<br><br>

* **As a general setting**<br>
  Simply use the snippet below:
  ```
  asStringConfig()
      .withPropertiesAlphabetic(true)
      .applyAsDefault()
  ```
  This is a convenient way to apply it application-wide.<br>
  See [Configure default settings →](configure-default-settings.md) for more on applying application-wide defaults.

<hr>

### By some custom order

To make custom ordering work, you first need an implementation (subclass) of<br>
[`nl.kute.asstring.property.ranking.PropertyRanking`](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.property.ranking/-property-ranking/index.html)

#### <u>Kute's pre-defined implementations</u>

**Kute** **provides some pre-defined implementations** that you may want to use.
> See package `nl.kute.asstring.property.ranking.impl`<br>
> Also summarized in the table of [**Kute** pre-defined implementations of `PropertyRanking`](#kute-pre-defined-implementations-of-propertyranking) below.

Say you want to use `PropertyRankingByType`, to have properties ordered by their type,<br>
and you want to have it applied application-wide:
```
AsStringConfig()
    .withPropertySorters(PropertyRankingByType::class)
    .applyAsDefault()
```
That's it !<br>
If you want to have it applied to a single class, you can use `@AsStringClassOption`:
```
@AsStringClassOption(propertySorters = [PropertyRankingByType::class])
class MyClassWithManyProperties { .... }
```
You can also combine classes, also in combination with alphabetic sorting:
```
@AsStringClassOption(sortNamesAlphabetic = true, propertySorters = [PropertyRankingByType::class, PropertyRankingByStringValueLength])
class MyClassWithManyProperties { .... }
```
This will have the effect that properties are sorted:
1. By type
2. Within the same type: by their value length (i.e., by the length of the property's `asString()`-result)
3. Within the same type and value length: alphabetically

#### <u>Write it yourself</u><br>
It's quite easy to roll your own implementation of `PropertyRanking`, if you prefer other characteristics.<br>
See the examples below ([here](#example-1-nulls-first) and [here](#example-2-id-first))

1. The class must have a <u>zero-arg constructor</u><br><br>

2. You need to <u>implement method `getRank()`</u>:<br>
`override public fun getRank(propertyValueMeta: PropertyValueMeta): Int`
   * Based on the `PropertyValueMeta` input, it must return an `Int`-value.
   * A lower value means: the property comes first in the `asString()`-result of the class.

`PropertyValueMeta` has these public properties:

| Signature                   | Description                                                                                        |
|:----------------------------|----------------------------------------------------------------------------------------------------|
| `objectClass: KClass<*>`    | The class of the object of which you want the properties to be sorted                              |
| `objectClassName: String?`  | The class-name of the object _(may be null for synthetic classes etc.)_                            |
| `packageName: String?`      | The package-name of the object _(may be null for synthetic classes etc/)_                          |
| `property: KProperty<*>`    | The property for which a rank (ordering) is to be returned                                         |
| `propertyName: String`      | The property's name                                                                                |
| `returnType: KType`         | The return-type of the property                                                                    |
| `isBaseType: Boolean`       | Is the return-type of the property a base-type?<br>E.g. numbers, date/time, `String`, `Char`, etc. |
| `isCharSequence: Boolean`   | Is the return-type of the property a `CharSequence` (`String`, `StringBuilder`, etc.)?             |
| `isCollectionLike: Boolean` | Is the return-type of the property collection-like?<br>(`Collection`, `Map`, `Array`, etc.)        |
| `isNull: Boolean`           | Has the property a `null`-value?                                                                   |
| `stringValueLength: Int?`   | The length of the property-value's `asString()` representation                                     |
___

#### Example 1: nulls first
1. You want to have `null`-values always rendered first in `asString()` output.<br>
   **Kute** does not have such implementation, so you have to write it yourself 
2. After the nulls, you want to use **Kute**'s pre-defined `PropertyRankingByType` to order them according to their return type
3. Within the same type, you want to have them sorted by value length, with **Kute**'s pre-defined `PropertyRankingByStringValueLength`
4. You want to apply this globally

Then your implementation might look like this:
```
class RankNullsFirst: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int =
        if (propertyValueMeta.isNull) 0 else 1
}
asStringConfig()
    .withPropertySorters(RankNullsFirst::class, PropertyRankingByType::class, PropertyRankingByStringValueLength::class)
    .applyAsDefault()
```

#### Example 2: `@Id` first
This is slightly more involved, due to some limitations of Kotlin's reflection.

1. If your property is annotated with JPA's `@Id`, you want it to be rendered first.
2. After that, you want to use **Kute**'s pre-defined `PropertyRankingByType` to order them according to their return type
3. Within the same type, you want to have them ordered alphabetically

Your first thought may be this:

<s>

```
class RankIdAnnotationFirst: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int =
        if (propertyValueMeta.property.hasAnnotation<Id>()) 0 else 1
}
```

</s>

But that won't work: JPA's `@Id` annotation has meta-annotation `@Target({METHOD, FIELD})`<br>
So it can't apply to a property; it can apply either to a _field_ or to a _getter_.

If you use this annotation on a Kotlin property, it will implicitly apply to the property's backing field.<br>
Kotlin's reflection does not disclose fields, so we have to switch to Java's reflection:

```
class RankIdAnnotationFirst: PropertyRanking() {
    override fun getRank(propertyValueMeta: PropertyValueMeta): Int =
        if (propertyValueMeta.property.javaField?.isAnnotationPresent(Id::class.java) == true) 0 else 1
}

asStringConfig()
    .withPropertiesAlphabetic(true)
    .withPropertySorters(RankIdAnnotationFirst::class, PropertyRankingByType::class)
    .applyAsDefault()
```
Resolved!<br>
___

For more information / inspiration, you can have a look at the documentation and/or the source code<br> of **Kute**'s pre-defined implementations of `PropertyRanking`.

### Kute pre-defined implementations of `PropertyRanking`
| Name                                                                                                                                                                                  | Goal                                                                                                                                                                                                                                                      |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [`PropertyRankingByType` →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.property.ranking.impl/-property-ranking-by-type/index.html)                             | Ranking in this order: <ol><li>Basic types (e.g. numbers, date/time, `Char`, etc.)</li> <li>`String`</li><li>Collection-like types (`Collection`, `Map`, `Array`, etc.)</li></ol>                                                                         |
| [`PropertyRankingByStringValueLength` →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.property.ranking.impl/-property-ranking-by-string-value-length/index.html) | Ranking by the length of the property's `asString()`-result<br>E.g.: `"short val"` before `"loooong value"`                                                                                                                                               |
| [`PropertyRankingByCommonNames` →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.property.ranking.impl/-property-ranking-by-common-names/index.html)              | Applies ranking by a combination of chararcteristics. E.g.:<ul><li>properties with `id` first</li><li>then properties that end at `Id`</li><li>then base types (e.g. numbers, date/time, `Char`, etc.)</li></ul>and so on, based on type and value length |

