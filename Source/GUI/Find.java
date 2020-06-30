package Source.GUI;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;

public class Find extends JDialog {
    // GUI components
    private JPanel contentPane;
    private JButton prevButton;
    private JButton nextButton;
    private JButton replaceButton;
    private JButton replaceAllButton;

    private JTextField queryField;
    private JTextField replaceField;
    private JPanel replacePanel;

    private JCheckBox replaceOption;
    private JPanel findPanel;

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
        area.setAutoscrolls(true);
        this.area = area;
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setTitle("Find");

        // Adding listeners
        prevButton.addActionListener(e -> onPrev());
        nextButton.addActionListener(e -> onNext());
        replaceButton.addActionListener(e -> onReplace());
        replaceAllButton.addActionListener(e -> onReplaceAll());

        queryField.addActionListener(e -> onFind());
        replaceField.addActionListener(e -> onReplace());

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
                if(isHidden){                       // Checks if window was opened before
                    reDisplay();                    // Re displays highlights
                    isHidden = false;               // Sets sentinel value
                }
                queryField.requestFocusInWindow();  // Gets focus for query field
            }
        });

        // Keyboard Listeners
        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

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
            high.removeAllHighlights();
            setPanelVis(false);
        }
    }

    // Previous instance of found string
    private void onPrev() {
        if(checkInstance()){
            // Determines new index based on old index and looping
            int newIdx = (index == 0) ? highlightArr.length - 1 : (index - 1);
            changeInstance(index, newIdx);
            this.getRootPane().setDefaultButton(prevButton); // Sets default button to last pressed one
        }
    }

    // Next instance of found string
    private void onNext() {
        if(checkInstance()){
            // Determines new index based on old index and looping
            int newIdx = (index == highlightArr.length - 1) ? 0 : (index + 1);
            changeInstance(index, newIdx);
            this.getRootPane().setDefaultButton(nextButton); // Sets default button to last pressed one
        }
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
        this.getRootPane().setDefaultButton(replaceButton);                 // Sets default button to last pressed one
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
        high.removeAllHighlights();                                         // Removes all highlights
        onFind();                                                           // Does new search
        this.getRootPane().setDefaultButton(replaceAllButton);              // Sets default button to last pressed one
    }

    // Sets the highlight for a single element
    private void setHighlight(int index, Highlighter.HighlightPainter p){
        high.removeHighlight(highlightArr[index]);
        try{
            highlightArr[index] = (Highlighter.Highlight) high.addHighlight(highlightArr[index].getStartOffset(),
                    highlightArr[index].getEndOffset(), p);
        } catch (Exception e) { e.printStackTrace(); }
        String resultString = "Showing: " + (index + 1) + " of " + highlightArr.length;
        javax.swing.border.TitledBorder titledBorder = javax.swing.BorderFactory.createTitledBorder(resultString);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        findPanel.setBorder(titledBorder);
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

    // Run by onNext and onPrev, generalizes their actions before their core code
    private boolean checkInstance(){
        if(highlightArr != null && highlightArr.length > 0){
            return true;
        } else{
            onFind();
            return highlightArr != null && highlightArr.length > 0;
        }
    }

    // Run by onNext and onPrev, generalizes their actions after their core code
    private void changeInstance(int oldIndex, int newIndex){
        if(checkInstance()){
            // If tempText did not change
            if(oldQuery.equals(queryField.getText())){ // No change
                if(newIndex != oldIndex){ // Only runs if new and old indexes differ
                    setHighlight(index, allResultsHighlight);
                    index = newIndex;
                    scrollToQuery(highlightArr[index]);
                }
            } else{ // Changed
                onFind(); // Searches for the new query
            }
        }
    }

    // Scrolls to query if it is off screen
    private void scrollToQuery(Highlighter.Highlight h){
        setHighlight(index, currResultHighlight);                       // Sets current highlight color
        int pos = h.getEndOffset();
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
            while ((pos = text.toUpperCase().indexOf(pattern.toUpperCase(), pos)) > -1) {
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
                onFind();                                                       // Searches new text based on old query
                index = tempIndex;                                              // Sets index to old value
                index = ((index > (highlightArr.length - 1)) ? 0 : index);      // Adjusts index to value in array
                if(index > 0){                                                  // Checks for default value
                    // Not default, so changes highlights accordingly
                    setHighlight(0, allResultsHighlight);                 // Sets first idx to all highlight color
                    scrollToQuery(highlightArr[index]);                         // Moves view box to cursor
                }
            } else{
                // No change
                if(checkInstance()){                                            // Has matches
                    setHighlights(allResultsHighlight);                         // Sets all to highlight color
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
            this.setSize(500, 240);
        }
        else{
            this.setSize(500, 140);
            javax.swing.border.TitledBorder titledBorder = javax.swing.BorderFactory.createTitledBorder("Find");
            titledBorder.setTitleJustification(TitledBorder.CENTER);
            findPanel.setBorder(titledBorder);
        }
        this.replacePanel.setVisible(isVis);
    }
}
