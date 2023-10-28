| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

### Including extra values in `asString()` output

Including extra values can, of course, be as simple as this:<br>
`override fun toString(): String = asString() + "aVar=$aVar"`

But:
* the value will be outside the parentheses:
`MyClass(val1=value 1, val2=value2) aVar=a var value`
* there may be some other inconveniences too

**Kute** `asString()` offers 3 alternatives to include name-value pairs in the `AsString()` output:<br>

1. [`NamedValue` →](#namedvalue)
2. [`NamedSupplier` →](#namedsupplier)
3. [`NamedProp` →](#namedprop)

All of these:
* implement interface `NameValue`
* involve usage of `AsStringBuilder`; see code snippet below.<br>
* keep weak references only to the value they represent.
  * So they don't prevent garbage collection of the value.

A typical implementation with `AsStringBuilder` and `NameValue`s might look like this:

```
class MyClass {
  val prop1 = "a value";
  var prop2 = "i can be reassigned"
  
  @AsStringOmit
  private val customAsString = asStringBuilder()
      .withAlsoNamed(aNamedSupplier, aNamedVal, aNamedProp) // vararg of `NameValue`
      .build()
      
  override fun toString(): String = customAsString.asString()
}
```

> Alternatively, you could simply do this, to avoid the declaration of `val customAsString`:
> ```
> override fun toString(): String =
>   asStringBuilder().withAlsoNamed(aNamedSupplier, aNamedVal, aNamedProp).asString()
> ```
> However, each call to `toString()` would involve building a new `AsStringBuilder`, which includes construction of some child-objects too.<br>
> This is not advised (because of to the overhead involved), unless calls of to `toString()` are infrequent.

<hr>

<br>

#### Below examples for each of the 3 `NameValue` implementations:

1. ### `NamedValue`
   `NamedValue` is the simplest thing: it simply holds a value, as of the time of initialization:<br>
   ```
   var valueStr = "the original value"
   val namedValue = NamedValue(name = "valueStr", value = valueStr)
   println(namedValue.value) // output: the original value
   valueStr = "another value"
   println(namedValue.value) // output: the original value
   ```
   * So it's a good choice for final (`val`) variables, or if you want to keep the original value, like above.
   * When the value has mutable state (think of `MutableList`, `StringBuffer`, etc.), the mutable state will be reflected

2. ### `NamedSupplier`
   Chances are that you want your `NameValue` to reflect changes of non-final (`var`) variables.<br>
   * That's where `NamedSupplier` comes in.
   * It also shines in cases where you want the value to be evaluated only when it is actually needed.

   ```
    var string = "first"
    val namedSup = { string }
    val namedSupplier = NamedSupplier("string", namedSup)
        // Or alternatively, use the convenience extension method:
        // val namedSupplier = namedSup.namedSupplier("stringBuf")
    println(namedSupplier.value) // output: first
    string = "second"
    println(namedSupplier.value) // output: second
   ```

3. ### `NamedProp`
   If you want to include a property value in your `asString()`, you might use `NamedSupplier` or `NamedProp`.
   <br><br>
   * `NamedProp` has the unique feature that it observes the rules of "normal" properties,
   e.g. hashing, masking, etc.
   * You don't have to (and can not) provide the name: it's derived from the property

   ```
   data class Person(
       val name: String,
       @AsStringMask(startMaskAt = 8, endMaskAt = 14)
       val iBan: String,
       @AsStringOmit
       val relatedPerson: Person?
   ) {
    @AsStringOmit
    private val customAsString = asStringBuilder()
        .withAlsoNamed(Person::iBan.namedProp(relatedPerson))
        .build()
   
    override fun toString() = customAsString.asString()
   }
   
   val other = Person("other", "NL29ABNA4408407916", null)
   val me = Person("me", "NL14INGB6708408508", other)
   other.relatedPerson = me
   // the iBan of the related person is masked as well, as you might hope!
   println(me) // Person(iBan=NL14INGB******8508, name=me, iBan=NL29ABNA******7916)
   ```
   
   So `NameProp` shines if you have `AsString...` annotations that you'd like to have obeyed in your `asString()` representation, like `@AsStringHash` or `@AsStringMask`.
   > Due to the recursive relationship in this example, we have 2 representations of an `iBan`, one of `me`, and one of related person `other`.<br>
   > `NameValue`s are always rendered at the end. So the last `iBan` (`NL29ABNA******7916`) is the value represented by the `NamedProp`