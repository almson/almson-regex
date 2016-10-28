package net.almson.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import static java.lang.String.format;

/**
 * A utility class for writing readable regular expressions. 
 * Unlike <tt>Hamcrest reg</tt>, it doesn't try to fit regexes into an object-oriented hole. 
 * This library is based on string operations, and is easy to use for all, some, or parts of your regular expressions.
 * 
 * <p> The documentation for the library doesn't replace knowledge of how to write regular expressions.
 * However, this library succeeds in making your regular expressions easy to read by those who are not very familiar
 * with them.
 * 
 * <p> For the best reference on Java regular expressions, see {@link Pattern}.
 * 
 * @author Aleksandr Dubinsky
 */
public class Regex {

    /****************************************************************************************************************
     *                                          Embedded flag expressions                                           *
     ****************************************************************************************************************/
    
    /** Turns on {@link Pattern#MULTILINE}, {@link Pattern#UNICODE_CHARACTER_CLASS}, and {@link Pattern#UNICODE_CASE}.*/
    static public final String CONFIG_STD = "(?mU)";
    
    /** Embedded flag turns on {@link Pattern#UNIX_LINES}, which enables Unix lines mode.
     * <p> In this mode, only the <tt>'\n'</tt> character is recognized as a line terminator.
     * This flag affects the behavior of {@code ANY_CHARACTER}, {@code START_BOUNDARY} (if {@code CONFIG_MULTILINE} is enabled), 
     * {@code END_BOUNDARY} (if {@code CONFIG_MULTILINE} is enabled), 
     * and {@code INPUT_END_BOUNDARY_SANS_TERMINATOR}. */
    static public final String CONFIG_UNIX_LINES = "(?d)";
    
    /** Embedded flag turns on {@link Pattern#MULTILINE}, which enables case-insensitive matching.
     * <p> By default, case-insensitive matching assumes that only characters in the US-ASCII charset are being matched.
     * Unicode-aware case-insensitive matching can be enabled by specifying the {@link #UNICODE_CASE} flag in 
     * conjunction with this flag.
     * <p> Specifying this flag may impose a slight performance penalty. */
    static public final String CONFIG_CASE_INSENSITIVE = "(?i)";
    
    /** Embedded flag turns on {@link Pattern#COMMENTS}, which permits whitespace and comments in pattern.
     * <p> In this mode, whitespace is ignored, and embedded comments starting 
     * with <tt>#</tt> are ignored until the end of a line. */
    static public final String CONFIG_COMMENTS = "(?x)";
    
    /** Embedded flag turns on {@link Pattern#MULTILINE}, which enables multiline mode.
     * <p> In multiline mode the expressions <tt>^</tt> and <tt>$</tt> match just after or just before, respectively, 
     * a line terminator or the end of the input sequence. By default these expressions only match at the beginning 
     * and the end of the entire input sequence. */
    static public final String CONFIG_MULTILINE = "(?m)";
    
    /** Embedded flag turns on {@link Pattern#DOTALL}, which enables dotall mode.
     * <p> In dotall mode, {@link #ANY_CHARACTER} (ie, the expression <tt>.</tt>) matches any character, including a line terminator.
     * By default this character class does not match line terminators.
     * <p> The Unicode characters {@code \\u0085}, {@code \\u2028}, and {@code \\u2029} are considered line terminators 
     * regardless whether {@code CONFIG_UNICODE_CHARACTER_CLASSES} is set. */
    static public final String CONFIG_DOTALL = "(?s)";
    
    /** Embedded flag turns on {@link Pattern#UNICODE_CASE}, which enables Unicode-aware case folding.
     * <p> When this flag is specified then case-insensitive matching, when enabled by the {@link #CASE_INSENSITIVE} 
     * flag, is done in a manner consistent with the Unicode Standard.  By default, case-insensitive
     * matching assumes that only characters in the US-ASCII charset are being matched.
     * <p> Specifying this flag may impose a performance penalty. */
    static public final String CONFIG_UNICODE_CASE = "(?u)";
    
