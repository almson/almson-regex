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
by those who do not have expert knowledge. For the best reference on Java regular expressions, see {@link Pattern}.

# Examples

### Match leading or trailiing whitespace:

	"^[ \t]+|[ \t]+$"

becomes:

	either (sequence (START_BOUNDARY, oneOrMore (HORIZONTAL_WHITESPACE)), 
			sequence (oneOrMore (HORIZONTAL_WHITESPACE), END_BOUNDARY));

but since `sequence` simply does string concatenation, we can also write:

	either (START_BOUNDARY + oneOrMore (HORIZONTAL_WHITESPACE), 
			oneOrMore (HORIZONTAL_WHITESPACE) + END_BOUNDARY);

### Match an IP address (simple version):

	"\\b(\\d{1,3}\\.){3}\\d{1,3}\\b"

becomes:

	WORD_BOUNDARY + exactly(3, between(1, 3, DIGIT) + text(".")) + between (1, 3, DIGIT) + WORD_BOUNDARY

### Match an email address (simple version):

	"\\b(<user>[a-zA-Z0-9._%+-]+)@(?<domain>[A-Z0-9.-]+\.\\p{L}{2,})\\b"

becomes:

	WORD_BOUNARY 
	+ namedGroup ("user", 
			oneOrMore (charclassUnion (LETTER, DIGIT, charclass ('.', '_', '%', '+', '-'))))
	+ text ("@")
	+ namedGroup ("domain"
			, oneOrMore (charclassUnion (LETTER, DIGIT, charclass ('.', '-')))
			+ text (".")
			+ atLeast (2, LETTER)
	+ WORD_BOUNDARY

### Select consecutive duplicates from a comma-delimited list

    (?<=,|^)([^,]*)(,\1)+(?=,|$)

becomes:

    precededBy (either (text (","), text ("^")))
    + group (zeroOrMore (charclassComplement (charclass (','))))
    + oneOrMore (text (",") + backreference (1))
    + followedBy (either (text (","), text ("$")))
