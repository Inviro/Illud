package Source.GUI;


import javax.swing.*;

public class UserInput {
    // Class Variables
    private JPanel MainPanel;
    private JTextArea mainTextArea;
    private JList jList;
    private JPanel jPanel;
    private JScrollPane scrollPane;
    private JButton findAndReplace; // Button added to main GUI

    // The following is the listerner to the Find and Replace Button. It
    // triggers the Find and Replace UI to pop up.
    public UserInput() {

    }

    // Getters
    public JPanel getMainPanel() { return MainPanel; }
    public JTextArea getMainTextArea() { return mainTextArea; }
    public JList getJList() { return jList; }
    public void setFile(String text) {
        mainTextArea.setText(text);
    }
}
