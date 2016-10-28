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
                 ,"A", "test", "of", "many", "words");
        }
    
      @Test public void
    charclassMetacharacters() {
        
            test ( charclass ('^', '.', '[', ']', '-', '+')
                 , "^+[.]-"
                 , "^", "+", "[", ".", "]", "-" );
            
            test ( charclassUnion (text ("^.[]-+"))
                 , "^+[.]-"
                 , "^", "+", "[", ".", "]", "-" );
        }
    
      private void
    test (String regex, String string, String... expectedMatches) {
        
            Pattern pattern = Pattern.compile (regex);
            Matcher matcher = pattern.matcher (string);
            
            ArrayList matches = new ArrayList<>();
            while (matcher.find ())
                matches.add (matcher.group ());
            
            assertArrayEquals (expectedMatches, matches.toArray ());
        }
}
