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
    private JButton clearButton;
    private JTextArea area;

    private final Highlighter.HighlightPainter currResultHighlight = new highlighter(Color.ORANGE);
    private final Highlighter.HighlightPainter allResultsHighlight = new highlighter(Color.YELLOW);
    private Highlighter.Highlight[] highlightArr;
    private Highlighter high;
    private int index;

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
        clearButton.addActionListener(e -> onClear());
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
        } else {
            instanceSearch.setVisible(false);
        }
    }

    private void setHighlight(int index, Highlighter.HighlightPainter p){
        high.removeHighlight(highlightArr[index]);
        try{
            highlightArr[index] = (Highlighter.Highlight) high.addHighlight(highlightArr[index].getStartOffset(),
                    highlightArr[index].getEndOffset(), p);
        } catch (Exception e) { e.printStackTrace(); }
        String resultString = "Showing result " + (index + 1) + " of " + highlightArr.length + ".";
        instanceSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(resultString));
    }

    // Previous instance of found string
    private void onPrev() {
        int newIdx = (index == 0) ? index : (index - 1);
        if(newIdx != index){
            setHighlight(index, allResultsHighlight);
            index = newIdx;
            setHighlight(newIdx, currResultHighlight);
        }
    }

    // Next instance of found string
    private void onNext() {
        int newIdx = (index == highlightArr.length - 1) ? index : (index + 1);
        if(newIdx != index){
            setHighlight(index, allResultsHighlight);
            index = newIdx;
            setHighlight(newIdx, currResultHighlight);
        }
    }

    // Next instance of found string
    private void onClear() {
        high.removeAllHighlights();
        textField1.setText("");
        instanceSearch.setVisible(false);
    }

    private class highlighter extends DefaultHighlighter.DefaultHighlightPainter {
        public highlighter(Color color) {
            super(color);
        }
    }

    private void highlight(String pattern) {
        high.removeAllHighlights();
        index = 0;
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

        highlightArr = high.getHighlights();
        if(highlightArr.length > 0){
            setHighlight(index, currResultHighlight);
            instanceSearch.setVisible(true);
        } else{
            instanceSearch.setVisible(false);
        }
    }
}





