[← README.md](../README.md)

- [**Kute** aims to be a <u>better</u> alternative to other libraries](#kute-aims-to-be-a-better-alternative-to)
- [How is **Kute** better than others for implementing `toString()`?](#how-is-kute-better-than-others-for-implementing-tostring)
    * [Compared to Apache's `ToStringBuilder`, `Objects.toString()`, `Lombok`, `Gson`, `Jackson`](#compared-to-apaches-tostringbuilder-objectstostring-lombok-gson-jackson)

<hr>

### **Kute** aims to be a <u>better</u> alternative to

* Java's built-in `Objects.toString()`
* Apache's `ToStringBuilder`
* Lombok's `@ToString` annotation
    * Lombok's `@ToString` is really good if you get it working
    * But it does not work with later versions of IntelliJ, with maddening version dependencies between IntelliJ, Gradle, IntelliJ plugin, `javac`, and Lombok (maybe some bugs too?).<br>
      Lombok and Kotlin are not always friends either.
* `Gson` and `Jackson`
    * May work well for you if json is OK for you.
    * But intended in first place for serialization, not for `String` representation in logging, etc.
       * `Gson` and `Jackson` do not take precautions to prevent them (e.g. `StackOverflowException` with recursive data). And they shouldn't, of course!
* IDE generated `toString()` methods
    * IDE generated `toString()` methods are simple and light-weight, but not maintenance-friendly (refactoring)

### How is **Kute** better than others for implementing `toString()`?
#### Compared to Apache's `ToStringBuilder`, `Objects.toString()`, `Lombok`, `Gson`, `Jackson`
<hr>

1. **Ease of use**
    * **With Kute, a `toString()` method typically is as simple as <br>
      ```override fun toString(): String = asString()```** <br>
        * *With `Objects.toString()`, you have to call it for every individual property - ugly code*
           > *`public override toString() = Objects.toString()` does not work!*<br>
             (throws `StackOverflowError`!!)
    * **`String` representation of *any* Java or Kotlin object is as simple as
      ```obj.asString()")```**
<br><br>
    * **No maddening issues with plugins and version dependencies**
        * With **Lombok**, getting it working is maddening, due to combination of Lombok, IntelliJ, Kotlin, Gradle, javac and IntelliJ settings that may or may not work.
            * You probably need to downgrade IntelliJ to some 2019(!) version to make it work 😭...
            * Switching from Gradle to Maven may help too 🤯...
<br><br>
    * **No fear for exceptions**
        > `toString()` implementations should never get in your way.
        * With Kute, you never have to include a `try`...`catch`-block in your `toString()`
            * With most (all?) alternatives, **you do have to care for exceptions** in your `toString()` method 
              > And maybe you'll realize so at an inconvenient time in the night or weekend...
            * Apache's `ToStringBuilder` or `Objects.toString()`, `Gson` or `Jackson` **do** propagate exceptions
        * With **Kute**, in many cases, exceptions are prevented rather than caught
            * If exceptions are encountered yet, they are handled gracefully and not propagated to the caller
<br><br>
    * Embraces `null`
        * **Kute** renders `null`s by default, and is fully `null`-safe
          * With Kotlin: fully `null`-safe; with Java: `null`-safe in nearly all situations.
        * `null.asString()` returns `"null"`; same goes for properties with `null` value.
        * Apache's `ToStringBuilder` is not null-safe
        * With `Gson`, you need to use a builder to have `null`s serialized
           * `GsonBuilder().serializeNulls().create().toJson(obj)`
<br><br>
    * **Kute** is intended **_primarily_** for `String` representations for use in logging, etc.
        * `Gson` and `Jackson` are intended for serialization
    * Any object, including Lambda's, `Number`, collections, maps, custom objects, native Java & Kotin stuff, synthetic objects, can be represented as `String` by calling `obj.asString()`

<br><hr><br>
2. **Stability**
   > `Objects.toString()` and Apache's `ToStringBuilder` are less stable than you might hope or expect...
* **As a developer, I don't want any `toString()`-like thing get in the way**
    * I want decent output and no `NullPointerException` when the object I called it on appears to be `null`
      > *Apache's `ToStringBuilder` is **not** `null`-safe*    
          → *you need something like `obj?.let { ToStringBuilder.reflectionToString(it) }`   
          on nullable objects (in Java even more ugly)*
    * I really don't want to care about *any* exception (neither `RuntimeException` nor `Exception`) in my `toString()` methods 😑
<br><br>
    * With **Kute** you get decent output (and no `ConcurrentModificationException`) when a `Collection` or `Map` is modified concurrently by another thread
        * Note that, due to reflective access, this `ConcurrentModificationException` may occur even when the `Collection` or `Map` is properly guarded (e.g. `private` or `protected`)
        * Both `Objects.toString()` and Apache's `ToStringBuilder` let exceptions go
          unhandled
        * **Kute** takes an optimistic approach, by not preventively taking a defensive copy of collections etc.
          * When `ConcurrentModification` occurs, it is caught and a defensive copy is taken. If a `ConcurrentModification` (or other exception) occurs again while taking the defensive copy, **Kute** falls back to a default representation that omits the collection's content.<br><br>
    * **You can exclude properties** (e.g. `Collection`s of child records in database-stuff like entities in JPA, Hibernate, Exposed, etc.), **to avoid performance issues** by reflective collection of data
        * Apache's `ToStringBuilder.reflectionToString` has this option as well (`@ToStringExclude`)
        * `Gson` has a more complicated approach to excluding properties<br><br>
    * **Kute**'s `asString()` outputs recursive data properly, without `StackOverflowError`
        * Lombok's `ToString` also handles recursive data properly! 👍🏽👍🏽
        * `Objects.toString()`, Apache's `ToStringBuilder` and `Gson` let your application ***crash*** with `StackOverflowError` on recursive data 👎🏽 (*assuming you don't catch `Error` stuff*)
        * Recursive data are quite common, actually, and should not blow up your` toString()` anyway.
         For example, think of:
             * Circular routes in route or flight planning
             * Parent-child relationships in data-centric applications, where the child refers to the parent, and the
                parent has a list of children
             * Nested collections, where the same object may appear in both the outer and inner Lists
             * Custom linked-list implementations, where each element points to its predecessor, and/or its successor
             * ...
             > Even while you might hate such constructs, you may have to deal with them, and you probably want them represented without `StackOverflowError`, I assume...

<br><hr><br>
3. **Better `String` representation**
 
    > * You hate things like `[Ljava.lang.Object;@e3b3b2f` as much as anybody
    > * You expect that Lambdas are represented decently<br>
    > * You hate it when every object is preceded by its fully qualified package name
    > * You hate yourself and your IDE when you forgot to include the newly added property in your IDE-generated `toString()` implementation (and even more when it's causing issues in production)
    </span>

    * Dynamic (reflective) property representation of custom Java and Kotlin classes
    * Built-in protection against `StackOverflowError` in case of self-references, or mutual/circular references
    * Adheres by default to implicit `toString()` representation of Kotlin data classes
    * Adheres by default to `Array.contentDeepToString()`, and to `toString()` representation of `Collection` and `Map` (but with protection against recursive data)
    * Adheres to Java- and Kotlin built-in `toString()` insofar implemented
      * E.g. for `Date`, `DateTime`, `Number`, `CharSequence`, `String`, `UUID`, etc.
    * Improved handling for Lambda's, Java's functional interface's etc. to give a much better representation than default `toString()`, IDE-generated `toString(),` Apache's `ToStringBuilder` or `Objects.toString()`
    * 👉🏽 If your custom objects feature carefully implemented `toString()` implementations, you can have these preferred by applying `@AsStringClassOption(toStringPreference = PREFER_TOSTRING)`
      > Or, alternatively, by applying `ToStringPreference.PREFER_TOSTRING` as application-wide default

<br><hr><br>
4. **Kotlin first**

    * By default, **Kute**'s `asString()` representation of objects is equal to Kotlin's `toString()` representation of **_data_** classes
        * To be used with any object (including Lambda's, `Number`, collections, maps, custom objects, native Java & Kotlin stuff, synthetic objects, `null`, ...)
    * **Kute** offers the option to include `companion` objects in the `asString` output
    * **Kute** handles `lateinit` properties decently, whether initialized or not
    * Intuitive API by usage of extension methods: `asString()` can be called on *any* object (even `null.asString()`).

<br><hr><br>
5. **Protection of <u>P</u>ersonally <u>I</u>dentifiable <u>D</u>ata / GDPR**
 > Think of properties that contain data like family names, dates of birth, bank account numbers, ID-card numbers, e-mail addresses, social media account names, login names, password data (no plain text, hopefully!), etc. etc. etc.

**Kute** has several options that may help to keep Personally Identifiable Data out of your log files:
 * Property values can be masked (fully or partially) with `@AsStringMask`
 * Property values can be represented by their hashed value with `@AsStringHash`
 * Property values can be replaced (fully or partially) with `@AsStringReplace`
     * using regular expressions, or by plain text 
 * Properties can be excluded completely with annotation `@AsStringOmit`
