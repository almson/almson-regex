# Introduction

`almson-regex` is a simple library for writing *readable* regular expressions.

The goals of this library are:

- Descriptive syntax
- Good documentation
- Support of all Java regex features

The following are *not* goals:

- Type safety (keep it simple, use Strings!)
- Extreme brevity (if you want super-compact, illegible regular expressions, write them the old way!)
- Allow user to avoid learning regular expressions (fundamentally, you're still writing and reading regexes, but the descriptive names and good documentation makes it much easier!)

`almson-regex` is based on string operations, and is easy to use for all, some, or parts of your regular expressions. In the future, we'd like to use the Checker Framework to add type-safety (for things like character classes, literals, and the possessive/reluctant qualifiers) without using a class heirarchy.

`almson-regex` can be compiled with Java 8, although it supports all Java 17 regex features, such as named capturing groups and glyph cluster matchers.

The documentation for the library doesn't replace knowledge of how regular expressions work. 
However, this library succeeds in making your regular expressions easy to read
by those who do not have expert knowledge. For the best reference on Java regular expressions, see [the java.util.regex.Pattern documentation](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html).

Please post your feedback about any unclear documentation or missing functionality.

# Examples

### Match leading or trailiing whitespace:

```java
String pattern = "^[ \t]+|[ \t]+$";
```

becomes:

```java
String pattern = either (sequence (START_BOUNDARY, oneOrMore (HORIZONTAL_WHITESPACE)),
        sequence (oneOrMore (HORIZONTAL_WHITESPACE), END_BOUNDARY));
```

but since `sequence` simply does string concatenation, we can also write:

```java
String pattern = either (START_BOUNDARY + oneOrMore (HORIZONTAL_WHITESPACE),
        oneOrMore (HORIZONTAL_WHITESPACE) + END_BOUNDARY);
```

### Match an IP address (simple version):

```java
String ipAddressPattern = "\\b(\\d{1,3}\\.){3}\\d{1,3}\\b";
```

becomes:

```java
String ipAddressPattern = WORD_BOUNDARY + exactly(3, between(1, 3, DIGIT) + text(".")) + between (1, 3, DIGIT) + WORD_BOUNDARY
```

### Match an email address (simple version):

```java
String emailAddressPattern = "\\b(<user>[a-zA-Z0-9._%+-]+)@(?<domain>[A-Z0-9.-]+\.\\p{L}{2,})\\b";
```

becomes:

```java
String emailAddressPattern
        = WORD_BOUNDARY
            + namedGroup ("user"
                    , oneOrMore (charclassUnion (LETTER, DIGIT, charclass ('.', '_', '%', '+', '-'))))
            + text ("@")
            + namedGroup ("domain"
                    , oneOrMore (charclassUnion (LETTER, DIGIT, charclass ('.', '-')))
                    + text (".")
                    + atLeast (2, LETTER))
            + WORD_BOUNDARY;
```

and is demonstrated by:

```java
  @Test public void
exampleEmailAddress() {

        Pattern pattern = Pattern.compile (emailAddressPattern);

        Matcher matcher = pattern.matcher ("An email address\njohn@acme.com");

        assertTrue (matcher.find());

        assertEquals ("john@acme.com", matcher.group());
        assertEquals ("john", matcher.group("user"));
        assertEquals ("acme.com", matcher.group("domain"));

        assertFalse (matcher.find());
    }
```

### Select consecutive duplicates from a comma-delimited list

```java
String duplicatedItemPattern = (?<=,|^)([^,]*)(,\1)+(?=,|$)
```

becomes:

```java
String duplicatedItemPattern
        = precededBy (either (START_BOUNDARY, text (",")))
            + group (zeroOrMore (charclassComplement (charclass (','))))
            + oneOrMore (text (",") + backreference (1))
            + followedBy (either (text (","), END_BOUNDARY));
```

and is demonstrated by:

```java
  @Test public void
exampleDuplicates() {

        Pattern pattern = Pattern.compile (duplicatedItemPattern);

        Matcher matcher = pattern.matcher ("dog,cat,cat,tree,apple,tree,tree,tree");

        assertTrue (matcher.find());

        assertEquals ("cat", matcher.group(1));

        assertTrue (matcher.find());

        assertEquals ("tree", matcher.group(1));

        assertFalse (matcher.find());
    }
```
