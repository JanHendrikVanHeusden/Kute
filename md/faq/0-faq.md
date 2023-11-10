| [← 🏠](../../)                           | [← README.md](../../README.md) |
|:-----------------------------------------|:-------------------------------|
| [→ How to...](../../md/howto/0-howto.md) | [FAQ]()  (<i>this page</i>)    |

<hr>

### Kute `asString` <u>F</u>requently <u>A</u>sked <u>Q</u>uestions

* [I saw in the `README.md` that Kute is written in Kotlin, and meant for Kotlin. <br> Can I use **Kute** `asString()` with <u>Java</u>?](#i-saw-in-the-readmemd-that-kute-is-written-in-kotlin-and-meant-for-kotlin-can-i-use-kute-asstring-with-java)
* [Does Kute `asString()` use annotation processing?](#does-kute-asstring-use-annotation-processing)
* [Is **Kute** `asString()` stable?](#is-kute-asstring-stable)
* [OK, but is **Kute** `asString()`'s <u>API</u> stable?](#ok-but-is-kute-asstrings-api-stable)
* [Is Kute `asString()`'s output guaranteed to stay the same, over versions?](#is-kute-asstrings-output-guaranteed-to-stay-the-same-over-versions)
* [Is **Kute** `asString()` open-source?](#is-kute-asstring-open-source)

<hr>

* #### I saw in the `README.md` that Kute is written in Kotlin, and meant for Kotlin <br> Can I use **Kute** `asString()` with <u>Java</u>?
  > Sure! All features of **Kute** `asString()` are available in Java as well<br>
  > * **Kute** `asString()` has various features aimed to make using it in Java as easy as it is in Kotlin
  >    * See [the documentation with more about usage in Java →]((#use-asstring-from-java-))
  > * Several tests and/or test objects of **Kute** are Java-code, to ensure that it interoperates smoothly with Java

* #### Does Kute `asString()` use annotation processing?
  > No

* #### Is **Kute** `asString()` stable?
  > It should be!
  > 
  > **Kute** `asString()` is tested both with extensive unit tests, and with other applications.<br>
  > The unit tests contain all the kind of exotic stuff, both Kotlin and Java, I could came up with.
  > 
  > * Think of lambdas, both Kotlin and Java, both as instance variables and as local variables,<br>
  > SAM-wrappers, extension-methods, anonymous inner classes, method-local classes, property extensions, callables, functional interfaces, delegates, etc. etc. etc.
  > * The tests with other applications involve tests with Java 11 module system, JPA, and others.
  > 
  > **Kute** `asString()` is tested on Java versions from 11 to 21 <br>
  > So yes, **Kute** `asString()` should be stable.<br>
  > But as everyone knows, practice can always come up with new surprises.<br>
  > 
  > In any case, **Kute** `asString()` handles all `Exception`s, so it should not come in your way, anyway.

* #### OK, but is **Kute** `asString()`'s <u>API</u> stable?

  _**I like Kute `asString()` but I don't want to risk having to change my code base with every new version of **Kute**... `asString()`**_
  > Fair point. YES. This is important.
  > 
  > The API of **Kute** with regard to existing `public` classes, methods, properties etc. <u>MUST **NOT** change over successive versions</u>.<br>
  > This is a **_hard requirement_** for contributors who want to contribute to **Kute** `asString()`<br>
  > See the instructions [here](../howto/contribute/contribute.md).
  > 
  > Also note:
  > * **Kute** `asString()` is tested on Java versions from 11 to 21, and on different OS's.
  > * **Kute** `asString()` has hundreds of tests, both Kotlin and Java, written over a span of some years.<br>
  >   During development of the initial version, when things did not feel logical or intuitive anymore,<br>
  > the code (package structure) has been reorganised to correct that.<br>
  > It has been reviewed a few times too, to make sure it was logical to others too.

* #### Is Kute `asString()`'s output guaranteed to stay the same, over versions?
  > No.
  > * Newer versions may cause variations in how properties are rendered
  > * Users may suggest improvements
  > * Kotlin's reflection libraries may introduce new features
  > 
  > So you shouldn't take the current output for granted.
  >
  > That being said, changes in the output should be limited / minimal, and if, feasible, have a toggle<br>
  > (i.e. a parameter or annotation that you can use to choose for that new behaviour).
  >
  > Summarized, it would not be realistic to promise that it will stay the same forever.

* #### Is **Kute** `asString()` open-source?
  > Yes, in accordance to [The Open Source Definition](https://opensource.org/osd/) of the [Open Source Initiative®](https://opensource.org/osd/).
  > * It is under [MIT](https://opensource.org/license/mit/)-license.<br>
      The license statement can be found in the project root: see the **[LICENSE](../../LICENSE)**.
