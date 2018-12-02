package com.github.t1.codepoint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CodePointKotlinTest {
    @Test fun charRangeToCodePointRange() {
        assertThat(('A'..'C').toCodePointRange()).isEqualTo(CodePoint.of('A')..CodePoint.of('C'))
    }
}
