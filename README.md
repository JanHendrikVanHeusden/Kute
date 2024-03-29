# Kute

- ### Kute
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
  * [Planned, wishes, to-do](#planned-wishes-to-do)
  * [Open source, license](#license)
<hr>

- ### Documentation:

  * [→ How to...](md/howto/0-howto.md)
  * [→ FAQ](md/faq/0-faq.md)
  * [→ API documentation](https://janhendrikvanheusden.github.io/Kute/index.html)
     * [→ API docs <u>root</u>](https://janhendrikvanheusden.github.io/Kute/index.html)
     * [→ API docs of <u>`asString()`</u>](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/as-string.html)

| You are especially encouraged to check out the [_How to..._ page →](md/howto/0-howto.md) <br><br> It gives an overview of _all options_ of Kute `asString()`, with further links:<br>e.g. on how to start with Kute `asString()`,  examples / snippets, etc.) |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

<hr>

## **Kute**?

***Kute*** stands for **K**otlin **Ut**ility.<br>
*And also for cute, as it was intended to be a tiny, little, cuddly, **easy to use** utility.*<br>

Currently, **Kute** contains `asString()`, which is basically a `toString()` alternative,
but customizable to fit your needs.<br>
And with many practice-based, developer-friendly features.
> Comparable with Apache's `ToStringBuilder.reflectionToString` - but better!<br>
> Stay with me: see [Basic ideas of Kute's <span style="font-family: monospace">asString()</span>](#basic-ideas-of-kutes-asstring) and [How is **Kute** better?](#how-is-kute-better)

More advanced options are available, but basically it should be very easy to use,<br>
never getting in the way when developing great code (or when troubleshooting less great code).

<hr>

### Basic ideas of Kute's `asString()`
**Kute** is built with the following ideas about `toString()` implementations in mind:
1. **<u>Untroubled</u>**
   * `toString()` implementations should be as basic as possible.
   * Renaming, adding or removing properties should be followed automagically
   > You really should not have to bother about your `toString()` implementations.<br>
    So you can focus on the functionals you are working on.
2. **<u>Helpful</u>**
   * `toString()`'s purpose is to help **solving issues**, not to cause new issues
   * Exceptions should never propagate out of your `toString()`. Period.
       * Uhmmm... except `InterruptedException` and `CancellationException`
   * If your object model includes recursive data, you still want your `toString()` giving you the information you expect<br>
     (without `StackOverflowError`)
   * If your data is modified concurrently, you still don't want to have a `ConcurrentModificationException` thrown by your `toString()` method
3. **<u>Decent & informative</u>**
   * Decent, informative, human-readable representation of just **any** object, whether your own custom-made stuff,<br>
     library objects, or Java/Kotlin built-in stuff
4. **<u>Customizable</u>**
   * In practice, there will be some obstacles that you need to tackle, and where you want customization:
       * GDPR / Personally Identifiable Data
          * You may want to keep certain data out of log files (omitted completely, or hashed, or ...)
       * Data that you want to exclude, to prevent performance issues
          * E.g. output of a `List` of children in JPA Entities may not be desirable (think of performance, verbosity)
       * Customization to your personal or business preferences
5. **Zero transitive dependencies**
   * See below, under [Compatibility → dependencies](#dependencies)

<hr>

## Aim of **Kute**

### **Kute** aims to be a <u>better</u> alternative to:

* IDE generated `toString()` methods
* Apache's `ToStringBuilder` (and other, comparable solutions)
* Java's built-in `Objects.toString()`
* Lombok's `@ToString` annotation
    * Lombok's `@ToString` is really good; but it has maddening dependencies between IntelliJ, Gradle, IntelliJ plugin, `javac`, Kotlin, and Lombok.<br>
     Chances are well that you can't get it working in your environment.<br>
* `Gson` and `Jackson`
    * `json`-libraries may work well for you if json is OK for you.
    * But these are intended in first place for serialization, not for `String` representation in logging, etc.
       * Think of lengthy `Collection`s - you can't limit the amount of String data with these libs - they shouldn't!
* Home-grown solutions

### How is **Kute** better?
Below, a *summary* of why Kute is a better choice for your `toString()` implementations.<br>
> Want more details? See [→ How is Kute better than others](md/kute-better-details.md) on how **Kute** compares to:<br>
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
    * I want the option to limit or exclude properties, e.g. `Collection`s of child records in database-stuff<br>
      (JPA, Hibernate, Exposed, etc.), to avoid performance issues by reflective collection of data.
       * Use `@AsStringOmit` for individual properties
       * Use property filters for categories of properties
    * Kute has been tested against various JVM's / Java versions / Kotlin versions / OS
    * Kute is tested heavily with all kinds of exotic objects and properties
       * Think of delegates, `lateinit`, property extensions, properties with explicit `get`-ters, object expressions,<br>
         nested classes, anonymous classes, companion objects, recursive / mutually referencing data, synthetic stuff,<br>
         SAM-wrappers, callables, fun interfaces, etc. etc. etc.
         > * Apache's `ToStringBuilder` and `Objects.toString()` fail on some / several of these.<br>
         > * `Gson` and `Jackson` fail on recursive data.<br>
         They should: they are not intended for `toString()`-like usage, but for serialization.


3. **Better `String` representation**
   > * You hate things like `[Ljava.lang.Object;@e3b3b2f` as much as anybody
   > * You hate yourself and your IDE when you forgot to include the newly added property in your IDE-generated `toString()` implementation<br>
   > (even more when it's causing issues in production)
       </span>

   * Built for `String` representations, to use in logging, etc.
   * Improved representation of Lambdas and functional interfaces
   * `Array` representation like `contentDeepToString`
   * Automatic resolution of custom objects
   * Options to have properties sorted by type, name, value length, etc.
     * Especially useful for entities with (too) many properties
   * 👉🏽 If your custom objects feature carefully implemented `toString()` implementations, you can have these preferred
      * E.g. by using `@AsStringClassOption(toStringPreference = PREFER_TOSTRING)`<br><br>

4. **Kotlin first**
   * By default, **Kute**'s `asString()` representation of objects is equivalent to Kotlin's `toString()` representation<br>
     of collections, Kotlin's `data` classes, etc.
   * Proper handling of `lateinit` properties
   * Option to include `companion` objects in the `asString` output
   * Intuitive API by usage of extension methods etc.<br><br>

5. **Protection of <u>P</u>ersonally <u>I</u>dentifiable <u>D</u>ata / GDPR**
   * **Kute** has several options that may help to keep Personally Identifiable Data out of your log files<br>
    (or, in general, out of your `toString()` representations)


6. **Performance**
   * You want `toString()` methods with performance comparable to IDE-generated `toString()` methods
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
   * Not just with `sourceCompatibility` / `targetCompatibility` 11, but really have it built and tested on a Java 11 JVM.
   * For instance, `HexFormat` class can not be used (added in Java 17)
   * The `Gradle` build script [build.gradle.kts](build.gradle.kts) outputs the Java version by means of<br>
     `println("Running on JVM version: ${JavaVersion.current()}")`

### Kotlin version
* Current Kotlin version for **Kute** is `1.9.x`
   * But the Kotlin version should not have runtime implications
       * All versions of Kotlin produce Java 8+ compatible code
   * Also tested with Kotlin `1.3` without issues
     * I.e., no issues at the time of writing
     * But: no effort or even guarantees that 1.3 compatibility will be maintained for **Kute** `asString()`

### Dependencies
* **Kute** is built with **zero-dependencies** as a basic principle.<br>
  Applications that use **Kute** shall not face _any_ additional transitive dependency through Kute. Hence:
   * **Kute**'s runtime code should not rely on any 3rd-party library
      * Just on Java and Kotlin built-ins, `kotlin-stdlib`, `kotlin-reflect`
   * No logging framework is included or presumed
   * Tests & build scripts use 3rd-party libraries, though
      * These do not induce runtime dependencies
      * _Tests_ of **Kute** may use any library they wish, like `JUnit`, `Mockito`, `AssertJ`, `Awaitility`, Apache's Common Lang, `Gson`, etc.
      * Gradle build scripts may use any library / plugin they wish, e.g. `Dokka`, `pitest`, `kover`, Apache's `commons-io`, etc.

<hr>

### Platform: JVM
* **Kute** is built with the `JVM` platform in mind.
   * It uses Kotlin's reflection wherever possible
   * But various edge cases are not supported by Kotlin's reflection.<br>
     In these cases, Java's reflection is used.
* Tests are also made with the `JVM` platform in mind
   * Using `JUnit 5`, `Mockito`, `AssertJ`, `Awaitility`, etc.

#### Porting to other platforms?
Porting **Kute** to another platform won't be easy.
It uses Kotlin's reflection wherever possible.<br>
But, some edge cases are not supported by *Kotlin*'s reflection. In these cases *Java*'s reflection is used.

Besides that, the JVM-platform is omnipresent in Kute; so porting it would probably result in a need to rewrite quite some code,<br>
maybe with limited functionality (due to limitations of Kotlin's reflection).

### Planned, wishes, to do
In my opinion (being the author, _Jan-Hendrik van Heusden_) **Kute** `asString` should be a mature, practice-oriented library.<br>
The current version is heavily tested with more exotic stuff than you probably ever will have in your code-base.<br>
Still, we all know that practice may have more tricks than you can imagine!
> **NB:** not tested with a restrictive SecurityManager.

I have a few things I'd like to **improve** yet. And maybe others may contribute!
* Tests with a restrictive **SecurityManager**? But maybe **Kute** `asString()` isn't the best solution for that anyway;<br>
  in such environments you may rather want to stick with IDE-generated `toString()` implementations.<br><br>

* **Logging**: Kute only logs exceptional situations, so quite minimal.<br>
  However, Kotlin's reflection does not like some situations (e.g. *Java* classes with anonymous inner classes,<br>
  classes nested inside methods, etc.), and throws a reflection-exception.<br>
  This situation is handled gracefully, but not logged, to avoid clogging your log files or std-out.<br>
  The output is different from normal, though, so it may leave the user with questions why she does not get the expected output.<br>
  A choice for more **verbose logging** should be implemented, to service situations like these.<br><br>

* Option to have **all** provided `NameValue` implementations honour annotations.<br>
  Currently this is only the case for `NamedProp`.

#### **Other thoughts?**<br>
I'd like to hear more about your experiences with **Kute** `asString()`, and any wishes.
* Feel free to suggest changes by adding a [pull request](https://github.com/JanHendrikVanHeusden/Kute/pulls).
   * See [how to contribute](md/howto/contribute/contribute.md)!
* Or discuss what / how you want to have things [improved or extended](https://github.com/JanHendrikVanHeusden/Kute/issues).

### License
Kute is an open-source project, according to [The Open Source Definition](https://opensource.org/osd/) of the [Open Source Initiative®](https://opensource.org/osd/), under [MIT](https://opensource.org/license/mit/)-license.<br>
The **license statement** can be found in the project root: see the **[→ LICENSE](LICENSE)**.