    /** Embedded flag turns on {@link Pattern#UNICODE_CHARACTER_CLASS}, which enables the Unicode version of 
     * <i>Predefined character classes</i> and <i>POSIX character classes</i>.
     * <p> <b>Note:</b> This class does not expose any <i>Predefined</i> or <i>POSIX</i> character classes which may be 
     * affected by this flag. All of the character classes exposed as static fields of this class are not affected
     * by this flag (and are generally Unicode-compliant).
     * Moreover, line terminators (as relevant for ANY_CHARACTER, START_BOUNDARY, and END_BOUNDARY) are always Unicode,
     * as defined by {@link #VERTICAL_WHITESPACE}.
     * <p> When this flag is specified then the (US-ASCII only) <i>Predefined character classes</i> and 
     * <i>POSIX character classes</i> are in conformance with<a href="http://www.unicode.org/reports/tr18/">
     * <i>Unicode Technical Standard #18: Unicode Regular Expression</i></a> <i>Annex C: Compatibility Properties</i>.
     * <p> The following character classes that are understood by the Java regular expression engine are affected by this flag.
     * <table border="0" cellpadding="1" cellspacing="0" summary="predefined and posix character classes in Unicode mode">
     * <tr align="left">
     *   <th align="left" id="predef_classes">Class</th>
     *   <th align="left" id="predef_description">Description</th>
     *   <th align="left" id="predef_matches_non_unicode">Matches (Unicode off)</th>
     *   <th align="left" id="predef_matches_unicode">Matches (Unicode on)</th>
     * </tr>
     * <tr><td><tt>\p{Lower}</tt></td>
     *     <td>A lowercase character</td>
     *     <td><tt>[a-z]</tt></td>
     *     <td><tt>\p{IsLowercase}</tt></td></tr>
     * <tr><td><tt>\p{Upper}</tt></td>
     *     <td>An uppercase character</td>
     *     <td><tt>[A-Z]</tt></td>
     *     <td><tt>\p{IsUppercase}</tt></td></tr>
     * <tr><td><tt>\p{ASCII}</tt></td>
     *     <td>All ASCII</td>
     *     <td><tt>[\x00-\x7F]</tt></td>
     *     <td><tt>[\x00-\x7F]</tt></td></tr>
     * <tr><td><tt>\p{Alpha}</tt></td>
     *     <td>An alphabetic character</td>
     *     <td><tt>[\p{Lower}\p{Upper}]</tt></td>
     *     <td><tt>\p{IsAlphabetic}</tt></td></tr>
     * <tr><td><tt>\p{Digit}</tt></td>
     *     <td>A decimal digit character</td>
     *     <td><tt></tt>[0-9]</td>
     *     <td><tt>p{IsDigit}</tt></td></tr>
     * <tr><td><tt>\p{Alnum}</tt></td>
     *     <td>An alphanumeric character</td>
     *     <td><tt>[\p{Alpha}\p{Digit}]</tt></td>
     *     <td><tt>[\p{IsAlphabetic}\p{IsDigit}]</tt></td></tr>
     * <tr><td><tt>\p{Punct}</tt></td>
     *     <td>A punctuation character</td>
     *     <td>One of <tt>!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~</tt></td>
     *     <td><tt>p{IsPunctuation}</tt></td></tr>
     * <tr><td><tt>\p{Graph}</tt></td>
     *     <td>A visible character</td>
     *     <td><tt>[\p{Alnum}\p{Punct}]</tt></td>
     *     <td><tt>[^\p{IsWhite_Space}\p{gc=Cc}\p{gc=Cs}\p{gc=Cn}]</tt></td></tr>
     * <tr><td><tt>\p{Print}</tt></td>
     *     <td>A printable character</td>
     *     <td><tt>[\p{Graph}\x20]</tt></td>
     *     <td>{@code [\p{Graph}\p{Blank}&&[^\p{Cntrl}]]}</td></tr>
     * <tr><td><tt>\p{Blank}</tt></td>
     *     <td>Horizontal whitespace</td>
     *     <td><tt>[ \t]</tt></td>
     *     <td>{@code [\p{IsWhite_Space}&&[^\p{gc=Zl}\p{gc=Zp}\x0a\x0b\x0c\x0d\x85]]}</td></tr>
     * <tr><td><tt>\p{Cntrl}</tt></td>
     *     <td>A control character</td>
     *     <td><tt>[\x00-\x1F\x7F]</tt></td>
     *     <td><tt>\p{gc=Cc}</tt></td></tr>
     * <tr><td><tt>\p{XDigit}</tt></td>
     *     <td>A hexadecimal digit</td>
     *     <td><tt>[0-9a-fA-F]</tt></td>
     *     <td><tt>[\p{gc=Nd}\p{IsHex_Digit}]</tt></td></tr>
     * <tr><td><tt>\p{Space}</tt></td>
     *     <td>A whitespace character</td>
     *     <td><tt>[ \t\n\x0B\f\r]</tt></td>
     *     <td><tt>\p{IsWhite_Space}</tt></td></tr>
     * <tr><td><tt>\d</tt></td>
     *     <td>A digit</td>
     *     <td><tt>[0-9]</tt></td>
     *     <td><tt>\p{IsDigit}</tt></td></tr>
     * <tr><td><tt>\D</tt></td>
     *     <td>A non-digit</td>
     *     <td><tt>[^0-9]</tt></td>
     *     <td><tt>[^\d]</tt></td></tr>
     * <tr><td><tt>\s</tt></td>
     *     <td>A whitespace character</td>
     *     <td><tt>[ \t\n\x0B\f\r]</tt></td>
     *     <td><tt>\p{IsWhite_Space}</tt></td></tr>
     * <tr><td><tt>\S</tt></td>
     *     <td>A non-whitespace character</td>
     *     <td><tt>[^\s]</tt></td>
     *     <td><tt>[^\s]</tt></td></tr>
     * <tr><td><tt>\w</tt></td>
     *     <td>A word character</td>
     *     <td><tt>[a-zA-Z_0-9]</tt></td>
     *     <td><tt>[\p{Alpha}\p{gc=Mn}\p{gc=Me}\p{gc=Mc}\p{Digit}\p{gc=Pc}\p{IsJoin_Control}]</tt></td></tr>
     * <tr><td><tt>\W</tt></td>
     *     <td>A non-word character</td>
     *     <td><tt>[^\w]</tt></td>
     *     <td><tt>[^\w]</tt></td></tr>
     * </table>
     * <p> The flag implies UNICODE_CASE, that is, it enables Unicode-aware case folding.
     * <p> Specifying this flag may impose a performance penalty. */
    static public final String CONFIG_UNICODE_CHARACTER_CLASSES = "(?U)";
    
    
    /****************************************************************************************************************
     *                                                 Literals                                                     *
     ****************************************************************************************************************/
    
