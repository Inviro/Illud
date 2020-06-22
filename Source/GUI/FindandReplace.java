package Source.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindandReplace extends JDialog{
    private JPanel findAndReplace;
    private JButton buttonFind;
    private JTextField t1;
    private JTextField t2;
    private JTextArea area = new JTextArea();

    public  FindandReplace() {
        setContentPane(findAndReplace);
        setModal(true);
        getRootPane().setDefaultButton(buttonFind);

        buttonFind.addActionListener(e -> onFind());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void onFind() {
        // add your code here
        String a = t1.getText(); // Find field
        String a2 = String.format(" %s ", a); // add spaces before and after word

        String b = t2.getText(); // Replace field
        String b2 = String.format(" %s ", b); // add spaces before and after word

        String c = area.getText(); // Assign text from mainTextArea

        JOptionPane.showMessageDialog(this,"Are you sure you want to " +
                "replace \"" + a + "\" with \"" + b + "\" " +
                "? \nPlease click ok." );

        String d = c.replaceAll(a2,b2); // Replace a2 with b2
        area.setText(d); // Place the corrected text into the main panel
        dispose();
    }

    public void setFindAndReplace(JTextArea copy) {
        area = copy; // Set JtextArea to mainTextArea
    }
}
