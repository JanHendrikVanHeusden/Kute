package nl.kute.asstring.annotation.option

import nl.kute.asstring.annotation.option.PropertyValueSurrounder.Companion
import nl.kute.asstring.annotation.option.PropertyValueSurrounder.NONE

/**
 * Enum to define prefixes and postfixes, to distinguish property value Strings from other text.
 * E.g.
 * ```
 * class MyClass {
 *     public val my1stVal: String = "first value"
 *     public val my2ndVal: String = "second value"
 *     public val myNullableVal: String? = null
 * }
 * // ...
 * asStringConfig().withSurroundPropValue(PropertyValueSurrounder.`«»`).applyAsDefault()
 * println(MyClass().asString()) // MyClass(my1stVal=«first value», my2ndVal=«second value», myNullableVal=null)
 * ```
 * * Some pre/postfixes may look a bit exotic, e.g. `¶¶`, `÷÷`, `¦¦`, this is done deliberately:
 *   these do not appear usually in normal text, so these will clearly set the value String apart.
 *   It's a matter of choice which to choose; or use [NONE] for no prefix/postfix.
 * * A few pre/postfixes are convenient, yet not recommended, e.g. `[]`, `{}`, `()`, `''`, `""`, `` ` ``.
 *   These are somewhat confusing because they are also used in various other stuff;
 *   e.g. [toString] or [nl.kute.asstring.core.asString] of [Collection], [Map]; SQL-Strings; plain texts, etc.
 * * Some pre/postfixes use Ascii codes 128 - 255, these may not render properly on non-UTF-8 systems
 *   (all should render correctly on Unicode / UTF-8 compatible systems and on Microsoft Windows, though).
 *
 * > **Java:** Backtick-ed names, e.g.  `` `«»`, `^^`, `~~` `` cannot be used directly in Java code.
 * The [Companion] object contains aliases for these, to allow usage of these in Java.
 * @param [prefix] The prefix (if any) to be concatenated at the start of a property's String value
 * @param [postfix] The postfix (if any) to be concatenated at the end of a property's String value
 */
@Suppress("DANGEROUS_CHARACTERS", "NonAsciiCharacters", "EnumEntryName")
public enum class PropertyValueSurrounder(public val prefix: String, public val postfix: String) {

    // Using backtick-ed names (insofar possible), so what you see is what you get :-)
    // 
    // Backtick-ed names can not be used in Java sources. These are annotated as @JvmSynthetic,
    // to make them invisible for the Java compiler and for IDE code completion in Java.
    //
    // The companion object below defines "properly named" constants for these to use in Java.

    /** No pre/postfix: empty Strings */
    NONE("", ""),

    /** [prefix] = `«` *(Ascii 171)*, [postfix] = `»` *(Ascii 187)*. In Java, use [Companion.GUILLEMETS] */
    @JvmSynthetic
    `«»`("«", "»"),

    /** [prefix]/[postfix] = `^` *(Ascii 94)*. In Java, use [Companion.CARETS] */
    @JvmSynthetic
    `^^`("^", "^"),

    /** [prefix]/[postfix] = `~` *(Ascii 126)*. In Java, use [Companion.TILDES] */
    @JvmSynthetic
    `~~`("~", "~"),

    /** [prefix]/[postfix] = `¦` *(Ascii 166)*. In Java, use [Companion.BROKEN_PIPES] */
    @JvmSynthetic
    `¦¦`("¦", "¦"),

    /** [prefix]/[postfix] = `|` *(Ascii 124)*. In Java, use [Companion.PIPES] */
    @JvmSynthetic
    `||`("|", "|"),

    /** [prefix]/[postfix] = `§` *(Ascii 167)*. In Java, use [Companion.PARAGRAPHS] */
    @JvmSynthetic
    `§§`("§", "§"),

    /** [prefix]/[postfix] = `¶` *(Ascii 182)*. In Java, use [Companion.ALINEA_SIGNS] */
    @JvmSynthetic
    `¶¶`("¶", "¶"),

    /** [prefix]/[postfix] = `÷` *(Ascii 247)*. In Java, use [Companion.DIVISION_SIGNS] */
    @JvmSynthetic
    `÷÷`("÷", "÷"),

    /** [prefix]/[postfix] = `#` *(Ascii 35)*. In Java, use [Companion.HASH_SIGNS] */
    @JvmSynthetic
    `##`("#", "#"),

    /** [prefix]/[postfix] = `"` *(Ascii 34)*. In Java, use [Companion.DOUBLE_QUOTES] */
    @JvmSynthetic
    `""`("\"", "\""),

    /** [prefix]/[postfix] = `'` *(Ascii 39)*. In Java, use [Companion.SINGLE_QUOTES] */
    @JvmSynthetic
    `''`("'", "'"),

    /** [prefix] = `<` *(Ascii 60)*, [postfix] = `>` *(Ascii 62)* */
    ANGLED_BRACKETS("<", ">"),