    /** Matches the specified string, automatically escaping any characters that would otherwise be interpreted as metcharacters.
     * @return Result of {@link Pattern#quote Pattern.quote(string)} */
    public static String text(String string) { return Pattern.quote(string); }
    /** Matches the specified Unicode character.
     * @return {@link "\\u" + Integer.toHexString (character)} */
    public static String text(char character) { return "\\x{" + Integer.toHexString (character) + "}"; }
    /** Matches the specified Unicode codepoint.
     * @return {@code "\\x{" + Integer.toHexString (codepoint) + "}"} */
    public static String text(int codepoint) { return "\\x{" + Integer.toHexString (codepoint) + "}"; }
    
    
    /****************************************************************************************************************
     *                                        Predefined character classes                                          *
     ****************************************************************************************************************/
    
    /** Does not match any input sequence. */
    static public final String DO_NOT_MATCH = "$a";
    
    
    /** Any character (may or may not match line terminators). 
     * Is affected by {@link #CONFIG_DOTALL CONFIG_DOTALL} and {@link #CONFIG_UNIX_LINES CONFIG_UNIX_LINES}. */
    static public final String ANY_CHARACTER = ".";
    
    /** Any Arabic numeral. */
    static public final String DIGIT = "[0-9]";
    
    /** Any Unicode letter. */
    static public final String LETTER = "\\p{L}";
    
    /** Any Unicode whitespace character, as well as {@code ZERO WIDTH SPACE}, {@code WORD JOINER}, and {@code ZERO WIDTH NON-BREAKING SPACE}.
     * @see <a href="https://en.wikipedia.org/wiki/Whitespace_character">Wikipedia</a> */
    static public final String WHITESPACE = "[\\p{IsWhite_Space}\\u200B\\u2060\\uFFEF]";
    
