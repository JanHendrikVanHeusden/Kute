| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Using `asString()` in Java code
<hr>

[I. Differences between Kotlin API-signature and Java API-signature](#i-differences-between-kotlin-api-signature-and-java-api-signature)
<hr>

[II. Some tips about using `asString()` in Java](#ii-some-tips-about-using-asstring-in-java)
  * [1. `Any` vs. `Object`](#1-any-vs-object)
  * [2. Kute & extension methods](#2-kute--extension-methods)
  * [3. No `void` return type](#3-no-void-return-type)
  * [4. Kotlin properties vs. Java getters & setters](#4-kotlin-properties-vs-java-getters--setters)
  * [5. Static members](#5-static-members)
  * [6. Kotlin lambdas are a bit different from Java lambdas](#6-kotlin-lambdas-are-a-bit-different-from-java-lambdas)
<hr>

  * [7. Restrictions when using Kute `asString()` with Java](#7-restrictions-when-using-kute-asstring-with-java)

### I. Differences between Kotlin API-signature and Java API-signature

Using `Kute / asString()` in Java code is not particularly difficult, but the signature of Kotlin methods looks different when seen from the Java perspective, for a few reasons:

1. `Kute` heavily relies on _**extension methods**_
2. Kotlin has `Any` as the supertype-of-everything, where Java has `Object`
3. There is no `void` in Kotlin
4. Kotlin uses _**properties**_ to access fields (_fields: aka instance variables_)
    * In Java, you see _**getters**_ (`get...()`) and _**setters**_ for these
5. Kotlin does not have static members (variables and methods). Instead, there are:
    * Package-level methods and properties
    * `companion` objects (with methods and/or properties)
<br>
<br>
6. And: Kotlin lambdas are a bit different from Java lambdas

<hr>

When you consult the [→ API docs](https://janhendrikvanheusden.github.io/Kute/index.html), or when your IDE shows a method signature, you may wonder how to use the method in Java.

In that case, the following <u>brief explanation of Kotlin vs. Java</u> may be helpful:

1. #### `Any` vs. `Object`
   * Kotlin’s `Any` is pretty similar to Java’s `Object`, which means it’s the supertype of all other classes.
   * Type `Any` translates 1-to-1 to `Object` when seen from Java code (JVM)

<hr>

2. #### Extension methods
   Kotlin's _extension methods_ are in fact syntactic sugar, but very convenient.
   
   > Suppose that an extension method `String.reverse()` exists, defined as<br>
   >  `fun String.reverse(): String = StringBuilder(this).reverse()`<br>
   > * In **Kotlin**, this method could be called like this:<br>
   >    `reversed = myString.reverse()`<br>
   >    `reversed = "abc".reverse()`
   > <br><br>
   > * In **Java**, it would be like this:<br>
   > `reversed = reverse(myString)`<br>
   > `reversed = reverse("abc)`

<hr>

3. #### No `void` return type
   When consulting the API-documentation, you won't find `void` methods.
   
   Kotlin methods that do not return anything have `Unit` as implicit return type (which is somewhat comparable with the `Void` type in Java).<br>
   <br>
   `Unit` differs from `Void` insofar that `Unit` is actually instantiable.
   > * Typically, you would never construct or mention `Unit`
   >   * Except when mocking `void`-methods in Kotlin.
   > * You don't have to return `Unit` from regular Kotlin methods.
   > * Methods that do not return anything (so implicit `Unit` return type) have their return type omitted in Kotlin.<br>
   Such methods are `void`-methods when seen from the Java perspective.
   
   It makes consumer-type lambdas easier. In Kotlin, you would write a consumer like this:<br>
   `var consumer: (String) -> Unit = { doSomething(msg) }`<br>
   or simply<br>
   `var consumer = { doSomething(msg) } // type implicit, inferred by Kotlin compiler` <br>
   
   In Java, you can't write a lambda with `void` return, so you would use `Consumer`:<br>
   `Consumer<String> consumer = msg -> doSomething(msg);`

<hr>

4. #### Kotlin properties vs. Java getters & setters
   When consulting the API-documentation, you have to internally translate the Kotlin properties to what it translates to in Java.
   
   In Kotlin, you would write:
   ```
      val id: Long = ...
      var counter: Int = ...
   ```
   Translated to Java, this would be:
   ```
       private final long id = ...;
       private int counter = ...;
       
       public long getId() { return id; }
       public int getCounter() { return counter; }
       public void setCounter(final int counter) { this.counter = counter; }
   ```
   In Java code, you don't have to care about properties. You simply use the getters and setters, like you poor Java guys are accustomed to.<br>

<hr>

5. #### Static methods
   Kotlin has 2 types of "static" members:
   * **Package level members (variables, methods)**
      * Typically, you would import these as `static import` in Java.
      * Seen from the Java perspective, these are `static` methods inside a class.
      * Seen from the Kotlin perspective, these are simply standalone methods that are not bound to a specific class.
      
     In Kute, many extension methods are defined as package level methods.
   
   * **Companion object members (variables, methods)**
      * Kotlin's `companion object`s are like static nested classes in Java
         * But they are part of the Kotlin language syntax, and, if defined, the `companion object` is instantiated implicitly, 1 per class.
      * Depending on how these are created, you will see them in your Java IDE as either:
         * `static` methods inside the class, if the `companion object`'s member is annotated with `@JvmStatic`
         * Methods of a `Companion` nested class, when the member was _not_ annotated with  `@JvmStatic`
      * When no name is specified, the `companion object` is named `Companion`.
         * In Kute `asString()` 

<hr>

6. #### Kotlin lambdas are a bit different from Java lambdas
   In Java, you could write something like
   ```
   Consumer<String> aLogger = msg -> log(msg);
   Supplier<Integer> aSupplier = () -> new Random().nextInt();
   ```
   The same thing in Kotlin might look like
   ```
       val logConsumer: (String) -> Unit = { msg -> log(msg) }
       val aSupplier: () -> Int = { Random().nextInt() }
   ```
   or (with implicit type):
   ```
       val logConsumer = { msg -> log(msg) }
       val aSupplier = { Random().nextInt() }
   ```
   
   So in Kotlin you typically won't see Java types like `Consumer`, `Supplier`, `Function`, `BiFunction`, `Predicate`, etc.<br>

<hr><hr>

### II. Some tips about using `asString()` in Java

#### 1. `Any` vs. `Object`
Type `Any` translates 1-to-1 to `Object` when seen from Java code (JVM)

For instance, class `AsStringBuilder` has `var obj: Any?` as constructor argument.<br>

As far as Java is concerned:
* Kotlin's `Any` is equivalent to 'non-nullable `Object`'
* Kotlin's `Any?` is equivalent to 'nullable `Object`'

<hr>

#### 2. **Kute** & extension methods
Let's see how to use `asString()` from Java

`asString()` is an extension method.<br>
According to the API documentation, it is defined as:

`fun Any?.asString(): String`

* So a **Kotlin** `toString()` method using `asString()` is something like:<br><br>
```override fun toString() = this.asString()```<br>
or<br>
```override fun toString() = asString() // 'this' is implicit, so can be omitted```<br><br>

* In **Java**, you would write this:

```
 @override
 public String toString() {
     return asString(this); // 'this' inside the brackets
 }
 ```
 
 So be aware of implicit `this`, and make the _receiver_ object the 1st parameter.

<hr>

#### 3. No `void` return type
**Kute** has a log method, defined as:<br>
`fun Any?.log(msg: Any?)`<br>
As you would expect, it does not return anything; so from the Java perspective, it is a `void` method.

#### 4. Kotlin properties vs. Java getters & setters
You definitely recognise this if you have ever used Lombok.<br>
Or when you have your IDE generate getters / setters.

E.g, interface `PropertyMeta` in Kute has these properties:<br>
```
public val propertyName: String
public val isCollectionLike: Boolean
```

From the Java perspective, you don't use the fields, but instead you use the getters (as your IDE's code completion will show you):
`getPropertyName()` and `isCollectionLike()`<br>
Non-problematic, I guess.

<hr>

#### 5. Static members
As stated above, "static" does not really exist in Kotlin.
Still, there's a lot of stuff that is static, as seen from the Java perspective.

Your IDE will show you the proper method or property.<br>
But, be aware that the location of the method or property may be different from what you expect from the API documentation.

Examples of methods that are static from the Java perspective:
<br>
* `KuteLogConsumer.setLogConsumer()` is, from the Java perspective, a static method of class `KuteLogConsumer`.
   > In Kotlin, as the API documentation shows, it is _a method defined in the unnamed `Companion` object_ of class `KuteLogConsumer`.AsStringOption.DefaultOption.getDefaultOption()

* `AsStringOption.DefaultOption.getDefaultOption()` is, from the Java perspective, a static member of inner class `DefaultOption`.
   > In Kotlin, it is defined as a _property of the companion object_ named `DefaultOption` of class `AsStringOption`.

* `AsString.asString()` is, from the Java perspective, a static method of class `AsString()`
   > In Kotlin, it is defined as a package-level method in package `nl.kute.asstring.core`

So you may have to puzzle a bit on how to refer to "static" Kotlin stuff.
<hr>

#### 6. Kotlin lambdas are a bit different from Java lambdas

When using filters for **Kute** `asString()`, you can write this Java code:

```
new AsStringConfig()
    .withForceToStringFilters(meta -> "MyClassName".equals(meta.getObjectClassName()))
    .applyAsDefault();
```
It compiles and works nicely!

Now you may think that the argument has a `Predicate` type, like:<br>
`Predicate<ClassMeta> classMetaPredicate = meta -> meta.getObjectClassName().equals("MyClassName");`

But if you try that, it won't compile: the actual type is a Kotlin reflective type `Function1` (which you may or many not like to use in your Java code).

**Kute** `asString()` offers a workaround, so **_Java_** developers can use this alternative method, which does exactly the same:

```
Predicate<ClassMeta> myPredicateFilter = meta ->
        Objects.equals(meta.getObjectClassName(), TestClass1.class.getSimpleName());
new AsStringConfig()
        .withForceToStringFilterPredicates(myPredicateFilter)
        .applyAsDefault();
```

There are a few places in **Kute** `asString()` where lambdas are used as input parameters. In these cases, a workaround is provided for Java developers, so that the Java functional interfaces can be used (i.e., `Predicate`) instead of equivalent Kotlin types.<br>
This is clearly documented in the API specifications (KDoc / Javadoc).
<hr>
<hr>

### 7. Restrictions when using Kute `asString()` with Java
Most features of **Kute** `asString()` work in Java exactly the same as in Kotlin.

There is an exception however: classes that are nested inside a method.<br> Example:
```
public void myMethod() {
    class MyNestedClass {
        private String myString = "I am a String";
    }
    MyNestedClass myObj = new MyNestedClass();

    // yields something like "MyNestedClass@4c178a76"
    System.out.println(asString(myObj));
}
```

Alas, this type of Java classes causes a `KotlinReflectionInternalError`, Kotlin's reflection can't handle such Java-classes, somehow.
> Method-local classes in Kotlin are handled correctly, though.

Due to this error, Kute can't resolve any property information for such classes / objects.<br>
The `KotlinReflectionInternalError` is caught by **Kute**`asString()`, and **Kute** falls back to a default _fallback representation_.
> <u>**Fallback representation**</u> is:<br>
> the <u>class name</u> followed by <u>@</u> and the hexadecimal <u>object identity</u>, e.g. `MyClass@3d188b74`<br>
> * _The object identity is the same value that you get as `String` representation from an object that does not have `toString()` implemented.<br>
> You will also see this hex value in your IDE's debug window._

