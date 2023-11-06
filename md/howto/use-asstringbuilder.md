| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

* **[Goal / usage](#goal--usage)**
* **[Examples](#examples)**
* **[Methods / signature](#methods--signature)**

## Using `AsStringBuilder`

### Goal / usage
**Kute**'s `AsStringBuilder()` is meant for situations where you want more control on what is included in the `asString()` output.

**Examples are:**
1. You may [prefer specifying the values to be included in `asString()` programmatically](#programmatically-specify-values-to-be-included) rather than declarative (by means of annotations)

2. [Depending on some condition, you want to include or exclude a certain property](#include-or-exclude-properties-depending-on-a-condition)

3. You want to [add extra values, e.g. argument values](#add-extra-values-eg-argument-values) in the output of `asString()`

This is the kind of requirements where `AsStringBuilder()` shines.

### Examples

1. #### Programmatically specify values to be included
    Instead of using `@AsStringOmit` annotations, you can choose to use `AsStringBuilder` to specify what is included / excluded in the output.
    * An advantage of this way of working is that you see immediately what is included in your `asString()` / `toString()` output.
    * A drawback is that newly added properties are not "automagically" included<br>
      So it's less maintenance-friendly.
    ```
    class MyClass(val included1: Any, val included2: Any, val excluded3: Any, val included4: Any) {
        private val customAsString = asStringBuilder()
            .withOnlyProperties(::included1, ::included2, ::included4)
            .build()
        override fun toString(): String = customAsString.asString()
    }
    // Output: MyClass(included1=i1, included2=i2, included4=i4)
    println(MyClass("i1", "i2", "e3", "i4"))
    ```
    > Note that property `customAsString` is not present in the output.<br>
    You typically don't want to have such property in your `toString()` / `asString()` output;<br> so **Kute** excludes properties of type `AsStringBuilder` / `AsStringProducer`.

    A more concise alternative is:
    
    ```
    class MyClass(val included1: Any, val included2: Any, val excluded3: Any, val included4: Any) {
        override fun toString(): String = asStringBuilder()
            .withOnlyProperties(::included1, ::included2, ::included4)
    }
    // Output: MyClass(included1=i1, included2=i2, included4=i4)
    println(MyClass("i1", "i2", "e3", "i4"))
    ```

    **Note however** that this way a new `AsStringBuilder` object is built on each call of `toString()`, which includes construction of some child-objects too.
    > This way should be chosen only if you don't care that much about efficiency, and/or calls to `toString()` are infrequent.

    <hr>

2. #### Include or exclude properties depending on a condition
    See the class below. If the Contract is a draft one, we know that the contract will not have an acceptor yet, so we can leave out these details.<br>
    Of course there are several ways to accomplish this; below an example.
    ```
    class Contract(val orderNo: String, val contractingPartnerId: UUID) {
        var draft: Boolean = true
        val clauses: List<String> = mutableListOf()
        var acceptorId: UUID? = null
        var acceptorSignatureHash: Long? = null

        private val draftAsString = asStringBuilder()
            .withOnlyProperties(::orderNo, ::draft, ::contractingPartnerId)
        override fun toString(): String =
                if (draft) draftAsString.asString() else asString()
    }
    ```

    <hr>

3. #### Add extra values, e.g. argument values
    In the example below, we want the function argument `relatedInvoices` to be included in the logging:
    ```
    class InvoiceAmountCalculator(val invoiceId: UUID) {
        // lots of properties
        fun calculateAmount(relatedInvoices: Collection<UUID>): BigDecimal {
            // lots of calculations
            return amount
                .also {
                    // Output like this: Calculated amount: InvoiceAmountCalculator(amount=8963.12, invoiceId=84e678f8-1e73-4744-a3ad-3524735a2548, relatedInvoices=[1bea740f-01f3-43b4-aaef-81fb1937affa, 5ed6407e-8ddd-439e-b577-198a09bd4a74])
                    log.info("Calculated amount: " + asStringBuilder()
                        .withAlsoNamed(NamedValue("relatedInvoices", relatedInvoices))
                        .asString())
                }
        }
    }
    ```

<hr>

### Methods / signature

`AsStringBuilder`'s methods fall apart in 2 categories:
1. Methods that specify what to include / exclude
2. Terminating methods: `build()` and `asString()`

<style>
table, td, tr {
    vertical-align: top;
    text-align: left;
}
td:nth-child(1) {
    text-align: center;
}
</style>

| Category | Signature | Description, usage                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ----| ----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1. | `withAlsoProperties(vararg props: KProperty<*>): AsStringBuilder `<br><hr>` withAlsoNamed(varargnameValues: NameValue): AsStringBuilder`<br><hr>`withOnlyProperties(vararg props: KProperty<*>): AsStringBuilder `<br><hr>` withOnlyPropertyNames(vararg names: String): AsStringBuilder `<br><hr>` exceptProperties(vararg props: KProperty<*>): AsStringBuilder `<br><hr>` exceptPropertyNames(vararg names: String): AsStringBuilder` | As the name suggests, `AsStringBuilder` follows the well-known <i>builder</i>-pattern, so these methods all return `AsStringBuilder`, to allow chained dot-notation. <br><br> See the [API-documentation of `AsStringBuilder()`](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.core/-as-string-builder/index.html).<ul><li> For specific explanation of <u>`withAlsoNamed`</u>: see [Including extra values in `asString()` output →](add-extra-values.md)<br>That documentation page also includes usage notes and example snippets.<br><br> </li> <li> The names and signatures of the further methods should be largely self-explainatory.</li></ul> |
|||                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| 2. | `build(): AsStringProducer` | As you may expect with a builder,  `build()` is a terminating method, it makes the builder effectively immutable. <br><br> The return type, `AsStringProducer`, is a super-type of `AsStringBuilder` with `asString()` as it's only functionally relevant public method.                                                                                                                                                                                                                                                                                                                                                                                                    |
| 2. | `asString(): String` | Calling `asString()` on the `AsStringBuilder` / `AsStringProducer` does the following: <ol><li>If `build()` was not called already, it calls the `build()`-method</li><li>It returns the `asString()` result according to the properties / values specified with the methods of category 1</li></ol>                                                                                                                                                                                                                                                                                                                                                                        |

