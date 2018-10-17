package net.almson.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Objects;

/**
 * This class is a simple library for writing readable regular expressions. 
 * 
 * <p> The goals of this library are:
 *   <ul>
 *     <li> Good documentation </li>
 *     <li> Descriptive syntax </li>
 *     <li> Support of all Java 8 regex features </li>
 *   </ul>
 * 
 * <p> The following are *not* goals:
 *   <ul>
 *     <li> Type safety </li>
 *     <li> Brevity (normal regular expressions are very compact--that's why they're hard to read) </li>
 *     <li> Allow user to avoid learning regular expressions (sorry, you're still writing and reading regexes) </li>
 *   </ul>
 * 
 * <p> This library is based on string operations, 
 * and is easy to use for all, some, or parts of your regular expressions. 
 * An effort is being made to write a compiler plugin based on the Checker Framework to add type-safety 
 * (for things like character classes, literals, and the possessive/reluctant qualifiers) 
 * without using a class hierarchy.
 * 
 * <p> This library is compiled against Java 5, although it supports all Java 8 regex features, 
 * such as named capturing groups.
 * 
 * <p> The documentation for the library doesn't replace knowledge of how to write regular expressions.
 * However, this library succeeds in making your regular expressions easy to read by those who are not very familiar
 * with them. For the best reference on Java regular expressions, see {@link Pattern}.
 * 
 * @author Aleksandr Dubinsky
 */
public final class Regex {
    
    private Regex() { throw new AssertionError (); }

    /****************************************************************************************************************
     *                                          Embedded flag expressions                                           *
     ****************************************************************************************************************/
    
    /** Turns on {@link Pattern#MULTILINE}, {@link Pattern#UNICODE_CHARACTER_CLASS}, and {@link Pattern#UNICODE_CASE}.*/
    static public final String CONFIG_STD = "(?mU)";
    
    /** Embedded flag turns on {@link Pattern#UNIX_LINES}, which enables Unix lines mode.
     * <p> In this mode, only the <tt>'\n'</tt> character is recognized as a line terminator.
     * This flag affects the behavior of {@code ANY_CHARACTER}, 
     * {@code START_BOUNDARY} (if {@code CONFIG_MULTILINE} is enabled), 
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
     * <p> In dotall mode, {@link #ANY_CHARACTER} (ie, the expression <tt>.</tt>) matches any character, 
     * including a line terminator.
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
     * <p> The following character classes that are understood by the Java regular expression engine are affected 
     * by this flag.
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
    
    /** Matches the specified string, automatically escaping any characters that would otherwise be interpreted 
     * as metacharacters.
     * @return Result of {@link Pattern#quote Pattern.quote(string)} */
    @Expr public static String text (@Literal String string) { return Pattern.quote(string); }
    
    /** Matches the specified Unicode codepoint.
     * @return {@code "\\x{" + Integer.toHexString (codepoint) + "}"} */
    @Expr public static String text (int codepoint) { return "\\x{" + Integer.toHexString (codepoint) + "}"; }
    
    
    /****************************************************************************************************************
     *                                        Predefined character classes                                          *
     ****************************************************************************************************************/
    
    /** Does not match any input sequence. */
    @Expr static public final String DO_NOT_MATCH = "$a";
    
    
    /** Character class matching any character (may or may not match line terminators). 
     * Is affected by {@link #CONFIG_DOTALL CONFIG_DOTALL} and {@link #CONFIG_UNIX_LINES CONFIG_UNIX_LINES}. */
    @Charclass static public final String ANY_CHARACTER = ".";
    
    /** Character class matching any Arabic numeral. */
    @Charclass static public final String DIGIT = "[0-9]";
    
    /** Character class matching any Unicode letter. */
    @Charclass static public final String LETTER = "\\p{L}";
    
    /** Character class matching any Unicode punctuation. */
    @Charclass static public final String PUNCTUATION = "\\p{IsPunctuation}";
    
    /** 
     * Character class matching any Unicode whitespace character, 
     * as well as {@code ZERO WIDTH SPACE}, {@code WORD JOINER}, and {@code ZERO WIDTH NON-BREAKING SPACE}.
     * @see <a href="https://en.wikipedia.org/wiki/Whitespace_character">Wikipedia</a> 
     */
    @Charclass static public final String WHITESPACE = "[\\p{IsWhite_Space}\\u200B\\u2060\\uFFEF]";
    
