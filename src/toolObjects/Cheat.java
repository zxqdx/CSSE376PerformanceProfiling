package toolObjects;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import state.StateManager;


/**
 * Handles cheat code entry anytime in the game. Currently only checks if the Konami code is entered.
 * 
 * @author Mark Hays and his students. Created Feb 16, 2015.
 */
public class Cheat {
	// Field Constants.
	// Konami Code.
	private final ArrayList<Integer> KONAMI = new ArrayList<Integer>(
			Arrays.asList(KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN,
					KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
					KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_A,
					KeyEvent.VK_B, KeyEvent.VK_A, KeyEvent.VK_B));
	
	// Field Variables.
	private ArrayList<Integer> code;
	private StateManager stateManager;
	private int currentIndex;

	private MusicPlayer victorySound; // Victory music played on code completion.

	/**
	 * Constructs a Cheat handler.
	 * 
	 * @param stateManager
	 */
	public Cheat(StateManager stateManager) {
		this.code = new ArrayList<Integer>();
		this.stateManager = stateManager;
		this.currentIndex = 0;
		this.victorySound = new MusicPlayer(
				"/music/victorySound.mp3");
	}

	/**
	 * Adds a key, the integer value of it, into the code array list. If it does
	 * not match the KONAMI code, the code array list is cleared.
	 * 
	 * @param key
	 *            the integer value of the array list.
	 */
	public void keyPressed(int key) {
		// Add the key to the code array.
		this.code.add(key);

		// Checks if player has entered the konami code sequence. Activate Cheat if so.
		if (this.code.equals(this.KONAMI)) {
			this.victorySound.play();
			this.stateManager.getPlayer().setLife(9);
		}

		// Clear if the arraylist if it is greater than or equal to 12 in size
		// or that the currentIndex in both array list are not equal. Else,
		// increment the currentIndex.
		if (this.code.size() >= 12
				|| this.KONAMI.get(this.currentIndex) != this.code
						.get(this.currentIndex)) {
			this.code.clear();
			this.currentIndex = 0;
		} else {
			this.currentIndex++;
		}

		// Console Feedback.
		// System.out.println(this.code.toString());

	}
}
