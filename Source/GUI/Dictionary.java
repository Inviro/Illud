package Source.GUI;

import javax.swing.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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

    // Keys to get each part of the dictionary entry
    private static final String PHONETIC = "phonetic";
    private static final String WORD = "word";
    private static final String MEANINGS = "meanings";
    private static final String SPEECH_PART = "partOfSpeech";
    private static final String DEFINITIONS = "definitions";
    private static final String DEFINITION = "definition";
    private static HashMap<String, String> displayMap;

    private StringBuffer resultSB;  // String buffer used to build result string

    public Dictionary() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonDefine);

        // Setting listeners
        buttonDefine.addActionListener(e -> onDefine());
        buttonCancel.addActionListener(e -> onCancel());
        wordInputTextField.addActionListener(e -> onDefine());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setSize(800, 400);                     // Setting Dialog Size
        this.setLocationRelativeTo(null);                       // Centers Dialog
        this.setTitle("Dictionary");                            // Sets Dialog Title

        // JSON Variables
        jsonParser = new JSONParser(); // JSON parser

        // Used to display values of each part of a dictionary definition
        displayMap = new HashMap<>();
        displayMap.put(PHONETIC, "Pronunciation");
        displayMap.put(WORD, "Word");
        displayMap.put(SPEECH_PART, "Part of Speech");
        displayMap.put(DEFINITIONS, "Definitions");
        displayMap.put(DEFINITION, "Definition");
    }

    // onDefine Function
    // Will initiate reading online JSON File and output to the JTextArea
    private void onDefine() {
        definitionJTextArea.setText(null);                  // Clear text area for new output
        readJSON();                                         //retrieve definition
        this.getRootPane().setDefaultButton(buttonDefine);  // Sets default button to last pressed one
    }

    // Will Close Window when initiated
    private void onCancel() {
        dispose();
        this.getRootPane().setDefaultButton(buttonCancel);  // Sets default button to last pressed one
    }

    // Read JSON File from "https://api.dictionaryapi.dev/api/v2/entries/en/insertWordHere"
    private void readJSON(){
        // Get input from text field
        String input = wordInputTextField.getText();
        if(!input.isEmpty()){
            // Getting information from online JSON File
            try {
                // Url is from Merriam Webster's Dictionary API
                URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + input); //Creates URL Object
                BufferedReader reader = new BufferedReader(new InputStreamReader((url.openStream())));  //Reads URL

                resultSB = new StringBuffer();
                String output = parseDictionaryJSON(jsonParser.parse(reader));
                definitionJTextArea.setText(output);
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
        // Meanings is another JSONArray array inside the object
        JSONArray meanings = (JSONArray)jsonObject.get(MEANINGS);

        // Appends each String for each part of the JSON file
        appendJSONStringFromKey(jsonObject, WORD);
        appendJSONStringFromKey(jsonObject, PHONETIC);
        appendJSONStringFromKey(jsonObject, MEANINGS);

        // Count of array elements
        int partOfSpeechCount = 1;
        int definitionCount = 1;

        // For each meaning
        for(Object meaning: meanings){
            JSONObject meaningObj = (JSONObject)meaning; // Cast to JSONObject
            appendJSONStringFromKey(meaningObj, SPEECH_PART, partOfSpeechCount++, 1); // Add formatted string

            // Definitions is yet another array inside meanings
            JSONArray definitions = (JSONArray)meaningObj.get(DEFINITIONS);
            // For each definition
            for(Object o : definitions){
                JSONObject defObj = (JSONObject) o; // Cast to JSONObject
                appendJSONStringFromKey(defObj, DEFINITION, definitionCount++, 2);
            }
        }
    }

    // Appends String from JSONObject from key if it exists to string buffer object
    private void appendJSONStringFromKey(JSONObject jsonObject, String key){
        Object o = jsonObject.get(key);
        if(o instanceof String){                                    // Simple entry
            resultSB.append(displayMap.get(key) + ": " + o + "\n"); // Appends formatted string to string buffer
        }
        // Not string or null, so nothing happens
    }

    // Appends String from JSONObject from key if it exists to string buffer object
    // Prints formatted index of array with tabbing
    private void appendJSONStringFromKey(JSONObject jsonObject, String key, int index, int num_tabs){
        Object o = jsonObject.get(key);
        if(o instanceof String){                                                // Simple entry
            resultSB.append("\t".repeat(num_tabs) +                             // Appends num_tabs tabs
                    displayMap.get(key) + " " + index + ": " + o + "\n") ;      // Appends string to string buffer
        }
        // Not string or null, so nothing happens
    }
}
