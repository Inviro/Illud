package Source.GUI;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        findAndReplace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame(("FindandReplace"));
                frame.setContentPane(new FindandReplace().getFindAndReplace(mainTextArea));

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    // Getters
    public JPanel getMainPanel() { return MainPanel; }
    public JTextArea getMainTextArea() { return mainTextArea; }
    public JList getJList() { return jList; }

}
