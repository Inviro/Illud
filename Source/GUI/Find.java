
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
    private JTextField queryField;
    private JButton prevButton;
    private JButton nextButton;
    private JPanel instanceSearch;
    private JButton clearButton;
    private JTextField replaceField;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JCheckBox replaceOption;
    private JPanel replacePanel;
    private JTextArea area;

    // Highlighter components
    private final Highlighter.HighlightPainter currResultHighlight = new highlighter(Color.ORANGE);
    private final Highlighter.HighlightPainter allResultsHighlight = new highlighter(Color.YELLOW);

    private Highlighter.Highlight[] highlightArr;   // Array of highlights of occurrences
    private Highlighter high;                       // Highlights each element
    private int index;                              // Current index in search
    private boolean isHidden;                       // Used for when find window is closed to reshow results
    private String oldQuery;                        // Used to store the user search when hiding find
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
        this.setTitle("Find");

        // Listener that checks for window closing and opening events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                isHidden = true;                                        // Find has been opened before and is hidden
                oldQuery = queryField.getText();                        // Saves query text
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
                if(isHidden){           // Checks if window was opened before
                    reDisplay();        // Re displays highlights
                    isHidden = false;   // Sets sentinel value
                }
            }
        });

        // Adding listeners
        buttonOK.addActionListener(e -> onFind());
        prevButton.addActionListener(e -> onPrev());
        nextButton.addActionListener(e -> onNext());
        clearButton.addActionListener(e -> onClear());
        replaceButton.addActionListener(e -> onReplace());
        replaceAllButton.addActionListener(e -> onReplaceAll());

        setPanelVis(false);                 // Cannot access some GUI elements initially
        this.setLocationRelativeTo(null);   // Centers dialog
        high = area.getHighlighter();       // Class highlighter variable
        isHidden = false;                   // Is not in hidden state
    }

    private void onFind() {
        oldQuery = queryField.getText();
        String title = "Search Results";
        if (!oldQuery.isEmpty()) {
            highlight(oldQuery);
            if(highlightArr.length > 0){
                scrollToQuery(highlightArr[index]);
            }
            else{
                String message = "No results found for: \"" + oldQuery + "\"";
                JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error: No input detected",
                    title,
                    JOptionPane.ERROR_MESSAGE);
            setPanelVis(false);
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

    // Next instance of found string
    private void onClear() {
        high.removeAllHighlights();
        queryField.setText("");
        setPanelVis(false);
    }

    // Replace current instance
    private void onReplace(){
        java.lang.StringBuffer sb = new StringBuffer(area.getText());       // New string buffer
        int startPos = highlightArr[index].getStartOffset();
        int endPos = highlightArr[index].getEndOffset();
        area.setText(                                                       // Setting text of main text area
                sb.replace(startPos,                                        // Replace the substring from startPos
                        endPos,                                             // to endPos
                        replaceField.getText()).toString()                  // with what is in replace field
        );
        reDisplay();                                                        // Re displays text
    }

    // Replace all instances
    private void onReplaceAll(){
        String currentText = area.getText();
        String replacement = replaceField.getText();                        // String to replace
        if(!replaceOption.isSelected()) {                                   // Boundary checking is required
            area.setText(currentText.replaceAll(oldQuery, replacement));    // Replaces all of the instances
        } else{
            String regexQuery = String.format("\\b%s\\b", oldQuery);        // Regular expression for strict matches
            area.setText(currentText.replaceAll(regexQuery, replacement));  // Replaces all of the instances
        }
        high.removeAllHighlights();                                         // Clears all highlights
        onFind();                                                           // Does new search
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
        high.removeAllHighlights();
        for(int i = 0; i < highlightArr.length; i++){
            try{
                highlightArr[i] = (Highlighter.Highlight) high.addHighlight(highlightArr[i].getStartOffset(),
                        highlightArr[i].getEndOffset(), p);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void changeInstance(int oldIndex, int newIndex){
        // If tempText did not change
        if(oldQuery.equals(queryField.getText())){ // No change
            if(newIndex != oldIndex){ // Only runs if new and old indexes differ
                setHighlight(index, allResultsHighlight);
                index = newIndex;
                setHighlight(newIndex, currResultHighlight);
                scrollToQuery(highlightArr[index]);
            }
        } else{ // Changed
            onFind(); // Searches for the new query
        }
    }

    // Scrolls to query if it is off screen
    private void scrollToQuery(Highlighter.Highlight h){
        int pos = h.getStartOffset();
        try{
            java.awt.geom.Rectangle2D view = area.modelToView2D(pos);   // View where pos is visible
            area.scrollRectToVisible(view.getBounds());                 // Scroll to the rectangle
            area.setCaretPosition(pos);                                 // Sets carat position to pos
        } catch (Exception e) {e.printStackTrace();}
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
            setPanelVis(true);                          // Shows GUI elements
        } else{ // 0 matches
            setPanelVis(false);                         // Hides GUI elements
        }
    }

    // Re displays find after it is closed
    private void reDisplay(){
        if(!oldQuery.isEmpty()){                                                // Has saved query
            boolean textChanged = (area.getText().hashCode() != oldTextHash);   // Check for change in text input
            boolean arrChanged = (oldArrChecksum != getHighlightArrCheckSum()); // Check for change in checksum
            queryField.setText(oldQuery);                                       // Sets search bar to old text
            if(textChanged || arrChanged){                                      // Area did not change since hiding
                // Changed
                int tempIndex = index;                                          // Saves index before doing new search
                onFind();                                                         // Searches new text based on old query
                index = tempIndex;                                              // Sets index to old value
                index = ((index > (highlightArr.length - 1)) ? 0 : index);      // Adjusts index to value in array
                if(index > 0){                                                  // Checks for default value
                    // Not default, so changes highlights accordingly
                    setHighlight(0, allResultsHighlight);                 // Sets all to highlight color
                    setHighlight(index, currResultHighlight);                   // Sets current highlight color
                    scrollToQuery(highlightArr[index]);                         // Moves view box to cursor
                }
            } else{
                // No change
                if(highlightArr.length > 0){ // Has matches
                    setHighlights(allResultsHighlight);                         // Sets all to highlight color
                    setHighlight(index, currResultHighlight);                   // Sets current highlight color
                    scrollToQuery(highlightArr[index]);                         // Moves view box to cursor
                    setPanelVis(true);                                          // Shows GUI elements
                }
            }
        }
    }

    private void highlightElement(Highlighter.Highlight h){
        int pos = h.getEndOffset();
        try{
            java.awt.geom.Rectangle2D view = area.modelToView2D(pos);           // View where pos is visible
            area.scrollRectToVisible(view.getBounds());                         // Scroll to the rectangle
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

    // Sets panel visibility for panels that only appear if there are search results
    private void setPanelVis(boolean isVis){
        // Sets size depending on the GUI elements that are visible
        if(isVis){
            this.setSize(750, 210);
        }
        else{
            this.setSize(600, 130);
        }
        this.instanceSearch.setVisible(isVis);
        this.replacePanel.setVisible(isVis);
    }
}
