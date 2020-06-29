package Source.GUI;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;

public class Find extends JDialog {
    // GUI components
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField queryField;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel instanceSearch;
    private JButton clearButton;
    private JTextArea area;

    // Highlighter components
    private final Highlighter.HighlightPainter currResultHighlight = new highlighter(Color.ORANGE);
    private final Highlighter.HighlightPainter allResultsHighlight = new highlighter(Color.YELLOW);

    private Highlighter.Highlight[] highlightArr;   // Array of highlights of occurrences
    private Highlighter high;                       // Highlights each element
    private int index;                              // Current index in search
    private boolean isHidden;                       // Used for when find window is closed to reshow results
    private String tempText;                        // Used to store the user search when hiding find

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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                isHidden = true;
                tempText = queryField.getText();
                onClear();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                if(isHidden){
                    reDisplay();
                    isHidden = false;
                }
            }
        });

        highlightArr = null;
        buttonOK.addActionListener(e -> find());
        buttonCancel.addActionListener(e -> onCancel());
        prevButton.addActionListener(e -> onPrev());
        nextButton.addActionListener(e -> onNext());
        clearButton.addActionListener(e -> onClear());
        instanceSearch.setVisible(false);
        high = area.getHighlighter();
        isHidden = false;
    }

    private void onCancel() {
        dispose();
    }

    private void find() {
        String input = queryField.getText();
        if(tempText == null || !tempText.equals(input)){
            if (!input.isEmpty()) {
                highlight(input);
            } else {
                instanceSearch.setVisible(false);
            }
        } else{
            tempText = input;
        }
    }

    private void setHighlight(int index, Highlighter.HighlightPainter p){
        high.removeHighlight(highlightArr[index]);
        try{
            highlightArr[index] = (Highlighter.Highlight) high.addHighlight(highlightArr[index].getStartOffset(),
                    highlightArr[index].getEndOffset(), p);
        } catch (Exception e) { e.printStackTrace(); }
        String resultString = "Showing " + (index + 1) + " of " + highlightArr.length + " results";
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
        queryField.setText("");
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

    // Re displays find after it is closed
    private void reDisplay(){
        if(!tempText.isEmpty()){
            queryField.setText(tempText);
            if(highlightArr.length > 0){
                for(int i = 0; i < highlightArr.length; i++){
                    setHighlight(i, allResultsHighlight);
                }
                setHighlight(index, currResultHighlight);
                instanceSearch.setVisible(true);
            }
        }
    }
}





