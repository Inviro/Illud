package Source.Logic;

public class CounterUtil{
    public static int characterCounter(String input){
        return input.length(); // Number of characters in the input
    }

    public static int wordCounter(String input){
        // Counts regex captures separated by whitespace
        return input.split("\\s+").length;
    }

    public static int lineCounter(String input){
        // Counts regex captures separated by newline characters
        return input.split("\\n").length;
    }

    public static int paragraphCounter(String input){
        // Counts regex captures separated by line feed characters
        return input.split("\\R+").length;
    }

    public static int sentenceCounter(String input) {
        // Counts regex captures separated by a period followed by a space
        return input.split("[!?.:]+").length;
    }
}
