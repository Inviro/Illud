package Source.GUI;
import Libraries.MaryTTS.Tutorial.TextToSpeech;
import Source.Logic.CounterUtil;

import javax.swing.*;                       // Used for GUI
import javax.swing.event.DocumentEvent;     // Used for getting jTextArea text
import javax.swing.event.DocumentListener;  // Used for creating jTextArea listeners
import javax.swing.text.Document;           // Used to listen for text change
import java.awt.event.ActionEvent;          // Used to handle events
import java.awt.event.ActionListener;       // Adds a listener to events
import java.util.Vector;                    // Used for JList

public class Window {
    // Window Variables
    private JFrame jFrame;          // Main JFrame where everything is put on top of
    private int width, height;      // Window Dimensions

    // JMenuBar Variables
    private JMenuBar jMenuBar;
    private JMenu file;
    private JMenuItem open;
    private JMenu settings;
    private JMenuItem about;

    // UserInput variables
    private UserInput userInput;    // Form for user input

    // Text to Speech Variables
    private TextToSpeech tts;       // Text to speech object
    private float volume;           // Volume of Text To Speech

    // Enum for getting strings corresponding to different voices
    public enum Voice{
        poppy("dfki-poppy-hsmm"),
        rms("cmu-rms-hsmm"),
        slt("cmu-slt-hsmm");
        public final String voiceString;    // Unmodifiable value
        Voice(String vS){                   // Enum constructor
            this.voiceString = vS;
        }
    }

    /// Testing Only, delete me later
    private int call_count = 0;
    ///

    // Constructor
    public Window(String name){
        // Setting look and feel
        try {
            // For each installed look and feel (UI theme)
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // Searches for Nimbus theme
                    UIManager.setLookAndFeel(info.getClassName()); // Sets to Nimbus theme if found
                    break; // Breaks loop
                }
            }
        }
        catch (Exception e) { // Theme is not found
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Sets to default theme
            } catch (Exception ex) {}
        }

        // Initializing text to speech
        tts = new TextToSpeech();                               // Creates new Text to Speech Object
        tts.setVoice(Voice.poppy.voiceString);                  // Sets a voice to the text to speech object
        volume = 1.0f;                                          // Sets volume to a default number

        // Initializing JFrame
        jFrame = new JFrame(name);                              // Creates new JFrame to put JPanels on
        userInput = new UserInput();                            // Creates new instance of UserInput
        jFrame.setContentPane(userInput.getMainPanel());        // Sets content pane to new instance of UserInput
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exits when X is clicked
        jFrame.pack();                                          // Packs the elements on top of the JFrame
        jFrame.setVisible(true);                                // Makes everything visible
        this.setSize(500, 400);                     // Sets size to a default amount

        // Creating jMenuBar, jMenus and jMenuItems
        // jMenuBar > jMenu > jMenuItem
        // jMenuBar holds all of the jMenus
        // A jMenu for example would be "File" or something you would see inside the bar
        // A jMenuItem or jMenu would show up once you click the jMenu in the jMenuBar
        jMenuBar = new JMenuBar();
        file = new JMenu("File");                 // "File"
        open = new JMenuItem("Open");           // "File > Open"
        settings = new JMenu("Settings");         // "Settings"
        about = new JMenuItem("About");         // "Settings > About"

        jMenuBar.add(file);
        jMenuBar.add(settings);
        file.add(open);
        settings.add(about);

        jFrame.setJMenuBar(jMenuBar);                // Sets the menu bar
        makeListeners();                             // Creates action listeners
    }

    // Setter for size
    public void setSize(int width, int height){
        jFrame.setSize(width, height);
        this.width = width;
        this.height = height;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // Misc Functions
    private void speak(String text) {tts.speak(text, volume, false, false);}

    // Makes listeners for UserInput
    private void makeListeners(){
        // Gets UI elements from userInput
        JTextArea jTextArea = userInput.getMainTextArea();
        JButton jButton = userInput.getPressMeButton();

        // Creates action listener for jButton
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Runs on button click
                // Gets Text from jTextArea
                String text = jTextArea.getText();

                // Uses TTS on the text
                if(!text.equals("")){ // Makes sure that there is text to be read
                    speak(text);
                }

                // Displays the text
                JOptionPane.showMessageDialog(jFrame, text);
            }
        });

        JList list = userInput.getJList();
        Document doc = jTextArea.getDocument();
        doc.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCounters(jTextArea, list);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCounters(jTextArea, list);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCounters(jTextArea, list);
            }
        });
    }

    private void updateCounters(JTextArea jTextArea, JList jList){
        Vector<String> result = new Vector<>();

        /// Delete anything in between triple quotes, it's just for testing JList updating
        call_count++;
        result.add(call_count + " characters\n");
        result.add((call_count + 1) + " words\n");
        result.add((call_count + 2) + " lines\n");
        result.add((call_count + 3) + " paragraphs\n");
        result.add((call_count + 4) + " sentences\n");
        ///

        // TODO: Uncomment after implementing counters in CounterUtil
//        result.add(CounterUtil.characterCounter(jTextArea) + " characters\n");
//        result.add(CounterUtil.wordCounter(jTextArea) + " words\n");
//        result.add(CounterUtil.lineCounter(jTextArea) + " lines\n");
//        result.add(CounterUtil.paragraphCounter(jTextArea) + " paragraphs\n");
//        result.add(CounterUtil.sentenceCounter(jTextArea) + " sentences\n");
        jList.setListData(result);
    }
}