    /** Any Unicode linebreak sequence. Is equivalent to:
     * <blockquote><pre>{@code either ("\r\n", VERTICAL_WHITESPACE)}</pre></blockquote>
     * <p>Similar to {@link #VERTICAL_WHITESPACE VERTICAL_WHITESPACE}, but captures Windows linebreaks in a single group.
     * However, {@code NEWLINE} is not a character class and cannot participate in character class operations. */
    static public final String NEWLINE = "\\R";
    
    /** Any Unicode vertical whitespace character. 
     * <p> Equivalent to: <blockquote>{@code charclass ("\n\x0B\f\r\\u0085\\u2028\\u2029")}</blockquote>
     * <p> Similar to {@link #NEWLINE NEWLINE}, but is a character class and can participate in character class 
     * operations like intersection, subtraction, and negation.*/
    static public final String VERTICAL_WHITESPACE = "\\v";
    
    /** Any Unicode horizontal whitespace, as well as {@code ZERO WIDTH SPACE}, {@code WORD JOINER}, and {@code ZERO WIDTH NON-BREAKING SPACE}. */
//    static public final String HORIZONTAL_WHITESPACE = charclassSubtraction (WHITESPACE, VERTICAL_WHITESPACE); // Does not display in javadoc
    static public final String HORIZONTAL_WHITESPACE = "[" + WHITESPACE + "&&[^" + VERTICAL_WHITESPACE + "]]";
    
    
    /** A built-in, named character class.
     * There is no single list of built-in named character classes, 
     * but examples are given and explained throughout {@link Pattern Pattern javadocs}.
     * @return A character class. Literally: {@code "\\p{" + name + "}"}  */
    public static String charclass (String name) { return "\\p{" + name + "}"; }
    
    /** Any character possessing the Unicode binary property. 
     * The supported binary properties by <code>Pattern</code> are
     * <ul>
     *   <li> Alphabetic
     *   <li> Ideographic
     *   <li> Letter
     *   <li> Lowercase
     *   <li> Uppercase
     *   <li> Titlecase
     *   <li> Punctuation
     *   <Li> Control
     *   <li> White_Space
     *   <li> Digit
     *   <li> Hex_Digit
     *   <li> Join_Control
     *   <li> Noncharacter_Code_Point
     *   <li> Assigned
     * </ul>
     * @param name Name of a supported Unicode binary property.
     * @return A character class. Literally: {@code "\\p{Is" + name + "}"} */
    static public final String charclassFromUnicodeProperty (String name) { return Regex.charclass ("Is" + name); }
    
    /** Any character in the Unicode script.
     * <p> The script names supported by <code>Pattern</code> are the valid script names accepted and defined by 
     * {@link java.lang.Character.UnicodeScript#forName(String) UnicodeScript.forName}.
     * @param name Name of a Unicode script as defined in the Unicode standard.
     * @return A character class. Literally: {@code "\\p{Is" + name + "}"} */
    static public final String charclassFromUnicodeScript (String name) { return Regex.charclass ("Is" + name); }
    
    /** Any single character in the Unicode block.
     * <p> The block names supported by <code>Pattern</code> are the valid block names accepted and defined by
     * {@link java.lang.Character.UnicodeBlock#forName(String) UnicodeBlock.forName}.
     * @param name Name of a Unicode block as defined in the Unicode standard.
     * @return A character class. Literally: {@code "\\p{In" + name + "}"} */
    static public final String charclassFromUnicodeBlock (String name) { return Regex.charclass ("In" + name); }
    
    /** Any character in the Unicode category.
     * <p> The supported categories are those of <a href="http://www.unicode.org/unicode/standard/standard.html">
     * <i>The Unicode Standard</i></a> in the version specified by the {@link java.lang.Character Character} class. 
     * The category names are those defined in the Standard, both normative and informative.
     * @param name Name of a Unicode category as defined in the Unicode standard.
     * @return A character class. Literally: {@code "\\p{" + name + "}"} */
    static public final String charclassFromUnicodeCategory (String name) { return Regex.charclass ("In" + name); }
    
    
    /****************************************************************************************************************
     *                                               Character classes                                              *
     ****************************************************************************************************************/
    