    /** 
     * Any Unicode linebreak sequence. Is equivalent to:
     * <blockquote><pre>{@code either ("\r\n", VERTICAL_WHITESPACE)}</pre></blockquote>
     * <p>Similar to {@link #VERTICAL_WHITESPACE VERTICAL_WHITESPACE}, 
     * but captures Windows line breaks in a single group.
     * However, {@code NEWLINE} is not a character class and cannot participate in character class operations.
     */
    @Expr static public final String NEWLINE = "\\R";
    
    /** 
     * Character class matching any Unicode vertical whitespace character. 
     * <p> Equivalent to: <blockquote>{@code charclass ("\n\x0B\f\r\\u0085\\u2028\\u2029")}</blockquote>
     * <p> Similar to {@link #NEWLINE NEWLINE}, but is a character class and can participate in character class 
     * operations like intersection, subtraction, and negation.
     */
    @Charclass static public final String VERTICAL_WHITESPACE = "\\v";
    
    /** 
     * Character class matching any Unicode horizontal whitespace, 
     * as well as {@code ZERO WIDTH SPACE}, {@code WORD JOINER}, and {@code ZERO WIDTH NON-BREAKING SPACE}. 
     */
    @Charclass static public final String HORIZONTAL_WHITESPACE = "[" + WHITESPACE + "&&[^" + VERTICAL_WHITESPACE + "]]";
    
    
    /** A built-in, named character class.
     * There is no single list of built-in named character classes, 
     * but examples are given and explained throughout {@link Pattern Pattern javadocs}.
     * @return A character class. Literally: {@code "\\p{" + name + "}"}  */
    @Charclass public static String charclassFromName (@Literal String name) { return "\\p{" + name + "}"; }
    
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
    @Charclass static public final String charclassFromUnicodeProperty (@Literal String name) { 
            return Regex.charclassFromName ("Is" + name); }
    
    /** Any character in the Unicode script.
     * <p> The script names supported by <code>Pattern</code> are the valid script names accepted and defined by 
     * {@link java.lang.Character.UnicodeScript#forName(String) UnicodeScript.forName}.
     * @param name Name of a Unicode script as defined in the Unicode standard.
     * @return A character class. Literally: {@code "\\p{Is" + name + "}"} */
    @Charclass static public final String charclassFromUnicodeScript (@Literal String name) { 
            return Regex.charclassFromName ("Is" + name); }
    
    /** Any single character in the Unicode block.
     * <p> The block names supported by <code>Pattern</code> are the valid block names accepted and defined by
     * {@link java.lang.Character.UnicodeBlock#forName(String) UnicodeBlock.forName}.
     * @param name Name of a Unicode block as defined in the Unicode standard.
     * @return A character class. Literally: {@code "\\p{In" + name + "}"} */
    @Charclass static public final String charclassFromUnicodeBlock (@Literal String name) { 
            return Regex.charclassFromName ("In" + name); }
    
    /** Any character in the Unicode category.
     * <p> The supported categories are those of <a href="http://www.unicode.org/unicode/standard/standard.html">
     * <i>The Unicode Standard</i></a> in the version specified by the {@link java.lang.Character Character} class. 
     * The category names are those defined in the Standard, both normative and informative.
     * @param name Name of a Unicode category as defined in the Unicode standard.
     * @return A character class. Literally: {@code "\\p{" + name + "}"} */
    @Charclass static public final String charclassFromUnicodeCategory (@Literal String name) { 
            return Regex.charclassFromName ("In" + name); }
    
    
    /****************************************************************************************************************
     *                                               Character classes                                              *
     ****************************************************************************************************************/
    
      /** 
       * Matches any of the specified characters,
       * automatically escaping any characters that would otherwise be interpreted as metacharacters.
       * 
       * <p> Example to match any of {@code '\\', '+', '\n', '\0',} or {@code '^'}: 
       *      <blockquote>{@code charclass ('\\', '+', '\n', '\0', '^')}</blockquote>
       * 
       * @param characters characters to match
       * @return A character class. Literally: {@code "[" + String.join ("", escape (elements)) + "]"} 
       */
      @Charclass public static String 
    charclass (char... characters) {
        
            Objects.requireNonNull (characters);
            
            StringBuilder sb = new StringBuilder ();
            
            sb.append ("[");
            for (char character: characters)
            {
                sb.append (Regex.escapeCharclassMetacharacter (character));
            }
            sb.append ("]");
            
            return sb.toString();
        }
    
