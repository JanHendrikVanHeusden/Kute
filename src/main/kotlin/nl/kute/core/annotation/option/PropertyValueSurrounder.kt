package nl.kute.core.annotation.option

/**
 * Enum to define prefixes and postfixes, to distinguish property value Strings from other text.
 * * Some pre/postfixes may look a bit exotic, e.g. `¶¶`, `÷÷`, `¦¦`, this is done deliberately:
 *   these do not appear usually in normal text, so these will clearly set the value String apart.
 * * A few pre/postfixes are actually not recommended, e.g. `[]`, `{}`, `()`, `''`, `""`, `` ` ``.
 *   These are convenient, but on the other hand somewhat confusing because they are also used
 *   in various other stuff, e.g. [toString]/[nl.kute.core.asString] of [Collection], [Map], SQL-Strings, etc.
 * * Some pre/postfixes use Ascii codes 128 - 255, these may not render properly on non-UTF-8 systems
 *   (all should render correctly on Unicode / UTF-8 compatible systems and on Windows, though).
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

    /** [prefix] = `«` (Ascii 171), [postfix] = `»` (Ascii 187). In Java, use [GUILLEMETS] */
    @JvmSynthetic
    `«»`("«", "»"),

    /** [prefix]/[postfix] = `^` (Ascii 94). In Java, use [CARETS] */
    @JvmSynthetic
    `^^`("^", "^"),

    /** [prefix]/[postfix] = `~` (Ascii 126). In Java, use [TILDES] */
    @JvmSynthetic
    `~~`("~", "~"),

    /** [prefix]/[postfix] = `¦` (Ascii 166). In Java, use [BROKEN_PIPES] */
    @JvmSynthetic
    `¦¦`("¦", "¦"),

    /** [prefix]/[postfix] = `|` (Ascii 124). In Java, use [PIPES] */
    @JvmSynthetic
    `||`("|", "|"),

    /** [prefix]/[postfix] = `§` (Ascii 167). In Java, use [PARAGRAPHS] */
    @JvmSynthetic
    `§§`("§", "§"),

    /** [prefix]/[postfix] = `¶` (Ascii 182). In Java, use [ALINEA_SIGNS] */
    @JvmSynthetic
    `¶¶`("¶", "¶"),

    /** [prefix]/[postfix] = `÷` (Ascii 247). In Java, use [DIVISION_SIGNS] */
    @JvmSynthetic
    `÷÷`("÷", "÷"),

    /** [prefix]/[postfix] = `#` (Ascii 35). In Java, use [HASH_SIGNS] */
    @JvmSynthetic
    `##`("#", "#"),

    /** [prefix]/[postfix] = `"` (Ascii 34). In Java, use [DOUBLE_QUOTES] */
    @JvmSynthetic
    `""`("\"", "\""),

    /** [prefix]/[postfix] = `'` (Ascii 39). In Java, use [SINGLE_QUOTES] */
    @JvmSynthetic
    `''`("'", "'"),

    /** [prefix] = `<` (Ascii 60), [postfix] = `>` (Ascii 62) */
    ANGLED_BRACKETS("<", ">"),

    /** [prefix] = `[` (Ascii 91), [postfix] = `]` (Ascii 93) */
    SQUARE_BRACKETS("[", "]"),

    /** [prefix] = `(` (Ascii 40), [postfix] = `)` (Ascii 41). In Java, use [PARENTHESES] */
    @JvmSynthetic
    `()`("(", ")"),

    /** [prefix] = `{` (Ascii 123), [postfix] = `}` (Ascii 125). In Java, use [BRACES] */
    @JvmSynthetic
    `{}`("{", "}"),

    /** [prefix]/[postfix] = `` ` `` (Ascii 96) */
    BACKTICKS("`", "`"),

    /** [prefix]/[postfix] = `*` (Ascii 42). In Java, use [ASTERISKS] */
    @JvmSynthetic
    `**`("*", "*")
    ;

    public companion object {

        /**
         * Surrounds the receiver String by [surrounder].[prefix] / [surrounder].[postfix].
         * If the receiver is `null`, it will be rendered as String `"null"` (without the `"`).
         * @return [prefix] + `receiver` + [postfix]
         */
        public fun String?.surroundBy(surrounder: PropertyValueSurrounder): String =
            "${surrounder.prefix}$this${surrounder.postfix}"
        
    // region ~ Constants for Java compatibility

        // Constants to make the backtick-ed (non-ASCII) entries available in Java:
        // poor Java does not support the backtick-ed names.
        // Usable from Kotlin too, if you don't like the backtick-ed names.

        /** [prefix] = `«` (Ascii 171), [postfix] = `»` (Ascii 187). */
        @JvmField
        public val GUILLEMETS: PropertyValueSurrounder = `«»`

        /** [prefix]/[postfix] = `^` (Ascii 94). */
        @JvmField
        public val CARETS: PropertyValueSurrounder = `^^`

        /** [prefix]/[postfix] = `~` (Ascii 126) */
        @JvmField
        public val TILDES: PropertyValueSurrounder = `~~`

        /** [prefix]/[postfix] = `¦` (Ascii 166) */
        @JvmField
        public val BROKEN_PIPES: PropertyValueSurrounder = `¦¦`

        /** [prefix]/[postfix] = `|` (Ascii 124) */
        @JvmField
        public val PIPES: PropertyValueSurrounder = `||`

        /** [prefix]/[postfix] = `§` (Ascii 167) */
        @JvmField
        public val PARAGRAPHS: PropertyValueSurrounder = `§§`

        /** [prefix]/[postfix] = `¶` (Ascii 182) */
        @JvmField
        public val ALINEA_SIGNS: PropertyValueSurrounder = `¶¶`

        /** [prefix]/[postfix] = `÷` (Ascii 247) */
        @JvmField
        public val DIVISION_SIGNS: PropertyValueSurrounder = `÷÷`

        /** [prefix]/[postfix] = `#` (Ascii 35) */
        @JvmField
        public val HASH_SIGNS: PropertyValueSurrounder = `##`

        /** [prefix]/[postfix] = `'` (Ascii 39) */
        @JvmField
        public val SINGLE_QUOTES: PropertyValueSurrounder = `''`

        /** [prefix]/[postfix] = `"` (Ascii 34) */
        @JvmField
        public val DOUBLE_QUOTES: PropertyValueSurrounder = `""`

        /** [prefix] = `(` (Ascii 40), [postfix] = `)` (Ascii 41). */
        @JvmField
        public val PARENTHESES: PropertyValueSurrounder = `()`

        /** [prefix] = `{` (Ascii 123), [postfix] = `}` (Ascii 125). */
        @JvmField
        public val BRACES: PropertyValueSurrounder = `{}`

        /** [prefix]/[postfix] = `*` (Ascii 42) */
        @JvmField
        public val ASTERISKS: PropertyValueSurrounder = `**`

    // endregion
    }
}