    /** Matches any of the specified characters.
     * <p> Example to match 'a' or a digit: <blockquote>{@code charclass (text ("a"), DIGIT)}</blockquote>
     * <p> Example to match any of 'a', '+', or '^': <blockquote>{@code charclass (text ("a"), text ('+'), text ('^'))}</blockquote>
     * <p> Example to match any of 'a', 'b', or 'c': <blockquote>{@code charclass ("a-c")}</blockquote>
     * @param characters characters to match
     * @return A character class. Literally: {@code "[" + String.join ("", elements) + "]"} */
    public static String charclass (char... characters) {
            
            String retval = "[";
            for (char character : characters)
            {
                if (character == '-')
                    retval += "\\-";
                else if (character == '+')
                    retval += "\\+";
                else if (character == '^')
                    retval += "\\^";
                else if (character == '[')
                    retval += "\\[";
                else if (character == ']')
                    retval += "\\]";
                else
                    retval += character;
            }
            return retval + "]"; 
        }
    
    /** Matches any character in the specified range.
     * @return A character class. Literally: {@code "[" + from + "-" + toInclusive + "]"} */
    public static String charclassRange (char from, char toInclusive) { 
            assert from <= toInclusive : "from must be less than or equal to toInclusive"; 
            return format ("[%s-%s]", from, toInclusive); 
        }
    
    /** Matches any characters that are not in the specified character class.
     * Same as {@link #not}.
     * <p> Due to a seeming bug in the JVM when it encounters {@code [^[ ... ]]}, we must use {@code [^\\uFFFF[ ... ]]}
     * which is effecively the same as {@code charclassComplement (charclassUnion (charclass ('\\uFFFF'), ...))}
     * and should not affect the results of the match unless the character {@code \\uFFFF} is being sought.
     * @param characterClass A character class
     * @return A character class. Literally: {@code "[^\\uFFFF" + characterClass + "]"} */
    public static String charclassComplement (String characterClass) {
            
            return format ("[^\\uFFFF%s]", characterClass); }
    
    /** Matches any characters that are not in the specified character class.
     * Same as {@link #charclassComplement}.
     * <p> Due to a seeming bug in the JVM when it encounters {@code [^[ ... ]]}, we must use {@code [^\\uFFFF[ ... ]]}
     * which is effecively the same as {@code charclassComplement (charclassUnion (charclass ('\\uFFFF'), ...))}
     * and should not affect the results of the match unless the character {@code \\uFFFF} is being sought.
     * @param characterClass A character class
     * @return A character class. Literally: {@code "[^0xFFFF" + characterClass + "]"} */
    public static String not (String characterClass) { return charclassComplement (characterClass); }
    
    /** Matches any of the characters that are in either {@code charclass1} and/or {@code charclass2}; ie, creates a character class grouping.
     * <p> Example to match 'a' or a digit: <blockquote>{@code charclassUnion (charclass ('a'), DIGIT)}</blockquote>
     * <p> Example to match any of 'a', '+', or '^': <blockquote>{@code charclassUnion (charclass ('a', '+'), charclass ('^'))}</blockquote>
     * @param charclasses character classes.
     * @return A character class. Literally: {@code "[" + String.join ("", elements) + "]"} */
    public static String charclassUnion (String... charclasses) { return format ("[%s]", String.join ("", charclasses)); }
    
    /** Matches any of the characters that are in both {@code charclass1} and {@code charclass2}.
     * @return A character class. Literally: {@code "[" + class1 + "&&[" + class2 + "]]"} */
    public static String charclassIntersection (String charclass1, String charclass2) { return format ("[%s&&[%s]]", charclass1, charclass2); }
    
    /** Matches any of the characters in {@code classclass1} but not any which are in {@code charclass2}.
     * @return A character class. Literally: {@code "[" + class1 + "&&[^" + class2 + "]]"} */
    public static String charclassSubtraction (String charclass1, String charclass2) { return charclassIntersection (charclass1, not (charclass2)); }
    
    
    /****************************************************************************************************************
     *                                             Boundary matchers                                                *
     ****************************************************************************************************************/
    
