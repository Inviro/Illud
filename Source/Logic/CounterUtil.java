package Source.Logic;

import javax.swing.JTextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class CounterUtil{
    public static int characterCounter(JTextArea input) throws IOException{
        StringReader sr = new StringReader(input.getText());
        BufferedReader br = new BufferedReader(sr);
        int numOfChar = 0;
        String temp;

        while ((temp = br.readLine()) != null){
            numOfChar += temp.length();
        }
        return numOfChar;
    }

    public static int wordCounter(JTextArea input) throws IOException{
        StringReader sr = new StringReader(input.getText());
        BufferedReader br = new BufferedReader(sr);
        int numOfWords = 0;
        String temp;

        while ((temp = br.readLine()) != null){
            String[] wordList = temp.split("\\s");
            numOfWords += wordList.length;
        }
        return numOfWords;
    }

    public static int lineCounter(JTextArea input){
        return input.getLineCount();
    }

    public static int paragraphCounter(JTextArea input) throws IOException{
        StringReader sr = new StringReader(input.getText());
        BufferedReader br = new BufferedReader(sr);
        int numOfParagraphs=0;
        String temp;

        while ((temp = br.readLine()) != null){
            numOfParagraphs++;
        }
        return numOfParagraphs;
    }

    public static int sentenceCounter(JTextArea input) throws IOException{
        StringReader sr = new StringReader(input.getText());
        BufferedReader br = new BufferedReader(sr);
        int numOfSentences = 0;
        String line;

        while ((line = br.readLine()) != null){
            String [] sentenceList = line.split("[!?.:]+");
            numOfSentences += sentenceList.length;
        }
        return numOfSentences;
    }
}
