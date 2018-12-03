# CodePoints

Unicode characters require 32 bits, while Java `char`s have only 16 bit,
so a Java `String` uses one or two `char`s for each Unicode character,
where the first `char` indicates that this is not a single `char`.
This is called UTF-16 encoding, and it works fine when working with `String` s,
but when you have to work with individual characters, you'll have to use special APIs,
e.g. to find out how many Unicode characters are in a `String`,
instead of `string.length()`, you'll have to call `string.codePointCount(0, string.length())`.
There are many of these helper methods in `Character` and `String`, but they are not convenient to use.

This project provides a class `CodePoint` that you can use instead of `Character`
to easily work with code points.

# Examples

You can construct a `CodePoint` from a `String` containing only one code point: `CodePoint GRINNING_FACE = CodePoint.of("😀");`.

For convenience, you can also construct single-`char` `CodePoint`s from a `char`: `CodePoint A = CodePoint.of('A');`.

Sometimes you'll want to construct a `CodePoint` from an `int`: `CodePoint ARABIC_1 = CodePoint.of(0x0661);`


You can test some properties on a `CodePoint`, e.g. `ARABIC_1.isDigit()` is `true`.

You can test if a `CodePoint` is within a range: `A.rangeTo(Z).contains(Z)`.

To add a `CodePoint` to a `StringBuilder`, call `GRINNING_FACE.appendTo(stringBuilder)`.

To get the number of `CodePoint`s in a `String`, call `CodePoint.getCodePointCount("ABC😀")` (which is 4),
or to get a `List` of all `CodePoint`s in a `String`, call `CodePoint.allOf("ABC")`.


There is one special `CodePoint` that is actually not a valid Unicode character:
`EOF` is defined as a constant for `End Of File` with the value -1,
which is used, e.g. by a `Reader`, to indicate the end of a file.

# Dependency

```xml
<dependency>
    <groupId>com.github.t1</groupId>
    <artifactId>codepoint</artifactId>
    <classifier>java</classifier>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

# Kotlin

Oh, BTW, the `CodePoint` class is written in [Kotlin](https://kotlinlang.org),
but you shouldn't have to care when you use Java or any other JVM language.
The `codepoint.jar` only contains a few small classes from the Kotlin runtime library,
nicely shaded, so you don't have to worry about any conflicts, even if you do use Kotlin.

Some APIs are more convenient in Kotlin, e.g. to express a range of `CodePoint`s, call `A..Z`
or convert a `CharRange` like this: `('A'..'C').toCodePointRange()`.

Just make sure, you don't use the `java` classifier, when you bring your own Kotlin runtime.
