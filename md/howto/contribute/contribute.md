| [← 🏠](../../../)            | [← README.md](../../../README.md) |
|:-----------------------------|:----------------------------------|
| [← How to...](../0-howto.md) | [→ FAQ](../../../md/faq/0-faq.md) |

<hr>

## How to contribute to Kute `asString()`?

### Principles
There are a few important principles to keep in mind, when working on **Kute**:
1. #### Stability of the API
   >Users hate it when they have to change their code base for a new version of a library. And they are right.<br>
   See this _Q & A_ in the FAQ: [OK, but is **Kute** `asString()`'s <u>API</u> stable?](../../faq/0-faq.md#ok-but-is-kute-asstrings-api-stable)

2. #### Zero dependencies
   > Kute only uses Java / Kotlin code for hashing, logging, etc., without using 3rd party libraries.<br>
   One of the starting points of **Kute** `asString()` has been that, by its nature, it would not really need other libraries / frameworks.<br>
   This way the users can be sure that no transitive dependencies come in their way, other than Kotlin & Java.

These principles are advertised in the [README.md →](../../../README.md) and in the [FAQ →](../../../md/faq/0-faq.md), and should be honoured.

### Contribute
 

#### Related

TODO:
* Keep API stable
* zero dependencies
* Java 11 .. 20 & beyond
* Tests / build
* Documentation
   * KDoc / API-docs
   * Readme
   * Howto / Supporting docs