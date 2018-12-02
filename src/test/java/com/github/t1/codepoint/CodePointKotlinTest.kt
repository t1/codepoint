package com.github.t1.codepoint

import com.github.t1.codepoint.CodePoint.Companion.codePointCount
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@Suppress("PrivatePropertyName")
class CodePointKotlinTest {
    private val A = CodePoint.of('A')
    private val C = CodePoint.of('C')

    @Test fun charRangeToCodePointRange() {
        assertThat(('A'..'C').toCodePointRange()).isEqualTo(A..C)
    }

    @Test fun stringCodePointCount() {
        assertThat("ABC😀".codePointCount).isEqualTo(4)
    }
}
