package Source.GUI;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
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
    private JTextField replaceField;
    private JTextArea area;

    // Highlighter components
    private final Highlighter.HighlightPainter currResultHighlight = new highlighter(Color.ORANGE);
    private final Highlighter.HighlightPainter allResultsHighlight = new highlighter(Color.YELLOW);

    private Highlighter.Highlight[] highlightArr;   // Array of highlights of occurrences
    private Highlighter high;                       // Highlights each element
    private int index;                              // Current index in search
    private boolean isHidden;                       // Used for when find window is closed to reshow results
    private String tempText;                        // Used to store the user search when hiding find
    private int oldTextHash;                        // Used to check if the document changed while hiding
    private int oldArrChecksum;                     // Same as above

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

        // Listener that checks for window closing and opening events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                isHidden = true;                                        // Find has been opened before and is hidden
                tempText = queryField.getText();                        // Saves query text
                oldTextHash = area.getText().hashCode();                // Saves hash of current document
                oldArrChecksum = getHighlightArrCheckSum();
                onClear();                                              // Clears the window
                if(highlightArr != null && highlightArr.length > 0){
                    highlightElement(highlightArr[index]);              // Highlights the search result
                }
            }

            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                if(isHidden){                           // Checks if window was opened before
                    reDisplay();
                    isHidden = false;
                }
            }
        });

        // Adding listeners
        buttonOK.addActionListener(e -> find());
        buttonCancel.addActionListener(e -> onCancel());
        prevButton.addActionListener(e -> onPrev());
        nextButton.addActionListener(e -> onNext());
        clearButton.addActionListener(e -> onClear());

        instanceSearch.setVisible(false);   // Cannot access next and previous buttons initially
        high = area.getHighlighter();       // Class highlighter variable
        isHidden = false;                   // Is not in hidden state
    }

    private void onCancel() {
        dispose();
    }

    private void find() {
        tempText = queryField.getText();
        String title = "Search Results";
        if (!tempText.isEmpty()) {
            highlight(tempText);
            if(highlightArr.length > 0){
                scrollToQuery(highlightArr[index]);
            }
            else{
                String message = "No results found for: \"" + tempText + "\"";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error: No input detected",
                    title,
                    JOptionPane.ERROR_MESSAGE);
            instanceSearch.setVisible(false);
        }
    }

    // Sets the highlight for a single element
    private void setHighlight(int index, Highlighter.HighlightPainter p){
        high.removeHighlight(highlightArr[index]);
        try{
            highlightArr[index] = (Highlighter.Highlight) high.addHighlight(highlightArr[index].getStartOffset(),
                    highlightArr[index].getEndOffset(), p);
        } catch (Exception e) { e.printStackTrace(); }
        String resultString = "Showing result: " + (index + 1) + " of " + highlightArr.length;
        instanceSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(resultString));
    }

    // Overloaded function that sets highlights for all elements
    private void setHighlights(Highlighter.HighlightPainter p){
        for(int i = 0; i < highlightArr.length; i++){
            high.removeHighlight(highlightArr[i]);
            try{
                highlightArr[i] = (Highlighter.Highlight) high.addHighlight(highlightArr[i].getStartOffset(),
                        highlightArr[i].getEndOffset(), p);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    // Previous instance of found string
    private void onPrev() {
        // Determines new index based on old index and looping
        int newIdx = (index == 0) ? highlightArr.length - 1 : (index - 1);
        changeInstance(index, newIdx);
    }

    // Next instance of found string
    private void onNext() {
        // Determines new index based on old index and looping
        int newIdx = (index == highlightArr.length - 1) ? 0 : (index + 1);
        changeInstance(index, newIdx);
    }

    private void changeInstance(int oldIndex, int newIndex){
        // If tempText did not change
        if(tempText.equals(queryField.getText())){ // No change
            if(newIndex != oldIndex){ // Only runs if new and old indexes differ
                setHighlight(index, allResultsHighlight);
                index = newIndex;
                setHighlight(newIndex, currResultHighlight);
                scrollToQuery(highlightArr[index]);
            }
        } else{ // Changed
            find(); // Searches for the new query
        }
    }

    // Scrolls to query if it is off screen
    private void scrollToQuery(Highlighter.Highlight h){
        int pos = h.getStartOffset();
        try{
            java.awt.Rectangle view = area.modelToView(h.getEndOffset());   // Gets view rectangle where pos is visible
            area.scrollRectToVisible(view);                                 // Scroll to the rectangle
            area.setCaretPosition(pos);                                     // Sets carat position to pos
        } catch (Exception e) {e.printStackTrace();}
    }

    // Next instance of found string
    private void onClear() {
        high.removeAllHighlights();
        queryField.setText("");
        instanceSearch.setVisible(false);
    }

    private static class highlighter extends DefaultHighlighter.DefaultHighlightPainter {
        public highlighter(Color color) {
            super(color);
        }
    }

    private void highlight(String pattern) {
        index = 0;
        high.removeAllHighlights();
        try {
            String text = area.getText();
            int pos = 0;
            while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) >= 0) {
                high.addHighlight(pos, pos + pattern.length(), allResultsHighlight);
                pos += pattern.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        highlightArr = high.getHighlights();            // Populates array of highlights
        if(highlightArr.length > 0){ // 1+ matches
            setHighlight(index, currResultHighlight);   // Highlights index 0
            instanceSearch.setVisible(true);            // Enables prev and next buttons
        } else{ // 0 matches
            instanceSearch.setVisible(false);           // Disables prev and next buttons
        }
    }

    // Re displays find after it is closed
    private void reDisplay(){
        if(!tempText.isEmpty()){                                                // Has saved query
            boolean textChanged = (area.getText().hashCode() != oldTextHash);   // Check for change in text input
            boolean arrChanged = (oldArrChecksum != getHighlightArrCheckSum()); // Check for change in checksum
            queryField.setText(tempText);                                       // Sets search bar to old text
            if(textChanged || arrChanged){                                      // Area did not change since hiding
                // Changed
                int tempIndex = index;                                          // Saves index before doing new search
                find();                                                         // Searches new text based on old query
                index = tempIndex;                                              // Sets index to old value
                index = ((index > (highlightArr.length - 1)) ? 0 : index);      // Adjusts index to value in array
                if(index > 0){                                                  // Checks for default value
                    // Not default, so changes highlights accordingly
                    setHighlight(0, allResultsHighlight);
                    setHighlight(index, currResultHighlight);
                    scrollToQuery(highlightArr[index]);                         // Moves view box to cursor
                }
            } else{
                // No change
                if(highlightArr.length > 0){ // Has matches
                    setHighlights(allResultsHighlight);                         // Sets all highlight color
                    setHighlight(index, currResultHighlight);                   // Sets current highlight color
                    scrollToQuery(highlightArr[index]);                         // Moves view box to cursor
                    instanceSearch.setVisible(true);                            // Shows the next and prev buttons
                }
            }
        }
    }

    private void highlightElement(Highlighter.Highlight h){
        int pos = h.getEndOffset();
        try{
            java.awt.Rectangle view = area.modelToView(pos);                    // View where pos is visible
            area.scrollRectToVisible(view);                                     // Scroll to the rectangle
            area.setCaretPosition(pos);                                         // Sets carat position to pos
            area.moveCaretPosition(h.getStartOffset());                         // Highlights text
        } catch (Exception e) {e.printStackTrace();}
    }

    // Sum of start and end offsets to ensure no change has occurred
    private int getHighlightArrCheckSum(){
        int sum = 0;
        if(highlightArr != null){
            for(Highlighter.Highlight h : highlightArr){
                sum += h.getStartOffset();
                sum += h.getEndOffset();
            }
        }
        return sum;
    }
}