    /** The start of a line, if MULTILINE mode is enabled. Otherwise, the start of the input. */
    static public final String START_BOUNDARY = "^";
    /** The end of a line, if MULTILINE mode is enabled. Otherwise, the end of the input. */
    static public final String END_BOUNDARY = "$";
    /** A word boundary. */
    static public final String WORD_BOUNDARY = "\\b";
    /** A non-word boundary. */
    static public final String NON_WORD_BOUNDARY = "\\B";
    /** The beginning of the input. */
    static public final String INPUT_START_BOUNDARY = "\\A";
    /** The end of the previous match. */
    static public final String PREVIOUS_MATCH_BOUNDARY = "\\G";
    /** The end of the input but for the final terminator, if any.
     * Is affected by {@link #CONFIG_UNIX_LINES CONFIG_UNIX_LINES}.
     * <p> A line terminator is any character that matches {@link #VERTICAL_WHITESPACE}. */
    static public final String INPUT_END_BOUNDARY_SANS_TERMINATOR = "\\Z";
    /** The end of the input. */
    static public final String INPUT_END_BOUNDARY = "\\z";
    
    
    /****************************************************************************************************************
     *                                                Quantifiers                                                   *
     ****************************************************************************************************************/
    
    /** Matches once or not at all.
     * @return {@code "(" + "pattern + ")?" } */
    public static String optional (String pattern) { return format("(%s)?", pattern); }
    /** Matches zero or more times.
     * @return {@code "(" + "pattern + ")*" } */
    public static String zeroOrMore(String pattern) { return format( "(%s)*", pattern ); }
    /** Matches one or more times.
     * @return {@code "(" + "pattern + ")+" } */
    public static String oneOrMore(String pattern) { return format( "(%s)+", pattern ); }
    /** Matches exactly <i>n</i> times.
     * @return {@code "(" + "pattern + "){" + times  + "}" } */
    public static String exactly (int times, String pattern) { return format( "(%s){%d}", pattern, times ); }
    /** Matches at least <i>n</i> times.
     * @return {@code "(" + "pattern + "){" + times  + ",}" } */
    public static String atLeast (int times, String pattern) { return format ("(%s){%d,}", pattern, times); }
    /** Matches at least <i>min</i> but not more than <i>maxInclusive</i> times.
     * @return {@code "(" + "pattern + "){" + from + "," + toInclusive  + "}" } */
    public static String between (int min, int maxInclusive, String pattern) { return format( "(%s){%d,%d}", pattern, min, maxInclusive ); }

    /** Modifies the quantifier to be reluctant. A reluctant quantifier will try not to match the input if possible,
     * that is if a different way of matching the input exists. 
     * The input to this method must be one of the quantifiers (optional, zeroOrMore, oneOrMore, atLeast, exactly, between).
     * @return {@code pattern + "?"} */
    public static String reluctantly (String pattern) { return format( "%s?", pattern ); }
    /** Modifies the quantifier to be possessive. A possessive quantifier will try to match the input,
     * and won't backtrack to attempt a different way of matching the input. 
     * The input to this method must be one of the quantifiers (optional, zeroOrMore, oneOrMore, atLeast, exactly, between).
     * @return {@code pattern + "+"} */
    public static String possessively (String pattern) { return format( "%s+", pattern ); }
    
    
    public static String optionalCombine (String a, String b) { return either( a + b, a, b ); }
    
    
    /****************************************************************************************************************
     *                                              Logical operators                                               *
     ****************************************************************************************************************/
    
    /** Matches several regular expressions in order.
     * Since this basic operation is implemented as simple string concatenation, 
     * most users do not use this method in favor of concatenation.
     * @return {@code String.join ("", regexes)} */
    public static String sequence (String... regexes) { return String.join ("", regexes); }
    
    /** Matches any one of several regular expressions.
     * @return {@code "(" + String.join ("|", regexes) + ")"} */
    public static String either (String... regexes) { return format ("(%s)", String.join ("|", regexes)); }
    
