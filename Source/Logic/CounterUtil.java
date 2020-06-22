package Source.Logic;

import java.util.Vector;

public class CounterUtil{
    private static int characterCounter(String input){
        return input.length(); // Number of characters in the input
    }

    private static int wordCounter(String input){
        // Counts regex captures separated by whitespace
        return input.split("\\s+").length;
    }

    private static int lineCounter(String input){
        // Counts regex captures separated by newline characters
        return input.split("\\n").length;
    }

    private static int paragraphCounter(String input){
        // Counts regex captures separated by line feed characters
        return input.split("\\R+").length;
    }

    private static int sentenceCounter(String input) {
        // Counts regex captures separated by a period followed by a space
        return input.split("[!?.:]+").length;
    }

    public static Vector<String> getCounterData(String input){
        Vector<String> output = new Vector<>();                 // Stores output

        // Adds each stat to the output
        output.add(characterCounter(input) + " characters\n");
        output.add(wordCounter(input) + " words\n");
        output.add(lineCounter(input) + " lines\n");
        output.add(paragraphCounter(input) + " paragraphs\n");
        output.add(sentenceCounter(input) + " sentences\n");
        return output;                                          // Returns output
    }
}
