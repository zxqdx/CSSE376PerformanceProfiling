package state;

import java.awt.Graphics2D;
import java.util.ArrayList;

import objects.Bullet;
import objects.Digger;
import objects.Enemy;
import objects.Gold;
import objects.Level;
import toolObjects.Cheat;

/**
 * StateManager manages the various States of the game and assist in passing a
 * consistent objects.
 * 
 * @author Mark Hays and his students. Created Feb 6, 2015.
 */
public class StateManager {
	// Field Constants.
	private final int NUMBER_OF_STATES = 7;

	// Passed Digger.
	protected Digger player; // The player object that moves between the states.

	// Passed Bullet.
	protected Bullet bullet;

	// Passed Gold.
	protected Gold gold;
	
	public ArrayList<Enemy> enemies;

	private State[] states; // The states the StatesManager is
										// managing.
	private int currentState; // The index position on the states arrayList.
	private boolean isPlayingSurvival;// The boolean value if survival mode is selected.
	private Cheat cheat; // The Cheat handler.

	/**
	 * Constructs the StateManager that manages various States of the game.
	 * 
	 */
	public StateManager() {
		// Creates an arraylist for states.
		this.states = new State[this.NUMBER_OF_STATES];
		
		// Set isPlayingSurvival condition.
		this.isPlayingSurvival = false;
		
		this.cheat = new Cheat(this);

		// Sets the currentState to the menuState.
		this.currentState = 0;
		
		loadState(this.currentState);
		
		// Initialize a player object and enemy with null level.
		// this.gold = new Gold(this);
		this.player = new Digger(null);
		
		this.enemies = new ArrayList<Enemy>();

		// Initialize the bullet.
		this.bullet = new Bullet(this);

	}
	
	/**
	 * Sets the specified state to the corresponding State.
	 *
	 * @param state the specified state to update.
	 */
	public void loadState(int state) {
		long start = System.nanoTime();
		// Constants.
		final int MENU_STATE = 0;
		final int LEVEL_0 = 1;
		final int LEVEL_1 = 2;
		final int LEVEL_2 = 3;
		final int LEVEL_3 = 4;
		final int HIGH_SCORE_STATE = 5;
		final int SURVIVAL_STATE = 6;
		
		// Find corresponding state to update. Use final variables to obtain the correct numbers.
		if (state == MENU_STATE) {
			this.states[state] = new MenuState(this);
		} 
		if (state == LEVEL_0) {
			this.states[state] = new LevelState(this, LEVEL_0 - 1);
		} 
		if (state == LEVEL_1) {
			this.states[state] = new LevelState(this, LEVEL_1 - 1);
		}
		if (state == LEVEL_2) {
			this.states[state] = new LevelState(this, LEVEL_2 - 1); 
		}
		if (state == LEVEL_3) {
			this.states[state] = new LevelState(this, LEVEL_3 - 1);
		} 
		if (state == HIGH_SCORE_STATE) {
			if (this.isPlayingSurvival) {
				this.states[state] = new LevelState(this, LEVEL_0 - 1);
			} else {
				this.states[state] = new HighScoreState(this);
			}
		}
		if (state == SURVIVAL_STATE) {
			this.states[state] = new LevelState(this, LEVEL_0 - 1);
			this.isPlayingSurvival = true;
		}
		
		long delta = System.nanoTime() - start;
		System.err.println("Load time (ms): "+(delta/1000000L));
	}
	
	/**
	 * Set the specified state to null.
	 *
	 * @param state the specified state in relation to the states array.
	 */
	public void unloadState(int state) {
		if (state == 0) {
			this.states[state] = null;
		} 
		if (state == 1) {
			this.states[state] = null;		
		} 
		if (state == 2) {
			this.states[state] = null;		
		} 
		if (state == 3) {
			this.states[state] = null;		
		} 
		if (state == 4) {
			this.states[state] = null;		
		} 
		if (state == 5) {
			this.states[state] = null;
		}
	}

	/**
	 * Updates the currentState.
	 * 
	 */
	public void update() {
		if (this.states[this.currentState] != null) {
			this.states[this.currentState].update();
		}
	}

	/**
	 * Draws the currentState.
	 * 
	 * @param g2
	 *            the Graphics2D object to draw on.
	 */
	public void draw(Graphics2D g2) {
		if (this.states[this.currentState] != null) {
			this.states[this.currentState].draw(g2);
		}
	}

	/**
	 * Returns an array list of enemies. Useful for bullet to track enemy collision boxes.
	 * 
	 * @return an array list of enemies.
	 */
	public ArrayList<Enemy> getEnemies() {
		return this.enemies;

	}
	
	/**
	 * Sets the field array list enemies.
	 *
	 * @param enemies
	 */
	public void setEnemies(ArrayList<Enemy> enemies) {
		this.enemies = enemies;
	}

	/**
	 * Gets the player object assigned to the StateManager.
	 * 
	 * @return the player object.
	 */
	public Digger getPlayer() {
		return this.player;
	}

	/**
	 * Transitions the currentState to the specified state.
	 * 
	 * @param state
	 *            the newState index to move to.
	 */
	public void setState(int state) {
		unloadState(this.currentState);
		this.currentState = state;
		loadState(this.currentState);
	}

	/**
	 * Gets the level of the currentState.
	 * 
	 * @return the level of the currentState.
	 */
	public Level getLevel() {
		return this.states[this.currentState].getLevel();
	}

	/**
	 * Gets the states arraylist size.
	 * 
	 * @return the states arraylist size.
	 */
	public int getStatesSize() {
		return this.states.length;
	}

	/**
	 * Returns the current State the State Manager is on.
	 * 
	 * @return the current State.
	 */
	public State getState() {
		return this.states[this.currentState];
	}

	/**
	 * Returns the bullet passed to frames.
	 * 
	 * @return the bullet.
	 */
	public Bullet getBullet() {
		return this.bullet;
	}
	
	public boolean getIsPlayingSurvival() {
		return this.isPlayingSurvival;
	}
	
	public void setIsPlayingSurvival(boolean isPlayingSurvival) {
		this.isPlayingSurvival = isPlayingSurvival;
	}

	/**
	 * Passes KeyEvents (in int code form) to the currentState.
	 * 
	 * @param key
	 *            the int form of a key.
	 */
	public void keyPressed(int key) {
		if (this.states[this.currentState] != null) {
			this.states[this.currentState].keyPressed(key);
		}
		this.cheat.keyPressed(key);
	}

	/**
	 * Passes KeyEvents (in int code form) to the currentState.
	 * 
	 * @param key
	 *            the int form of a key.
	 */
	public void keyReleased(int key) {
		if (this.states[this.currentState] != null) {
			this.states[this.currentState].keyReleased(key);
		}
	}
}