    /** A group acts like parentheses in a regex expression, affecting evaluation order.
     * In addition, groups "capture" the matched text and make it available via {@link Matcher#group(int)}.
     * However, tracking indeces can be problematic, and if capturing is desired, 
     * it is recommended to use {@link #namedGroup named groups} instead.
     * @return {@code "(" + regex + ")"} 
     * @see Pattern Pattern section <i>Group and capturing</i> */
    public static String group (String regex) { return format ("(%s)", regex); }
    
    
    /****************************************************************************************************************
     *                                              Back references                                                 *
     ****************************************************************************************************************/
    
//    public static String backreference (int i) { return "\\" + i; }
//    
//    public static String backreference (String name) { return "(\\k" + name+ ")"; }
    
    
    /****************************************************************************************************************
     *                                           Replacement utilities                                              *
     ****************************************************************************************************************/
    
    /** A literal replacement, used with methods {@code String.replaceAll}, {@code Matcher.replaceAll}, 
     * {@code Matcher#appendReplacement}, and {@code Matcher.replaceFirst}.
     * @return {@code Matcher.quoteReplacement (text)}
     * @see Matcher#appendReplacement */
    public static String replacementText (String text) { return Matcher.quoteReplacement (text); }
    
    /** A reference to a captured group, used with methods {@code String.replaceAll}, {@code Matcher.replaceAll}, 
     * {@code Matcher#appendReplacement}, and {@code Matcher.replaceFirst}.
     * @return {@code "$" + i}
     * @see Matcher#appendReplacement */
    public static String replacementBackreference (int i) { return "$" + i; }
    
    /** A reference to a named captured group, used with methods {@code String.replaceAll}, {@code Matcher.replaceAll}, 
     * {@code Matcher#appendReplacement}, and {@code Matcher.replaceFirst}.
     * @return {@code "${" + name + "}"}
     * @see Matcher#appendReplacement */
    public static String replacementBackreference (String name) { return "${" + name + "}"; }
    
    
    /****************************************************************************************************************
     *                           Special constructs (named-capturing and non-capturing)                             *
     ****************************************************************************************************************/
    
    /** A named group acts like parentheses in a regex expression, while also "capturing" the matched text
     * and making it available via {@link Matcher#group(java.lang.String)}.
     * @return {@code "(?<" + name + ">" + regex + ")"}
     * @see Pattern Pattern section <i>Groups and capturing</i> */
    public static String namedGroup (String name, String regex) { return format ("(?<%s>%s)", name, regex); }
    
    /** A non-capturing group acts like parentheses in a regex expression, 
     * while not capturing text and not counting towards the group count.
     * @return {@code "(?:" + regex + ")"} */
    public static String noncapturingGroup (String regex) { return format ("(?:%s)", regex); }
    
    /** Zero-width positive lookahead confirms that a pattern appears in the input sequence 
     * without consuming it or including it as part of the match.
     * @return {@code "(?=" + regex + ")"} */
    public static String followedBy (String regex) { return format( "(?=%s)", regex ); }
    
    /** Zero-width negative lookahead confirms that a pattern does not appear in the input sequence.
     * @return {@code "(?!" + regex + ")"} */
    public static String notFollowedBy (String regex) { return format( "(?!%s)", regex ); }
    
    /** Zero-width positive lookbehind confirms that a pattern appears in the input sequence
     * without consuming it or including it as part of the match.
     * @return {@code "(?<=" + regex + ")"} */
    public static String precededBy (String regex) { return format( "(?<=%s)", regex ); }
    
    /** Zero-width negative lookbehind confirms that a pattern does not appear in the input sequence.
     * @return {@code "(?<!" + regex + ")"} */
    public static String notPrecededBy (String regex) { return format( "(?<!%s)", regex ); }
    
    
    /****************************************************************************************************************
     *                                               Utility methods                                                *
     ****************************************************************************************************************/
    
    /** Convert any named capturing groups into plain capturing groups. This method is useful when including
     * a regular expression that contains named capturing groups in another regular expression where the possibility
     * of name conflicts exists.
     * @return {@code regex.replaceAll ( precededBy(text("(")) + text ("?<") + oneOrMore (charclass (LETTER, DIGIT)) + text (">") + followedBy(text(")")), "") } */
    public static String stripNamedGroups(String regex) {
        
            return regex.replaceAll (precededBy(text("(")) + text ("?<") + oneOrMore (charclassUnion (LETTER, DIGIT)) + text (">")
                                    , ""); }
    
}
