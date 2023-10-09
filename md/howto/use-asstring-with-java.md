| [← 🏠](../)               | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [→ How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Using `asString()` in Java code

Using `Kute / asString()` in Java code is not particularly difficult, but the signature of the methods is different, for a few reasons:
1. `Kute` heavily relies on _**extension methods**_
2. Kotlin uses _**properties**_ to access fields (or instance variables)
    * In Java, you see _**getters**_ (`get...()`) for the same fields.
3. Kotlin does not have static members (variables and methods). Instead, there are:
    * Package-level methods and properties
    * `companion` objects (with methods and/or properties)
<hr>

4. And: Kotlin lambdas are a bit different from Java lambdas

Of course, in your IDE you will see the Java signature, so it shouldn't be that difficult.<br>
So it's rather when you consult the [→ API docs](https://janhendrikvanheusden.github.io/Kute/index.html) that you may wonder how to use the documented method in Java.

In that case, the following may be helpful:

<hr>

1. ### Extension methods
Kotlin's _extension methods_ are in fact syntactic sugar, but very convenient.

> Say, for instance that an extension method `String.reverse()` exists, defined as<br>
>  `fun String.reverse(): String = StringBuilder(this).reverse()`<br>
> * In **Kotlin**, this method could be called like this:<br>
>    `reversed = myString.reverse()`<br>
>    `reversed = "abc".reverse()`
> <br><br>
> * In **Java**, it would be like this:<br>
> `reversed = reverse(myString)`<br>
> `reversed = reverse("abc)`

<hr>

Now with `asString()`: a **Kotlin** `toString()` method could be<br>
```override fun toString() = this.asString()```<br>
or<br>
```override fun toString() = asString() // 'this' is implicit here```<br>
<br>

<hr>

In **Java** you would write this:
   ```
   @override
   public String toString() {
       return asString(this); // 'this' needs to be explicit here; and inside the brackets
   }
   ```
So be aware of implicit `this`, and make the _receiver_ object the 1st parameter.

