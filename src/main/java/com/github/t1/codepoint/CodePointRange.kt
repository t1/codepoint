package com.github.t1.codepoint

data class CodePointRange(
    override val start: CodePoint,
    override val endInclusive: CodePoint
) : ClosedRange<CodePoint>

fun CharRange.toCodePointRange() = CodePoint.of(this.start)..CodePoint.of(this.endInclusive)
