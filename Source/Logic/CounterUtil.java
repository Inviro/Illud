package Source.Logic;

import javax.swing.JTextArea;

public class CounterUtil{
    public static int characterCounter(JTextArea input){
        return input.getText().length(); // Number of characters in the input
    }

    public static int wordCounter(JTextArea input){
        // Counts regex captures separated by whitespace
        return input.getText().split("\\s").length;
    }

    public static int lineCounter(JTextArea input){
        // Counts regex captures separated by newline characters
        return input.getText().split("\\n").length;
    }

    public static int paragraphCounter(JTextArea input){
        // Counts regex captures separated by line feed characters
        return input.getText().split("\\R+").length;
    }

    public static int sentenceCounter(JTextArea input) {
        // Counts regex captures separated by a period followed by a space
        return input.getText().split("[!?.:]+").length;
    }
}
