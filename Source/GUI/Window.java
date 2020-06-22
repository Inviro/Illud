package Source.GUI;
import Libraries.MaryTTS.Tutorial.TextToSpeech;
import Source.Logic.CounterUtil;
import javax.swing.*;                                           // Used for GUI
import javax.swing.event.DocumentEvent;                         // Used for getting jTextArea text
import javax.swing.event.DocumentListener;                      // Used for creating jTextArea listeners
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;                               // Used to listen for text change
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;                                        // Used for JList

public class Window extends JFrame {
    // Window Variables
    private final String ICON_PATH = "/Resources/icon.png";     // Path to the icon
    private final String WIN_NAME = "Illud - Text Analysis";    // Name of the window
    private ImageIcon illudIcon;                                // Used to set icons of dialog classes

    private Find find;                                          // Find dialog
    private FindandReplace findandReplace;                      // Find and Replace dialog
    private Dictionary dictionary;                              // Dictionary dialog
    private About about;                                        // About dialog

    // JMenuItems to add listeners to in the menu
    private JMenuItem open_menu_item;
    private JMenuItem dict_menu_item;
    private JMenuItem find_menu_item;
    private JMenuItem find_replace_menu_item;
    private JMenuItem tts_menu_item;
    private JMenuItem about_menu_item;

    private JFileChooser fc;                                    // File chooser
    private Vector<String> acceptedTypes;
    static private final String newline = "\n";

    // UserInput variables
    private UserInput userInput;                                // Form for user input

    // Text to Speech Variables
    private TextToSpeech tts;                                   // Text to speech object
    private float volume;                                       // Volume of Text To Speech

    // Enum for getting strings corresponding to different voices
    public enum Voice{
        poppy("dfki-poppy-hsmm"),
        rms("cmu-rms-hsmm"),
        slt("cmu-slt-hsmm");
        public final String voiceString;                        // Unmodifiable value
        Voice(String vS){                                       // Enum constructor
            this.voiceString = vS;
        }
    }

    // Constructor
    public Window(){
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
        this.volume = 1.0f;                                     // Sets volume to a default number
        initUI();                                               // Initializes the User Interface
    }

    // Misc Functions
    private void speak(String text) {tts.speak(text, volume, false, false);}    // Uses MaryTTS on the text
    private void endSpeak() { tts.stopSpeaking(); }                                         // Ends MaryTTS playback