      /** 
       * Matches any character in the specified range.
       * 
       * @return A character class. Literally: {@code "[" + from + "-" + toInclusive + "]"} 
       */
      @Charclass public static String 
    charclassRange (char from, char toInclusive) {
        
            assert from <= toInclusive : "from must be less than or equal to toInclusive"; 
            return "[" + escapeCharclassMetacharacter (from) + "-" + escapeCharclassMetacharacter (toInclusive) + "]";
        }
    
      /** 
       * Matches any characters that are not in the specified character class. 
       * Same as {@link #not}.
       * 
       * <p> Unlike the normal behavior of the {@code ^} metacharacter, 
       * this method takes care to take the complement of the entire input character class, 
       * distributing it across unions and intersections. Because there is no built-in operator that does this,
       * this method uses De Morgan's laws to transform the regular expression.
       * 
       * <p> <b>Warning:</b> This method is experimental, because the transformation logic may contain bugs.
       * 
       * @param characterClass A character class
       * @return A character class. 
       */
      @Charclass public static String 
    charclassComplement (@Charclass String characterClass) {
        
            if (! characterClass.startsWith ("["))
                characterClass = "[" + characterClass + "]";
        
            // Negate non-empty character classes
            String searchPattern = notPrecededBy (text ("\\")) + text("[") + notFollowedBy (text("["));
            characterClass = characterClass.replaceAll (searchPattern, replacementText ("[^"));
            
            // Remove double-negation
            searchPattern = notPrecededBy (text ("\\")) + text("[^^");
            characterClass = characterClass.replaceAll (searchPattern, replacementText ("["));
            
            // Transform intersection to union
            searchPattern = notPrecededBy (text ("\\")) + text("&&[");
            characterClass = characterClass.replaceAll (searchPattern, "placeholderb69351cd84c888ade1ae");
            
            // Transform union to intersection
            searchPattern = notPrecededBy (either (START_BOUNDARY, text ("["), text ("\\"))) + text("[");
            characterClass = characterClass.replaceAll (searchPattern, "&&[");
            
            // Finish transforming intersection to union
            characterClass = characterClass.replace ("placeholderb69351cd84c888ade1ae", "[");
        
            return characterClass;
        }
    
      /** 
       * Matches any characters that are not in the specified character class.
       * Same as {@link #charclassComplement}.
       * 
       * <p> Unlike the normal behavior of the {@code ^} metacharacter, 
       * this method takes care to take the complement of the entire input character class, 
       * distributing it across unions and intersections. Because there is no built-in operator that does this,
       * this method uses De Morgan's laws to transform the regular expression.
       * 
       * <p> <b>Warning:</b> This method is experimental, because the transformation logic may contain bugs.
       * 
       * @param characterClass A character class
       * @return A character class. 
       */
      @Charclass public static String 
    not (@Charclass String characterClass) { return charclassComplement (characterClass); }
    
      /** 
       * Matches any of the characters that are in either of {@code charclass1} or {@code charclass2}; 
       * ie, creates a character class grouping.
       * 
       * <p> Example to match 'a' or a digit: <blockquote>{@code charclassUnion (charclass ('a'), DIGIT)}</blockquote>
       * 
       * <p> Example to match any of 'a', '+', or '^': 
       *     <blockquote>{@code charclassUnion (charclass ('a', '+'), charclass ('^'))}</blockquote>
       * 
       * @param charclasses character classes.
       * @return A character class. Literally: {@code "[" + String.join ("", elements) + "]"} 
       */
      @Charclass public static String 
    charclassUnion (@Charclass String... charclasses) { 
        
            return "[" + join (charclasses) + "]";
        }
    
      /** 
       * Matches any of the characters that are in both {@code charclass1} and {@code charclass2}.
       * 
       * @return A character class. Literally: {@code "[" + class1 + "&&[" + class2 + "]]"} 
       */
      @Charclass public static String 
    charclassIntersection (@Charclass String charclass1, @Charclass String charclass2) { 
        
            return "[" + charclass1 + "&&[" + charclass2 + "]]"; 
        }
    
      /** 
       * Matches any of the characters in {@code classclass1} but not any which are in {@code charclass2}.
       * 
       * @return A character class. Literally: {@code "[" + class1 + "&&[^" + class2 + "]]"} 
       */
      @Charclass public static String 
    charclassSubtraction (@Charclass String charclass1, @Charclass String charclass2) { 
        
            return charclassIntersection (charclass1, not (charclass2)); 
        }
    
    
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
    public static @QuantifierExpr String optional (@Expr String pattern) { return noncapturingGroup (pattern) + "?"; }
    
    /** Matches zero or more times.
     * @return {@code "(" + "pattern + ")*" } */
    public static @QuantifierExpr String zeroOrMore(@Expr String pattern) { return noncapturingGroup (pattern) + "*"; }
    
