package Source.GUI;

// IO imports
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JList;

public class UserInput {
    // Class Variables
    private JPanel MainPanel;
    private JTextArea mainTextArea;
    private JList<String> jList;

    // Getters
    public JPanel getMainPanel() { return MainPanel; }
    public JTextArea getMainTextArea() { return mainTextArea; }
    public JList<String> getJList() { return jList; }
    public void setFile(String text) {
        mainTextArea.setText(text);
    }
}
