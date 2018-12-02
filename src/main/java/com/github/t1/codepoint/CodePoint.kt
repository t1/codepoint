package com.github.t1.codepoint

import kotlin.streams.asSequence

/**
 * Standard [java.lang.Character]s have only 16-bit, while some Unicode characters require 32 bit,
 * so not all Unicode characters can be represented with a Character object. They can represented
 * in Strings, but then require two `char`s to store. This makes working with `char`s inconvenient.
 * This class allows working with all possible Unicode characters, extracting them from Strings
 * and working with them as objects in their own right.
 */
data class CodePoint(private val value: Int) : Comparable<CodePoint> {
    init {
        require(Character.isValidCodePoint(value) || value == EOF_VALUE) { "invalid code point 0x${HEX(value)}" }
    }

    override fun compareTo(other: CodePoint) = this.value.compareTo(other.value)

    /**
     * A String containing only this CodePoint. Can be one or two [Character]s long.
     */
    override fun toString(): String = if (value < 0) "" else String(Character.toChars(value))

    /**
     * The integer value of this CodePoint.
     */
    fun toInt() = value

    /**
     * Get a sequence of informational descriptions of the CodePoint: The [escaped string][escaped],
     * the [name], and the [hex code][hex].
     */
    val info get() = "[$escaped][$name][0x$hex]"

    /**
     * A string that can be used in a String object to represent this CodePoint, i.e. `\t` for the tab character.
     */
    val escaped
        get() = when {
            value == '\t'.toInt() -> "\\t"
            value == '\n'.toInt() -> "\\n"
            value == '\r'.toInt() -> "\\r"
            value == '\''.toInt() -> "\\'"
            value == '\"'.toInt() -> "\\\""
            value == '\\'.toInt() -> "\\\\"
            this == EOF -> "\\uFFFF"
            this == BOM || this == NEL -> unicodeEscape
            isSupplementary -> "\\u${pad(HEX(highSurrogate))}\\u${pad(HEX(lowSurrogate))}"
            else -> toString()
        }

    private val unicodeEscape get() = "\\u${pad(HEX)}"
    private val isSupplementary get() = Character.isSupplementaryCodePoint(value)
    private val lowSurrogate get() = Character.lowSurrogate(value)
    private val highSurrogate get() = Character.highSurrogate(value)

    /**
     * A description of the CodePoint, i.e. `LATIN CAPITAL LETTER A` for the character `A`.
     */
    val name
        get() = when {
            this == EOF -> "END OF FILE"
            value == 0 -> "NULL"
            else -> Character.getName(value) ?: "?"
        }

    /**
     * The shortest possible hex representation of the CodePoint value (using upper case `A` to `F`)
     */
    @Suppress("PropertyName") val HEX: String get() = hex.toUpperCase()

    /**
     * The shortest possible hex representation of the CodePoint value (using lower case `a` to `f`)
     */
    val hex: String get() = hex(value)

    private fun pad(string: String) = "0000".substring(string.length) + string

    /**
     * Is this CodePoint a valid hex character, i.e. `0` - `9`, `A` - `F`, or `a` - `f`
     */
    val isHex: Boolean
        get() = value >= '0'.toInt() && value <= '9'.toInt()
            || value >= 'A'.toInt() && value <= 'F'.toInt()
            || value >= 'a'.toInt() && value <= 'f'.toInt()

    /**
     * A CodePoint is a digit if its general category type `DECIMAL_DIGIT_NUMBER`, for example
     * * ISO-LATIN-1 digits (`0` - `9`)
     * * Arabic-Indic digits
     * * and many more
     */
    val isDigit get() = Character.isDigit(value)

    /**
     * A CodePoint is alphabetic if its general category type is any of the following:
     *
     * * `UPPERCASE_LETTER`
     * * `LOWERCASE_LETTER`
     * * `TITLECASE_LETTER`
     * * `MODIFIER_LETTER`
     * * `OTHER_LETTER`
     * * `LETTER_NUMBER`
     * * or it has contributory property Other_Alphabetic
     */
    val isAlphabetic get () = Character.isAlphabetic(value)