    // Initializing all of the UI elements in Window
    private void initUI() {
        // Initializing JFrame
        this.setTitle(WIN_NAME);                                // Creates new JFrame to put JPanels on

        illudIcon = new ImageIcon(                              // New icon image composed of:
                        getClass()                              // From the instance of current class:
                        .getResource(ICON_PATH));               // Get the resource at ICON_PATH

        // Setting Icon image in the JFrame
        this.setIconImage(illudIcon.getImage());

        userInput = new UserInput();                            // Creates new instance of UserInput
        this.setContentPane(userInput.getMainPanel());          // Sets content pane to new instance of UserInput
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // Exits when X is clicked
        this.pack();                                            // Packs the elements on top of the JFrame
        this.setSize(500, 400);                     // Sets size to a default amount
        this.setLocationRelativeTo(null);                       // Centers Window
        this.setVisible(true);                                  // Makes everything visible

        // Creating jMenuBar, jMenus and jMenuItems
        // jMenuBar > jMenu > jMenuItem
        // jMenuBar holds all of the jMenus
        // A jMenu for example would be "File" or something you would see inside the bar
        // A jMenuItem or jMenu would show up once you click the jMenu in the jMenuBar
        JMenuBar jMenuBar = new JMenuBar();
        JMenu file = new JMenu("File");                               // "File"
        open_menu_item = new JMenuItem("Open");                     // "File > Open"
        JMenu actions = new JMenu("Actions");                         // "Actions"
        dict_menu_item = new JMenuItem("Dictionary");               // "Actions" > "Dictionary"
        find_menu_item = new JMenuItem("Find");                     // "Actions" > "Find"
        find_replace_menu_item = new JMenuItem("Find and Replace"); // "Actions" > "Find and Replace"
        tts_menu_item = new JMenuItem("Text To Speech");            // "Actions" > "Text to Speech"
        JMenu help = new JMenu("Help");                               // "Help"
        about_menu_item = new JMenuItem("About");                   // "Help" > About"

        // Listener for File > Open
        open_menu_item.addActionListener(e -> {
            fc = new JFileChooser(); // New file chooser object

            // Opens the dialog for the file chooser
            int returnVal = fc.showOpenDialog(Window.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file1 = fc.getSelectedFile();
                System.out.println("Opening: " + file1.getName() + "." + newline);

                // Reading file into a string
                Scanner scanner = null;
                try {
                    scanner = new Scanner(file1);
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                String fileText = scanner.useDelimiter("\\A").next();
                scanner.close();

                userInput.setFile(fileText); // Puts string from file into main text area
            }
        });

        // Creating the menu bar from the above elements
        jMenuBar.add(file);
        jMenuBar.add(actions);
        jMenuBar.add(help);
        file.add(open_menu_item);
        actions.add(dict_menu_item);
        actions.add(find_menu_item);
        actions.add(find_replace_menu_item);
        actions.add(tts_menu_item);
        help.add(about_menu_item);
        this.setJMenuBar(jMenuBar);                                         // Sets the menu bar
        makeListeners();                                                    // Creates action listeners

        find = new Find();                                                  // Creating Find Dialog
        find.setSize(500, 150);                                 // Setting Dialog Size
        find.setLocationRelativeTo(null);                                   // Centers Dialog

        findandReplace = new FindandReplace(userInput.getMainTextArea());   // Creating Find and Replace Dialog
        findandReplace.setIconImage(illudIcon.getImage());                  // Sets Icon to Illud Icon

        dictionary = new Dictionary();                                      // Creating Dictionary Dialog
        dictionary.setSize(500, 150);                           // Setting Dialog Size
        dictionary.setLocationRelativeTo(null);                             // Centers Dialog

        about = new About();                                                // Creating About Dialog
        about.setIconImage(illudIcon.getImage());                           // Sets Icon to Illud Icon

        // Creating File Chooser
        fc = new JFileChooser();                                            // New file chooser object

        // Setting acceptable file types
//        fc.setAcceptAllFileFilterUsed(false);                             // Does not accept all file types

        acceptedTypes = new Vector<>();                                     // Holds accepted file types
        acceptedTypes.add("txt");                                           // Text files
        fc.setFileFilter(new FileFilter() {                                 // Creates a new filter
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()){                                       // Allows folders to be selected
                    return true;
                } else{
                    String filename = f.getName().toLowerCase();
                    for(String ele: acceptedTypes){                         // For each accepted file type
                        if (filename.endsWith(ele)){                        // Returns true if suffix type is accepted
                            return true;
                        }
                    }
                    return false;
                }
            }
            @Override
            public String getDescription() {
                // Creating accepted file type descriptions
                String temp = "Text Files ";
                for(String ele: acceptedTypes) {                            // For each accepted file type
                    temp += "(*." + ele + ") ";
                }
                return temp;
            }
        });
    }

    // Makes listeners for UserInput
    private void makeListeners(){
        // Gets UI elements from userInput
        JTextArea jTextArea = userInput.getMainTextArea();

        JList list = userInput.getJList();
        Document doc = jTextArea.getDocument();
        // Listener for Document
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

        // Listener for File > Open
        open_menu_item.addActionListener(e -> {
            // Opens the dialog for the file chooser
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                // Reading file into a string
                Scanner scanner = null;
                try {
                    scanner = new Scanner(file, "utf-8");
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                String fileText = scanner
                        .useDelimiter("\\A")                        // Delimiter - End of Line
                        .next()                                     // Next input
                        .replace("\r", "");         // Removes extra CR
                scanner.close();
                userInput.setFile(fileText); // Puts string from file into main text area
            }
        });

        // Listener for Action > Find
        find_menu_item.addActionListener(e -> {
            find.setVisible(true);
        });

        // Listener for Action > Find and Replace
        find_replace_menu_item.addActionListener(e -> {
            findandReplace.setVisible(true);
        });

        // Listener for Action > Dictionary
        dict_menu_item.addActionListener(e -> {
            dictionary.setVisible(true);
        });

        // Listener for Action > Dictionary
        about_menu_item.addActionListener(e -> {
            about.setVisible(true);
        });

        tts_menu_item.addActionListener(e -> {
            // Gets Text from jTextArea
            String text = jTextArea.getSelectedText();

            // No text highlighted
            if(text == null){           // If text is null end TTS playback
                endSpeak();
            } else if (text != ""){     // Checks if text is not empty
                speak(text);            // Uses TTS on the text
            }
        });
    }

    private void updateCounters(JTextArea jTextArea, JList jList){
        Vector<String> result = new Vector<>();
        result.add(CounterUtil.characterCounter(jTextArea) + " characters\n");
        result.add(CounterUtil.wordCounter(jTextArea) + " words\n");
        result.add(CounterUtil.lineCounter(jTextArea) + " lines\n");
        result.add(CounterUtil.paragraphCounter(jTextArea) + " paragraphs\n");
        result.add(CounterUtil.sentenceCounter(jTextArea) + " sentences\n");
        jList.setListData(result);
    }
}
