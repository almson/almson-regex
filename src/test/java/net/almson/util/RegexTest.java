package net.almson.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import static net.almson.util.Regex.*;
import static org.junit.Assert.*;

/**
 *
 * @author Aleksandr Dubinsky
 */
public class RegexTest {
    
      @Test public void
    example1 () {
        
            test (reluctantly (between (1, 3, not (charclass (' ')))) + followedBy (oneOrMore(" "))
                 ,"A test of many words"
                 ,"A", "est", "of", "any");
        }
    
      @Test public void
    example2 () {
        
            test (text ("\n")
                 ,"A test\nof many\nwords"
                 ,"\n", "\n");
        }
    
      @Test public void
    example3 () {
        
            test (not (charclassIntersection ("\\s", charclass (' ')))
                 ," \na"
                 ,"\n", "a");
        }
    
      @Test public void
    charclassMetacharacters() {
        
            test ( charclass ('^', '.', '[', ']', '-', '+', '\\')
                 , "^+\\[.]-"
                 , "^", "+", "\\", "[", ".", "]", "-" );
            
            test ( charclassUnion (text ("^.[]-+"))
                 , "^+[.]-"
                 , "^", "+", "[", ".", "]", "-" );
        }
    
      @Test public void
    charclass2() {
        
            test(charclassUnion 
                    (charclass ('"', '*', '?', '/', '|', '<')
                    ,charclassRange ('\0', '\u0020'))
                ,"^+\"*[.\0]?\r\n-\\"
                ,"\"", "*", "\0", "?", "\r", "\n");
        }
    
      @Test public void
    unicodeLowercaseLetter() {
        
            test (charclassFromUnicodeCategory("Ll")
                , "HeLlo"
                , "e", "l", "o");
        }
    
      @Test public void
    exampleWhitespace() {
        
            test (either (START_BOUNDARY + oneOrMore (HORIZONTAL_WHITESPACE)
                    , oneOrMore (HORIZONTAL_WHITESPACE) + END_BOUNDARY)
                , "  Hello world! \n"
                , "  ", " ");
        }
    
      @Test public void
    exampleIpAddress() {
        
            test (WORD_BOUNDARY + exactly(3, between(1, 3, DIGIT) + text(".")) + between (1, 3, DIGIT) + WORD_BOUNDARY
                , "My IP address is: 192.168.1.2:4000"
                , "192.168.1.2");
        }
    
      @Test public void
    exampleEmailAddress() {
        
            Pattern pattern = Pattern.compile (WORD_BOUNDARY 
                    + namedGroup ("user", 
                            oneOrMore (charclassUnion (LETTER, DIGIT, charclass ('.', '_', '%', '+', '-'))))
                    + text ("@")
                    + namedGroup ("domain"
                            , oneOrMore (charclassUnion (LETTER, DIGIT, charclass ('.', '-')))
                            + text (".")
                            + atLeast (2, LETTER))
                    + WORD_BOUNDARY);
            
            Matcher matcher = pattern.matcher ("An email address\njohn@acme.com");
            
            assertTrue (matcher.find());
            
            assertEquals ("john@acme.com", matcher.group());
            assertEquals ("john", matcher.group("user"));
            assertEquals ("acme.com", matcher.group("domain"));
            
            assertFalse (matcher.find());
        }
    
      @Test public void
    exampleDuplicates() {
        
            Pattern pattern = Pattern.compile (precededBy (either (START_BOUNDARY, text (",")))
                    + group (zeroOrMore (charclassComplement (charclass (','))))
                    + oneOrMore (text (",") + backreference (1))
                    + followedBy (either (text (","), END_BOUNDARY)));
            
            Matcher matcher = pattern.matcher ("dog,cat,cat,tree,apple,tree,tree,tree");
            
            assertTrue (matcher.find());
            
            assertEquals ("cat", matcher.group(1));
            
            assertTrue (matcher.find());
            
            assertEquals ("tree", matcher.group(1));
            
            assertFalse (matcher.find());
        }
    
      private void
    test (String regex, String string, String... expectedMatches) {
        
            Pattern pattern = Pattern.compile (regex);
            Matcher matcher = pattern.matcher (string);
            
            ArrayList matches = new ArrayList();
            while (matcher.find ())
                matches.add (matcher.group ());
            
            assertArrayEquals (expectedMatches, matches.toArray ());
        }
}