    /**
     * A CodePoint is a white space according to Java
     */
    val isWhitespace get () = Character.isWhitespace(value)

    /**
     * A CodePoint is a space character, i.e. one of these categories:
     * * `SPACE_SEPARATOR`
     * * `LINE_SEPARATOR`
     * * `PARAGRAPH_SEPARATOR`
     */
    val isSpaceChar get () = Character.isSpaceChar(value)

    /** Carriage Return (`\r` = 0x0D) or New Line (`\n` = 0x0A) */
    val isNewLine: Boolean get() = value == '\n'.toInt() || value == '\r'.toInt()

    /** Append the CodePoint to a [StringBuilder] */
    fun appendTo(out: StringBuilder) = out.appendCodePoint(value)!!

    /** Create a [CodePointRange] from this until that CodePoint (excluding that) */
    infix fun until(that: CodePoint) = CodePointRange(this, that - 1)

    /** Create a [CodePointRange] from this to that CodePoint (including that) */
    operator fun rangeTo(that: CodePoint) = CodePointRange(this, that)

    /** Add an offset to this CodePoint, e.g. adding 2 to an `A` returns a `C` */
    operator fun plus(i: Int) = CodePoint.of(value + i)

    /** Subtract an offset from this CodePoint, e.g. subtracting 2 from a `C` returns an `A` */
    operator fun minus(i: Int) = CodePoint.of(value - i)

    companion object {
        private const val EOF_VALUE = -1

        /** End Of File, represented by a -1 integer. Note that this is actually not a valid Unicode code point */
        @JvmField val EOF = of(EOF_VALUE)

        /**
         * Byte Order Mark: If the first character of a file is a `ZERO WIDTH NO-BREAK SPACE`,
         * it is commonly used as an indicator of the byte-order in the file:
         * little endian vs. big endian
         */
        @JvmField val BOM = of(0xFEFF)

        /** Next Line */
        @JvmField val NEL = of(0x0085)

        /** Construct from a single [Character]. Note that not all CodePoints can be created like this. */
        @JvmStatic fun of(codePoint: Char): CodePoint {
            require(!Character.isHighSurrogate(codePoint)) { "expected non-surrogate but got high surrogate 0x${HEX(codePoint)}" }
            require(!Character.isLowSurrogate(codePoint)) { "expected non-surrogate but got low surrogate 0x${HEX(codePoint)}" }
            return of(codePoint.toInt())
        }

        /** Construct from two [Character]s. Note that not all CodePoints can be created like this. */
        @JvmStatic fun of(high: Char, low: Char): CodePoint {
            require(Character.isSurrogatePair(high, low)) { "expected surrogate pair but got 0x${HEX(high)} 0x${HEX(low)}" }
            return of(Character.toCodePoint(high, low))
        }

        /** Construct from an Integer value. */
        @JvmStatic fun of(codePoint: Int): CodePoint = CodePoint(codePoint)

        /** Construct from a String containing one or two characters that form a valid UTF-16 encoded CodePoint */
        @JvmStatic fun of(string: String): CodePoint {
            require(string.codePointCount == 1) { "expected a string with one code point but got ${string.codePointCount} in \"$string\"" }
            return of(string.codePointAt(0))
        }

        /** Construct a List of all CodePoints in a String */
        @JvmStatic fun allOf(string: String): List<CodePoint> = string.codePoints().asSequence().map(Companion::of).toList()

        /** Construct a CodePoint from the Integer value in the String, e.g. `65` -> `A` or `0x43` -> `C` */
        @JvmStatic fun decode(text: String): CodePoint = of(Integer.decode(text)!!)

        private val String.codePointCount get(): Int = this.codePointCount(0, length)

        @Suppress("FunctionName") private fun HEX(char: Char) = hex(char).toUpperCase()
        @Suppress("FunctionName") private fun HEX(int: Int) = hex(int).toUpperCase()

        private fun hex(char: Char) = hex(char.toInt())
        private fun hex(int: Int) = Integer.toHexString(int)
    }
}
