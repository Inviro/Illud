package Source.Logic;

import Source.GUI.UserInput;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class FileOpener {
    // Class Variables
    private Scanner scanner;                                                // Scanner for getting input
    private JFileChooser fc;                                                // File Chooser
    private Vector<String> acceptedFileTypes;

    // Default Constructor
    public FileOpener(){
        // Creating File Chooser
        fc = new JFileChooser();                                            // New file chooser object
        scanner = null;                                                     // Initializing scanner object

        // Setting acceptable file types
//        fc.setAcceptAllFileFilterUsed(false);                             // Does not accept all file types

        acceptedFileTypes = new Vector<>();                                 // Holds accepted file types
        acceptedFileTypes.add("txt");                                       // Text files
        fc.setFileFilter(new FileFilter() {                                 // Creates a new filter
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()){                                       // Allows folders to be selected
                    return true;
                } else{
                    String filename = f.getName().toLowerCase();
                    for(String ele: acceptedFileTypes){                     // For each accepted file type
                        if (filename.endsWith(ele)){                        // Returns true if suffix type is accepted
                            return true;
                        }
                    }
                    return false;
                }
            }
            @Override
            public String getDescription() {
                // Creating accepted file type descriptions
                String temp = "Text Files ";
                for(String ele: acceptedFileTypes) {                        // For each accepted file type
                    temp += "(*." + ele + ") ";
                }
                return temp;
            }
        });
    }

    public void activate(java.awt.Component component, UserInput userInput){
        // Opens the dialog for the file chooser
        int returnVal = fc.showOpenDialog(component);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            // Reading file into a string
            try {
                scanner = new Scanner(file);
            } catch (Exception e) { e.printStackTrace(); }
            if(scanner.hasNext()){
                String fileText = scanner
                        .useDelimiter("\\A")                        // Delimiter - End of Line
                        .next()                                     // Next input
                        .replace("\r", "");         // Removes extra CR
                scanner.close();
                userInput.setFile(fileText); // Puts string from file into main text area
            }
        }
    }
}
