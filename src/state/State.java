package state;
import java.awt.Graphics2D;

import objects.Level;

/**
 * The basic requirements of a State object within a State manager.
 *
 * @author Mark Hays and his students.
 *         Created Feb 6, 2015.
 */
public abstract class State {
	protected StateManager stateManager; // The StateManager managing this particular State.

	/**
	 * Sets up the State according to its purpose.
	 * 
	 */
	public abstract void initialize();

	/**
	 * Updates the State according to certain actions.
	 * 
	 */
	public abstract void update();

	/**
	 * Draws the State on a given Graphics2D object.
	 * 
	 * @param g2
	 *            the given Graphics2D object to be drawn on.
	 */
	public abstract void draw(Graphics2D g2);

	/**
	 * Responds to keyboard Events.
	 * 
	 * @param key
	 *            the integer code of a key pressed.
	 */
	public abstract void keyPressed(int key);

	/**
	 * Responds to keyboard Events.
	 * 
	 * @param key
	 *            the integer code of a key released.
	 */
	public abstract void keyReleased(int key);

	/**
	 * Gets the level object of a particular state. Returns null if the state
	 * does not have a Level object.
	 * 
	 * @return the level object.
	 */
	public Level getLevel() {
		return null;
	}

}
