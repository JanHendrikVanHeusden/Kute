| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Kute Exception handling

* [What kind of exceptions can be avoided?](#what-kind-of-exceptions-can-be-avoided)<br>
     * [`KotlinReflectionInternalError`, `KotlinReflectionNotSupportedError`](#kotlinreflectioninternalerror-kotlinreflectionnotsupportederror)<br>
     * [`StackOverflowError`](#stackoverflowerror)<br>
* [How do other libraries do?**<br>](#how-do-other-libraries-do)
* [What kind of exceptions can NOT be avoided?](#what-kind-of-exceptions-can-not-be-avoided)
* [`Error` & `Throwable`](#error--throwable)

<hr>

It is well known that exceptions are very costly, in terms of performance.<br>
So if you can, you better avoid these.

##### What kind of exceptions can be avoided?
Well, not that much, actually...<br>
Yet there are some exceptions (or even `Error`s), that can be avoided.

<hr><u>

##### `KotlinReflectionInternalError`, `KotlinReflectionNotSupportedError`
</u>Some situations are not handled well by Kotlin's reflection; specifically certain types of `lambda`-properties, and synthetic classes.<br>
In many cases this can be detected beforehand, and **Kute** `asString()` switches to Java's reflection when applicable; or another workaround is applied.

<u>

##### `StackOverflowError`
</u>You might expect that **Kute** `asString()` would run into `StackOverflowError`s when your model looks like this:
   ```
   interface Entity {
       val id: Long
       val name: String
       override fun toString(): String
   }
   abstract class AbstractEntity: Entity {
       override fun equals(other: Any?): Boolean = other?.let { it::class == this::class && (it as Entity).id == this.id && it.name == this.name} == true
       override fun hashCode(): Int = id.toInt()
   }
   class Parent(override val id: Long, override val name: String, val children: MutableSet<Child>): AbstractEntity() {
       override fun toString(): String = asString()
   }
   class Child(override val id: Long, override val name: String, val parent: Parent): AbstractEntity() {
       init { parent.children.add(this) }
       override fun toString(): String = asString()
   }
   
   val parent = Parent(0, "parent", mutableSetOf())
   val child1 = Child(1, "child 1", parent)
   val child2 = Child(2, "child 2", parent)
   // Output: Parent(children=[Child(id=1, name=child 1, parent=recursive: Parent(...)), Child(id=2, name=child 2, parent=recursive: Parent(...))], id=0, name=parent)
   println(parent)
   ```

But, hurrah! Given the model and snippet above, **Kute** `asString()` does **not** run into a `StackOverflowError`, because it keeps track of the objects being processed.<br>
**Kute** `asString()` detects the recursion beforehand, instead of entering an endless loop.<br>
It adds a placeholder `recursive: < className > (...)` to the output, to inform the reader about the recursive relationship.

> **Remarks**
> 1. Mark that, in the above snippet, it would be recommended to exclude the `children` in `Parent` from `asString()`.<br>
> The same goes for the `parent`-attribute in `Child`.<br>
> See [exclude properties by adding a filter →](omit-values.md#exclude-properties-by-adding-a-filter-to-kutes-default-settings).<br>
> This avoids both performance issues and cluttered output.
> <br><br>
> 2. **Remark on _data_ classes with recursive relationships**<br>
>   When you would change the `Parent` and the `Child` entities in the snippet above to be `data class`es, you will encounter a `StackOverflowError`.<br>
>   That's not because of **Kute** `asString()`, but because of Kotlin's default `hashCode()` implementation.<br>
>   To make the snippet above work with data classes, you have to explicitly call `super.hashCode()` in the `Parent` and `Child` classes.

<hr>

#### How do other libraries do?
Other libraries, like Apache's _ToStringBuilder_, run into a `StackOverflowError` in such cases.<br>
In general, other libraries are not as stable (in not throwing exceptions) as you might hope or think.<br>
This is one of the main reasons actually to start writing **Kute** `asString()`.

I (_JHvH_) was really disappointed & dissatisfied to learn how many situations made these libraries crash (throw exceptions or errors).<br>
> For those interested, you may check out ['this' repo (**Kute** `asString()`)](https://github.com/JanHendrikVanHeusden/Kute) and run the test demos in package `package nl.kute.demo.alternatives`.<br>
> These demos of (among others) Apache's _ToStringBuilder_ and `Objects.toString()` run a number of test cases<br>
> (both usual and more exotic stuff), that are handled smoothly by **Kute** `asString()`, but where the other libraries / tools often fail.<br>
> 

<hr>

#### What kind of exceptions can NOT be avoided?<br>
**Kute** can't always avoid exceptions / errors; but it **handles all exceptions gracefully**,<br>
it never throws exceptions to the calling code.

Known situations where exceptions may occur are:

* <u>`KotlinReflectionInternalError`</u><br>
  **Java classes** that are **declared inside a method** (i.e., "method-local classes") cause `KotlinReflectionInternalError`:<br>
  ```
  void myMethod() {
      class MethodLocalClass { String myString = "I am a String"; }
      // Output something like: MethodLocalClass@b2c9a9c
      System.out.println(new MethodLocalClass());
  }
  ``` 
  Kotlin's reflection can't handle these. Instead of the expected output, you may get something like `MyClass@b2c9a9c`.
  > The equivalent in Kotlin is handled properly, though.

* **Uninitialized `lateinit` properties**<br>
  Resolving the value of an uninitialized `lateinit` property causes an `UninitializedPropertyAccessException`<br>
  wrapped in an `InvocationTargetException`.<br>
  Kotlin's reflection doesn't offer a way to detect the uninitialized state beforehand, so there is no other way than try-and-catch...<br><br>
  **Kute** `asString()` simply returns `null` in this case.<br><br>

* <u>`ConcurrentModificationException`</u><br>
  Even properly guarded collections are not safe for reflective reading; so `ConcurrentModificationException`s may occur.<br><br>
  
  When a `ConcurrentModificationException` occurs, **Kute** tries to take a defensive copy of the `Collection` or `Map` involved,<br>
  and uses that to resolve the values of the `Collection` / `Map`.<br>
  If a `ConcurrentModificationException` (or other exception) occurs again while taking the defensive copy,<br>
  a placeholder value is returned, something like `MyList@c2f829d`<br><br>

* **Other exceptions**<br>
  All other `Exception`s are caught, and logged.<br><br>

<u>

* #### `Error` & `Throwable`
  </u>`Error`s and `Throwable`s are not caught.
  > Except specific known cases of
  `KotlinReflectionInternalError` and `KotlinReflectionNotSupportedError`<br>
  > (as documented above).
