package toolObjects;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * A media player that plays specified music files.
 *
 * @author Mark Hays and his students.
 *         Created Feb 14, 2015.
 */
public class MusicPlayer {
	
	private Clip clip;
	static HashMap<String, Clip> clips=new HashMap<String, Clip>();
	
	/**
	 * Constructs a Music Player.
	 *
	 * @param fileName the destination of the media file.
	 */
	public MusicPlayer(String fileName) {
		// FIXME: reduce the number of calls to the code below
		// Obtain a clip.
		try {
			if(clips.containsKey(fileName)){
				this.clip = clips.get(fileName);
				return;
			}
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
			AudioFormat baseFormat = audioInputStream.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
					baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
					false);
			AudioInputStream decodeAudioInputStream = AudioSystem.getAudioInputStream(decodeFormat, audioInputStream);
			this.clip = AudioSystem.getClip();
			this.clip.open(decodeAudioInputStream);
			audioInputStream.close();
			decodeAudioInputStream.close();
			clips.put(fileName, clip);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Plays the MusicPlayer.
	 *
	 */
	public void play() {
		if (this.clip == null) {
			return;
		}
		stop();
		this.clip.setFramePosition(0);
		this.clip.start();
	}
	
	/**
	 * Plays the MusicPlayer in a continuous loop. Ideal for level background music.
	 *
	 */
	public void playLoop() {
		if (this.clip == null) {
			return;
		}
		stop();
		this.clip.flush();
		this.clip.setFramePosition(0);
		this.clip.start();
		this.clip.loop(this.clip.LOOP_CONTINUOUSLY);
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops the MusicPlayer.
	 *
	 */
	public void stop() {
		if (this.clip.isRunning()) {
			this.clip.stop();
		}
	}
	
	/**
	 * Stops and closes the MusicPlayer.
	 *
	 */
	public void close() {
		stop();
		//this.clip.close();
	}
}
