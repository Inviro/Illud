package Source.GUI;


import javax.swing.*;

public class UserInput {
    // Class Variables
    private JPanel MainPanel;
    private JTextArea mainTextArea;
    private JButton pressMeButton;
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
    public JButton getPressMeButton() { return pressMeButton; }
    public JList getJList() { return jList; }

    public void setMainTextArea(JTextArea mainTextArea) {
        String f = mainTextArea.getText();
        this.mainTextArea.setText(f);

        this.mainTextArea = mainTextArea;
    }

    public JButton getFindAndReplace() {
        return findAndReplace;
    }
}