    /** [prefix] = `[` *(Ascii 91)*, [postfix] = `]` *(Ascii 93)* */
    SQUARE_BRACKETS("[", "]"),

    /** [prefix] = `(` *(Ascii 40)*, [postfix] = `)` *(Ascii 41)*. In Java, use [Companion.PARENTHESES] */
    @JvmSynthetic
    `()`("(", ")"),

    /** [prefix] = `{` *(Ascii 123)*, [postfix] = `}` *(Ascii 125)*. In Java, use [Companion.BRACES] */
    @JvmSynthetic
    `{}`("{", "}"),

    /** [prefix]/[postfix] = ``` ` ``` *(Ascii 96)* */
    BACKTICKS("`", "`"),

    /** [prefix]/[postfix] = `*` *(Ascii 42)*. In Java, use [Companion.ASTERISKS] */
    @JvmSynthetic
    `**`("*", "*")
    ;

    /**
     * Contains constants for Java compatibility
     * > Java does not support backtick-ed names like ``` `«»`, `^^`, `~~` etc.```
     */
    public companion object Companion {

        /**
         * Surrounds the receiver String by [surrounder].[prefix] / [surrounder].[postfix].
         * If the receiver is `null`, it will be rendered as String `"null"` (without the `"`).
         * @return [prefix] + `receiver` + [postfix]
         */
        @JvmSynthetic // avoid access from external Java code
        internal fun String?.surroundBy(surrounder: PropertyValueSurrounder): String =
            "${surrounder.prefix}$this${surrounder.postfix}"
        
    // region ~ Constants for Java compatibility

        // Constants to make the backtick-ed (non-ASCII) entries available in Java:
        // poor Java does not support the backtick-ed names.
        // Usable from Kotlin too, if you don't like the backtick-ed names.

        /**
         * [prefix] = `«` *(Ascii 171)*, [postfix] = `»` *(Ascii 187)*.
         * > Allows Java code to use [«»]
         */
        @JvmField
        public val GUILLEMETS: PropertyValueSurrounder = `«»`

        /**
         * [prefix]/[postfix] = `^` *(Ascii 94)*.
         * > Allows Java code to use [^^]
         */
        @JvmField
        public val CARETS: PropertyValueSurrounder = `^^`

        /**
         * [prefix]/[postfix] = `~` *(Ascii 126)*.
         * > Allows Java code to use [~~]
         */
        @JvmField
        public val TILDES: PropertyValueSurrounder = `~~`

        /**
         * [prefix]/[postfix] = `¦` *(Ascii 166)*.
         * > Allows Java code to use [¦¦]
         */
        @JvmField
        public val BROKEN_PIPES: PropertyValueSurrounder = `¦¦`

        /**
         * [prefix]/[postfix] = `|` *(Ascii 124)*.
         * > Allows Java code to use [||]
         */
        @JvmField
        public val PIPES: PropertyValueSurrounder = `||`

        /**
         * [prefix]/[postfix] = `§` *(Ascii 167)*.
         * > Allows Java code to use [§§]
         */
        @JvmField
        public val PARAGRAPHS: PropertyValueSurrounder = `§§`

        /**
         * [prefix]/[postfix] = `¶` *(Ascii 182)*.
         * > Allows Java code to use [¶¶]
         */
        @JvmField
        public val ALINEA_SIGNS: PropertyValueSurrounder = `¶¶`

        /**
         * [prefix]/[postfix] = `÷` *(Ascii 247)*.
         * > Allows Java code to use [÷÷]
         */
        @JvmField
        public val DIVISION_SIGNS: PropertyValueSurrounder = `÷÷`

        /**
         * [prefix]/[postfix] = `#` *(Ascii 35)*.
         * > Allows Java code to use [##]
         */
        @JvmField
        public val HASH_SIGNS: PropertyValueSurrounder = `##`

        /**
         * [prefix]/[postfix] = `'` *(Ascii 39)*.
         * > Allows Java code to use ['']
         */
        @JvmField
        public val SINGLE_QUOTES: PropertyValueSurrounder = `''`

        /**
         * [prefix]/[postfix] = `"` *(Ascii 34)*.
         * > Allows Java code to use [""]
         */
        @JvmField
        public val DOUBLE_QUOTES: PropertyValueSurrounder = `""`

        /**
         * [prefix] = `(` *(Ascii 40)*, [postfix] = `)` (Ascii 41)*.
         * > Allows Java code to use [()]
         */
        @JvmField
        public val PARENTHESES: PropertyValueSurrounder = `()`

        /**
         * [prefix] = `{` *(Ascii 123)*, [postfix] = `}` *(Ascii 125)*.
         * > Allows Java code to use [{}]
         */
        @JvmField
        public val BRACES: PropertyValueSurrounder = `{}`

        /**
         * [prefix]/[postfix] = `*` *(Ascii 42)*.
         * > Allows Java code to use [**]
         */
        @JvmField
        public val ASTERISKS: PropertyValueSurrounder = `**`

    // endregion
    }
}