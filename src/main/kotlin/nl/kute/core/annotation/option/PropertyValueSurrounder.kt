package nl.kute.core.annotation.option

// TODO: KDoc
@Suppress("DANGEROUS_CHARACTERS", "NonAsciiCharacters", "KDocMissingDocumentation", "EnumEntryName")
public enum class PropertyValueSurrounder(public val begin: String, public val end: String) {

    NONE("", ""),

    // Entries that are not accessible in Java, because of backtick-ed names, are annotated
    // as @JvmSynthetic. So in Java they won't be visible for the compiler or the IDE.
    // The companion object defines "properly named" constants for these, to allow usage within Java.

    @JvmSynthetic
    `«»`("«", "»"), // 186 187

    @JvmSynthetic
    `^^`("^", "^"),

    @JvmSynthetic
    `~~`("~", "~"),

    @JvmSynthetic
    `¦¦`("¦", "¦"), // 166

    @JvmSynthetic
    `||`("|", "|"),

    @JvmSynthetic
    `§§`("§", "§"), // 167

    @JvmSynthetic
    `¶¶`("¶", "¶"), // 182

    @JvmSynthetic
    `÷÷`("÷", "÷"), // 247

    @JvmSynthetic
    `##`("#", "#"),

    @JvmSynthetic
    `""`("\"", "\""),

    @JvmSynthetic
    `''`("'", "'"),

    @JvmSynthetic
    `()`("(", ")"),

    ANGLED_BRACKETS("<", ">"),

    BACKTICKS("`", "`"),

    @JvmSynthetic
    `**`("*", "*")
    ;

    // TODO: KDoc
    // Constants to make the backtick-ed (non-ASCII) entries available in Java:
    // poor Java does not support the backtick-ed names
    public companion object {

        public fun String?.surroundBy(surrounder: PropertyValueSurrounder): String =
            "${surrounder.begin}$this${surrounder.end}"

        @JvmField
        public val GUILLEMETS: PropertyValueSurrounder = `«»`

        @JvmField
        public val CARETS: PropertyValueSurrounder = `^^`

        @JvmField
        public val TILDES: PropertyValueSurrounder = `~~`

        @JvmField
        public val BROKEN_PIPES: PropertyValueSurrounder = `¦¦`

        @JvmField
        public val PIPES: PropertyValueSurrounder = `||`

        @JvmField
        public val PARAGRAPHS: PropertyValueSurrounder = `§§`

        @JvmField
        public val ALINEA_SIGNS: PropertyValueSurrounder = `¶¶`

        @JvmField
        public val DIVISION_SIGNS: PropertyValueSurrounder = `÷÷`

        @JvmField
        public val HASH_SIGNS: PropertyValueSurrounder = `##`

        @JvmField
        public val APOSTROPHES: PropertyValueSurrounder = `""`

        @JvmField
        public val QUOTES: PropertyValueSurrounder = `''`

        @JvmField
        public val PARENTHESES: PropertyValueSurrounder = `()`

        @JvmField
        public val ASTERISKS: PropertyValueSurrounder = `**`
    }
}
