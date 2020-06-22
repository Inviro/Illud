package Source.GUI;

import javax.swing.*;

public class UserInput {
    // Class Variables
    private JPanel MainPanel;
    private JTextArea mainTextArea;
    private JList jList;
    private JPanel jPanel;
    private JScrollPane scrollPane;

    // Getters
    public JPanel getMainPanel() { return MainPanel; }
    public JTextArea getMainTextArea() { return mainTextArea; }
    public JList getJList() { return jList; }
    public void setFile(String text) {
        mainTextArea.setText(text);
    }
}