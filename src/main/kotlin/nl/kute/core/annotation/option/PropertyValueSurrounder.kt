package nl.kute.core.annotation.option

// TODO: KDoc
@Suppress("DANGEROUS_CHARACTERS", "NonAsciiCharacters", "KDocMissingDocumentation", "EnumEntryName")
public enum class PropertyValueSurrounder(public val begin: String, public val end: String) {
    EMPTY("", ""),

    @JvmSynthetic
    `«»`("«", "»"),

    @JvmSynthetic
    `^^`("^", "^"),

    @JvmSynthetic
    `~~`("~", "~"),

    @JvmSynthetic
    `¦¦`("|", "|"),

    @JvmSynthetic
    `||`("", ""),

    @JvmSynthetic
    `§§`("§", "§"),

    @JvmSynthetic
    `¶¶`("¶", "¶"),

    @JvmSynthetic
    `÷÷`("÷", "÷"),

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

