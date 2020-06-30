package Source.GUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import groovy.json.JsonException;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class Dictionary extends JDialog {
    // JavaSwing Variables
    private JPanel contentPane;
    private JButton buttonDefine;
    private JButton buttonCancel;
    private JTextField wordInputTextField;
    private JTextArea definitionJTextArea;

    // JSON variables
    private JSONParser jsonParser;

    // Regex variables
    private static java.util.regex.Pattern pattern;
    private static java.util.regex.Matcher matcher;

    // Regular expression that I made to find the first word of almost any string. - Abraham
    // Works for words with single contractions.
    private static final String regex = "^.*?([a-zA-Z']+'?[a-zA-Z']*)";

    private StringBuffer resultSB;  // String buffer used to build result string

    public Dictionary() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDefine);

        buttonDefine.addActionListener(e -> onDefine());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setSize(600, 320);                     // Setting Dialog Size
        this.setLocationRelativeTo(null);                       // Centers Dialog
        this.setTitle("Dictionary");                            // Sets Dialog Title

        pattern = java.util.regex.Pattern.compile(regex);   // Pattern to match
        matcher = pattern.matcher(regex);                   // Matcher for the regular expression

        // JSON Variables
        jsonParser = new JSONParser(); // JSON parser
    }

    // onDefine Function
    // Will initiate reading online JSON File and output to the JTextArea
    private void onDefine() {
        definitionJTextArea.setText(null);  // Clear text area for new output
        readJSON();                         //retrieve definition
    }

    // Will Close Window when initiated
    private void onCancel() {
        dispose();
    }

    // Read JSON File from "https://api.dictionaryapi.dev/api/v2/entries/en/insertWordHere"
    private void readJSON(){
        // Get input from text field
        String input = wordInputTextField.getText();
        matcher.reset(input); // Runs regex on input

        // Regex has capture groups
        if(matcher.find()){
            this.wordInputTextField.setText(matcher.group(1)); // Sets text to second match group from regex

            // Getting information from online JSON File
            try {

                URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + input); //Creates URL Object
                BufferedReader reader = new BufferedReader(new InputStreamReader((url.openStream())));  //Reads URL

                resultSB = new StringBuffer();
                String output = parseDictionaryJSON(jsonParser.parse(reader));
                System.out.println(output);
            }
            //Exceptions
            catch (MalformedURLException e){
                JOptionPane.showMessageDialog(this, "Malformed URL: "  + e.getMessage() + "\n");
            }
            catch(FileNotFoundException e){
                JOptionPane.showMessageDialog(this,
                        "No definitions found for: " + wordInputTextField.getText() + "\n");
            }
            catch (ParseException e){
                JOptionPane.showMessageDialog(this, "Parse Error :" + e.getMessage() + "\n");
            }
            catch (IOException e){
                JOptionPane.showMessageDialog(this, "I/O Error: " + e.getMessage() + "\n");
            }
        }
    }

    // Outputs definition to text area
    private void displayDef(int num, String type, String def){
        definitionJTextArea.append("Definition " + num + "\nType: " + type + "\nDefinition: " + def +"\n\n");
    }

    // Sets word input and searches
    public void setAndSearch(String query){
        this.wordInputTextField.setText(query);
        this.onDefine();
        this.setVisible(true);
    }

    // Recursive function for parsing the dictionary JSON file
    private String parseDictionaryJSON(Object o){
        try{
            if(o instanceof JSONObject){                        // If is JSONObject
                getDictionaryEntry((JSONObject)o);              // Cast o to JSONObject and get dictionary results
            } else if (o instanceof JSONArray){                 // If is JSONArray
                for(Object obj : (JSONArray)o){                 // Cast o to JSONArray and get dictionary results
                    parseDictionaryJSON(obj);
                }
            }
        } catch (JsonException jsonException){
            jsonException.printStackTrace();
        }
        return resultSB.toString();
    }

    // Displays
    private void getDictionaryEntry(JSONObject jsonObject){
        // Keys to get each part of the dictionary entry
        final String PHONETIC = "phonetic";
        final String WORD = "word";
        final String MEANINGS = "meanings";
        final String SPEECH_PART = "partOfSpeech";
        final String DEFINITIONS = "definitions";
        final String DEFINITION = "definition";

        // Meanings is another array inside the object
        JSONArray meanings = (JSONArray)jsonObject.get(MEANINGS);

        getJSONStringFromKey(jsonObject, WORD);
        getJSONStringFromKey(jsonObject, PHONETIC);
        getJSONStringFromKey(jsonObject, MEANINGS);

        // Count of array elements
        int partOfSpeechCount = 1;
        int definitionCount = 1;

        // For each meaning
        for(Object meaning: meanings){
            JSONObject meaningObj = (JSONObject)meaning;
            getJSONStringFromKey(meaningObj, SPEECH_PART, partOfSpeechCount++, 1);

            // Definitions is yet another array inside meanings
            JSONArray definitions = (JSONArray)meaningObj.get(DEFINITIONS);
            for(Object o : definitions){
                JSONObject defObj = (JSONObject) o;
                getJSONStringFromKey(defObj, DEFINITION, definitionCount++, 2);
            }
        }
    }

    // Returns String from JSONObject from key if it exists as a String
    private void getJSONStringFromKey(JSONObject jsonObject, String key){
        Object o = jsonObject.get(key);
        if(o instanceof String){                        // Simple entry
            resultSB.append(key + ": " + o + "\n");    // Returns string
        }
        // Not string or null, so does nothing
    }

    // Returns String from JSONObject from key if it exists
    // Prints formatted index of array with tabbing
    private void getJSONStringFromKey(JSONObject jsonObject, String key, int index, int tabbing){
        Object o = jsonObject.get(key);
        if(o instanceof String){                                                            // Simple entry
            resultSB.append("\t".repeat(tabbing) + key + " " + index + ": " + o + "\n") ;   // Returns formatted string
        }
        // Not string or null, so does nothing
    }
}
