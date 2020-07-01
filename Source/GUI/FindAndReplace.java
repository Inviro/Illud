package Source.GUI;

import javax.swing.*;
import java.awt.event.KeyEvent;

// Legacy code, its functionality was implemented in Find.
public class FindAndReplace extends JDialog{
    private JPanel findAndReplace;
    private JButton buttonFind;
    private JTextField t1;
    private JTextField t2;
    private JCheckBox replaceOption;
    private JTextArea area;

    public FindAndReplace(JTextArea area) {
        this.area = area;
        setContentPane(findAndReplace);
        setModal(true);
        getRootPane().setDefaultButton(buttonFind);
        buttonFind.addActionListener(e -> onFind());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Dispose on pressing ESC
        findAndReplace.registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.setSize(600, 150);                     // Setting Dialog Size
        this.setLocationRelativeTo(null);                       // Centers Dialog
        this.setTitle("Find and Replace");                      // Sets Dialog Title
    }

    private void onFind() {
        String findText = t1.getText();                                 // Find field
        String regexText = String.format("\\b%s\\b", findText);         // Regular expression to find exact matches per word
        String replaceText = t2.getText();                              // Replace field
        String mainText = area.getText();                               // Assign text from mainTextArea

        JOptionPane.showMessageDialog(this,
                "Are you sure you want to " +
                        "replace \"" + findText + "\" with \"" + replaceText + "\"" +
                        "? \nPress ok to continue.");

        // Makes sure that there is something to replace
        if(!findText.isEmpty()){
            // Checks if word boundary checking is required
            if(!replaceOption.isSelected()){        // Boundary checking is required
                area.setText(mainText.replaceAll(findText, replaceText));   // Replaces all exact word matches
            } else{                                 // Boundary checking not required
                area.setText(mainText.replaceAll(regexText, replaceText));  // Simple replace
            }
        }
    }
}
