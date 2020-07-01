package Source.GUI;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.File;

public class About extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JEditorPane editorPane;

    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 340;

    public About() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onOK() on Escape
        contentPane.registerKeyboardAction(e -> onOK(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Hyperlink Listener
        editorPane.addHyperlinkListener(e -> {
            if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){ // Clicked Hyperlink
                // Launching Hyperlink
                if(Desktop.isDesktopSupported()){ // Checks if desktop is supported
                    // Checks link type
                    if(e.getURL() != null){ // Web link
                        try{
                            Desktop.getDesktop().browse(e.getURL().toURI()); // Open in browser
                        } catch (Exception ex) { ex.printStackTrace(); }
                    } else{ // File
                        try{
                            String filePath = new File("").getAbsolutePath();
                            filePath += "/" + e.getDescription();
                            Desktop.getDesktop().open(new File(filePath)); // Opens file using description
                        } catch (Exception ex) { ex.printStackTrace(); }
                    }
                }
            }
        });

        this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);              // Setting Dialog Size
        this.setLocationRelativeTo(null);                       // Centers Dialog
        this.setTitle("About");                                 // Sets Dialog Title
    }

    private void onOK() {
        // add your code here
        dispose();
    }
}
