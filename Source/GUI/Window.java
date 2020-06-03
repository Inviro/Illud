package Source.GUI;
import Libraries.MaryTTS.Tutorial.TextToSpeech;

import javax.swing.*; // Import for GUI
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window {
    // Window Variables
    private JFrame jFrame;          // Main JFrame where everything is put on top of
    private int width, height;      // Window Dimensions

    // UserInput variables
    private UserInput userInput;    // Form for user input

    // Text to Speech Variables
    private TextToSpeech tts;       // Text to speech object
    private float volume;           // Volume of Text To Speech

    // Constructor
    public Window(String name){
        // Initializing text to speech
        tts = new TextToSpeech();                               // Creates new Text to Speech Object

        // This one sounds the least bad. cmu-rms-hsmm sounds horrible.
        tts.setVoice("dfki-poppy-hsmm");                        // Sets the text to speech voice
        volume = 1.0f;                                          // Sets volume to a default number

        // Initializing JFrame
        jFrame = new JFrame(name);                              // Creates new JFrame to put JPanels on
        userInput = new UserInput();                            // Creates new instance of UserInput
        makeListeners();                                        // Creates listeners for userInput
        jFrame.setContentPane(userInput.getMainPanel());        // Sets content pane to new instance of UserInput
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Exits when X is clicked
        jFrame.pack();                                          // Packs the elements on top of the JFrame
        jFrame.setVisible(true);                                // Makes everything visible
        this.setSize(500, 400);                     // Sets size to a default amount
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

        /// Anything in between triple comments is for the demo and can be deleted later
        jTextArea.setText("Change this to anything you want to be read and click the button.");
        /// End of demo

        // Creates action listener for jButton
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { // Runs on button click
                // Gets Text from jTextArea
                String text = jTextArea.getText();

                // Uses TTS on the text
                speak(text);

                // Displays the text
                JOptionPane.showMessageDialog(jFrame, text);
            }
        });
    }
}
