| [← 🏠](../../../)            | [← README.md](../../../README.md) |
|:-----------------------------|:----------------------------------|
| [← How to...](../0-howto.md) | [→ FAQ](../../../md/faq/0-faq.md) |

<hr>

* [How to contribute to Kute `asString()`?](#how-to-contribute-to-kute-asstring)
  * [Contribute](#contribute)
    * [Starting points](#starting-points)
    * [Pull requests - guidelines](#pull-requests---guidelines)
  * [Principles of Kute `asString()`](#principles-of-kute-asstring)

<hr>

# How to contribute to Kute `asString()`?

## Contribute
If you read this, you may be interested in contributing to **Kute** `asString()`. Nice!

### Starting points
Some suggestions for things you could contribute to:
* **Discussions**<br>
  You may [start or join a discussion →](https://github.com/JanHendrikVanHeusden/Kute/discussions/new/choose), e.g. to suggest changes, or share experiences
* **Work on / create issue**<br>
   * You may enter an issue on the [project board →](https://github.com/users/JanHendrikVanHeusden/projects/2) that's associated with **Kute** `asString()`
   * There are some suggestions in the [README.md →](../../../README.md#planned-wishes-to-do) under _Planned, wishes, to do_; but these are not very concrete yet.
* **Test projects**<br>
  You may start / improve / contribute to a test project
    * There are a few test projects already, but more / other are welcome
    * Separate projects to test **Kute** `asString()` with different technology stacks;<br>
     e.g. OSGI, database, Java 9 module system (JigSaw), Quarkus, and/or with a restrictive SecurityManager, etc.
* **Reviewing**
  <br>You may review existing code, or pull requests
    * Always be friendly in your comments

### Have fun!

### Pull requests - guidelines

Feel free to issue a pull request!
<br>Some guidelines for code changes / pull requests:

1. **Honour the [principles mentioned below](#principles-of-kute-asstring)**
   * Existing API should not change
   * Zero dependencies
<br><br>
2. Only expose what is actually **needed to use the API**
   * Stuff that the end-users do not need, should be `internal` or `private`
   * Java does not recognise `internal` visibility.<br>
     To avoid unwanted exposure of `internal` stuff, **all** `internal` properties and methods **must** be annotated with `@JvmSynthetic`
<br><br>
3. Create and run **tests**
   * Let tests fully cover all use cases of your new or changed code
<br><br>
4. **Test your code with a Java 11 JVM**
   * It is <u>not enough</u> that `sourceCompatibility` / `targetCompatibility` are set to Java 11
   * You actually have to install a Java 11 JVM and have it run on that JVM<br>
     > For instance, `HexFormat` (added in Java 17) class must not be used in **Kute** `asString()`
     > * On a 17+ JVM, code using `HexFormat` would run successfully,<br>
        _even while `sourceCompatibility` / `targetCompatibility` are set to Java 11_
     > * But on a Java 11 JVM **it won't compile / run**, so it's an incompatible change!
   * The `Gradle` build script [build.gradle.kts](build.gradle.kts) outputs the JVM version by means of<br>
    `println("Running on JVM version: ${JavaVersion.current()}")`
<br><br>
5. You may want to test your code additionally with Java 20+<br><br>
6. **Document** your code with **KDoc**
   * All public stuff should be documented
   * Generate the new API-documentation (it uses the _KDoc_) by running Gradle task `buildWithApiDocs`, and commit any changed / new documentation pages (in the `docs` directory)
<br><br>
7. **Adjust documentation**
   * If needed, also add / adjust other documentation (the `md` directory in the project's root)
<br><br>

### Principles of Kute `asString()`
There are a few important principles to keep in mind, when working on **Kute**:
1. #### Stability of the API
   >Users hate it when they have to change their code base for a new version of a library. And they are right.<br>
   See this _Q & A_ in the FAQ: [OK, but is **Kute** `asString()`'s <u>API</u> stable? →](../../faq/0-faq.md#ok-but-is-kute-asstrings-api-stable)

2. #### Zero dependencies
   > Kute only uses Java / Kotlin code for hashing, logging, etc., without using 3rd party libraries.<br>
   One of the starting points of **Kute** `asString()` has been that, by its nature,<br>
   > it does not really need other libraries / frameworks.<br>
   This way the users can be sure that no transitive dependencies come in their way, other than Kotlin & Java.

These principles are advertised in the [README.md →](../../../README.md) and in the [FAQ →](../../../md/faq/0-faq.md) and should be honoured.

