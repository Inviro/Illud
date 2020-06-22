package Source.GUI;

import javax.swing.*;

public class FindandReplace extends JDialog{
    private JPanel findAndReplace;
    private JButton buttonFind;
    private JTextField t1;
    private JTextField t2;
    private JTextArea area;

    public FindandReplace(JTextArea area) {
        this.area = area;
        setContentPane(findAndReplace);
        setModal(true);
        getRootPane().setDefaultButton(buttonFind);
        buttonFind.addActionListener(e -> onFind());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setSize(600, 150);                     // Setting Dialog Size
        this.setLocationRelativeTo(null);                       // Centers Dialog
        this.setTitle("Find and Replace");                      // Sets Dialog Title
    }

    private void onFind() {
        // add your code here
        String a = t1.getText(); // Find field
        String a2 = String.format(" %s ", a); // add spaces before and after word

        String b = t2.getText(); // Replace field
        String b2 = String.format(" %s ", b); // add spaces before and after word

        String c = area.getText(); // Assign text from mainTextArea

        JOptionPane.showMessageDialog(this,
                "Are you sure you want to " +
                        "replace \"" + a + "\" with \"" + b + "\" " +
                        "? \nPlease click ok.");

        String d = c.replaceAll(a2, b2); // Replace a2 with b2
        area.setText(d); // Place the corrected text into the main panel
        this.dispose();
    }
}
