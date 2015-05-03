package toolObjects;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
 
/**
 * FreeTTS reads the given text out loud.
 * This code was a given sample code from: 
 * http://moderntone.blogspot.com/2013/02/freetts-tutorial.html.
 *
 * @author Mark Hays and his students.
 *         Created Feb 17, 2015.
 */
public class FreeTTS {
 
	private static final String VOICENAME_kevin = "kevin";
	private String text; // string to speech
 
	/**
	 * Constructs a FreeTTS object.
	 * 
	 * @param text the String to read.
	 */
	public FreeTTS(String text) {
		this.text = text;
	}
 
	/**
	 * Enables speaking with voice.
	 *
	 */
	public void speak() {
		Voice voice;
		VoiceManager voiceManager = VoiceManager.getInstance();
		voice = voiceManager.getVoice(VOICENAME_kevin);
		voice.allocate();
		voice.speak(this.text);
	}
}
