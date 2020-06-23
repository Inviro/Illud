package Source.GUI;

import javax.swing.*;
import java.awt.event.KeyEvent;

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
        String a = t1.getText();                                // Find field
        String a1 = String.format("\\b%s\\b", a);               // Regular expression to find exact matches per word
        String b = t2.getText();                                // Replace field
        String c = area.getText();                              // Assign text from mainTextArea

        JOptionPane.showMessageDialog(this,
                "Are you sure you want to " +
                        "replace \"" + a + "\" with \"" + b + "\"" +
                        "? \nPress ok to continue.");

        // Makes sure that there is something to replace
        if(!a.isEmpty()){
            // Checks if word boundary checking is required
            if(!replaceOption.isSelected()){        // Boundary checking is required
                area.setText(c.replaceAll(a, b));   // Replaces all exact word matches
            } else{                                 // Boundary checking not required
                area.setText(c.replaceAll(a1, b));  // Simple replace
            }
        }
    }
}
