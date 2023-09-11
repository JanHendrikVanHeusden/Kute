<style>
    ol { font-weight: bolder}
    ul { font-weight: initial; line-height: 150% }
</style>

# Kute

## **Kute**?

***Kute*** stands for **K**otlin **Ut**ility.<br>
*And also for cute, as it was intended to be a tiny, little, cuddly, **easy to use** utility.*<br>
More advanced options are available, but basically it should be very easy to use,
never getting in the way when developing great code (or when troubleshooting less great code).

## Aim of **Kute**

### **Kute** aims to be a <u>better</u> alternative to

* Apache's `ToStringBuilder`
* Java's built-in `Objects.toString()`
* Lombok's `@ToString` annotation
    * Lombok's `@ToString` is really good but has maddening dependencies between IntelliJ, Gradle, IntelliJ plugin, `javac`, Kotlin, and Lombok, chances are well that you can't get it working in your environment.<br>
* `Gson` and `Jackson`
    * May work well for you if json is OK for you.
    * But intended in first place for serialization, not for `String` representation in logging, etc.
* IDE generated `toString()` methods
* Home-grown solutions

### Fundamental ideas
* You really should not have to bother about your `toString()` implementations, so you can focus on the functionals you are working on
* Exceptions should never propagate out of your `toString()`. Period.
   * Uhmmm... except `InterruptedException` 
* Decent representation of just **any** object
   * Whether your own custom-made stuff, library objects, or Java/Kotlin built-in stuff


### How is **Kute** better?
Below, a summary of why Kute is a better choice for your `toString()` implementations.<br>
> See [→ How is Kute better than others](docs/kute-better-details.md)
> for more details on how **Kute** compares to<br>
> Apache's `ToStringBuilder`, `Objects.toString()`, Lombok's `@ToString`, `Gson`
<hr>

1. Ease of use
   * With Kute, a `toString()` method typically is as simple as <br>
     ```override fun toString(): String = asString()```
   * No maddening issues with plugins and version dependencies (like with Lombok)
   * Fully `null`-safe & no fear for exceptions<br><br>

2. Stability
* **As a developer, I don't want any `toString()`-like thing get in the way**
    * I really don't want to care about *any* exception in my `toString()` implementations:
       * `NullPointerException`, `RuntimeException`, `ConcurrentModificationException`, ...
    * I do want my `toString()` handle recursive data properly, without `StackOverflowError`
    * I want the option to exclude properties, e.g. `Collection`s of child records in database-stuff (JPA, Hibernate, Exposed, etc.), to avoid performance issues by reflective collection of data.
       * Use `@AsStringOmit`

3. Better `String` representation
   > * You hate things like `[Ljava.lang.Object;@e3b3b2f` as much as anybody
   > * You hate yourself and your IDE when you forgot to include the newly added property in your IDE-generated `toString()` implementation (even more when it's causing issues in production)
       </span>

   * Built for `String` representations, to use in logging, etc.
   * 👉🏽 If your custom objects feature carefully implemented `toString()` implementations, you can have these preferred
      * E.g. by using `@AsStringClassOption(toStringPreference = PREFER_TOSTRING)`<br><br>

4. Kotlin first
   * By default, **Kute**'s `asString()` representation of objects is equal to Kotlin's `toString()` representation of data classes, collections, etc.
   * Improved representation of Lambdas and functional interfaces
   * Proper handling of `lateinit` properties
   * Option to include `companion` objects in the `asString` output
   * Intuitive API by usage of extension methods etc.<br><br>

5. Protection of <u>P</u>ersonally <u>I</u>dentifiable <u>D</u>ata / GDPR
   * **Kute** has several options that may help to keep Personally Identifiable Data out of your log files

## How to use Kute