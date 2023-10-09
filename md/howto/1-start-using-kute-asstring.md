### _TODO:_
> #### Kute has not been published yet to Maven Central or any other publicly accessible directory.<br>
> #### Of course, publishing is needed before Kute can be used by interested parties.

   * [Add dependency](#add-kute-asstring-to-your-dependency-section)
      * [Gradle](#gradle)
      * [Maven](#maven)
   * [Start using `asString()`](#start-using-asstring)
       * [Kotlin](#a-kotlin)
       * [Java](#b-java)
   * [Explore further options](#explore-further-options)

<hr>

1. ## Add Kute asString to your dependency section
   > Replace `[kuteversion]` by the latest version number

   ### Gradle
   * #### Kotlin `build.gradle.kts`
     `implementation("nl.kute:asstring:[kuteversion]")`<br>
   
   * #### Groovy `build.gradle`
     One of:
      * `compileOnly 'nl.kute:kute:[kuteversion]'`<br>
      * `compileOnly group: 'nl.kute', name: 'asstring', version: '[kuteversion]'`<br>

   * ### Maven
     In your `pom.xml` `dependencies` section:
        ```
        <dependencies>
            ...

            <dependency>
                <groupId>nl.kute</groupId>
                <artifactId>asstring</artifactId>
                <version>[kuteversion]</version>
            </dependency>
            ...
     
        </dependencies>
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