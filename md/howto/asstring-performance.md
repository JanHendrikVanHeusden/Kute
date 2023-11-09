| [← 🏠](../../)            | [← README.md](../../README.md) |
|:--------------------------|:-------------------------------|
| [← How to...](0-howto.md) | [→ FAQ](../../md/faq/0-faq.md) |

<hr>

## Kute `asString()` performance

* [Where can you expect performance issues?](#where-can-you-expect-performance-issues)
* [Avoid performance issues](#avoid-performance-issues)
* [Performance of Kute `asString()`](#performance-of-kute-asstring)
* [Optimizations](#optimizations)
* [Micro benchmarks](#micro-benchmarks)
  * [Competitors](#competitors)
  * [Test setup](#test-setup)
* [Results](#results)

<hr>

### Where can you expect performance issues?
Performance issues should not occur from **Kute** `asString()` itself, but might occur from side effects.
> Such performance issues may occur as well with IDE-generated `toString()`-methods,<br>
> e.g. in relationships with `FetchType.LAZY` in JPA.<br>

### Avoid performance issues
The <u>**main advice**</u> with regard to performance, is to **exclude properties** that, when fetched, would cause such side effects.<br>
This can be done [on a per-property basis →](omit-values.md#exclude-omit-specific-properties), or [by applying a filter globally →](omit-values.md#exclude-properties-by-adding-a-filter-to-kutes-default-settings) (recommended).

[**This document on excluding properties →**](omit-values.md#below-some-real-world-examples-that-may-help-avoid-real-world-performance-issues-with-jpa) details how to exclude such properties.<br>

> For sure, you don't want your `toString()` have the details fetched from a database or a remote system,<br>
> just because you called `toString()` / `asString()`<br>
> This is not specific for **Kute** `asString()`: it may occur with <u>IDE-generated `toString()` methods</u> as well.<br>
> 
> You are strongly **advised** to read [the document mentioned above →](omit-values.md#below-some-real-world-examples-that-may-help-avoid-real-world-performance-issues-with-jpa) **when your application has parent-child relationships**,<br>
> (and even more if the related entities are fetched lazily).<br>
> It has real-world examples on how to avoid performance issues by using filters.

<hr>

### Performance of Kute `asString()`
**Kute** `asString()` uses reflection to resolve property values. Saying goes that reflection is slow.<br>
  However, **there's not much difference** in getting a value from a property than getting a value by calling a getter.

> In most cases, **Kute** `asString()` is <u>at least as fast</u> as IDE-generated `toString()`-methods.<br>
> See the [section on Micro benchmarks](#micro-benchmarks).

<hr>

### Optimizations
The best optimization, of course, is to avoid doing things that you don't need to.<br>
One of the second-best ways is to execute code not more often than you need to: that's where caching comes in.

1. #### Caching by Kute
   * A lot of information that **Kute** `asString()` uses, is **compile-time static**: properties, annotations, etc. don't change over time.
     * **Kute** caches these, without need for a change / reset-mechanism (_insert-only_). 
     <br><br>
   * Other information may only **change when default-settings are changed**
     * E.g., if you opt globally to [`PREFER_TOSTRING` →](https://janhendrikvanheusden.github.io/Kute/kute/nl.kute.asstring.annotation.option/-to-string-preference/-p-r-e-f-e-r_-t-o-s-t-r-i-n-g/index.html), **Kute** `asString()` must start to behave differently when resolving string-representations of classes.<br>
      Such changes are supposed to be infrequent (typically one-time only, at start of application),<br>
       so also feasible for caching.
     * **Kute** `asString()` uses a subscription mechanism to have the cache(s) cleared when configuration settings change.
   <u>
   #### What is cached by Kute?
   </u>**Kute** `asString()` caches, among others:
   * Properties and their `@AsString...`-annotations, by class
   * Annotations that apply to `Companion`-objects, by class
   * `ToStringPreference`, by class
   * Whether a class has a `toString()`-method implemented
   * Object type categorization (for **Kute** internal use), by class
   * Compiled regular expressions of `@AsStringReplace.pattern`
     * To avoid compiling the same regular expression over and over

2. #### Avoid exceptions
   It is well known that exceptions are very costly, in terms of performance.<br>
   **Kute** `asString()` tries to avoid exceptions as much as possible.
   
   * Especially `StackOverflowError` does not occur when resolving mutual relationships or other recursive data.<br>
   **Kute** `asString()` keeps track of elements being processed, this way `StackOverflowError` are prevented instead of caught.
   > See [Kute Exception handling](kute-exception-handling.md) for more details.

<hr>

### Micro benchmarks

The **Kute** repository contains a number of [micro-benchmarks](https://github.com/JanHendrikVanHeusden/Kute/tree/main/src/jmh/kotlin/nl/kute/performance), using the [JMH Java Micro Benchmark Harness](https://github.com/openjdk/jmh).<br>

#### Competitors
Most of these JMH tests compare the following libraries, to retrieve a String-representation of objects:
1. **Kute** `asString()`
2. Google's `Gson.gsonToJson()`
3. IDE-generated `toString()`
4. Apache's `toStringBuilder`

#### Test setup
* Each object under test has 4 methods (one for each competing library) to produce a String-representation.
   * To avoid issues caused by the order in which te tests are run, the 4 methods on each object are called in random order.<br>
* There are different tests, and different sets of test objects, e.g.:
   * A test with one thousand different classes, each with a not-too-big number of properties
   * Test classes with a huge number of properties (500 per class)
   * Tests with lots of non-default settings, e.g. sorting properties
* The properties of each object are set to random values (prior to the actual benchmark execution)
* The tests run with sufficiently large populations, with sufficient iterations

> You are encouraged to run and/or review these benchmarks; especially if you have the chance to run these on representative server hardware!

<hr>

### Results
The results (on different machines, MacOs & Windows) can be [found here](https://github.com/JanHendrikVanHeusden/Kute/tree/main/performance-test-results).<br>

> **Remarks**<br>
> 1. These JMH tests are performed on a Mac laptop, and on a Windows desktop machine, from within the IntelliJ IDE.<br>
>   Although I have tried to minimize disturbing factors (minimizing all window, not doing any other work while running the tests),<br>
>   the results can not be interpreted as solid benchmark results. They may give an indication, though.
> 
> 2. The kind of job that **Kute** `asString()` does is, in general, not the stuff that makes or breaks your application's performance (think of nanoseconds);<br>
> usually, the real performance challenges come from database access, remote web calls, concurrency etc. 

<hr>

1. In **general**, `asString()` performance is comparable to its competitors, including IDE-generated `toString()`-methods<br><br>
   * Often, **Kute** `asString()` performs even slightly better than its competitors, although the differences seem not statistically relevant.<br><br>
   * If this surprises you, keep in mind that retrieving a value from a property (by reflection) is, performance-wise,<br>
     not significantly different to retrieving a value from a getter.<br>
     > The crux with reflection is avoiding to do work that you don't need to; see the section on [optimizations](#optimizations) above.

<br>

2. In _**single-shot**_ tests, where each object is processed only a single time per method, `asString()` seems to do slightly worse than others.<br><br>
   * This is probably due to:
      * Initialization of caches
      * A lot of work that other libraries/competitors do not do, e.g. processing annotations and preferences;<br>
        This usually needs to be done only once per class or once per property (or even once per application), due to caching,<br>
        but as expected, it hits **Kute** `asString()` a bit harder than its competitors, in _single-shot_ tests<br><br> 

     **Remarks**
     > 1. As can be expected, the _single-shot_ results show a too wide spread to draw hard conclusions;<br>
     this is more or less inherent to single-shot tests with split-second operations.
     > 2. In some tests, `asString()` performs as well, or better than, competitors;<br>
         but the general picture is, that in _single-shot_ tests, **Kute** `asString()` is doing slightly worse than its competitors, performance-wise.<br>
      Please review [the results](https://github.com/JanHendrikVanHeusden/Kute/tree/main/performance-test-results/environment-1/PerformanceSingleShot) yourself, to make up your mind on this.

<br><br>
3. Applying **more non-default options** in **Kute** `asString()` has little effect on performance<br><br>
    * This was a bit unexpected, yet positive!
    * The results can be found [here](https://github.com/JanHendrikVanHeusden/Kute/tree/main/performance-test-results/environment-1/PerformanceFewPropsLotsOfOptions) and [here](https://github.com/JanHendrikVanHeusden/Kute/tree/main/performance-test-results/environment-1/PerformanceManyPropsWithPropSorting).
      * Even sorting of properties (with classes with 500 properties) did not cause significant delays.
      * The same is true when a lot of non-default options were set.