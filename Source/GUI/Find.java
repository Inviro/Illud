package Source.GUI;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;

public class Find extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel instanceSearch;
    private JTextArea area;

    private final Highlighter.HighlightPainter currResultHighlight = new highlighter(Color.ORANGE);
    private final Highlighter.HighlightPainter allResultsHighlight = new highlighter(Color.YELLOW);
    private Highlighter.Highlight[] highlightArr;
    private Highlighter high;

    public Find(JTextArea area) {
        this.area = area;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.setSize(600, 200);
        this.setLocationRelativeTo(null);
        this.setTitle("Find");

        highlightArr = null;
        buttonOK.addActionListener(e -> find());
        buttonCancel.addActionListener(e -> onCancel());
        prevButton.addActionListener(e -> onPrev());
        nextButton.addActionListener(e -> onNext());
        instanceSearch.setVisible(false);
        high = area.getHighlighter();
    }

    private void onCancel() {
        dispose();
    }

    private void find() {
        String input = textField1.getText();
        if (!input.isEmpty()) {
            highlight(input);
        }
    }

    private void setHighlight(int index,  Highlighter.HighlightPainter p){
        high.removeHighlight(highlightArr[index]);
        try{
            highlightArr[index] = (Highlighter.Highlight) high.addHighlight(highlightArr[0].getStartOffset(),
                    highlightArr[index].getEndOffset(), p);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Previous instance of found string
    private void onPrev() {
    }

    // Next instance of found string
    private void onNext() {
    }

    private class highlighter extends DefaultHighlighter.DefaultHighlightPainter {
        public highlighter(Color color) {
            super(color);
        }
    }

    private void highlight(String pattern) {
        high.removeAllHighlights();
        try {
            Document doc = area.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
                high.addHighlight(pos, pos + pattern.length(), allResultsHighlight);
                pos += pattern.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        perInstance();
    }

    private void perInstance() {
        highlightArr = high.getHighlights();
        instanceSearch.setVisible(highlightArr.length > 0);
    }
}





