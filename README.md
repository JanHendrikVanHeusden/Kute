# Kute

- [Kute](#kute)
    * [**Kute**?](#kute-1)
        + [Basic ideas of Kute's `asString()`](#basic-ideas-of-kutes-asstring)
    * [Aim of **Kute**](#aim-of-kute)
        + [**Kute** aims to be a <u>better</u> alternative to other libraries](#kute-aims-to-be-a-better-alternative-to)
        + [How is **Kute** better?](#how-is-kute-better)
    * [Compatibility](#compatibility)
        + [Java version](#java-version)
        + [Kotlin version](#kotlin-version)
        + [Dependencies](#dependencies)
        + [Platform: JVM](#platform-jvm)
            - [Porting to other platforms?](#porting-to-other-platforms)

## **Kute**?

***Kute*** stands for **K**otlin **Ut**ility.<br>
*And also for cute, as it was intended to be a tiny, little, cuddly, **easy to use** utility.*<br>

Currently, **Kute** contains `asString()`, which is basically a `toString()` alternative,
but customizable to fit your needs.
> Comparable with Apache's `ToStringBuilder.reflectionToString` - but better!<br>
> Stay with me: see [Basic ideas of Kute's <span style="font-family: monospace">asString()</span>](#basic-ideas-of-kutes-asstring) and [How is **Kute** better?](#how-is-kute-better)

More advanced options are available, but basically it should be very easy to use,
never getting in the way when developing great code (or when troubleshooting less great code).

<hr>

### Basic ideas of Kute's `asString()`
**Kute** is built with the following ideas about `toString()` implementations in mind:
1. **<u>Untroubled</u>**
   * `toString()` implementations should be as basic as possible.
   * Renaming, adding or removing properties should be followed automagically
   > You really should not have to bother about your `toString()` implementations<br>
     so you can focus on the functionals you are working on
2. **<u>Helpful</u>**
   * `toString()`'s purpose is to help **solving issues**, not to cause new issues
   * Exceptions should never propagate out of your `toString()`. Period.
       * Uhmmm... except `InterruptedException`
   * If your object model includes recursive data, you still want your `toString()` giving you the information you expect (without `StackOverflowError`)
   * If your data is modified concurrently, you still don't want to have a `ConcurrentModificationException` thrown by your `toString()` method
3. **<u>Decent & informative</u>**
   * Decent, informative, human-readable representation of just **any** object, whether your own custom-made stuff, library objects, or Java/Kotlin built-in stuff
4. **<u>Customizable</u>**
   * In practice, there will be some obstacles that you need to tackle, and where you want customization:
       * GDPR / Personally Identifiable Data
          * You may want to keep certain data out of log files
       * Data that you want to exclude, as it might cause performance issues
          * E.g. `List` of children in JPA Entities
       * etc.
5. **Zero transitive dependencies**
   * See below, under [Compatibility → dependencies](#dependencies)

<hr>

## Aim of **Kute**

### **Kute** aims to be a <u>better</u> alternative to:

* Apache's `ToStringBuilder` (and other, comparable solutions)
* Java's built-in `Objects.toString()`
* Lombok's `@ToString` annotation
    * Lombok's `@ToString` is really good but has maddening dependencies between IntelliJ, Gradle, IntelliJ plugin, `javac`, Kotlin, and Lombok, chances are well that you can't get it working in your environment.<br>
* `Gson` and `Jackson`
    * May work well for you if json is OK for you.
    * But intended in first place for serialization, not for `String` representation in logging, etc.
* IDE generated `toString()` methods
* Home-grown solutions

### How is **Kute** better?
Below, a *summary* of why Kute is a better choice for your `toString()` implementations.<br>
> See [→ How is Kute better than others](docs/kute-better-details.md)
> for more *details* on how **Kute** compares to<br>
> Apache's `ToStringBuilder`, `Objects.toString()`, Lombok's `@ToString`, `Gson`
<hr>

1. **Ease of use**
   * With Kute, a `toString()` method typically is as simple as <br>
     ```override fun toString(): String = asString()```
   * No maddening issues with plugins and version dependencies (like with Lombok)
   * Fully `null`-safe & no fear for exceptions<br><br>

2. **Stability**
    * **As a developer, I don't want any `toString()`-like thing get in the way**
    * I really don't want to care about *any* exception in my `toString()` implementations:
       * `NullPointerException`, `RuntimeException`, `ConcurrentModificationException`, ...
    * I do want my `toString()` handle recursive data properly, without `StackOverflowError`
    * I want the option to exclude properties, e.g. `Collection`s of child records in database-stuff (JPA, Hibernate, Exposed, etc.), to avoid performance issues by reflective collection of data.
       * Use `@AsStringOmit` for individual properties
       * Use property filters for categories of properties


3. **Better `String` representation**
   > * You hate things like `[Ljava.lang.Object;@e3b3b2f` as much as anybody
   > * You hate yourself and your IDE when you forgot to include the newly added property in your IDE-generated `toString()` implementation (even more when it's causing issues in production)
       </span>

   * Built for `String` representations, to use in logging, etc.
   * 👉🏽 If your custom objects feature carefully implemented `toString()` implementations, you can have these preferred
      * E.g. by using `@AsStringClassOption(toStringPreference = PREFER_TOSTRING)`<br><br>

4. **Kotlin first**
   * By default, **Kute**'s `asString()` representation of objects is equal to Kotlin's `toString()` representation of data classes, collections, etc.
   * Improved representation of Lambdas and functional interfaces
   * Proper handling of `lateinit` properties
   * Option to include `companion` objects in the `asString` output
   * Intuitive API by usage of extension methods etc.<br><br>

5. **Protection of <u>P</u>ersonally <u>I</u>dentifiable <u>D</u>ata / GDPR**
   * **Kute** has several options that may help to keep Personally Identifiable Data out of your log files


6. **Performance**
   * You want a String representation with performance comparable to IDE-generated `toString()` methods
   * You want the possibility to exclude individual properties or certain categories of properties
      * E.g., if your JPA entities contain lists of child entities, you want to keep the child entities
        out of the parent's String representation
        > You don't want your `toString()` have fetching records from the database...!

<hr>

## Compatibility
### Java version
* **Kute** is built for compatibility with **Java 11+**
* **Kute** has been built and tested on various JVMs with Java versions **11** to **20**.
* **Kute** has been built and tested on different environments, e.g. MacOs and Windows
   * Building API documentation on Windows has known issues.
      * Contributors: see comments in [Gradle task <span style="font-family: monospace">buildWithApiDocs</span>](build.gradle.kts) 
* Contributors are asked to have **Kute** tested on a Java 11 JVM
   * Not just with `sourceCompatibility` / `targetCompatibility` 11, but really have it built on a Java 11 JVM.
   * For instance, `HexFormat` class can not be used (added in Java 17)
   * The `Gradle` build script [build.gradle.kts](build.gradle.kts) outputs the Java version by means of<br>
     `println("Running on JVM version: ${JavaVersion.current()}")`

### Kotlin version
* Current Kotlin version for **Kute** is `1.9.x`
   * But the Kotlin version should not have runtime implications
       * All versions of Kotlin produce Java 8+ compatible code
   * Also tested with Kotlin `1.3` without issues

### Dependencies
* **Kute** is built with **zero-dependencies** as a basic principle.<br>
  Applications that use **Kute** shall not face _any_ additional transitive dependency through Kute. Hence:
   * **Kute**'s runtime code should not rely on any 3rd-party library
      * Just on Java and Kotlin built-ins, `kotlin-stdlib`, `kotlin-reflect`
   * No logging framework is included or presumed.
   * Tests & build scripts use 3rd-party libraries, though
      * These do not induce runtime dependencies
      * Tests of **Kute** may use any library they wish, like `JUnit`, `Mockito`, `AssertJ`, `Awaitility`, Apache's Common Lang, `Gson`, etc.
      * Gradle build scripts may use any library / plugin they wish, e.g. `Dokka`, `pitest`, `kover`, Apache's `commons-io`, etc.

<hr>

### Platform: JVM
* **Kute** is built with the `JVM` platform in mind.
   * It uses Kotlin's reflection wherever possible
   * But some edge cases are not supported by Kotlin's reflection. In these cases, Java's reflection is used.
* Tests are also made with the `JVM` platform in mind
   * Using `JUnit 5`, `Mockito`, `AssertJ`, `Awaitility`, etc.

#### Porting to other platforms?
Porting **Kute** to another platform won't be easy.
It uses Kotlin's reflection wherever possible.<br>
But, some edge cases are not supported by *Kotlin*'s reflection. In these cases *Java*'s reflection is used.

Besides that, the JVM-platform is omnipresent in Kute; so porting it would probably result in a need to rewrite quite some code, maybe with limited functionality (due to limitations of Kotlin's reflection).

> **NB**: to be honest, some features after all *are* present in later versions of Kotlin's reflection, but I just had not been aware at the time.
> * E.g. finding out if a class is a subclass of another one does not need Java's `isAssignableFrom()`; it can be done with `isSubtypeOf()`, if needed in combination with `typeOf<...>()`
