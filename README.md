<style>
    ol { font-weight: bolder}
    ul { font-weight: initial; line-height: 150% }
</style>

# Kute

## **Kute**?

***Kute*** stands for **K**otlin **Ut**ility.<br>
*And also for cute, as it was intended to be a tiny, little, cuddly, **easy to use** utility.*<br>
More advanced options are available, but basically it should be easy to use, never getting in the way when developing
great code (or when troubleshooting less great code).

## Aim of **Kute**

Currently, **Kute** aims to be a <u>better</u> alternative to

* `Objects.toString()`
* Apache's `ToStringBuilder`
* Lombok' `ToString`
    * Works very well; but the Lombok plugin does not work properly with later versions of IntelliJ.<br>
      And Lombok and Kotlin are not always friends.
* `Gson` and `Jackson`
    * Good solutions for regular attributes, but not useful for
* IDE generated `toString()` methods
* Home-grown solutions

### How is **Kute** better than Apache's `ToStringBuilder`, `Objects.toString()`, `Lombok`, `Gson`, `Jackson`?

1. Ease of use
    * **A `toString()` method typically is as simple as <br>
      ```override fun toString(): String = asString()```** <br>
        * *With `Objects.toString()`, you have to call it for every individual property - ugly code*
        * *`public override toString() = Objects.toString()` does not work!* (throws `StackOverflowError`...)
    * **`String` representation of any Java or Kotlin object is as simple as
      ```obj.asString()")```**
        * *Apache's `ToStringBuilder.reflectionToString` is **not** `null`-safe*    
    * **No maddening issues with plugins and version dependencies**
        * *Combination of Lombok, IntelliJ, Kotlin, Gradle, javac and IntelliJ settings is maddening*
            * You probably need to downgrade IntelliJ to some 2018 version 🤯...<br>
              Switching from Gradle to Maven may help too 😭
    * **No fear for exceptions**
        * *Unlike Apache's `ToStringBuilder` or `Objects.toString()`, **Kute** does not propagate any exceptions
    * **`null`s are rendered by default**
        * `null.asString()` returns `"null"`; same goes for properties with `null` value.
        * With `Gson`, you have to use a builder to serialize `null`s
            * Something like `GsonBuilder().serializeNulls().create().toJson(obj)`
    * **Kute is intended primarily for `String` representations for use in logging, etc.**
        * `Gson` and `Jackson` are intended for serialization
    * Any object, including Lambda's, `Number`, collections, maps, custom objects, native Java & Kotin stuff, synthetic objects, can be represented as `String` by calling `obj.asString()` 

2. Stability
    * **As a developer, I don't want any `toString()`-like thing get in the way**
      * `Objects.toString()` and Apache's `ToStringBuilder` are less stable than you might hope or expect...
      * I want decent output and no `NullPointerException` when the object I called it on appears to be `null`
          * *Apache's `ToStringBuilder` is **not** `null`-safe*    
            → *you do need something like `obj?.let { ToStringBuilder.reflectionToString(it) }`   
            on nullable objects (in Java even more ugly)*
      * I really don't want to care about *any* exception (neither `RuntimeException` nor `Exception`) in my `toString()` methods 😑
      * I want decent output and no `ConcurrentModificationException` when a `Collection` or `Map` is concurrently
        modified by another thread
          * Note that, due to reflective access, this `ConcurrentModificationException` may occur even
            when the `Collection` or `Map` is properly guarded (e.g. `private` or `protected`)
          * Both `Objects.toString()` and Apache's `ToStringBuilder` let exceptions go
            unhandled
      * I do want my `toString()` handle recursive data properly, without `StackOverflowError`
        * Lombok's `ToString` handles recursive data properly! 👍🏽
        * Both `Objects.toString()` let your application crash with `StackOverflowError` on recursive data 👎🏽
        * Recursive data are quite common, actually, and should not blow up your` toString()` anyway.
        For example, think of:
          * Circular routes in route or flight planning
          * Parent-child relationships in data-centric applications, where the child refers to the parent, and the
            parent has a list of children
          * Nested collections, where the same object may appear in both the outer and inner Lists
          * ...

3. Better `String` representation
      * Dynamic (reflective) property representation of custom Java and Kotlin classes
      * For most Java- and Kotlin built-in stuff, `asString()` simply calls `toString()`, e.g. for `Date`, `DateTime`, `Number`, `CharSequence`, `String`, `UUID`, etc.
      * Improved handling for `array`s, Lambda's, Java's functional interface's etc. to give a much better representation than default `toString()`
          * Much better `String` representation than `Objects.toString()` or Apache's `ToStringBuilder`

4. Kotlin first
    * By default, **Kute**'s `asString()` representation of objects is equal to Kotlin's `toString()` representation of data classes
      * To be used with any object (including Lambda's, `Number`, collections, maps, custom objects, native Java & Kotlin stuff, synthetic objects, `null`, ...)
    * **Kute** offers the option to include `companion` objects in the `asString` output
    * Intuitive usage of extension method: `asString()` can be called on *any* object (even `null.asString()`.

5. Options for protecting Personally Identifiable Data / GPTR
   *  

## How to use Kute