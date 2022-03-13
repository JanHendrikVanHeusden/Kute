# Kute
One-stop Kotlin library to support common operations like `toString()`, `equals()`, `hashCode()` and `compare()`.

### Why would you want this?
1. Every developer who has made or debugged things like `equals()` / `hashCode()`, `compare()` etc.
knows that this kind of stuff is cumbersome. The same is true for `toString()`.    
But it can be done better and easier - with **Kute**!

2. Kute has features that are not provided by other libraries.    
    * You are probably aware that equality is not just equality. Think of entities.
You may have defined that entities are equal if they have the same ID (like a database identifier).    
But... even if the ID is the same, other data may be different...    
And sometimes, you just want to know if it is equal given other (or all) properties, apart from ID.    
Wouldn't it be great if you not only could do  `equals(other)`, but also `equalBy(other, property1, property3, propertyX)`?

    * `toString()`: maybe you want to exclude some fields from the `toString()` method.    
E.g. personally identifying or otherwise sensitive data that you don't want to be logged,
so you excluded it from your `toString()`.     
But any subclass's `toString()` may unwittingly expose these data that you carefully left out,
so effectively breaking your intentions...    
And maybe you don't want to leave them out completely, but just mask the data, e.g. replace text by ********** 
so that you at least can see the length of the data. Or other ways of masking.

    * Think of `compare()` and `Comparator`. These were designed in the past for quite static situations.    
But nowadays, we want things to have compared in more flexible ways, such that anything can be compared / sorted
by any property or combination we want.    
Luckily, Kotlin helps us with `Collection.sortedBy { ... }`, which is great!    
Still, it would be nice if we can define ordering in the file itself, by something like `equalBy(asc(property1), desc(property3), desc(propertyX))`

### Summary
So what do we miss in the built-ins (`Objects`-stuff) and libraries:
* Flexibility, e.g. easy `compareBy` or `equalsBy` with multiple properties
* Features like keeping stuff out of `toString` or masking them, consistently over the class hierarchy
* `toString()` representation similar to Kotlin's data class's `toString()`
* Ease of use, ease of maintenance, with readable and easily understandable code
* Massive robustness, even with circular dependencies etc.
    * That's quite common actually, but many libraries and built-ins do not handle circular dependencies properly,    
      causing endless loops / `StackOverflowError`s
      _E.g. contacts that are mutually in each other's contact list,
      or a parent object with a reference to child objects, with child objects having a back-reference to their parent_
    
The **Kute** library should make your developer-life easier by addressing these issues and requirements!

### Alternatives & issues
A non-exhaustive summary of issues with alternatives

1. ###### Using IDE templates    
    Of course, you can simply generate `toString()`, `equals()` and `hashCode()` using your IDE's templates, and it works as a charm

    But then: you can't see at a glance which properties are in there, in which order, and even more important: what is missing!
    And wish you luck if you need to add some property somewhere in the middle!

2. ###### Objects methods for `equals()` and `hashCode()`    
    Works well... but no help or guarantees to assure that this couple is in sync
   * In sync: such that `hashCode()` does not include properties that `equals()` does not reference

3. ###### `Objects.toString()` not as robust as we want
   `Objects.toString()` should be a no-brainer. But it appears that you quickly run into endless loops /`StackOverflowError`s
   when using with Kotlin... especially with anonymous nested classes. Let alone circular stuff...

4. ##### `Comparator`s and `compare()` miss flexibility    
    Designed in past times when requirements weren't changing that fast, and flexibility less needed

5. ###### Usability    
   Usage of libraries like Apache's is not as elegant or flexible as you might want, and all libraries definitely
   miss features we'd like to have
