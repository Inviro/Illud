
package Source.GUI;

// IO imports
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JButton;

public class UserInput {
    // Class Variables
    private JPanel MainPanel;
    private JTextArea mainTextArea;
    private JList<String> jList;
    private JPanel jPanel;
    private JScrollPane scrollPane;
    private JButton findAndReplace; // Button added to main GUI

    // Getters
    public JPanel getMainPanel() { return MainPanel; }
    public JTextArea getMainTextArea() { return mainTextArea; }
    public JList<String> getJList() { return jList; }
    public void setFile(String text) {
        mainTextArea.setText(text);
    }
}
