package Source.GUI;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;


public class Find extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextArea area;


    public Find(JTextArea area) {
        this.area= area;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> find());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane.registerKeyboardAction(e->dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent. WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.setSize(600, 150);
        this.setLocationRelativeTo(null);
        this.setTitle("Find");


        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
    }

    private void onCancel() {
        dispose();
    }

    private void find(){
        String a= textField1.getText();
        String c= area.getText();
        highlight(area, textField1.getText());
        }



    class highlighter extends DefaultHighlighter.DefaultHighlightPainter {
        public highlighter(Color color) {
            super(color);
        }
    }

    Highlighter.HighlightPainter highlighter = new highlighter(Color.yellow);

    public void removeHighlight(JTextComponent textComp) {
        Highlighter high = textComp.getHighlighter();
        Highlighter.Highlight[] oldHighlighted = high.getHighlights();
        for (int i = 0; i < oldHighlighted.length; i++) {
            if (oldHighlighted[i].getPainter() instanceof highlighter) {
                high.removeHighlight(oldHighlighted[i]);
            }
        }
    }


    public void highlight(JTextComponent textComp, String pattern) {
        removeHighlight(textComp);
        try {
            Highlighter high = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;

            while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
                high.addHighlight(pos, pos + pattern.length(), highlighter);
                pos += pattern.length();

            }


        } catch (Exception e) {
        }
    }




    }