    /** Matches one or more times.
     * @return {@code "(" + "pattern + ")+" } */
    public static @QuantifierExpr String oneOrMore(@Expr String pattern) { return noncapturingGroup (pattern) + "+"; }
    
    /** Matches exactly <i>n</i> times.
     * @return {@code "(" + "pattern + "){" + times  + "}" } */
    public static @QuantifierExpr String exactly (int times, @Expr String pattern) { 
            return noncapturingGroup (pattern) + "{" + times + "}"; }
    
    /** Matches at least <i>n</i> times.
     * @return {@code "(" + "pattern + "){" + times  + ",}" } */
    public static @QuantifierExpr String atLeast (int times, @Expr String pattern) { 
            return noncapturingGroup (pattern) + "{" + times + ",}"; }
    
    /** Matches at least <i>min</i> but not more than <i>maxInclusive</i> times.
     * @return {@code "(" + "pattern + "){" + from + "," + toInclusive  + "}" } */
    public static @QuantifierExpr String between (int min, int maxInclusive, @Expr String pattern) { 
            return noncapturingGroup (pattern) + "{" + min + "," + maxInclusive + "}"; }

    /** Modifies the quantifier to be reluctant. A reluctant quantifier will not try to match the input 
     * if a different way of matching the input exists. 
     * The input to this method must be one of the quantifiers 
     * (optional, zeroOrMore, oneOrMore, atLeast, exactly, between).
     * @return {@code pattern + "?"} */
    public static @Expr String reluctantly (@QuantifierExpr String pattern) { return pattern + "?"; }
    
    /** Modifies the quantifier to be possessive. A possessive quantifier will try to match the input,
     * and won't backtrack to attempt a different way of matching the input. 
     * The input to this method must be one of the quantifiers 
     * (optional, zeroOrMore, oneOrMore, atLeast, exactly, between).
     * @return {@code pattern + "+"} */
    public static @Expr String possessively (@QuantifierExpr String pattern) { return pattern + "+"; }
    
    
    /****************************************************************************************************************
     *                                              Logical operators                                               *
     ****************************************************************************************************************/
    
    /** Matches several regular expressions in order.
     * Since this basic operation is implemented as simple string concatenation, 
     * most users do not use this method.
     * @return {@code String.join ("", regexes)} */
    public static @Expr String sequence (@Expr String... regexes) { return join (regexes); }
    
    /** Matches any one of several regular expressions.
     * @return {@code String.join ("|", regexes)} */
    public static @Expr String either (@Expr String... regexes) { return noncapturingGroup (join (regexes, '|')); }
    
    /**
     * Matches any or both of two regular expressions.
     * @return {@code either (sequence (a, b), a, b)}
     */
    public static @Expr String eitherOr (@Expr String a, @Expr String b) { return either (sequence (a, b), a, b); }
    
    
    /****************************************************************************************************************
     *                                              Back references                                                 *
     ****************************************************************************************************************/
    
    public static @Expr String backreference (int i) { return "\\" + i; }
//    
//    public static String backreference (String name) { return "(\\k" + name+ ")"; }
    
    
    /****************************************************************************************************************
     *                                           Replacement utilities                                              *
     ****************************************************************************************************************/
    
    /** A literal replacement, used with methods {@code String.replaceAll}, {@code Matcher.replaceAll}, 
     * {@code Matcher#appendReplacement}, and {@code Matcher.replaceFirst}.
     * @return {@code Matcher.quoteReplacement (text)}
     * @see Matcher#appendReplacement */
    public static @ReplacementExpr String replacementText (@Literal String text) { 
            return Matcher.quoteReplacement (text); }
    
    /** A reference to a captured group, used with methods {@code String.replaceAll}, {@code Matcher.replaceAll}, 
     * {@code Matcher#appendReplacement}, and {@code Matcher.replaceFirst}.
     * @return {@code "$" + i}
     * @see Matcher#appendReplacement */
    public static @ReplacementExpr String replacementBackreference (int i) { return "$" + i; }
    
    /** A reference to a named captured group, used with methods {@code String.replaceAll}, {@code Matcher.replaceAll}, 
     * {@code Matcher#appendReplacement}, and {@code Matcher.replaceFirst}.
     * @return {@code "${" + name + "}"}
     * @see Matcher#appendReplacement */
    public static @ReplacementExpr String replacementBackreference (@Literal String name) { return "${" + name + "}"; }
    
    
    /****************************************************************************************************************
     *                                                   Groups                                                     *
     ****************************************************************************************************************/
    
