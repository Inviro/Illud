
package Source.Logic;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterUtil{
    // Precompiled regex patterns to improve speed
    private static final Pattern WORD_PATTERN = Pattern.compile("\\s+");
    private static final Pattern LINE_PATTERN = Pattern.compile("\\n");
    private static final Pattern PARA_PATTERN = Pattern.compile("\\R+");
    private static final Pattern SENT_PATTERN = Pattern.compile("[!?.:]+");
    private static Matcher word_matcher = WORD_PATTERN.matcher("");
    private static Matcher line_matcher = LINE_PATTERN.matcher("");
    private static Matcher para_matcher = PARA_PATTERN.matcher("");
    private static Matcher sent_matcher = SENT_PATTERN.matcher("");

    public static Vector<String> getCounterData(String input){
        Vector<String> output = new Vector<>();                 // Stores output

        // Adds each stat to the output
        output.add(input.length() + " characters\n");

        // Counts regex captures separated by whitespace and adds it to output
        output.add(word_matcher.reset(input).results().count() + " words\n");

        // Counts regex captures separated by newline characters and adds it to output
        output.add(line_matcher.reset(input).results().count() + " lines\n");

        // Counts regex captures separated by line feed characters and adds it to output
        output.add(para_matcher.reset(input).results().count() + " paragraphs\n");

        // Counts regex captures separated by a period followed by a space and adds it to output
        output.add(sent_matcher.reset(input).results().count() + " sentences\n");
        return output; // Returns output
    }
}
