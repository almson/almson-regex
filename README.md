# Introduction

`almson-regex` is a simple library for writing readable regular expressions.

The goals of this library are:

- Good documentation
- Descriptive syntax
- Support of all Java 8 regex features

The following are *not* goals:

- Type safety
- Brevity (normal regular expressions are very compact--that's why they're hard to read)
- Allow user to avoid learning regular expressions (sorry, you're still writing and reading regexes)

`almson-regex` is based on string operations, and is easy to use for all, some, or parts of your regular expressions. An effort is being made to write a compiler plugin based on the Checker Framework to add type-safety (for things like character classes, literals, and the possessive/reluctant qualifiers) without using a class heirarchy.

`almson-regex` is compiled against Java 5, although it supports all Java 8 regex features, such as named capturing groups.

The documentation for the library doesn't replace knowledge of how to write regular expressions.
However, this library succeeds in making your regular expressions easy to read
by those who do not have expert knowledge. For the best reference on Java regular expressions, see [the java.util.regex.Pattern documentation](https://docs.oracle.com/javase/10/docs/api/java/util/regex/Pattern.html).

Please post your feedback about any unclear documentation or missing functionality.

# Examples

### Match leading or trailiing whitespace:

    String pattern = "^[ \t]+|[ \t]+$";

becomes:

    String pattern = either (sequence (START_BOUNDARY, oneOrMore (HORIZONTAL_WHITESPACE)),
            sequence (oneOrMore (HORIZONTAL_WHITESPACE), END_BOUNDARY));

but since `sequence` simply does string concatenation, we can also write:

    String pattern = either (START_BOUNDARY + oneOrMore (HORIZONTAL_WHITESPACE),
            oneOrMore (HORIZONTAL_WHITESPACE) + END_BOUNDARY);

### Match an IP address (simple version):

    String ipAddressPattern = "\\b(\\d{1,3}\\.){3}\\d{1,3}\\b";

becomes:

    String ipAddressPattern = WORD_BOUNDARY + exactly(3, between(1, 3, DIGIT) + text(".")) + between (1, 3, DIGIT) + WORD_BOUNDARY

### Match an email address (simple version):

    String emailAddressPattern = "\\b(<user>[a-zA-Z0-9._%+-]+)@(?<domain>[A-Z0-9.-]+\.\\p{L}{2,})\\b";

becomes:

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

and is demonstrated by:

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

### Select consecutive duplicates from a comma-delimited list

    String duplicatedItemPattern = (?<=,|^)([^,]*)(,\1)+(?=,|$)

becomes:

    String duplicatedItemPattern
            = precededBy (either (START_BOUNDARY, text (",")))
                + group (zeroOrMore (charclassComplement (charclass (','))))
                + oneOrMore (text (",") + backreference (1))
                + followedBy (either (text (","), END_BOUNDARY));

and is demonstrated by:

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