      /** A group acts like parentheses in a regex expression, affecting evaluation order.
       * In addition, groups "capture" the matched text and make it available via {@link Matcher#group(int)}.
       * However, tracking indeces can be problematic, and if capturing is desired, 
       * it is recommended to use {@link #namedGroup named groups} instead.
       * @return {@code "(" + regex + ")"} 
       * @see Pattern Pattern section <i>Group and capturing</i> */
      public static @Expr String 
    group (@Expr String regex) { return "(" + regex + ")"; }
    
      /** 
       * A named group acts like parentheses in a regex expression, while also "capturing" the matched text
       * and making it available via {@link Matcher#group(java.lang.String)}.
       * 
       * <p> Requires Java 7.
       * 
       * @return {@code "(?<" + name + ">" + regex + ")"}
       * @see Pattern Pattern section <i>Groups and capturing</i> 
       */
      public static @Expr String 
    namedGroup (@Literal String name, @Expr String regex) { return "(?<" + name + ">" + regex + ")"; }
    
      /** 
       * A non-capturing group acts like parentheses in a regex expression, 
       * while not capturing text and not counting towards the group count.
       * 
       * @return {@code "(?:" + regex + ")"} 
       */
      public static @Expr String 
    noncapturingGroup (@Expr String regex) { return "(?:" + regex + ")"; }
    
    
    /****************************************************************************************************************
     *                                         Look-ahead and look-behind                                           *
     ****************************************************************************************************************/
    
    /** Zero-width positive lookahead confirms that a pattern appears in the input sequence 
     * without consuming it or including it as part of the match.
     * @return {@code "(?=" + regex + ")"} */
    public static @Expr String followedBy (@Expr String regex) { return "(?=" + regex + ")"; }
    
    /** Zero-width negative lookahead confirms that a pattern does not appear in the input sequence.
     * @return {@code "(?!" + regex + ")"} */
    public static @Expr String notFollowedBy (@Expr String regex) { return "(?!" + regex + ")"; }
    
    /** Zero-width positive lookbehind confirms that a pattern appears in the input sequence
     * without consuming it or including it as part of the match.
     * @return {@code "(?<=" + regex + ")"} */
    public static @Expr String precededBy (@Expr String regex) { return "(?<=" + regex + ")"; }
    
    /** Zero-width negative lookbehind confirms that a pattern does not appear in the input sequence.
     * @return {@code "(?<!" + regex + ")"} */
    public static @Expr String notPrecededBy (@Expr String regex) { return "(?<!" + regex + ")"; }
    
    
    /****************************************************************************************************************
     *                                               Utility methods                                                *
     ****************************************************************************************************************/
    
      /** 
       * Convert any named capturing groups into plain capturing groups. 
       * This method is useful when including a regular expression that contains named capturing groups 
       * in another regular expression where the possibility of name conflicts exists.
       * @return {@code regex.replaceAll ( precededBy(text("(")) + text ("?<") + oneOrMore (charclass (LETTER, DIGIT)) 
       *                + text (">") + followedBy(text(")")), "") } 
       */
      public static @Expr String 
    stripNamedGroups (@Expr String regex) {
        
            return regex.replaceAll 
                    (precededBy(text("(")) + text ("?<") + oneOrMore (charclassUnion (LETTER, DIGIT)) + text (">")
                    , ""); 
        }
    
    
    /****************************************************************************************************************
     *                                               Private methods                                                *
     ****************************************************************************************************************/
    
      private static String 
    escapeCharclassMetacharacter (char character) {
        
            switch (character) {
                case '-':
                    return "\\-";
                case '^':
                    return "\\^";
                case '[':
                    return "\\[";
                case ']':
                    return "\\]";
                case '\\':
                    return "\\\\";
                default:
                    return String.valueOf (character);
            }
        }
    
      private static String 
    join (String[] elements) {
        
            Objects.requireNonNull (elements);
            
            StringBuilder sb = new StringBuilder ();
            
            for (CharSequence element: elements) 
            {
                sb.append(element);
            }
            
            return sb.toString();
        }
    
      private static String 
    join (String[] elements, char delimiter) {
        
            Objects.requireNonNull (delimiter);
            Objects.requireNonNull (elements);
            
            StringBuilder sb = new StringBuilder ();
            
            for (CharSequence element: elements) 
            {
                sb.append (element);
                sb.append (delimiter);
            }
            if (sb.length() > 0)
                sb.setLength (sb.length() - 1);
            
            return sb.toString();
        }
}
