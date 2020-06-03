package Source.Main;
import Libraries.MaryTTS.Tutorial.TextToSpeech;
import marytts.modules.synthesis.Voice; // Used if you want to print out the available voices

public class Main {
    public static void main (String[] args){
        TextToSpeech tts = new TextToSpeech();

        // Prints out the available names in tts
//        for(Voice ele: tts.getAvailableVoices()){
//            System.out.println(ele.getName());
//        }

        tts.setVoice("dfki-poppy-hsmm"); // This one sounds the least bad. cmu-rms-hsmm sounds horrible.

        // Change these to adjust the tts settings
        final String TEXT_TO_SPEAK = "Hello World!";    // String to read
        final float VOLUME = 1.0f;                      // Volume on a scale from 0 being 0.0f and 100 being 1.0f

        tts.speak(TEXT_TO_SPEAK, VOLUME, false, false);

    }
}
