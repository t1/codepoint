package com.github.t1.codepoint;

import org.junit.jupiter.api.Test;

import static com.github.t1.codepoint.CodePoint.BOM;
import static com.github.t1.codepoint.CodePoint.EOF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodePointTest {
    private final CodePoint A = CodePoint.of('A');
    private final CodePoint C = CodePoint.of('C');
    private final CodePoint TAB = CodePoint.of('\t');
    private final CodePoint NL = CodePoint.of('\n');
    private final CodePoint CR = CodePoint.of('\r');
    private final CodePoint ARABIC_1 = CodePoint.of(0x0661);
    private final CodePoint GRINNING_FACE = CodePoint.of("😀");


    @Test void ofOneChar() { assertThat(CodePoint.of((char) 0x0041)).isEqualTo(A); }

    @Test void failOfOneHighSurrogate() {
        assertThatThrownBy(() -> CodePoint.of((char) 0xD83D))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("expected non-surrogate but got high surrogate 0xD83D");
    }

    @Test void failOfOneLowSurrogate() {
        assertThatThrownBy(() -> CodePoint.of((char) 0xDE00))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("expected non-surrogate but got low surrogate 0xDE00");
    }

    @Test void failOfOneNegative() {
        assertThatThrownBy(() -> CodePoint.of(-10))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("invalid code point 0xFFFFFFF6");
    }


    @Test void ofTwoChars() { assertThat(CodePoint.of((char) 0xD83D, (char) 0xDE00)).isEqualTo(GRINNING_FACE); }

    @Test void failOfTwoChars() {
        assertThatThrownBy(() -> CodePoint.of('X', 'Y'))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("expected surrogate pair but got 0x58 0x59");
    }


    @Test void ofString() { assertThat(CodePoint.of("A")).isEqualTo(A); }

    @Test void failOfEmptyString() {
        assertThatThrownBy(() -> CodePoint.of(""))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("expected a string with one code point but got 0 in \"\"");
    }

    @Test void failOfTooLongString() {
        assertThatThrownBy(() -> CodePoint.of("AB"))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("expected a string with one code point but got 2 in \"AB\"");
    }


    @SuppressWarnings("EqualsWithItself")
    @Test void compareAtoA() { assertThat(A.compareTo(A)).isEqualTo(0); }

    @Test void compareAtoC() { assertThat(A.compareTo(C)).isEqualTo(-1); }

    @Test void compareCtoA() { assertThat(C.compareTo(A)).isEqualTo(1); }


    @Test void toStringTAB() { assertThat(TAB.toString()).isEqualTo("\t"); }

    @Test void toStringA() { assertThat(A.toString()).isEqualTo("A"); }

    @Test void toStringGrinningFace() { assertThat(GRINNING_FACE.toString()).isEqualTo("😀"); }


    @Test void toIntA() { assertThat(A.toInt()).isEqualTo(0x41); }

    @Test void toIntGrinningFace() { assertThat(GRINNING_FACE.toInt()).isEqualTo(0x1f600); }


    @Test void infoEOF() { assertThat(EOF.getInfo()).isEqualTo("[\\uFFFF][END OF FILE][0xffffffff]"); }

    @Test void infoTAB() { assertThat(TAB.getInfo()).isEqualTo("[\\t][CHARACTER TABULATION][0x9]"); }

    @Test void infoNL() { assertThat(NL.getInfo()).isEqualTo("[\\n][LINE FEED (LF)][0xa]"); }

    @Test void infoCR() { assertThat(CR.getInfo()).isEqualTo("[\\r][CARRIAGE RETURN (CR)][0xd]"); }

    @Test void infoA() { assertThat(A.getInfo()).isEqualTo("[A][LATIN CAPITAL LETTER A][0x41]"); }

    @Test void infoGrinningFace() { assertThat(GRINNING_FACE.getInfo()).isEqualTo("[\\uD83D\\uDE00][GRINNING FACE][0x1f600]"); }


    @Test void escapedEOF() { assertThat(EOF.getEscaped()).isEqualTo("\\uFFFF"); }

    @Test void escapedTAB() { assertThat(TAB.getEscaped()).isEqualTo("\\t"); }

    @Test void escapedNL() { assertThat(NL.getEscaped()).isEqualTo("\\n"); }

    @Test void escapedCR() { assertThat(CR.getEscaped()).isEqualTo("\\r"); }

    @Test void escapedSingleQuote() { assertThat(CodePoint.of('\'').getEscaped()).isEqualTo("\\'"); }

    @Test void escapedDoubleQuote() { assertThat(CodePoint.of('\"').getEscaped()).isEqualTo("\\\""); }

    @Test void escapedBackslash() { assertThat(CodePoint.of('\\').getEscaped()).isEqualTo("\\\\"); }

    @Test void escapedA() { assertThat(A.getEscaped()).isEqualTo("A"); }

    @Test void escapedBOM() { assertThat(BOM.getEscaped()).isEqualTo("\\uFEFF"); }

    @Test void escapedArabic1() { assertThat(ARABIC_1.getEscaped()).isEqualTo("١"); }

    @Test void escapedGrinningFace() { assertThat(GRINNING_FACE.getEscaped()).isEqualTo("\\uD83D\\uDE00"); }


    @Test void supplementaryA() { assertThat(A.isSupplementary()).isFalse(); }

    @Test void supplementaryArabic1() { assertThat(ARABIC_1.isSupplementary()).isFalse(); }

    @Test void supplementaryGrinningFace() { assertThat(GRINNING_FACE.isSupplementary()).isTrue(); }


    @Test void nameGrinningFace() { assertThat(GRINNING_FACE.getName()).isEqualTo("GRINNING FACE"); }


    @Test void HEX_A() { assertThat(A.getHEX()).isEqualTo("41"); }

    @Test void HEX_TAB() { assertThat(TAB.getHEX()).isEqualTo("9"); }

    @Test void HEX_EOF() { assertThat(EOF.getHEX()).isEqualTo("FFFFFFFF"); }

    @Test void HEX_GrinningFace() { assertThat(GRINNING_FACE.getHEX()).isEqualTo("1F600"); }


    @Test void hexA() { assertThat(A.getHex()).isEqualTo("41"); }

    @Test void hexTAB() { assertThat(TAB.getHex()).isEqualTo("9"); }

    @Test void hexEOF() { assertThat(EOF.getHex()).isEqualTo("ffffffff"); }

    @Test void hexGrinningFace() { assertThat(GRINNING_FACE.getHex()).isEqualTo("1f600"); }


    @Test void isHexA() { assertThat(A.isHex()).isTrue(); }

    @Test void isHexTAB() { assertThat(TAB.isHex()).isFalse(); }

    @Test void isHexArabicOne() { assertThat(ARABIC_1.isHex()).isFalse(); }


    @Test void isDigitA() { assertThat(A.isDigit()).isFalse(); }

    @Test void isDigitTAB() { assertThat(TAB.isDigit()).isFalse(); }

    @Test void isDigitZero() { assertThat(CodePoint.of('0').isDigit()).isTrue(); }

    @Test void isDigitArabicOne() { assertThat(ARABIC_1.isDigit()).isTrue(); }


    @Test void isAlphabeticA() { assertThat(A.isAlphabetic()).isTrue(); }

    @Test void isAlphabeticTAB() { assertThat(TAB.isAlphabetic()).isFalse(); }

    @Test void isAlphabeticZero() { assertThat(CodePoint.of('0').isAlphabetic()).isFalse(); }

    @Test void isAlphabeticArabicOne() { assertThat(ARABIC_1.isAlphabetic()).isFalse(); }


    @Test void isWhitespaceTAB() { assertThat(TAB.isWhitespace()).isTrue(); }

    @Test void isWhitespaceA() { assertThat(A.isWhitespace()).isFalse(); }


    @Test void isSpaceTAB() { assertThat(TAB.isSpaceChar()).isFalse(); }

    @Test void isSpaceNL() { assertThat(NL.isSpaceChar()).isFalse(); }

    @Test void isSpaceSpace() { assertThat(CodePoint.of(' ').isSpaceChar()).isTrue(); }

    @Test void isSpaceA() { assertThat(A.isSpaceChar()).isFalse(); }


    @Test void isNlTAB() { assertThat(TAB.isNewLine()).isFalse(); }

    @Test void isNlCR() { assertThat(CR.isNewLine()).isTrue(); }

    @Test void isNlNL() { assertThat(NL.isNewLine()).isTrue(); }

    @Test void isNlSpace() { assertThat(CodePoint.of(' ').isNewLine()).isFalse(); }

    @Test void isNlA() { assertThat(A.isNewLine()).isFalse(); }

    @Test void appendToStringBuilder() {
        StringBuilder out = new StringBuilder("»");

        A.appendTo(out);
        TAB.appendTo(out);
        NL.appendTo(out);
        ARABIC_1.appendTo(out);
        GRINNING_FACE.appendTo(out);
        out.append("«");

        assertThat(out.toString()).isEqualTo("»A\t\n١😀«");
    }


    @Test void rangeAtoCContainsB() { assertThat(A.rangeTo(C).contains(CodePoint.of('B'))).isTrue(); }

    @Test void rangeAtoCContainsC() { assertThat(A.rangeTo(C).contains(C)).isTrue(); }

    @Test void rangeAUntilCContainsC() { assertThat(A.until(C).contains(C)).isFalse(); }


    @Test void cMinus2isA() { assertThat(C.minus(2)).isEqualTo(A); }

    @Test void failMinus() {
        assertThatThrownBy(() -> TAB.minus(11))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("invalid code point 0xFFFFFFFE");
    }


    @Test void aPlus2isC() { assertThat(A.plus(2)).isEqualTo(C); }

    @Test void failPlus() {
        assertThatThrownBy(() -> CodePoint.of(Character.MAX_CODE_POINT).plus(1))
            .isInstanceOf(IllegalArgumentException.class).hasMessage("invalid code point 0x110000");
    }


    @Test void allOfEmpty() { assertThat(CodePoint.allOf("")).isEmpty(); }

    @Test void allOfA() { assertThat(CodePoint.allOf("A")).containsExactly(A); }

    @Test void allOfAC() { assertThat(CodePoint.allOf("AC")).containsExactly(A, C); }


    @Test void decode65() { assertThat(CodePoint.decode("65")).isEqualTo(A); }

    @Test void decode0x43() { assertThat(CodePoint.decode("0x43")).isEqualTo(C); }


    @Test void codePointCount() { assertThat(CodePoint.getCodePointCount("ABC😀")).isEqualTo(4); }
}
