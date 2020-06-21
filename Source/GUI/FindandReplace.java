package Source.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindandReplace {
    private JPanel findAndReplace;
    private JButton buttonFind;
    private JTextArea ta1;
    private JTextField t1;
    private JTextField t2;

    public  FindandReplace() {

        buttonFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String a = t1.getText();
                String b = t2.getText();
                String c = ta1.getText();
                JOptionPane.showMessageDialog(null,"Are you sure you want to " +
                        "replace \"" + a + "\" with \"" + b + "\" " +
                        "? \nPlease click ok." );

                String d = c.replaceAll(a,b);
                ta1.setText(d);

            }
        });
    }


//    public static void main(String[] args) {
//        JFrame frame = new JFrame(("FindandReplace"));
//        frame.setContentPane(new FindandReplace().findAndReplace);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//    }

    public JPanel getFindAndReplace(JTextArea copy) {
        String f = copy.getText();
        ta1.setText(f);
        return findAndReplace;
    }




}
