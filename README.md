# Kute

- [Kute](#kute)
    * [**Kute**?](#--kute---)
        + [Basic ideas of Kute's `asString()`](#basic-ideas-of-kute-s--asstring---)
    * [Aim of **Kute**](#aim-of---kute--)
        + [**Kute** aims to be a <u>better</u> alternative to:](#--kute---aims-to-be-a--u-better--u--alternative-to-)
        + [How is **Kute** better?](#how-is---kute---better-)
    * [Compatibility](#compatibility)
        + [Java version](#java-version)
        + [Kotlin version](#kotlin-version)
        + [Dependencies](#dependencies)
        + [Platform: JVM](#platform--jvm)
            - [Porting to other platforms?](#porting-to-other-platforms-)

## **Kute**?

***Kute*** stands for **K**otlin **Ut**ility.<br>
*And also for cute, as it was intended to be a tiny, little, cuddly, **easy to use** utility.*<br>
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
3. **<u>Decent & informative</u>**
   * Decent, informative, human-readable representation of just **any** object, whether your own custom-made stuff, library objects, or Java/Kotlin built-in stuff
4. **<u>Customizable</u>**
   * In practice, there will be some obstacles that you need to tackle.
       * GDPR / Personally Identifiable Data
          * You may want to keep certain data out of log files
       * Data that, if included, might cause performance issues
          * E.g. `List` of children in JPA Entities
5. **Zero transitive dependencies**
   * See below, under [Compatibility → dependencies](#dependencies)

<hr>

## Aim of **Kute**

### **Kute** aims to be a <u>better</u> alternative to:

* Apache's `ToStringBuilder`
* Java's built-in `Objects.toString()`
* Lombok's `@ToString` annotation
    * Lombok's `@ToString` is really good but has maddening dependencies between IntelliJ, Gradle, IntelliJ plugin, `javac`, Kotlin, and Lombok, chances are well that you can't get it working in your environment.<br>
* `Gson` and `Jackson`
    * May work well for you if json is OK for you.
    * But intended in first place for serialization, not for `String` representation in logging, etc.
* IDE generated `toString()` methods
* Home-grown solutions

### How is **Kute** better?
Below, a summary of why Kute is a better choice for your `toString()` implementations.<br>
> See [→ How is Kute better than others](docs/kute-better-details.md)
> for more details on how **Kute** compares to<br>
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
       * Use `@AsStringOmit`

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

<hr>

## Compatibility
### Java version
* **Kute** is built for compatibility with **Java 11+**
   * Contributors are asked to have **Kute** tested on a Java 11 JVM
      * Not just with `sourceCompatibility` / `targetCompatibility` 11, but really have it built on a Java 11 JVM.
      * For instance, `HexFormat` class can not be used (added in Java 17)
      * The `Gradle` build script [build.gradle.kts](build.gradle.kts) outputs the Java version by means of<br>
        `println("Running on JVM version: ${JavaVersion.current()}")
* **Kute** has been tested on various JVMs with Java versions **11** to **20**.

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
   * Tests of **Kute** may use any library they wish, like JUnit, Mockito, AssertJ, Awaitility, Apache's Common Lang, Gson, etc.
   * Gradle build scripts may use any library / plugin they wish, e.g. `Dokka`, `pitest`, `kover`, Apache's `commons-io`, etc.

<hr>

### Platform: JVM
* **Kute** is built with the `JVM` platform in mind.
  * It uses Kotlin's reflection wherever possible; but many edge cases are not supported by Kotlin's reflection
    * In these cases Java's reflection is used.
* Tests are also made with the `JVM` platform in mind
   * Using `JUnit`, `Mockito`, `AssertJ`, etc.

#### Porting to other platforms?
Porting **Kute** to another platform won't be easy.
It uses Kotlin's reflection wherever possible.<br>
But many edge cases are not supported by Kotlin's reflection, in these cases Java's reflection is used.

In fact, the JVM-platform is omnipresent in Kute; so porting it would probably result in a need to rewrite much the code, with probably limited functionality (due to limitations of Kotlin's reflection).
