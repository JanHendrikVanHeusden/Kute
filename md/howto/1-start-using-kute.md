### _TODO:_
> #### Kute has not been published yet to Maven Central or any other publicly accessible directory.<br>
> #### Of course, publishing is needed before Kute can be used by interested parties.
 
<hr>

1. ## Add Kute asString to your depencency sections
   ### Gradle
   > Replace `[version]` by the latest version number:

   * #### Kotlin `build.gradle.kts`
     `implementation("nl.kute:asstring:[version]")`
   * #### Groovy `build.gradle`, one of:
      * `implementation 'nl.kute:kute:[version]'`<br>
      * `implementation group: 'nl.kute', name: 'asstring', version: '[version]'`

   * #### Maven `pom.xml`
        ```
        <dependency>
            <groupId>nl.kute</groupId>
            <artifactId>asstring</artifactId>
            <version>[version]</version>
        </dependency>
        ```

2. ## Start using `asString()`
   ### A. Kotlin

   You can use `asString()` mainly in two different ways:
   * As regular `toString()` drop-in implementation:<br>
   `override fun toString() = asString()`<br><br>
   * On-the-fly `String` representation of just any object:<br>
    E.g. in a log statement:<br> 
   `log.info { "Received event: ${event.asString()}" }`

   ### B. Java
   * As regular `toString()` drop-in implementation:
   ```
   @override
   public String toString() {
       return asString(this);
   }
   ```
   * To get an on-the-fly `String` representation of just any object:<br>
  E.g. in a log statement:<br>
  `logger.info("Received event: {}", asString(event));`

3. ## Explore further options
   See, for instance:
   * [→ How to...](0-howto.md)
   * [→ Omit specific property values](omit-values.md)
   * [→ Delimit property values](delimit-property-values.md)
   * [→ Hide, replace or obscure property values](hide-replace-obscure-values.md)
   * Limit output length:
      * [→ Limit length of property values in `asString()` output](limit-output-string-length.md)
      * [→ Limit the number of elements of collections etc. in `asString()` output](limit-elements-of-collections.md)
   * [→ Prefer existing `toString()`-implementations](prefer-existing-tostring.md)